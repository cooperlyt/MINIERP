package com.dgsoft.erp.action;

import com.dgsoft.common.SetLinkList;

import com.dgsoft.common.jbpm.ProcessInstanceHome;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.*;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.*;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/18/13
 * Time: 2:52 PM
 */
@Name("orderBackHome")
public class OrderBackHome extends ErpEntityHome<OrderBack> {

    protected List<BackItem> backItems;

    @Override
    protected void initInstance() {
        super.initInstance();
        backItems = new SetLinkList<BackItem>(getInstance().getBackItems());
    }

    public boolean needStoreIn(String storeId) {

        for (BackDispatch BACKDISPATCH : getInstance().getBackDispatchs()) {
            if (BACKDISPATCH.getStore().getId().equals(storeId)) {
                return true;
            }
        }
        return false;

    }

    public List<BackItem> getBackItems() {
        getInstance();
        return backItems;
    }

    public boolean isNeedBackMoney() {
        return getInstance().getMoney().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNeedBackRes() {
        return !getInstance().getBackItems().isEmpty();
    }


    public void calcBackMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (BackItem item : getBackItems()) {
            if (item.getTotalMoney() != null) {
                result = result.add(item.getTotalMoney());
            }
        }
        getInstance().setMoney(result);
    }

    @Deprecated
    public BigDecimal getResTotalMoney() {
        return getInstance().getMoney();
    }

    @Transactional
    public void deleteBack() {
        //BigDecimal operMoney = BigDecimal.ZERO;
        for (AccountOper ao : getInstance().getAccountOpers()) {
            ao.revertCustomerMoney();
        }
        //log.debug("operMoney:" + operMoney);
//        if (operMoney.compareTo(BigDecimal.ZERO) != 0) {
//            getInstance().getCustomer().setBalance(getInstance().getCustomer().getBalance().add(operMoney));
//        }
        getInstance().getCustomer().getAccountOpers().removeAll(getInstance().getAccountOpers());


        for (BackDispatch dispatch : getInstance().getBackDispatchs()) {
            if (dispatch.getStockChange() != null) {
                for (StockChangeItem item : dispatch.getStockChange().getStockChangeItems()) {
                    item.getStock().setCount(item.getStock().getCount().subtract(item.getCount()));
                }
                getEntityManager().remove(dispatch.getStockChange());
            }
        }

        ProcessInstanceHome processInstanceHome = (ProcessInstanceHome) Component.getInstance(ProcessInstanceHome.class, true);
        processInstanceHome.setProcessDefineName("orderCancel");
        processInstanceHome.setProcessKey(getInstance().getId());
        if ("removed".equals(remove())) {
            processInstanceHome.stop();
        }

    }


}
