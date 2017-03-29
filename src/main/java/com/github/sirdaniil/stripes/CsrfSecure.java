package com.github.sirdaniil.stripes;

import java.lang.annotation.*;

/**
 * Created by Daniil Sosonkin
 * 3/19/2017 11:29 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CsrfSecure
    { }
