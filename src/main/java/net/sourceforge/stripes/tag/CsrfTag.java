package net.sourceforge.stripes.tag;

import java.util.*;
import java.util.concurrent.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import com.orbis.*;
import net.sourceforge.stripes.action.*;

/**
 * Created by Daniil Sosonkin
 * 3/19/2017 8:54 PM
 */
public class CsrfTag extends InputTagSupport implements BodyTag
    {
        public CsrfTag()
            {
                getAttributes().put("type", "hidden");
            }

        @Override
        public void doInitBody() throws JspException
            { }

        @Override
        public int doAfterBody() throws JspException
            {
                return SKIP_BODY;
            }

        @Override
        public int doStartInputTag() throws JspException
            {
                return EVAL_BODY_BUFFERED;
            }

        @Override
        public int doEndInputTag() throws JspException
            {
                FormTag form = getParentFormTag();
                Class<? extends ActionBean> beanclass = form.getActionBeanClass();
                PageContext context = getPageContext();
                HttpSession session = context.getSession();

                // Get the store
                String name = Util.sha256(beanclass.getName());
                Object obj = session.getAttribute(name);
                if (obj != null && !(obj instanceof Map))
                    obj = null;

                if (obj == null)
                    session.setAttribute(name, obj = new ConcurrentHashMap<>());

                @SuppressWarnings("unchecked")
                Map<String, Long> map = (Map<String, Long>)obj;

                // Generate csrf
                long ts = System.currentTimeMillis();
                String slab1 = UUID.randomUUID().toString();
                String slab2 = Util.sha256("[" + ts + "][" + beanclass.getName() + "]");
                String slab3 = Util.sha1(slab1 + ":" + slab2 + ":" + ts);
                String csrf = slab1 + ":" + slab2 + ":" + slab3;

                // Save it
                map.put(csrf, ts);

                // Remove the oldest
                if (map.size() > 5)
                    {
                        List<Long> list = new ArrayList<>(map.values());
                        Collections.sort(list);

                        for (Map.Entry<String, Long> entry : map.entrySet())
                            {
                                if (!Objects.equals(entry.getValue(), list.get(0)))
                                    continue;

                                map.remove(entry.getKey());
                                break;
                            }
                    }

                // Write it
                getAttributes().put("value", csrf);
                getAttributes().put("name", "csrfToken");
                writeSingletonTag(context.getOut(), "input");

                // Clear out the value from the attributes
                getAttributes().remove("value");
                getAttributes().remove("name");

                return EVAL_PAGE;
            }
    }
