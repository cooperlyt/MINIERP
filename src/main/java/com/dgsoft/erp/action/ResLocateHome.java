package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.FormatDefine;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResCategory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/3/13
 * Time: 9:07 PM
 */
@Name("resLocateHome")
public class ResLocateHome extends ErpEntityHome<Res> {

    @In
    private FacesMessages facesMessages;

    @In(create = true)
    private StoreResFormatFilter storeResFormatFilter;

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void locateByCode() {
        List<Res> resList = getEntityManager().createQuery("select res from Res res where res.enable = true and res.code=:code").setParameter("code", code).getResultList();
        if (resList.isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "resCodeIllegal", code);
            return;
        }
        if (resList.size() > 1) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.WARN, "resCodeMulitResult", code);
        }
        setId(resList.get(0).getId());
        getInstance();
        code = null;
    }

    public String getResTitle() {
        if (isIdDefined()) {

            ResCategory category = getInstance().getResCategory();
            String result = "";
            if (category != null) {
                String categoryTitle = category.getName();

                while (category.getResCategory() != null) {
                    category = category.getResCategory();

                    categoryTitle = category.getName() + " > " + categoryTitle;
                }
                result = categoryTitle;
            }
            result = result + " : " + getInstance().getName() + "(" + getInstance().getCode() + ")";
            return result;

        } else
            return "";
    }


    @Override
    protected void initInstance(){
        super.initInstance();
        storeResFormatFilter.selectRes(getInstance());
    }



}
