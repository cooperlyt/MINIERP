package com.dgsoft.erp.model.api;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityLoader;
import com.dgsoft.erp.action.ResHome;
import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import javax.persistence.NoResultException;

/**
 * Created by cooper on 11/16/14.
 */
@Name("resCode")
@Scope(ScopeType.CONVERSATION)
public class ResCode {

    public enum CodeStatus {
        EMPTY, RES_DEFINED, STORERES_DEFINED, FAILD_CODE, RES_NOT_DEFINE, STORERES_NOT_DEFINE;
    }

    private String code;

    @In(create = true)
    private ErpEntityLoader erpEntityLoader;

    private CodeStatus codeStatus = CodeStatus.EMPTY;

    private StoreRes storeRes;

    private Res res;

    public CodeStatus getCodeStatus() {
        return codeStatus;
    }

    public StoreRes getStoreRes() {
        return storeRes;
    }

    public Res getRes() {
        return res;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isEmpty() {
        return DataFormat.isEmpty(code);
    }

    public boolean isResCode() {
        if (isEmpty()) {
            return false;
        }
        return getCode().matches(RunParam.instance().getStringParamValue(ResHome.RES_CODE_RULE_PARAM_NAME));
    }

    public boolean isStoreResCode() {
        if (isEmpty()) {
            return false;
        }
        return getCode().matches(RunParam.instance().getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME));
    }

    public CodeStatus find() {
        storeRes = null;

        if (isEmpty()) {
            codeStatus = CodeStatus.EMPTY;
        } else if (isStoreResCode()) {
            res = null;
            try {
                storeRes = erpEntityLoader.getEntityManager().createQuery("select storeRes from StoreRes storeRes where storeRes.code = :code", StoreRes.class).
                        setParameter("code", getCode()).getSingleResult();
                codeStatus = CodeStatus.STORERES_DEFINED;
            } catch (NoResultException e) {
                codeStatus = CodeStatus.STORERES_NOT_DEFINE;
                storeRes = null;
            }
        } else if (isResCode()) {
            storeRes = null;
            try {
                res = erpEntityLoader.getEntityManager().createQuery("select res from Res res where res.code = :code",Res.class).
                        setParameter("code",getCode()).getSingleResult();
                codeStatus = CodeStatus.RES_DEFINED;
            }catch(NoResultException e){
                codeStatus = CodeStatus.RES_NOT_DEFINE;
                res = null;
            }

        } else {
            codeStatus = CodeStatus.FAILD_CODE;

        }
        return codeStatus;
    }
}
