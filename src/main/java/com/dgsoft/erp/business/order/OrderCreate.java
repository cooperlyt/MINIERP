package com.dgsoft.erp.business.order;

import com.dgsoft.common.exception.ProcessCreatePrepareException;
import com.dgsoft.common.DataFormat;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.business.StartData;
import com.dgsoft.common.system.model.BusinessDefine;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/24/13
 * Time: 11:05 AM
 */
@Name("orderCreate")
public class OrderCreate extends OrderHome {

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
    private OrderDispatch orderDispatch;

    @In(create = true)
    private NeedResHome needResHome;

    @In
    private FacesMessages facesMessages;

    @In
    private Credentials credentials;

    @In(create = true)
    protected OrderHome orderHome;

    @In
    private NumberBuilder numberBuilder;

    @Override
    public Class<CustomerOrder> getEntityClass() {
        return CustomerOrder.class;
    }

    public void clearCustomerAndMiddleMan() {
        customerHome.clearInstance();
        customerHome.setHaveMiddleMan(false);
        middleManHome.clearInstance();
    }

    @Override
    protected CustomerOrder createInstance() {
        return new CustomerOrder(credentials.getUsername(), new Date());
    }

    private BigDecimal earnestScale = BigDecimal.ZERO;

    public BigDecimal getEarnestScale() {
        return earnestScale;
    }

    public void setEarnestScale(BigDecimal earnestScale) {
        this.earnestScale = earnestScale;
        calcEarnest();
    }

    @Override
    public void calcMoneys() {
        super.calcMoneys();
        calcEarnest();
        getInstance().setMoneyComplete(getInstance().getMoney().compareTo(BigDecimal.ZERO) == 0);
    }

    private void calcEarnest() {
        getInstance().setEarnest(DataFormat.halfUpCurrency(getInstance().getMoney()
                .multiply(earnestScale.divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP))));
    }


    public void orderTelChanged() {
        if ((!DataFormat.isEmpty(getInstance().getTel())) && (DataFormat.isEmpty(getInstance().getContact()))) {
            for (CustomerContact contact : customerHome.getInstance().getCustomerContacts()) {
                if ((contact.getTel() != null) && contact.getTel().equals(getInstance().getTel())) {
                    getInstance().setContact(contact.getName());
                    break;
                }
            }
        }
        orderContactInfoChanged();
    }

    public void orderContactChanged() {
        if ((DataFormat.isEmpty(getInstance().getTel())) && (!DataFormat.isEmpty(getInstance().getContact()))) {
            for (CustomerContact contact : customerHome.getInstance().getCustomerContacts()) {
                if ((contact.getName() != null) && contact.getName().equals(getInstance().getContact())) {
                    getInstance().setTel(contact.getTel());
                    break;
                }
            }
        }
        orderContactInfoChanged();
    }

    private void orderContactInfoChanged() {
        if ((!DataFormat.isEmpty(getInstance().getTel())) && (!DataFormat.isEmpty(getInstance().getContact())))
            if ((DataFormat.isEmpty(getNeedRes().getReceiveTel())) && (DataFormat.isEmpty(getNeedRes().getReceivePerson()))) {
                getNeedRes().setReceiveTel(getInstance().getTel());
                getNeedRes().setReceivePerson(getInstance().getContact());
            }
    }

    public void orderReceivceTelChanged() {
        if ((!DataFormat.isEmpty(getNeedRes().getReceiveTel())) && (DataFormat.isEmpty(getNeedRes().getReceivePerson()))) {
            for (CustomerContact contact : customerHome.getInstance().getCustomerContacts()) {
                if ((contact.getTel() != null) && contact.getTel().equals(getNeedRes().getReceiveTel())) {
                    getNeedRes().setReceivePerson(contact.getName());
                    break;
                }
            }
        }
        orderReceiveInfoChanged();
    }

    private void orderReceiveInfoChanged() {
        if ((!DataFormat.isEmpty(getNeedRes().getReceiveTel())) && (!DataFormat.isEmpty(getNeedRes().getReceivePerson())))
            if ((DataFormat.isEmpty(getInstance().getTel())) && (DataFormat.isEmpty(getInstance().getContact()))) {
                getInstance().setTel(getNeedRes().getReceiveTel());
                getInstance().setContact(getNeedRes().getReceivePerson());
            }
    }

    public void orderReceivceContactChanged() {
        if ((!DataFormat.isEmpty(getNeedRes().getReceivePerson())) && (DataFormat.isEmpty(getNeedRes().getReceiveTel()))) {
            for (CustomerContact contact : customerHome.getInstance().getCustomerContacts()) {
                if ((contact.getName() != null) && contact.getName().equals(getNeedRes().getReceivePerson())) {
                    getNeedRes().setReceiveTel(contact.getTel());
                    break;
                }
            }
        }
        orderReceiveInfoChanged();
    }

    private NeedRes getNeedRes() {
        return needResHome.getInstance();
    }

    public void removeItem() {
        needResHome.removeItem();
        resItemList = null;
    }

    public void addOrderItem() {
        needResHome.addOrderItem();
        calcMoneys();
        resItemList = null;
    }


    public void clearCustomer() {
        customerHome.clearInstance();
        middleManHome.clearInstance();
    }

    public String saveOrderCustomer() {
        if (customerHome.isIdDefined()) {
            customerHome.refresh();
            getInstance().setCustomer(customerHome.getInstance());
        } else {

            getInstance().setCustomer(customerHome.getReadyInstance());
            getInstance().getCustomer().setCustomerArea(customerAreaHome.getInstance());

        }
        return "/business/startPrepare/erp/sale/CreateSaleOrderItem.xhtml";
    }

    private boolean dispatched = false;

    public String dispatchBack(){
        dispatched = false;
        return "/business/startPrepare/erp/sale/CreateSaleOrderItem.xhtml";

    }

    public String saveOrderItem() {

        if (verifyItem()) {

            orderDispatch.init(needResHome.getInstance());
            dispatched = true;
            return "/business/startPrepare/erp/sale/CreateSaleOrderDispatch.xhtml";

        }

        return "fail";
    }

    private void genBusinessKey() {
        startData.setBusinessKey(numberBuilder.getDayNumber("order"));
        while (getEntityManager().find(getEntityClass(), startData.getBusinessKey()) != null) {
            startData.setBusinessKey(numberBuilder.getDayNumber("order"));
        }
    }

    public String beginCreateOrder() {
        businessDefineHome.setId("erp.business.order");
        genBusinessKey();

        needResHome.getInstance().setCustomerOrder(getInstance());
        needResHome.getInstance().setType(NeedRes.NeedResType.ORDER_SEND);
        needResHome.getInstance().setReason(ORDER_SEND_REASON_WORD_KEY);


        getInstance().setTotalRebateMoney(BigDecimal.ZERO);
        getInstance().setMoney(BigDecimal.ZERO);
        getInstance().setEarnest(BigDecimal.ZERO);
        getInstance().getNeedReses().add(needResHome.getInstance());
        return "beginning";
    }

    public String createCloneOrder() {
        if (orderHome.isIdDefined()) {

            businessDefineHome.setId("erp.business.order");
            genBusinessKey();
            needResHome.clearInstance();

            needResHome.getInstance().setCustomerOrder(getInstance());
            needResHome.getInstance().setType(NeedRes.NeedResType.ORDER_SEND);
            needResHome.getInstance().setReason(ORDER_SEND_REASON_WORD_KEY);
            getInstance().getNeedReses().add(needResHome.getInstance());

            for (OrderItem orderItem : orderHome.getMasterNeedRes().getOrderItems()) {

                needResHome.getOrderNeedItems().add(new OrderItem(needResHome.getInstance(),
                        orderItem.getStoreRes(), orderItem.getResUnit(), orderItem.getCount(),
                        orderItem.getMoney(), orderItem.getRebate(), orderItem.isPresentation(),
                        orderItem.getMemo(), orderItem.getNeedConvertRate()));
            }

            getInstance().setPayType(orderHome.getInstance().getPayType());
            //customerAreaHome.setId(orderHome.getInstance().getCustomer().getCustomerArea().getId());
            customerHome.setId(orderHome.getInstance().getCustomer().getId());
            getInstance().setContact(orderHome.getInstance().getContact());
            getInstance().setTel(orderHome.getInstance().getTel());

            needResHome.getInstance().setFareByCustomer(orderHome.getMasterNeedRes().isFareByCustomer());
            needResHome.getInstance().setPostCode(orderHome.getMasterNeedRes().getPostCode());
            needResHome.getInstance().setAddress(orderHome.getMasterNeedRes().getAddress());
            needResHome.getInstance().setReceivePerson(orderHome.getMasterNeedRes().getReceivePerson());
            needResHome.getInstance().setReceiveTel(orderHome.getMasterNeedRes().getReceiveTel());
            needResHome.getInstance().setLimitTime(orderHome.getMasterNeedRes().getLimitTime());

            getInstance().setIncludeMiddleMan(orderHome.getInstance().isIncludeMiddleMan());
            if (getInstance().isIncludeMiddleMan()) {
                middleManHome.setId(orderHome.getInstance().getCustomer().getMiddleMan().getId());
            }

            getInstance().setTotalRebateMoney(orderHome.getInstance().getTotalRebateMoney());
            getInstance().setEarnestFirst(orderHome.getInstance().isEarnestFirst());
            getInstance().setEarnest(orderHome.getInstance().getEarnest());
            getInstance().setMoney(orderHome.getInstance().getMoney());
            //getInstance().setTotalMoney(orderHome.getInstance().getTotalMoney());
            setEarnestScale(orderHome.getEarnestScale());
            calcEarnest();


        } else {
            return beginCreateOrder();
        }

        return "beginning";
    }

    @Factory("orderPayTypes")
    public CustomerOrder.OrderPayType[] getOrderPayTypes() {
        return CustomerOrder.OrderPayType.values();
    }


    //private boolean orderWired = false;

    protected boolean wireOrder() {

        //TODO cost calc for  BOM table
        getInstance().setTotalCost(new BigDecimal(0));
        getInstance().setMoneyComplete(false);

        calcMoneys();
        //getInstance().setCreateDate(new Date());

        getInstance().setId(startData.getBusinessKey());
        if (getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST)) {
            getInstance().setEarnestFirst(false);
        }


        if (getInstance().isIncludeMiddleMan()) {
            if (getInstance().getCustomer().getMiddleMan() == null) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderIncludeMiddleManError");
                return false;
            }
        }

        //getInstance().getNeedReses().clear();
        //getInstance().getNeedReses().add(needResHome.getReadyInstance());

        return true;
    }


    private boolean verifyItem() {

        if (needResHome.getOrderNeedItems().isEmpty()) {
            getStatusMessages().addFromResourceBundle(StatusMessage.Severity.ERROR, "createOrderItemIsEmptyError");

            return false;
        }

        if (getInstance().isEarnestFirst() && (getInstance().getEarnest().compareTo(BigDecimal.ZERO) <= 0)) {
            getStatusMessages().addFromResourceBundle(StatusMessage.Severity.ERROR, "createOrderEarnestIsZero");

            return false;
        }
        return true;
    }


    @Observer("com.dgsoft.BusinessCreatePrepare.order")
    @Transactional
    public void createOrder(BusinessDefine businessDefine) {


        if (!verifyItem()) {
            throw new ProcessCreatePrepareException("order item verify fail");
        }


        if (!wireOrder()) {
            throw new ProcessCreatePrepareException("create order wire fail");
        }

        if (dispatched) {

            if (!orderDispatch.isDispatchComplete()) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "dispatch_not_complete");
                throw new ProcessCreatePrepareException("dispatch not complete");
            }

            if (getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
                boolean allCustomerSelf = true;
                for (Dispatch dispatch : orderDispatch.getDispatchList()) {
                    if (!dispatch.getDeliveryType().equals(Dispatch.DeliveryType.CUSTOMER_SELF)) {
                        allCustomerSelf = false;
                        break;
                    }
                }
                if (allCustomerSelf) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "canotAllCustomerSelf");
                    throw new ProcessCreatePrepareException("dispatch error");
                }
            }
            orderDispatch.wire();
            needResHome.getInstance().setStatus(NeedRes.NeedResStatus.DISPATCHED);
            for (OrderItem orderItem : needResHome.getOrderNeedItems()) {
                orderItem.setStatus(OrderItem.OrderItemStatus.DISPATCHED);
            }
        }

        if (!"persisted".equals(persist())) {
            throw new ProcessCreatePrepareException("create order persist fail");
        }

        startData.setDescription(getInstance().getCustomer().getName());
        orderHome.setId(getInstance().getId());
        needResHome.setId(getInstance().getNeedResList().get(0).getId());
    }

    private List<TotalResItem> resItemList = null;

    public List<TotalResItem> getResItemList() {
        if (resItemList == null) {
            resItemList = new ArrayList<TotalResItem>();
            for (OrderItem item : needResHome.getOrderNeedItems()) {
                boolean find = false;
                for (TotalResItem tr : resItemList) {
                    if (tr.isSameItem(item)) {
                        tr.add(item);
                        find = true;
                    }
                }
                if (!find) {
                    resItemList.add(new TotalResItem(item.getRes(), item.getResUnit(), item.getMoney(), item.getRebate(), item.getUseUnitCount()));
                }

            }
        }
        return resItemList;
    }

    public BigDecimal getTotalResFreeMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (TotalResItem item : getResItemList()) {
            if (item.isCanRebate())
                result = result.add(item.getFreeMoney());
        }
        return result;
    }

    public void setResItemFreeMoneyToOrder() {
        setUseScaleRebate(false);
        getInstance().setTotalRebateMoney(getTotalResFreeMoney());
        calcMoneys();
    }

    public class TotalResItem {

        private Res res;

        private ResUnit resUnit;

        private BigDecimal money;

        private BigDecimal rebate;

        private BigDecimal count;

        private BigDecimal freeCountBasicRate = BigDecimal.ZERO;

        private BigDecimal freeCountRate = BigDecimal.ZERO;

        public TotalResItem(Res res, ResUnit resUnit, BigDecimal money, BigDecimal rebate, BigDecimal count) {
            this.res = res;
            this.resUnit = resUnit;
            this.money = money;
            this.rebate = rebate;
            this.count = count;
        }

        public void add(OrderItem other) {
            if (!isSameItem(other))
                throw new IllegalArgumentException("not same");
            count = count.add(other.getUseUnitCount());
        }

        public boolean isSameItem(OrderItem other) {
            return res.equals(other.getRes()) && resUnit.getId().equals(other.getResUnit().getId())
                    && (other.getMoney().compareTo(money) == 0) &&
                    (other.getRebate().compareTo(rebate) == 0);
        }

        public Res getRes() {
            return res;
        }

        public BigDecimal getFreeCount() {
            if (DataFormat.isEmpty(freeCountBasicRate)) {
                return BigDecimal.ZERO;
            }
            return count.divide(freeCountBasicRate, 0, BigDecimal.ROUND_DOWN).multiply(freeCountRate);
        }

        public boolean isCanRebate() {
            return (getMoney().compareTo(BigDecimal.ZERO) > 0);
        }

        public BigDecimal getFreeMoney() {
            if (DataFormat.isEmpty(freeCountBasicRate)) {
                return BigDecimal.ZERO;
            }

            return DataFormat.halfUpCurrency(getMoney().multiply(getRebate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP)).multiply(getFreeCount()));
        }

        public BigDecimal getTotalPrice() {

            return DataFormat.halfUpCurrency(count.multiply(getMoney().multiply(getRebate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP))));
        }

        public BigDecimal getMoney() {
            return money;
        }

        public void setMoney(BigDecimal money) {
            this.money = money;
        }

        public ResUnit getResUnit() {
            return resUnit;
        }

        public void setResUnit(ResUnit resUnit) {
            this.resUnit = resUnit;
        }

        public void setRebate(BigDecimal rebate) {
            this.rebate = rebate;
        }

        public BigDecimal getRebate() {
            return rebate;
        }

        public boolean isPresentation() {
            return false;
        }

        public void setPresentation(boolean presentation) {
        }

        public BigDecimal getCount() {
            return count;
        }

        public void setCount(BigDecimal count) {
            this.count = count;
        }

        public StoreRes getStoreRes() {
            return null;
        }

        public void setStoreRes(StoreRes storeRes) {
        }

        public BigDecimal getFreeCountRate() {
            return freeCountRate;
        }

        public void setFreeCountRate(BigDecimal freeCountRate) {
            this.freeCountRate = freeCountRate;
        }

        public BigDecimal getFreeCountBasicRate() {
            return freeCountBasicRate;
        }

        public void setFreeCountBasicRate(BigDecimal freeCountBasicRate) {
            this.freeCountBasicRate = freeCountBasicRate;
        }
    }

}
