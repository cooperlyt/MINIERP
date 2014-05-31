package com.dgsoft.erp.business.order;


import com.dgsoft.erp.action.MoneySaveHome;
import com.dgsoft.erp.action.NeedResHome;
import com.dgsoft.erp.business.CustomerAccountOper;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.In;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 9:55 AM
 */

public abstract class FinanceReceivables extends OrderTaskHandle {

    @In(create = true)
    private CustomerAccountOper customerAccountOper;

    @In(create = true)
    private MoneySaveHome moneySaveHome;

    @In(create = true)
    private NeedResHome needResHome;

    private boolean completeInOutSide = false;

    public abstract BigDecimal getNeedMoney();

    public BigDecimal getShortageMoney() {
        BigDecimal result = getNeedMoney().subtract(customerHome.getCanUseAdvanceMoney());
        if (result.compareTo(BigDecimal.ZERO) <= 0){
            return BigDecimal.ZERO;
        }else{
            return result;
        }
    }


    public boolean isMoneyComplete() {
        return getShortageMoney().compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isCompleteInOutSide() {
        return completeInOutSide;
    }

    public void setCompleteInOutSide(boolean completeInOutSide) {
        this.completeInOutSide = completeInOutSide;
    }

    @Override
    protected String completeOrderTask() {
        if (isMoneyComplete() || isCompleteInOutSide()) {
            orderHome.getInstance().setAdvanceMoney(getNeedMoney());
            if ("updated".equals(orderHome.update())) {
                needResHome.setId(orderHome.getMasterNeedRes().getId());
                return "taskComplete";
            } else {
                return null;
            }
        }else{
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                    "order_earnest_not_enough",
                    DecimalFormat.getCurrencyInstance(Locale.CHINA).format(getShortageMoney()));

            return null;
        }


    }

    public void receiveMoney(){
        if("persisted".equals(moneySaveHome.persist())){
            reset();
        }
    }


    public void reset() {
        customerAccountOper.setType(AccountOper.AccountOperType.CUSTOMER_SAVINGS);
        customerAccountOper.setOperMoney(BigDecimal.ZERO);
        moneySaveHome.clearInstance();
        moneySaveHome.initAccountOper();
    }

    @Override
    protected void initOrderTask() {
        reset();
        super.initOrderTask();
    }

}
