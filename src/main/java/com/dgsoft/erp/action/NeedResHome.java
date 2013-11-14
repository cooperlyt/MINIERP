package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Dispatch;
import com.dgsoft.erp.model.NeedRes;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/10/13
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("needResHome")
public class NeedResHome extends ErpEntityHome<NeedRes>{


    public synchronized boolean needStoreOut(String storeId) {


        for (Dispatch dispatch: getInstance().getDispatches()){
            if (dispatch.getStore().getId().equals(storeId)){
                log.debug("call need store out return true:" + storeId);
                return true;
            }
        }
        log.debug("call need store out return false:" + storeId);
        return false;
    }
}
