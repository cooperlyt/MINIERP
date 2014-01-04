package com.dgsoft.erp.action.store;

import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.business.BusinessCreate;
import com.dgsoft.common.system.business.StartData;
import com.dgsoft.common.system.model.BusinessDefine;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.StockChangeHome;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.api.StockChangeModel;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import javax.persistence.EntityManager;
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

    private static final String BUSINESS_DEFINE_ID_PREFIX ="stockChange.";

    protected abstract String beginStoreChange();

    protected abstract String storeChange(boolean verify);



    protected abstract StockChange.StoreChangeType getStoreChangeType();

    @In
    protected EntityManager systemEntityManager;

    @In(create = true)
    protected StartData startData;

    @In(create = true)
    protected BusinessDefineHome businessDefineHome;

    @In(create = true)
    protected BusinessCreate businessCreate;

    @In
    protected FacesMessages facesMessages;

    @In(create = true)
    protected StockChangeHome stockChangeHome;

    @In
    private Credentials credentials;

    @In
    private Map<String, String> messages;

    @Override
    public String begin() {

        stockChangeHome.getInstance().setOperEmp(credentials.getUsername());
        stockChangeHome.getInstance().setOperType(getStoreChangeType());
        BusinessDefine bd = systemEntityManager.find(BusinessDefine.class,BUSINESS_DEFINE_ID_PREFIX + getStoreChangeType().name());
        if (bd != null){
            businessDefineHome.setId(bd.getId());
            startData.generateKey();
            startData.setDescription(messages.get(getStoreChangeType().name()));
            stockChangeHome.getInstance().setVerify(false);
        }else{
            stockChangeHome.getInstance().setVerify(true);
        }
        return beginStoreChange();
    }


    protected boolean isIdAvailable(String newId) {
        return getEntityManager().createQuery("select sc from StockChange sc where sc.id = ?1").setParameter(1, newId).getResultList().size() == 0;
    }


    @Override
    @Transactional
    public String saveStoreChange() {
        if (!isIdAvailable(stockChangeHome.getInstance().getId())) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeInOutIDIsExists", stockChangeHome.getInstance().getId());
            return null;
        }
        boolean verify = !businessDefineHome.isIdDefined();
        String result = storeChange(verify);
        if (!verify){
            return businessCreate.create();
        }else{
            return result;
        }

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
