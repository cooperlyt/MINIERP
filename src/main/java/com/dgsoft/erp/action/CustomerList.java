package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Customer;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/20/13
 * Time: 12:50 PM
 */
@Name("customerList")
public class CustomerList extends ErpEntityQuery<Customer> {

    private static final String EJBQL = "select customer from Customer customer";


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

    public BigDecimal getTotalAdMoney(){

        return new BigDecimal(getResultTotalSum("customer.advanceMoney").doubleValue());
    }

    public BigDecimal getTotalAcMoney(){
        return new BigDecimal(getResultTotalSum("customer.accountMoney").doubleValue());
    }

    public BigDecimal getTotalPacMoney(){
        return new BigDecimal(getResultTotalSum("customer.proxyAccountMoney").doubleValue());
    }

    public BigDecimal getTotalNoProxyBalance(){
        return  getTotalAdMoney().subtract(getTotalAcMoney());
    }

    public BigDecimal getTotalBalance(){
        return getTotalAdMoney().subtract(getTotalAcMoney()).subtract(getTotalPacMoney());
    }


    //seam entity Query bug  "select have where"  this is dirty
//    @Override
//    protected String getRenderedEjbql()
//    {
//        return super.getRenderedEjbql().replace("from Customer customer and", "from Customer customer where");
//    }
//
//    private Long resultCount;
//
//    @Transactional
//    @Override
//    public Long getResultCount()
//    {
//        if (isAnyParameterDirty())
//        {
//            refresh();
//        }
//        initResultCount();
//        return resultCount;
//    }
//
//    private void initResultCount()
//    {
//        if ( resultCount==null )
//        {
//
//            parseEjbql();
//
//            evaluateAllParameters();
//
//            joinTransaction();
//
//
//            javax.persistence.Query query = getEntityManager().createQuery(getRenderedEjbql().replace(EJBQL,"select count(customer.id) from Customer customer").replaceAll("order by[\\s\\S]*",""));
//            setParameters( query, getQueryParameterValues(), 0 );
//            setParameters( query, getRestrictionParameterValues(), getQueryParameterValues().size() );
//            resultCount = query==null ?
//                    null : (Long) query.getSingleResult();
//        }
//    }
//
//
//    private void setParameters(javax.persistence.Query query, List<Object> parameters, int start)
//    {
//        for (int i=0; i<parameters.size(); i++)
//        {
//            Object parameterValue = parameters.get(i);
//            if ( isRestrictionParameterSet(parameterValue) )
//            {
//                query.setParameter( QueryParser.getParameterName(start + i), parameterValue );
//            }
//        }
//    }
//
//
//    public String customerTopReport(){
//        return "/report/customerTop.xhtml";
//    }
//
//
//
//    @Override
//    public void refresh()
//    {
//        super.refresh();
//        resultCount = null;
//    }


}
