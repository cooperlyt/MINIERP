package com.dgsoft.erp.action;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.OrderBack;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by cooper on 4/27/14.
 */
@Name("resBackList")
public class ResBackList extends ErpEntityQuery<OrderBack>{

    private static final String EJBQL = "select orderBack from OrderBack orderBack " +
            "left join fetch orderBack.customer customer left join fetch customer.customerArea customerArea";

    private static final String[] RESTRICTIONS = {
            "orderBack.applyEmp = #{resBackList.applyEmp}",
            "orderBack.customer.customerArea.id in (#{customerSearchCondition.customerAreaId})",
            "orderBack.customer.customerLevel >= #{customerSearchCondition.levelFrom}",
            "orderBack.customer.customerLevel <=#{customerSearchCondition.levelTo}",
            "lower(orderBack.customer.name) like lower(concat(#{customerSearchCondition.name},'%'))",
            "orderBack.customer.type = #{customerSearchCondition.type}",
            "orderBack.customer.provinceCode = #{customerSearchCondition.provinceCode}",
            "orderBack.completeDate >= #{resBackList.searchDateArea.dateFrom}",
            "orderBack.completeDate <= #{resBackList.searchDateArea.searchDateTo}",
            "lower(orderBack.id)  like lower(concat('%',#{resBackList.id}))",
            "orderBack.reason = #{resBackList.reason}",
            "orderBack.resComplete = #{resBackList.resComplete}",
            "orderBack.moneyComplete = #{resBackList.moneyComplete}"};


    public ResBackList(){
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
        setOrderColumn("orderBack.createDate");
        setOrderDirection("desc");
        searchDateArea = new SearchDateArea(null,null);
    }

    private SearchDateArea searchDateArea;

    private boolean myBusiness = true;

    private Boolean resComplete;

    private Boolean moneyComplete;

    private String id;

    private String reason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isMyBusiness() {
        return myBusiness;
    }

    public void setMyBusiness(boolean myBusiness) {
        this.myBusiness = myBusiness;
    }

    public Boolean getResComplete() {
        return resComplete;
    }

    public void setResComplete(Boolean resComplete) {
        this.resComplete = resComplete;
    }

    public Boolean getMoneyComplete() {
        return moneyComplete;
    }

    public void setMoneyComplete(Boolean moneyComplete) {
        this.moneyComplete = moneyComplete;
    }

    public String getApplyEmp(){
        if (myBusiness){
            return ((Credentials) Component.getInstance("org.jboss.seam.security.credentials")).getUsername();
        }else{
            return null;
        }
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public void setSearchDateArea(SearchDateArea searchDateArea) {
        this.searchDateArea = searchDateArea;
    }

    public Number getTotalMoney(){
        return super.getResultTotalSum("orderBack.money");
    }

}
