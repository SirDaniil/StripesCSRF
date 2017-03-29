package com.github.sirdaniil.stripes.interceptors;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.servlet.http.*;
import com.github.sirdaniil.stripes.*;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.*;

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

                if (method.isAnnotationPresent(CsrfSecure.class))
                    {
                        if (!"POST".equals(context.getActionBeanContext().getRequest().getMethod()))
                            throw new IOException("Only POST are supported!");

                        if (!(actionBean instanceof CsrfProtected))
                            throw new IOException("Action bean [" + actionBean.getClass() + "] is not protected.");

                        CsrfProtected csrf = (CsrfProtected) actionBean;
                        HttpSession session = context.getActionBean().getContext().getRequest().getSession();
                        String name = Util.sha256(actionBean.getClass().getName());
                        Object obj = session.getAttribute(name);

                        if (obj == null || !(obj instanceof Map))
                            throw new IOException("Invalid request");

                        @SuppressWarnings("unchecked")
                        Map<String, Long> map = (Map<String, Long>) obj;
                        Long ts = map.remove(csrf.getCsrfToken());

                        if (ts == null)
                            throw new IOException("Invalid CSRF.");

                        if ((System.currentTimeMillis() - ts) > 1000 * 60 * 20)
                            throw new IOException("CSRF has expired.");
                    }

                return context.proceed();
            }
    }
