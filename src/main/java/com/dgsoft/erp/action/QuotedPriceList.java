package com.dgsoft.erp.action;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.QuotedPrice;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by cooper on 3/30/14.
 */
@Name("quotedPriceList")
public class QuotedPriceList extends ErpEntityQuery<QuotedPrice>{

    private static final String EJBQL = "select quotedPrice from QuotedPrice quotedPrice left join fetch quotedPrice.customer customer";

    private static final String[] RESTRICTIONS = {
            "quotedPrice.createEmp = #{quotedPriceList.empId}",
            "quotedPrice.customer.customerArea.id = #{quotedPriceList.customerAreaId}",
            "lower(quotedPrice.customer) like lower(concat('%','#{quotedPriceList.customerName},'%'))",
            "quotedPrice.createDate >= #{quotedPriceList.searchDateArea.dateFrom}",
            "quotedPrice.createDate <= #{quotedPriceList.searchDateArea.searchDateTo}",
            "quotedPrice.type = #{quotedPriceList.type}",
            "quotedPrice.customer.customerLevel.priority >= #{quotedPriceList.levelFrom}",
            "quotedPrice.customer.customerLevel.priority <= #{quotedPriceList.levelTo}"};

    public QuotedPriceList(){
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }

    private SearchDateArea searchDateArea = new SearchDateArea(new Date(),new Date());

    private String customerName;

    private String customerAreaId;

    private String levelFrom;

    private String levelTo;

    private String empId;

    private String type;

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAreaId() {
        return customerAreaId;
    }

    public void setCustomerAreaId(String customerAreaId) {
        this.customerAreaId = customerAreaId;
    }

    public String getLevelFrom() {
        return levelFrom;
    }

    public void setLevelFrom(String levelFrom) {
        this.levelFrom = levelFrom;
    }

    public String getLevelTo() {
        return levelTo;
    }

    public void setLevelTo(String levelTo) {
        this.levelTo = levelTo;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
