package com.dgsoft.erp.action;

import com.dgsoft.common.exception.ProcessCreatePrepareException;
import com.dgsoft.common.jbpm.BussinessProcessUtils;
import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.business.StartData;
import com.dgsoft.common.system.model.BusinessDefine;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.OrderBack;
import com.dgsoft.erp.model.ProductBackStoreIn;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.security.Credentials;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/18/13
 * Time: 2:52 PM
 */
@Name("orderBackHome")
public class OrderBackHome extends ErpEntityHome<OrderBack> {

    @In(create = true)
    private StartData startData;

    @In(create = true)
    private BusinessDefineHome businessDefineHome;

    @In(required = false)
    private OrderHome orderHome;

    @In
    private Credentials credentials;

    @In(create = true)
    private BussinessProcessUtils businessProcess;

    @Factory(value = "orderBackTypes", scope = ScopeType.CONVERSATION)
    public OrderBack.OrderBackType[] getOrderBackTypes() {
        return OrderBack.OrderBackType.values();
    }

    public void init() {
        startData.generateKey();
        businessDefineHome.setId("erp.business.orderCancel");
    }

    public boolean needStoreIn(String storeId) {
        if (isIdDefined()) {
            for (ProductBackStoreIn productBackStoreIn : getInstance().getProductBackStoreIn()) {
                if (productBackStoreIn.getStore().getId().equals(storeId)) {
                    return true;
                }
            }
            return false;
        }
        throw new IllegalThreadStateException("business not init;");
    }

    public void backAllMoney(){
        getInstance().setMoney(orderHome.getTotalReveiveMoney());
    }

    public void backNoEarnestMoney(){
        getInstance().setMoney(orderHome.getTotalReveiveMoney().subtract(orderHome.getReveiveEarnest()));
    }

    @Override
    protected boolean wire() {
        if (!isManaged()) {
            if (!orderHome.isIdDefined()) {
                throw new IllegalStateException("order not select");
            }
            getInstance().setId(startData.getBusinessKey());
            getInstance().setApplyEmp(credentials.getUsername());
            getInstance().setCustomerOrder(orderHome.getInstance());
            getInstance().setCreateDate(new Date());
            getInstance().setMoneyComplete(!orderHome.isAnyOneMoneyPay());
            getInstance().setResComplete(!orderHome.isAnyOneStoreOut());
            getInstance().getCustomerOrder().setCanceled(true);

        }
        return true;
    }


    @Observer(value = "com.dgsoft.BusinessCreatePrepare.orderCancel", create = true)
    @Transactional
    public void createOrder(BusinessDefine businessDefine) {

        if (!"persisted".equals(persist())) {
            throw new ProcessCreatePrepareException("inventory persist fail");
        }

        ProcessDefinition definition = ManagedJbpmContext.instance().getGraphSession().findLatestProcessDefinition("order");
        ProcessInstance processInstance = definition==null ?
                null : ManagedJbpmContext.instance().getProcessInstanceForUpdate(definition, orderHome.getInstance().getId());

        processInstance.suspend();

        startData.setDescription(orderHome.getInstance().getCustomer().getName() + "[" + orderHome.getInstance().getId()  + "]");
    }




    @End
    public String cancelSimpleOrder(){

        businessProcess.stopProcess("order",orderHome.getInstance().getId());
        if ("persisted".equals(persist())){
            return "/func/erp/sale/CustomerOrder.xhtml";
        } else
            return null;
    }




}
