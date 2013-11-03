package com.dgsoft.erp.action;

import com.dgsoft.common.exception.ProcessCreatePrepareException;
import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.business.StartData;
import com.dgsoft.common.system.model.BusinessDefine;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.store.OrderNeedItem;
import com.dgsoft.erp.action.store.StoreInItem;
import com.dgsoft.erp.action.store.StoreResFormatFilter;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/24/13
 * Time: 11:05 AM
 */
@Name("customerOrderHome")
public class CustomerOrderHome extends ErpEntityHome<CustomerOrder> {

    private static final String ORDER_SEND_REASON_WORD_KEY = "erp.needResReason.order";

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

    @In(create = true)
    private StoreResHome storeResHome;

    @In(required = false)
    protected StoreResFormatFilter storeResFormatFilter;

    @In(create = true)
    protected ResHome resHome;

    @In
    private FacesMessages facesMessages;

    @In
    private Credentials credentials;

    private NeedRes needRes;

    @DataModel("orderNeedItems")
    private List<OrderNeedItem> orderNeedItems;

    @DataModelSelection
    private OrderNeedItem orderNeedItem;

    private OrderNeedItem editingItem;

    public OrderNeedItem getEditingItem() {
        return editingItem;
    }

    public void setEditingItem(OrderNeedItem editingItem) {
        this.editingItem = editingItem;
    }

    @Observer(value = "storeResCountIsChanged", create = false)
    public void itemCountChangeListener() {
        if (editingItem != null) {
            editingItem.calcPriceByCount();
        }
    }

    @Observer(value = "erp.resLocateSelected", create = false)
    public void generateSaleItemByRes(Res res) {
        editingItem = new OrderNeedItem(res);
        editingItem.setStoreResItem(false);
    }

    @Observer(value = "erp.storeResLocateSelected", create = false)
    public void generateSaleItemByStoreRes(StoreRes storeRes) {
        editingItem = new OrderNeedItem(storeRes.getRes(), storeRes.getFloatConversionRate());
        editingItem.setStoreResItem(true);
    }

    public int getItemsCount(){
        return orderNeedItems.size();
    }

    public BigDecimal getOrderTotalPrice(){
       BigDecimal result = new BigDecimal("0");
        for (OrderNeedItem item: orderNeedItems){
            result = result.add(item.getTotalPrice());
        }
        return result;
    }

    public void removeItem(){
        orderNeedItems.remove(orderNeedItem);
    }

    public void addOrderItem() {
        if ((editingItem == null)) {
            throw new IllegalArgumentException("editingItem state error");
        }
        if (editingItem.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)){
            editingItem.setUseUnit(editingItem.getStoreResCount().getUseUnit());
        }

        editingItem.calcPriceByCount();

        if (editingItem.isStoreResItem()){
            storeResHome.setRes(editingItem.getRes(), storeResFormatFilter.getResFormatList(), editingItem.getStoreResCount().getFloatConvertRate());
            if (storeResHome.isIdDefined()) {
                editingItem.setStoreRes(storeResHome.getInstance());
            } else {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderStoreResNotExists");
                return;
            }

        }

        for (OrderNeedItem orderNeedItem : orderNeedItems) {

            if (orderNeedItem.same(editingItem)) {
                orderNeedItem.merger(editingItem);
                editingItem = null;
                facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO,"storeResInOrderItemMerger");
                break;
            }
        }
        if (editingItem != null){
            orderNeedItems.add(editingItem);
        }
        editingItem = null;
        resHome.clearInstance();
        storeResHome.clearInstance();

    }

    public NeedRes getNeedRes() {
        return needRes;
    }

    public void setNeedRes(NeedRes needRes) {
        this.needRes = needRes;
    }

    public void clearCustomer() {
        customerHome.clearInstance();
        middleManHome.clearInstance();
    }

    public String beginCreateOrder() {
        businessDefineHome.setId("erp.business.order");
        startData.generateKey();
        needRes = new NeedRes(getInstance(),
                NeedRes.NeedResType.ORDER_SEND, ORDER_SEND_REASON_WORD_KEY, new Date());
        orderNeedItems = new ArrayList<OrderNeedItem>();
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

        if (getInstance().isIncludeMiddleMan()) {
            if (getInstance().getCustomer().getMiddleMan() == null) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderIncludeMiddleManError");
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