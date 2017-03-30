package com.github.sirdaniil.stripes.interceptors;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.servlet.http.*;
import com.github.sirdaniil.stripes.*;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.*;
import net.sourceforge.stripes.tag.*;

/**
 * Created by Daniil Sosonkin
 * 3/19/2017 7:54 PM
 */
@Intercepts(LifecycleStage.EventHandling)
public class CsrfInterceptor implements Interceptor
    {
        @Override
        public Resolution intercept(ExecutionContext context) throws Exception
            {
                ActionBean actionBean = context.getActionBean();
                Method method = context.getHandler();
                HttpServletRequest request = actionBean.getContext().getRequest();
                HttpSession session = request.getSession(false);
                Object csrf = request.getAttribute(CsrfTag.FIELD_NAME);

                if (method.isAnnotationPresent(CsrfSecure.class))
                    {
                        if (!"POST".equals(request.getMethod()))
                            throw new IOException("Only POST are supported!");

                        if (session == null || csrf == null)
                            throw new IOException("Invalid CSRF.");

                        String csrfToken = csrf.toString();
                        String name = Util.sha256(actionBean.getClass().getName());
                        Object obj = session.getAttribute(name);

                        if (obj == null || !(obj instanceof Map))
                            throw new IOException("Invalid request");

                        @SuppressWarnings("unchecked")
                        Map<String, Long> map = (Map<String, Long>) obj;
                        Long ts = map.remove(csrfToken);

                        if (ts == null)
                            throw new IOException("Invalid CSRF.");

                        if ((System.currentTimeMillis() - ts) > 1000 * 60 * 20)
                            throw new IOException("CSRF has expired.");
                    }

                return context.proceed();
            }
    }
