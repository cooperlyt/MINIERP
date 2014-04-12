package com.dgsoft.erp;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.model.UnitGroup;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResCountGroup;
import org.jboss.seam.annotations.In;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.persistence.EntityManager;
import java.util.Collection;

/**
 * Created by cooper on 4/12/14.
 */
public abstract class ResCountEntityItemCreate<E extends StoreResCountEntity> extends ResEntityItemCreate<E> {


    @In
    protected FacesMessages facesMessages;

    @In(create = true)
    protected ResHelper resHelper;

    @In
    private RunParam runParam;

    @In
    private EntityManager erpEntityManager;

    public String addToGroup(StoreResCountGroup<E> group){
        String result = prepareAdd(group.values());
        if ("added".equals(result)){
            if (group.put(getEditingItem()) != null){
                facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "StoreChangeItemIsExists", getEditingItem().getStoreRes().getCode());
            }
        }
        return result;
    }

    private String prepareAdd(Collection<E> collection){
        if (getEditingItem() == null) {
            throw new IllegalArgumentException("editingItem state error");
        }


        storeResHome.setRes(getEditingItem().getRes(), getEditingItem().getFormats(), getEditingItem().getFloatConvertRate());

        boolean newRes = !storeResHome.isIdDefined();

        if (!storeResHome.isIdDefined()) {
            for (E item : collection) {
                if (storeResHome.getInstance().equals(item.getStoreRes())) {
                    newRes = false;
                    //getEditingItem().setStoreRes(item.getStoreRes());
                    storeResHome.clearInstance();
                    storeResHome.setInstance(item.getStoreRes());
                    break;
                }
            }
        }

        if (newRes && DataFormat.isEmpty(getEditingItem().getCode())) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO,
                    "newSotreResTypedCodePlase");
            getEditingItem().setCode(resHelper.genStoreResCode(storeResHome.getInstance()));
            return "code_not_set";
        } else {
            if (newRes) {

                if (!getEditingItem().getCode().matches(runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME))) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeNotRule", getEditingItem().getCode(),
                            runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME));
                    return "code_not_rule";
                }

                for (E item : collection) {
                    if (getEditingItem().getCode().equals(item.getStoreRes().getCode())) {
                        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                                "storeResCodeExists", getEditingItem().getCode());
                        return "code_exists";
                    }
                }
                if (!erpEntityManager.createQuery("select storeRes from StoreRes storeRes where code = :code")
                        .setParameter("code", getEditingItem().getCode()).getResultList().isEmpty()) {

                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeExists", getEditingItem().getCode());
                    return "code_exists";
                }

                storeResHome.getInstance().setCode(getEditingItem().getCode());

            }
            getEditingItem().setStoreRes(storeResHome.getInstance());



            return "added";

        }
    }

    public String addToCollection(Collection<E> collection) {
        String result = prepareAdd(collection);
        if ("added".equals(result)) {
            collection.add(getEditingItem());
        }
        return result;
    }

}
