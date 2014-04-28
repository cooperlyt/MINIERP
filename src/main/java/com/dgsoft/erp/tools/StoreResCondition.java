package com.dgsoft.erp.tools;

import com.dgsoft.erp.action.ResCategoryHome;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.action.ResHome;
import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.ResCategory;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResEntity;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Logging;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-4-28
 * Time: 下午3:06
 */
@Name("storeResCondition")
public class StoreResCondition extends StoreResEntity {

    @In
    private ResHome resHome;

    @In
    private ResHelper resHelper;

    public void resSelected(){
        Logging.getLog(StoreResCondition.class).debug("resSelect id is:" + resHome.getId());
        initByRes(resHome.getInstance());
    }

    public boolean isResSearch() {
        if (getRes() == null){
            return false;
        }
        for (Format format : getFormats()) {
            if (format.getFormatValue() != null) {
                return false;
            }
        }
        return true;
    }

    public List<String> getMatchStoreResIds() {
        List<String> result = new ArrayList<String>();
        if (!isResSearch() && (getRes() != null)) {

            for(StoreRes storeRes: resHome.getInstance().getStoreReses()){
                if (resHelper.matchFormat(getFormats(),storeRes) &&
                        ((getFloatConvertRate() == null) || (getFloatConvertRate().compareTo(storeRes.getFloatConversionRate()) == 0) )){
                    result.add(storeRes.getId());
                }
            }
            if (result.isEmpty()){
                result.add("-1");
            }
        }
        return result;
    }


}
