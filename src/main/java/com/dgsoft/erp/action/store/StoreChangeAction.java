package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.Store;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/18/13
 * Time: 11:13 AM
 */
public interface StoreChangeAction {

    public abstract String begin();

    public abstract void addItem();

    public abstract void removeItem();

    public abstract String cancel();

    public abstract boolean isIdAvailable(String newId);

    public abstract String storeChange();


}
