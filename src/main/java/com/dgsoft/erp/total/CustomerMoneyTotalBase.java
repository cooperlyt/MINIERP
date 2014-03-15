package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.SearchDateArea;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.Customer;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14/03/14
 * Time: 12:21
 */
public abstract class CustomerMoneyTotalBase extends ErpEntityQuery<AccountOper> {

    protected static final String EJBQL = "select accountOper from AccountOper accountOper left join fetch accountOper.customer customer ";

    public CustomerMoneyTotalBase() {
        setEjbql(EJBQL);
        setRestrictionLogicOperator("and");
        setOrderColumn("accountOper.operDate");
    }

    private SearchDateArea searchDateArea = new SearchDateArea(new Date(), new Date());

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }


    private List<TotalDataGroup<Customer, AccountOper>> customerGroupResultList = null;

    private void initCustomerGroupResultList(){
        if (customerGroupResultList == null){
            customerGroupResultList = TotalDataGroup.groupBy(getResultList(),new TotalGroupStrategy<Customer, AccountOper>() {
                @Override
                public Customer getKey(AccountOper accountOper) {
                    return accountOper.getCustomerOrder().getCustomer();
                }

                @Override
                public Object totalGroupData(Collection<AccountOper> datas){
                    return
                }
            });
        }
    }

    public List<TotalDataGroup<Customer, AccountOper>> getCustomerGroupResultList(){
        if (isAnyParameterDirty()) {
            refresh();
        }
        initCustomerGroupResultList();
        return customerGroupResultList;
    }

    private List<TotalDataGroup<Date, AccountOper>> dayGroupResultList = null;

    private void initDayGroupResultList() {
        if (dayGroupResultList == null) {
            dayGroupResultList = TotalDataGroup.groupBy(getResultList(), new TotalGroupStrategy<Date, AccountOper>() {
                @Override
                public Date getKey(AccountOper accountOper) {
                    return DataFormat.halfTime(accountOper.getOperDate());
                }

                @Override
                public Object totalGroupData(Collection<AccountOper> datas){
                    return
                }

            });
        }
    }

    private CustomerMoneyTotalData totalData(Collection<AccountOper> datas){
        CustomerMoneyTotalData result = new CustomerMoneyTotalData();

    }


    public List<TotalDataGroup<Date, AccountOper>> getDayGroupResultList() {

        if (isAnyParameterDirty()) {
            refresh();
        }
        initDayGroupResultList();
        return dayGroupResultList;
    }

    @Override
    public void refresh() {
        super.refresh();
        dayGroupResultList = null;
        customerGroupResultList = null;
    }


    public class CustomerMoneyTotalData {

        BigDecimal outMoney;

        BigDecimal inMoney;

        BigDecimal outRealMoney;

        BigDecimal inRealMoney;

        BigDecimal remitFee;

        public CustomerMoneyTotalData() {
            outMoney = BigDecimal.ZERO;
            inMoney = BigDecimal.ZERO;
            outRealMoney = BigDecimal.ZERO;
            inRealMoney = BigDecimal.ZERO;
            remitFee = BigDecimal.ZERO;
        }

        public BigDecimal getOutMoney() {
            return outMoney;
        }

        public BigDecimal getInMoney() {
            return inMoney;
        }

        public BigDecimal getOutRealMoney() {
            return outRealMoney;
        }

        public BigDecimal getInRealMoney() {
            return inRealMoney;
        }

        public BigDecimal getRemitFee() {
            return remitFee;
        }
    }
}
