package com.github.sirdaniil.stripes;

/**
 * Created by Daniil Sosonkin
 * 3/19/2017 11:24 PM
 */
public interface CsrfProtected
    {
        String getCsrfToken();
        void setCsrfToken(String token);
    }
