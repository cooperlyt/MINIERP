package com.dgsoft.erp.action.store;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/18/13
 * Time: 11:13 AM
 */
public interface StoreChangeAction {

    public abstract String begin();

    public abstract String addItem();

    public abstract void removeItem();

    public abstract String cancel();

    public abstract String saveStoreChange();


}
