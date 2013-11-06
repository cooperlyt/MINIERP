package com.dgsoft.common.utils.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/6/13
 * Time: 10:33 PM
 * To change this template use File | Settings | File Templates.
 */

@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface UniqueVerifys {

    UniqueVerify [] value ();
}
