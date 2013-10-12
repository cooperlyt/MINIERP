package com.dgsoft.common.system.business;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/28/13
 * Time: 12:15 PM
 */
public interface SubscribeVar<T extends SubscribeVar> extends Comparable<T>{

    public abstract String getView();
}
