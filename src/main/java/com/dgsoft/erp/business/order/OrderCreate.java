package com.dgsoft.erp.business.order;

import com.dgsoft.common.SetLinkList;
import com.dgsoft.common.exception.ProcessCreatePrepareException;
import com.dgsoft.common.DataFormat;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.model.BusinessDefine;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.bpm.CreateProcess;
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

    @In
    private Credentials credentials;

    @In(create = true)
    private CustomerHome customerHome;

    @In(create = true)
    private MiddleManHome middleManHome;

    @In(required = false)
    private CustomerAreaHome customerAreaHome;

    @In(create = true)
    private OrderDispatch orderDispatch;

    @In(create = true)
    private NeedResHome needResHome;

    @In
    private FacesMessages facesMessages;


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
    }

    public void addOrderItem() {
        needResHome.addOrderItem();
        calcMoneys();
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

    public String toOrderMoneyCalc() {
        resSaleRebates.clear();


        for (OrderItem item : needResHome.getOrderNeedItems()) {
            boolean find = false;
            for (ResSaleRebate tr : resSaleRebates) {
                if (tr.isSameItem(item)) {
                    tr.add(item);
                    find = true;
                }
            }
            if (!find && (item.getMoney().compareTo(BigDecimal.ZERO) > 0)) {
                resSaleRebates.add(new ResSaleRebate(getInstance(), item.getRes(), item.getUseUnit(), item.getUseUnitCount(), item.getMoney(), item.getRebate()));
            }

        }
        return "/business/startPrepare/erp/sale/CreateSaleOrderMoney.xhtml";

    }

    private boolean dispatched = false;

    public boolean isDispatched() {
        return dispatched;
    }

    public String dispatchBack() {
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
        getInstance().setId(numberBuilder.getDayNumber("order"));
        while (getEntityManager().find(getEntityClass(), getInstance().getId()) != null) {
            getInstance().setId(numberBuilder.getDayNumber("order"));
        }
    }

    public String beginCreateOrder() {
        genBusinessKey();

        needResHome.getInstance().setCustomerOrder(getInstance());
        needResHome.getInstance().setType(NeedRes.NeedResType.ORDER_SEND);
        needResHome.getInstance().setReason(ORDER_SEND_REASON_WORD_KEY);


        getInstance().setTotalRebateMoney(BigDecimal.ZERO);
        getInstance().setMoney(BigDecimal.ZERO);
        getInstance().setEarnest(BigDecimal.ZERO);
        getInstance().getNeedReses().add(needResHome.getInstance());
        return "/business/startPrepare/erp/sale/CreateSaleOrder.xhtml";
    }

    public String createCloneOrder() {
        if (orderHome.isIdDefined()) {

            genBusinessKey();
            needResHome.clearInstance();

            needResHome.getInstance().setCustomerOrder(getInstance());
            needResHome.getInstance().setType(NeedRes.NeedResType.ORDER_SEND);
            needResHome.getInstance().setReason(ORDER_SEND_REASON_WORD_KEY);
            getInstance().getNeedReses().add(needResHome.getInstance());

            for (OrderItem orderItem : orderHome.getMasterNeedRes().getOrderItems()) {

                needResHome.getOrderNeedItems().add(new OrderItem(needResHome.getInstance(),
                        orderItem.getStoreRes(),
                        (orderItem.getResUnit() == null) ? orderItem.getStoreRes().getRes().getResUnitByOutDefault() : orderItem.getResUnit(),
                        orderItem.getCount(),
                        (orderItem.getMoney() == null) ? BigDecimal.ZERO : orderItem.getMoney(),
                        (orderItem.getRebate() == null) ? new BigDecimal("100"):orderItem.getRebate(),
                        orderItem.isPresentation(),
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

        return "/business/startPrepare/erp/sale/CreateSaleOrder.xhtml";
    }

    @Factory("orderPayTypes")
    public CustomerOrder.OrderPayType[] getOrderPayTypes() {
        return CustomerOrder.OrderPayType.values();
    }


    //private boolean orderWired = false;

    protected boolean wireOrder() {

        //TODO cost calc for  BOM table
        getInstance().setTotalCost(new BigDecimal(0));

        calcMoneys();
        //getInstance().setCreateDate(new Date());

        if (getInstance().getPayType().equals(CustomerOrder.OrderPayType.PAY_FIRST)) {
            getInstance().setEarnestFirst(false);
        }


        if (getInstance().isIncludeMiddleMan()) {
            if (getInstance().getCustomer().getMiddleMan() == null) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderIncludeMiddleManError");
                return false;
            }
        }


        List<ResSaleRebate> removeRebates = new ArrayList<ResSaleRebate>();
        for (ResSaleRebate resSaleRebate: resSaleRebates){
            if (resSaleRebate.getRebateMoney().compareTo(BigDecimal.ZERO) == 0){
                removeRebates.add(resSaleRebate);
            }
        }
        resSaleRebates.removeAll(removeRebates);

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


    @End
    @CreateProcess(definition = "order", processKey = "#{orderCreate.instance.id}")
    @Transactional
    public String createOrder() {


        if (!verifyItem()) {
            return null;
        }


        if (!wireOrder()) {
            return null;
        }

        if (dispatched) {

            if (!orderDispatch.isDispatchComplete()) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "dispatch_not_complete");
                return null;
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
                    return null;
                }
            }
            orderDispatch.wire();
            needResHome.getInstance().setStatus(NeedRes.NeedResStatus.DISPATCHED);
            for (OrderItem orderItem : needResHome.getOrderNeedItems()) {
                orderItem.setStatus(OrderItem.OrderItemStatus.DISPATCHED);
            }
        }

        if (!"persisted".equals(persist())) {
            return null;
        }

        orderHome.setId(getInstance().getId());
        needResHome.setId(getInstance().getNeedResList().get(0).getId());

        return "/business/startPrepare/erp/sale/SaleOrderCreated.xhtml";
    }

    //private List<TotalResItem> resItemList = null;


    public void calcOrderFreeMoney() {
        calcMoneys();
    }

//    public class TotalResItem {
//
//        private Res res;
//
//        private ResUnit resUnit;
//
//        private BigDecimal money;
//
//        private BigDecimal rebate;
//
//        private BigDecimal count;
//
//        private BigDecimal freeCountBasicRate = BigDecimal.ZERO;
//
//        private BigDecimal freeCountRate = BigDecimal.ZERO;
//
//        public TotalResItem(Res res, ResUnit resUnit, BigDecimal money, BigDecimal rebate, BigDecimal count) {
//            this.res = res;
//            this.resUnit = resUnit;
//            this.money = money;
//            this.rebate = rebate;
//            this.count = count;
//        }
//
//        public void add(OrderItem other) {
//            if (!isSameItem(other))
//                throw new IllegalArgumentException("not same");
//            count = count.add(other.getUseUnitCount());
//        }
//
//        public boolean isSameItem(OrderItem other) {
//            return res.equals(other.getRes()) && resUnit.getId().equals(other.getResUnit().getId())
//                    && (other.getMoney().compareTo(money) == 0) &&
//                    (other.getRebate().compareTo(rebate) == 0);
//        }
//
//        public Res getRes() {
//            return res;
//        }
//

//
//        public BigDecimal getTotalPrice() {
//
//            return DataFormat.halfUpCurrency(count.multiply(getMoney().multiply(getRebate().divide(new BigDecimal("100"), 20, BigDecimal.ROUND_HALF_UP))));
//        }
//
//        public BigDecimal getMoney() {
//            return money;
//        }
//
//        public void setMoney(BigDecimal money) {
//            this.money = money;
//        }
//
//        public ResUnit getResUnit() {
//            return resUnit;
//        }
//
//        public void setResUnit(ResUnit resUnit) {
//            this.resUnit = resUnit;
//        }
//
//        public void setRebate(BigDecimal rebate) {
//            this.rebate = rebate;
//        }
//
//        public BigDecimal getRebate() {
//            return rebate;
//        }
//
//        public boolean isPresentation() {
//            return false;
//        }
//
//        public void setPresentation(boolean presentation) {
//        }
//
//        public BigDecimal getCount() {
//            return count;
//        }
//
//        public void setCount(BigDecimal count) {
//            this.count = count;
//        }
//
//        public StoreRes getStoreRes() {
//            return null;
//        }
//
//        public void setStoreRes(StoreRes storeRes) {
//        }
//
//        public BigDecimal getFreeCountRate() {
//            return freeCountRate;
//        }
//
//        public void setFreeCountRate(BigDecimal freeCountRate) {
//            this.freeCountRate = freeCountRate;
//        }
//
//        public BigDecimal getFreeCountBasicRate() {
//            return freeCountBasicRate;
//        }
//
//        public void setFreeCountBasicRate(BigDecimal freeCountBasicRate) {
//            this.freeCountBasicRate = freeCountBasicRate;
//        }
//    }

}
