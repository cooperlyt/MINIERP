package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.CustomerArea;
import com.dgsoft.erp.model.api.CustomerData;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.Logging;
import org.jboss.seam.persistence.QueryParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/20/13
 * Time: 12:50 PM
 */
@Name("customerList")
public class CustomerList extends ErpEntityQuery<CustomerData> {



    private static final String EJBQL = "select new com.dgsoft.erp.model.api.CustomerData(" +
            "customer.id,customer.name,customer.type,customer.customerArea.name,customer.customerLevel.name,customer.customerLevel.priority,customer.provinceCode," +
            "customer.createDate,customer.balance,customer.enable," +
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false) as orderCount," +
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and o.moneyComplete = true) as completeOrderCount," +
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and (o.resReceived = false or o.moneyComplete = false or o.allStoreOut = false)) as runningOrderCount," +
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.moneyComplete = false and (o.allStoreOut = true or o.payType = 'PAY_FIRST')) as waitPayOrderCount," +
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and o.resReceived = false) as waitReceiveOrderCount," +
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = false and (o.payType <> 'PAY_FIRST' or o.moneyComplete = true)) as waitShipOrderCount," +
            "(select count(o.id) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and o.moneyComplete = false) as arrearsOrderCount," +
            "COALESCE((select sum(o.money - o.receiveMoney) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and o.moneyComplete = false),0) as orderArrears," +
            "COALESCE((select sum(o.money) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false),0) as orderTotalMoney," +
            "COALESCE((select sum(o.money) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and o.moneyComplete = true),0) as completeOrderMoney," +
            "COALESCE((customer.balance - (select sum(o.money - o.receiveMoney) from CustomerOrder o where o.customer.id = customer.id and o.canceled = false and o.allStoreOut = true and o.moneyComplete = false)),0) as lastMoney ) " +
            "from Customer customer";


    private static final String[] RESTRICTIONS = {
            "customer.customerArea.id in (#{customerSearchCondition.resultAcceptAreaIds})",
            "customer.customerLevel.priority >= #{customerSearchCondition.levelFrom}",
            "customer.customerLevel.priority <= #{customerSearchCondition.levelTo}",
            "lower(customer.name) like lower(concat(#{customerSearchCondition.name},'%'))",
            "customer.type = #{customerSearchCondition.type}",
            "customer.provinceCode = #{customerSearchCondition.provinceCode}"};


    public CustomerList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
    }


    //seam entity Query bug  "select have where"  this is dirty
    @Override
    protected String getRenderedEjbql()
    {
        return super.getRenderedEjbql().replace("from Customer customer and", "from Customer customer where");
    }

    private Long resultCount;


    @Transactional
    @Override
    public Long getResultCount()
    {
        if (isAnyParameterDirty())
        {
            refresh();
        }
        initResultCount();
        return resultCount;
    }

    private void initResultCount()
    {
        if ( resultCount==null )
        {

            parseEjbql();

            evaluateAllParameters();

            joinTransaction();


            javax.persistence.Query query = getEntityManager().createQuery(getRenderedEjbql().replace(EJBQL,"select count(customer.id) from Customer customer").replaceAll("order by[\\s\\S]*",""));
            setParameters( query, getQueryParameterValues(), 0 );
            setParameters( query, getRestrictionParameterValues(), getQueryParameterValues().size() );
            resultCount = query==null ?
                    null : (Long) query.getSingleResult();
        }
    }


    private void setParameters(javax.persistence.Query query, List<Object> parameters, int start)
    {
        for (int i=0; i<parameters.size(); i++)
        {
            Object parameterValue = parameters.get(i);
            if ( isRestrictionParameterSet(parameterValue) )
            {
                query.setParameter( QueryParser.getParameterName(start + i), parameterValue );
            }
        }
    }




    @Override
    public void refresh()
    {
        super.refresh();
        resultCount = null;
    }


}
