package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.SetLinkList;
import com.dgsoft.common.exception.ProcessCreatePrepareException;
import com.dgsoft.common.jbpm.BussinessProcessUtils;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.business.BusinessCreate;
import com.dgsoft.common.system.model.BusinessDefine;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.business.order.BackItemCreate;
import com.dgsoft.erp.model.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.bpm.CreateProcess;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by cooper on 2/23/14.
 */
@Name("storeResBackCreate")
@Scope(ScopeType.CONVERSATION)
public class StoreResBackCreate extends OrderBackHome {

    @In
    private Credentials credentials;

    @In(create = true)
    private OrderBackHome orderBackHome;

    @Override
    @DataModel("orderBackItems")
    public List<BackItem> getBackItems() {
        return backItems;
    }

    @In(required = false)
    private OrderHome orderHome;

    @In(create = true)
    private BackItemCreate backItemCreate;

    @In(create = true)
    private StoreResHome storeResHome;

    @In
    private FacesMessages facesMessages;

    @In(create = true)
    private CustomerHome customerHome;

    @In(required = false)
    private CustomerAreaHome customerAreaHome;

    @DataModelSelection
    private BackItem selectBackItem;

    @In(create = true)
    private ResBackDispatch resBackDispatch;

    @In
    private NumberBuilder numberBuilder;

    @Override
    protected OrderBack createInstance() {
        return new OrderBack(BigDecimal.ZERO, false);
    }

    @Override
    public void create(){
        super.create();

        getInstance().setId("T" + numberBuilder.getSampleNumber("storeResBack"));
        if ((orderHome != null) && orderHome.isIdDefined()){
            getInstance();
            for (OrderItem orderItem: orderHome.getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.COMPLETED, OrderItem.OrderItemStatus.WAIT_PRICE))){
                getBackItems().add(new BackItem(getInstance(),orderItem));
            }
            calcBackMoney();
        }
    }

    @Override
    public Class<OrderBack> getEntityClass() {
        return OrderBack.class;
    }

    @Override
    protected boolean wire() {
        if (!isManaged()) {

            getInstance().setApplyEmp(credentials.getUsername());
            getInstance().setMoneyComplete(!isNeedBackMoney());
            getInstance().setResComplete(!isNeedBackRes());
        }
        return true;
    }

    public void deleteItem() {
        getBackItems().remove(selectBackItem);
        calcBackMoney();
    }

    public void addNewBackItem() {
        BackItem item =  backItemCreate.getEditingItem();

        storeResHome.setRes(item.getRes(), item.getFormats(), item.getFloatConvertRate());
        if (storeResHome.isIdDefined()) {
            item.setStoreRes(storeResHome.getInstance());
            item.setOrderBack(getInstance());
            item.setBackItemStatus(BackItem.BackItemStatus.CREATE);
            item.calcMoney();

            backItems.add(item);


            calcBackMoney();
            backItemCreate.createNext();
        } else {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderStoreResNotExists");
        }
    }

    public String dispatchBack() {
        //if (!isCanCreate()) return null;
        resBackDispatch.init(getInstance());
        return "/business/startPrepare/erp/sale/BackStoreResDispatch.xhtml";
    }


    @Transactional
    public String dispatchAndCreateBack() {
        if (!resBackDispatch.isComplete()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "dispatchNotComplete");
            return null;
        }


        getInstance().setDispatched(true);
        getInstance().getBackDispatchs().clear();

        getInstance().getBackDispatchs().addAll(resBackDispatch.getResBackDispatcheds());
        for (BackItem item : getInstance().getBackItems()) {
            item.setBackItemStatus(BackItem.BackItemStatus.DISPATCH);
        }


        return createBack();
    }


    //TODO move to process EL
    //----------------
    @Out(scope = ScopeType.BUSINESS_PROCESS, required = false)
    private String businessDescription;

    @Out(scope = ScopeType.BUSINESS_PROCESS, required = false)
    private String businessName;

    //-----------------

    @CreateProcess(definition = "orderCancel", processKey = "#{storeResBackCreate.instance.id}")
    @Transactional
    public String createBack() {

        //if (!isCanCreate()) return null;

        calcBackMoney();
        if (customerHome.isIdDefined()) {
            customerHome.refresh();
            getInstance().setCustomer(customerHome.getInstance());
        } else {
            getInstance().setCustomer(customerHome.getReadyInstance());
            getInstance().getCustomer().setCustomerArea(customerAreaHome.getInstance());
        }

        getInstance().getBackItems().addAll(backItems);
        if (getInstance().getMoney().compareTo(BigDecimal.ZERO) == 0){
            getInstance().setMoneyComplete(true);
        }
        orderBackHome.setInstance(getInstance());
        if (!"persisted".equals(persist())) {
            return null;
        }
        businessDescription = customerHome.getInstance().getName();
        businessName = "客户退货";
        return "businessCreated";

    }

//    private boolean isCanCreate() {
//        if (backItems.isEmpty()){
//            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"noneBackItemAdd");
//            return false;
//        }
//        if (getInstance().getMoney().compareTo(BigDecimal.ZERO) < 0){
//            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"backMoneyCantLessZero");
//            return false;
//        }
//        return true;
//    }


}
