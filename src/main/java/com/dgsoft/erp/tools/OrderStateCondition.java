package com.dgsoft.erp.tools;

import javax.persistence.Query;

/**
 * Created by cooper on 3/31/14.
 */
public class OrderStateCondition {

    private Boolean canceled = false;

    private Boolean allStoreOut;

    private Boolean moneyComplete = true;

    private Boolean customerConfirm;

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public Boolean getAllStoreOut() {
        return allStoreOut;
    }

    public void setAllStoreOut(Boolean allStoreOut) {
        this.allStoreOut = allStoreOut;
    }

    public Boolean getMoneyComplete() {
        return moneyComplete;
    }

    public void setMoneyComplete(Boolean moneyComplete) {
        this.moneyComplete = moneyComplete;
    }

    public Boolean getCustomerConfirm() {
        return customerConfirm;
    }

    public void setCustomerConfirm(Boolean customerConfirm) {
        this.customerConfirm = customerConfirm;
    }

    public String genConditionSQL(String orderPatch, boolean addAnd) {
        String result = "";
        if (canceled != null){
            result = orderPatch + ".canceled = :canceled";
        }

        if (allStoreOut != null){
            if (!result.trim().equals("")){
                result += " AND ";
            }
            result +=  orderPatch + ".allStoreOut = :allStoreOut";
        }

        if (moneyComplete != null){
            if (!result.trim().equals("")){
                result += " AND ";
            }
            result +=  orderPatch + ".moneyComplete = :moneyComplete";
        }

        if (customerConfirm != null){
            if (!result.trim().equals("")){
                result += " AND ";
            }
            result +=  orderPatch + ".resReceived = :customerConfirm";
        }

        if (addAnd && !result.trim().equals("")){
            result = " AND " + result;
        }
        return result;
    }

    public Query setQueryParam(Query query){
        if (canceled != null){
            query.setParameter("canceled",getCanceled());
        }
        if (allStoreOut != null){
            query.setParameter("allStoreOut",getAllStoreOut());
        }
        if (moneyComplete != null){
            query.setParameter("moneyComplete",getMoneyComplete());
        }
        if (customerConfirm != null){
            query.setParameter("customerConfirm",getCustomerConfirm());
        }
        return query;
    }

}
