package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.MiddleMan;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created by cooper on 1/12/14.
 */
@Name("middleManList")
public class MiddleManList extends ErpEntityQuery<MiddleMan>{

    private static final String EJBQL = "select middleMan from MiddleMan middleMan";

    private static final String[] RESTRICTIONS = {
            "middleMan.enable = not #{middleManList.containDisable}",
            "middleMan.type = #{middleManList.type}",
            "lower(middleMan.name) like lower(concat(#{middleManList.middleMan.name},'%'))",
            "lower(middleMan.contact) like lower(concat(#{middleManList.middleMan.contact},'%'))"};


    public MiddleManList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }

    private MiddleMan middleMan = new MiddleMan();

    private String type;

    private Boolean containDisable;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MiddleMan getMiddleMan() {
        return middleMan;
    }

    public void setMiddleMan(MiddleMan middleMan) {
        this.middleMan = middleMan;
    }

    public Boolean getContainDisable() {
        return containDisable;
    }

    public void setContainDisable(Boolean containDisable) {
        this.containDisable = containDisable;
    }
}
