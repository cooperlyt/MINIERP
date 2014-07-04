package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Dispatch;
import com.dgsoft.erp.model.ResCategory;
import org.jboss.seam.annotations.Name;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by cooper on 7/4/14.
 */
@Name("deliveryFeeSearch")
public class DeliveryFareSearch extends ErpEntityQuery<Dispatch>{

    private static final String EJBQL = "select dispatch from Dispatch dispatch where dispatch.delivered=true and dispatch.fare > 0";

    private static final String[] RESTRICTIONS = {
            "dispatch.deliveryType in (#{deliveryFeeSearch.allowDeliveryTypes})",
            "dispatch.sendTime >= #{searchDateArea.dateFrom}",
            "dispatch.sendTime <= #{searchDateArea.searchDateTo}",
            "lower(dispatch.needRes.address) like lower(concat('%',#{deliveryFeeSearch.deliveryAddress}),'%')",
            "lower(dispatch.needRes.customerOrder.customer.name) like lower(concat(#{deliveryFeeSearch.customerName},'%'))",
            "dispatch.needRes.customerOrder.customer.customerArea.id = #{deliveryFeeSearch.customerAreaId}",
    };

    private String customerName;

    private String customerAreaId;

    private String deliveryAddress;

    public DeliveryFareSearch(){
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }

    public List<Dispatch.DeliveryType> getAllowDeliveryTypes(){
        return new ArrayList<Dispatch.DeliveryType>(EnumSet.of(Dispatch.DeliveryType.FULL_CAR_SEND,Dispatch.DeliveryType.EXPRESS_SEND));
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

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Number getTotalFare(){
        return getResultTotalSum("dispatch.fare");
    }
}
