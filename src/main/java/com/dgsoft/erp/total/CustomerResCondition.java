package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 15/04/14
 * Time: 09:08
 */
@Name("customerResCondition")
@BypassInterceptors
public class CustomerResCondition {


    //  money type


    // res type
    private boolean containStoreOut = true;

    private boolean containResBack = true;

    private boolean containFreeRes = true;

    @BypassInterceptors
    public boolean isContainStoreOut() {
        return containStoreOut;
    }

    public void setContainStoreOut(boolean containStoreOut) {
        this.containStoreOut = containStoreOut;
    }

    @BypassInterceptors
    public boolean isContainResBack() {
        return containResBack;
    }

    public void setContainResBack(boolean containResBack) {
        this.containResBack = containResBack;
    }


    public boolean isContainFreeRes() {
        return containFreeRes;
    }

    public void setContainFreeRes(boolean containFreeRes) {
        this.containFreeRes = containFreeRes;
    }

    public Boolean getFreeCondition(){
        if (containFreeRes){
            return null;
        }else {
            return false;
        }
    }


}
