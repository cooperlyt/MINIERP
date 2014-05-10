package com.dgsoft.erp.tools;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

/**
 * Created by cooper on 5/5/14.
 */
@Name("customerAdvance")
@Scope(ScopeType.STATELESS)
public class CustomerMoneyTool {

    @In
    private EntityManager erpEntityManager;

    public BigDecimal getOrderAdvance(String customerId){
        return erpEntityManager.createQuery("select COALESCE(sum(customerOrder.advanceMoney),0) from CustomerOrder customerOrder " +
                "where customerOrder.canceled = false and customerOrder.customer.id = :customerId and  customerOrder.payType = 'PAY_FIRST' " +
                "and customerOrder.allStoreOut = false",BigDecimal.class).setParameter("customerId", customerId).getSingleResult();

    }


    public static CustomerMoneyTool instance() {
        if (!Contexts.isEventContextActive()) {
            throw new IllegalStateException("no active event context");
        }
        return (CustomerMoneyTool) Component.getInstance(CustomerMoneyTool.class, ScopeType.STATELESS, true);
    }

}
