package com.dgsoft.erp.action;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.SetLinkList;
import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.business.order.OrderItemCreate;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/10/13
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("needResHome")
public class NeedResHome extends ErpEntityHome<NeedRes> {


    public synchronized boolean needStoreOut(String storeId) {


        for (Dispatch dispatch : getInstance().getDispatches()) {
            if (dispatch.getStore().getId().equals(storeId)) {
                log.debug("call need store out return true:" + storeId);
                return true;
            }
        }
        log.debug("call need store out return false:" + storeId);
        return false;
    }

    @In
    private FacesMessages facesMessages;

    @DataModel("orderNeedItems")
    private SetLinkList<OrderItem> orderNeedItems;

    @Override
    protected void initInstance() {
        super.initInstance();
        orderNeedItems = new SetLinkList<OrderItem>(getInstance().getOrderItems());
    }

    @DataModelSelection
    private OrderItem orderNeedItem;

    @In(create = true)
    private OrderItemCreate orderItemCreate;

    @In(create = true)
    private StoreResHome storeResHome;

    @In(required = false)
    private CustomerHome customerHome;

    @In
    private RunParam runParam;

    @In
    private DictionaryWord dictionary;

    private String addOrderItemLastStatus;

    public String getAddOrderItemLastStatus() {
        return addOrderItemLastStatus;
    }

    public void setAddOrderItemLastStatus(String addOrderItemLastStatus) {
        this.addOrderItemLastStatus = addOrderItemLastStatus;
    }

    public void addOrderItem() {
        OrderItem editingItem = orderItemCreate.getEditingItem();

        if (editingItem == null) {
            throw new IllegalArgumentException("editingItem state error");
        }

        if (editingItem.getMasterCount().equals(BigDecimal.ZERO)) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderItemZeroError");
            return;
        }


        storeResHome.setRes(editingItem.getRes(), editingItem.getFormats(), editingItem.getFloatConvertRate());
        if (storeResHome.isIdDefined()) {
            editingItem.setStoreRes(storeResHome.getInstance());
        } else {
            StoreRes itemNewStoreRes = findResInItems(editingItem);
            if (itemNewStoreRes == null){
                if (editingItem.getStoreRes() == null){
                    editingItem.setStoreRes(storeResHome.getInstance());
                }
                if (DataFormat.isEmpty(editingItem.getStoreRes().getCode())) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO,
                            "newSotreResTypedCodePlase");
                    addOrderItemLastStatus = "code_not_set";
                    editingItem.getStoreRes().setCode(ResHelper.instance().genStoreResCode(editingItem.getStoreRes()));
                    return;
                }
                if (!editingItem.getStoreRes().getCode().matches(runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME))) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeNotRule", editingItem.getStoreRes().getCode(),
                            runParam.getStringParamValue(StoreResHome.STORE_RES_CODE_RULE_PARAM_NAME));
                    addOrderItemLastStatus = "code_not_rule";
                    return ;
                }

                for (OrderItem orderItem: orderNeedItems) {
                    if (editingItem.getStoreRes().getCode().equals(orderItem.getStoreRes().getCode())) {
                        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                                "storeResCodeExists", orderItem.getStoreRes().getCode());
                        addOrderItemLastStatus = "code_exists";
                        return ;
                    }
                }
                if (!getEntityManager().createQuery("select storeRes from StoreRes storeRes where code = :code")
                        .setParameter("code", editingItem.getStoreRes().getCode()).getResultList().isEmpty()) {

                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                            "storeResCodeExists", editingItem.getStoreRes().getCode());
                    addOrderItemLastStatus = "code_exists";
                    return ;
                }

                storeResHome.clearInstance();
                storeResHome.setInstance(editingItem.getStoreRes());
            }else{
                editingItem.setStoreRes(itemNewStoreRes);
                storeResHome.clearInstance();
                storeResHome.setInstance(itemNewStoreRes);
            }
//            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderStoreResNotExists");
//            return;
        }
        editingItem.setStatus(OrderItem.OrderItemStatus.CREATED);
        editingItem.setOverlyOut(false);
        editingItem.setNeedRes(getInstance());
        orderNeedItems.add(editingItem);
        orderItemCreate.createNext();
        addOrderItemLastStatus = "added";
    }

    private StoreRes findResInItems(OrderItem editingItem){
        for(OrderItem orderItem: orderNeedItems){
           if (editingItem.getRes().equals(orderItem.getRes()) &&
                ResHelper.instance().sameFormat(orderItem.getStoreRes().getFormats(),editingItem.getFormats()) &&
                   (!editingItem.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                   || (orderItem.getStoreRes().getFloatConversionRate().compareTo(editingItem.getFloatConvertRate()) == 0))){
               storeResHome.clearInstance();
               return orderItem.getStoreRes();

           }
        }
        return null;
    }


    public void removeItem() {
        orderNeedItems.remove(orderNeedItem);
    }

    public SetLinkList<OrderItem> getOrderNeedItems() {
        return orderNeedItems;
    }

    @Override
    protected NeedRes createInstance() {
        return new NeedRes(new Date(), NeedRes.NeedResStatus.CREATED);
    }


    public void customerChangeListener() {
        if (customerHome.isIdDefined()) {
            if (runParam.getStringParamValue("erp.sale.receiveAddress").trim().equals("CITY")) {
                getInstance().setAddress(dictionary.getCityName(customerHome.getInstance().getProvinceCode()) + customerHome.getInstance().getCity());
            } else
                getInstance().setAddress(customerHome.getInstance().getAddress());
            getInstance().setPostCode(customerHome.getInstance().getPostCode());
        }
    }


    public BigDecimal getResTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : orderNeedItems) {
            if (item.getTotalMoney() != null)
                result = result.add(item.getTotalMoney());
        }
        return result;
    }

    public BigDecimal getResNeedTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : orderNeedItems) {
            if (item.getNeedMoney() != null)
                result = result.add(item.getNeedMoney());
        }
        return result;
    }

    @Override
    protected boolean wire() {
        if (!isManaged()) {
            getInstance().setCreateDate(new Date());
        }
        return true;
    }

}
