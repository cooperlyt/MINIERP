package com.dgsoft.erp.action;

import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResCategory;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StoreRes;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.EnumSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/3/13
 * Time: 9:07 PM
 */
@Name("resLocate")
@Scope(ScopeType.CONVERSATION)
public class ResLocate {

    public enum LocateResult{
        NOT_FOUND,FOUND_STORERES,FOUND_RES;
    }

    @In
    private FacesMessages facesMessages;

    @In(create = true)
    private EntityManager erpEntityManager;

    @In(create = true)
    private ResHome resHome;

    @In(create = true)
    private StoreResHome storeResHome;

    @In
    private RunParam runParam;


    private String code;

    private StockChange.StoreChangeType storeChangeType = null;

    private LocateResult result = LocateResult.NOT_FOUND;

    private Res resultRes;

    private StoreRes resultStoreRes;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public void setChangeType(String changeType) {
        storeChangeType = StockChange.StoreChangeType.valueOf(changeType);
    }

    public String getChangeType(){
        return  storeChangeType == null ? null : storeChangeType.name();
    }

    public Res getResultRes() {
        return resultRes;
    }

    public void setResultRes(Res resultRes) {
        this.resultRes = resultRes;
    }

    public StoreRes getResultStoreRes() {
        return resultStoreRes;
    }

    public void setResultStoreRes(StoreRes resultStoreRes) {
        this.resultStoreRes = resultStoreRes;
    }

    public LocateResult getResult() {
        return result;
    }

    public void setResult(LocateResult result) {
        this.result = result;
    }


    public LocateResult locateByCode(StockChange.StoreChangeType changeType){
        storeChangeType = changeType;
        locateByCode();
        return result;
    }

    public void locateByCode(String changeType){
        setChangeType(changeType);
        locateByCode();
    }

    public void locateByCode() {

        result = LocateResult.NOT_FOUND;
        resHome.clearInstance();
        storeResHome.clearInstance();
        if (code == null || code.trim().equals("")){
            return;
        }

        if (code.trim().matches(runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME))){
            locateStoreRes();
        }else if (code.trim().matches(runParam.getStringParamValue(ResHome.RES_CODE_RULE_PARAM_NAME))){
             locateRes();
        }else {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "resCodeIllegal", code);
        }

        code = null;

    }

    private void locateRes(){

        Query query = erpEntityManager.createQuery("select res from Res res where res.enable = true and res.code=:code and res.resCategory.type in (:resType)").setParameter("code", code);

        if (storeChangeType != null) {
            query = query.setParameter("resType", storeChangeType.getResTypes());
        } else {
            query = query.setParameter("resType", EnumSet.allOf(ResCategory.ResType.class));
        }


        List<Res> resList = query.getResultList();
        if (resList.isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "resNotFound", code);
            return;
        }
        if (resList.size() > 1) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN, "resCodeMulitResult", code);
        }
        result = LocateResult.FOUND_RES;
        resultRes = resList.get(0);
        resHome.setId(resultRes.getId());

        Events.instance().raiseEvent("erp.resLocateSelected",resultRes);
    }

    private void locateStoreRes(){
        Query query = erpEntityManager.createQuery("select storeRes from StoreRes storeRes where storeRes.res.enable = true and storeRes.code=:code and storeRes.res.resCategory.type in (:resType)").setParameter("code", code);

        if (storeChangeType != null) {
            query = query.setParameter("resType", storeChangeType.getResTypes());
        } else {
            query = query.setParameter("resType", EnumSet.allOf(ResCategory.ResType.class));
        }
        List<StoreRes> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeResNotFound", code);
            return;
        }
        if (resultList.size() > 1) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN, "storeResCodeMulitResult", code);
        }
        result = LocateResult.FOUND_STORERES;
        resultStoreRes = resultList.get(0);
        storeResHome.setId(resultStoreRes.getId());
        resHome.setId(resultStoreRes.getRes().getId());
        Events.instance().raiseEvent("erp.storeResLocateSelected",resultStoreRes);

    }

    private String localedId;

    public String getLocaledId() {
        return localedId;
    }

    public void setLocaledId(String localedId) {
        this.localedId = localedId;
    }

    public void resLocaledById(){
        resHome.setId(localedId);
        resultRes = resHome.getInstance();
        result = LocateResult.FOUND_RES;
        Events.instance().raiseEvent("erp.resLocateSelected",resultRes);
    }

    public void storeResLocaledById(){
        result = LocateResult.FOUND_STORERES;
        storeResHome.setId(localedId);
        resultStoreRes = storeResHome.getInstance();
        resHome.setId(resultStoreRes.getRes().getId());
        Events.instance().raiseEvent("erp.storeResLocateSelected",resultStoreRes);

    }
}
