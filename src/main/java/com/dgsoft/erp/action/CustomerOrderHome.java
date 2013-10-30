package com.dgsoft.erp.action;

import com.dgsoft.common.exception.ProcessCreatePrepareException;
import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.business.StartData;
import com.dgsoft.common.system.model.BusinessDefine;
import com.dgsoft.common.system.model.BusinessInstance;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.NeedRes;
import org.jboss.seam.annotations.*;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/24/13
 * Time: 11:05 AM
 */
@Name("customerOrderHome")
public class CustomerOrderHome extends ErpEntityHome<CustomerOrder> {

    private static final String ORDER_SEND_REASON_WORD_KEY ="erp.needResReason.order";

    @In(create = true)
    private CustomerHome customerHome;

    @In(create = true)
    private MiddleManHome middleManHome;

    @In(required = false)
    private CustomerAreaHome customerAreaHome;

    @In(create = true)
    private StartData startData;

    @In(create = true)
    private BusinessDefineHome businessDefineHome;

    @In
    private FacesMessages facesMessages;

    @In
    private Credentials credentials;

    private NeedRes needRes;

    public void clearCustomer() {
        customerHome.clearInstance();
        middleManHome.clearInstance();
    }

    public String beginCreateOrder() {
        businessDefineHome.setId("erp.business.order");
        startData.generateKey();
        needRes = new NeedRes(getInstance(),
                NeedRes.NeedResType.ORDER_SEND,ORDER_SEND_REASON_WORD_KEY,new Date());
        return "beginning";
    }

    @Factory("orderPayTypes")
    public CustomerOrder.OrderPayType[] getOrderPayTypes() {
        return CustomerOrder.OrderPayType.values();
    }

    @Override
    protected boolean wire() {

        //TODO cost calc for  BOM table
        getInstance().setTotalCost(new BigDecimal(0));

        if (getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST)) {
            getInstance().setState(CustomerOrder.OrderState.WAITING_PAY_FIRST);
        } else {
            getInstance().setState(CustomerOrder.OrderState.WAITING_SEND);
        }

        getInstance().setCreateDate(new Date());
        getInstance().setOrderEmp(credentials.getUsername());
        getInstance().setId(startData.getBusinessKey());
        if (customerHome.isIdDefined()) {
            getInstance().setCustomer(customerHome.getInstance());
        } else {

            getInstance().setCustomer(customerHome.getReadyInstance());
            getInstance().getCustomer().setCustomerArea(customerAreaHome.getInstance());
            getInstance().setContact(customerHome.getInstance().getContact());
            getInstance().setTel(customerHome.getInstance().getTel());
        }

        if (getInstance().isMiddleManPay()) {
            if (getInstance().getCustomer().getMiddleMan() == null) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"orderIncludeMiddleManError");
                return false;
            }
        }

        getInstance().getNeedReses().add(needRes);

        return true;
    }

    public void customerChangeListener() {
        if (customerHome.isIdDefined()) {
            getInstance().setContact(customerHome.getInstance().getContact());
            getInstance().setTel(customerHome.getInstance().getTel());
        }
    }


    @Observer("com.dgsoft.BusinessCreatePrepare.order")
    @Transactional
    public void createOrder(BusinessDefine businessDefine) {

        if (!"persisted".equals(persist())) {
            throw new ProcessCreatePrepareException("create order persist fail");
        }
    }

}
