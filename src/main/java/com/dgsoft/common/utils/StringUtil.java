package com.dgsoft.common.utils;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 1/1/14
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {

    public static boolean isEmpty(String value){
        return (value == null) || (value.trim().equals(""));
    }
}
