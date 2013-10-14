package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import org.jboss.seam.annotations.Name;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/7/13
 * Time: 5:43 PM
 */

@Name("storeResHome")
public class StoreResHome extends ErpEntityHome<StoreRes> {



    public void setRes(Res res, Collection<Format> formatList) {

        List<StoreRes> storeResList = getEntityManager().createQuery("select storeRes from StoreRes storeRes where storeRes.res.id = :resId").setParameter("resId", res.getId()).getResultList();
        for (StoreRes storeRes : storeResList) {
            if (ResHelper.sameFormat(storeRes.getFormats(), formatList)) {
                this.setId(storeRes.getId());
                getInstance();
                return;
            }
        }
        clearInstance();
        getInstance().setRes(res);
        for (Format format: formatList){
            format.setStoreRes(getInstance());
            getInstance().getFormats().add(format);
        }
    }

}
