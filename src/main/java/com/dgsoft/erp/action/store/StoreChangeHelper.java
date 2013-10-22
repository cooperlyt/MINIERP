package com.dgsoft.erp.action.store;

import com.dgsoft.common.system.AuthenticationInfo;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.StockChangeHome;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.api.StockChangeModel;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/20/13
 * Time: 7:32 PM
 */
public abstract class StoreChangeHelper<E extends StockChangeModel> extends ErpEntityHome<E> implements StoreChangeAction {

    public abstract String beginStoreChange();

    public abstract String storeChange();

    @In(create = true)
    protected StockChangeHome stockChangeHome;

    @In
    private AuthenticationInfo authInfo;

    @Override
    public String begin() {
        stockChangeHome.getInstance().setOperEmp(authInfo.getLoginEmployee().getId());
        return beginStoreChange();
    }

    @Override
    @End
    public String saveStoreChange(){
        if (!isIdAvailable()){

            return null;
        }
        return storeChange();
    }

    public static boolean sameFormat(Collection<Format> formatList1, Collection<Format> formatList2) {
        if (formatList1.size() != formatList2.size()) {
            return false;
        }

        Map<String, String> format1Values = new HashMap<String, String>();
        for (Format format : formatList1) {
            format1Values.put(format.getFormatDefine().getId(), format.getFormatValue());
        }
        for (Format format : formatList2) {
            if (!format1Values.get(format.getFormatDefine().getId()).equals(format.getFormatValue())) {
                return false;
            }
        }
        return true;
    }

}
