package com.dgsoft.erp.action;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.SetLinkList;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.total.SameFormatResGroupStrategy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 10:11 AM
 */
@Name("orderHome")
public class OrderHome extends ErpEntityHome<CustomerOrder> {


    protected SetLinkList<ResSaleRebate> resSaleRebates;

    public void reCreateResSaleBebates() {
        resSaleRebates.clear();
    }


    @Override
    protected void initInstance() {
        super.initInstance();
        resSaleRebates = new SetLinkList<ResSaleRebate>(getInstance().getResSaleRebates());
    }

    public SetLinkList<ResSaleRebate> getResSaleRebates() {
        return resSaleRebates;
    }

    @In(create = true)
    protected Map<String, String> messages;

    public List<OrderItem> getOrderItemByStatus(EnumSet<OrderItem.OrderItemStatus> statuses) {
        List<OrderItem> result = new ArrayList<OrderItem>();
        for (NeedRes needRes : getInstance().getNeedReses()) {
            for (OrderItem item : needRes.getOrderItems()) {
                if (statuses.contains(item.getStatus())) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public List<OweOut> getNoAddOweItems() {
        List<OweOut> result = new ArrayList<OweOut>();
        for (NeedRes needRes : getInstance().getNeedReses()) {
            for (Dispatch dispatch : needRes.getDispatches()) {
                for (OweOut oweOut : dispatch.getOweOuts()) {
                    if (!oweOut.isAdd()) {
                        result.add(oweOut);
                    }
                }
            }
        }
        return result;
    }

    public List<OrderItem> getAllCompleteOrderItem() {
        return getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.COMPLETED));
    }

    public List<OrderItem> getAllOrderItem() {
        List<OrderItem> result = getOrderItemByStatus(EnumSet.allOf(OrderItem.OrderItemStatus.class));
        Collections.sort(result, new Comparator<OrderItem>() {
            @Override
            public int compare(OrderItem o1, OrderItem o2) {
                int result = Integer.valueOf(o1.getStatus().ordinal()).compareTo(o2.getStatus().ordinal());
                if (result == 0) {
                    result = o1.getStoreRes().compareTo(o2.getStoreRes());
                }
                return result;
            }
        });
        return result;
    }

    public BigDecimal getCompleteOrderItemMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.COMPLETED))) {
            if (item.getTotalMoney() != null)
                result = result.add(item.getTotalMoney());
        }
        return result;
    }


    private boolean useScaleRebate = false;

    public boolean isUseScaleRebate() {
        return useScaleRebate;
    }

    public void setUseScaleRebate(boolean useScaleRebate) {
        this.useScaleRebate = useScaleRebate;
    }

    public void setOrderRebate(BigDecimal rebate) {
        if (useScaleRebate) {
            calcTotalResMoney();
            if (DataFormat.isEmpty(rebate)) {
                getInstance().setTotalRebateMoney(getInstance().getResMoney());
            } else {
                getInstance().setTotalRebateMoney(
                        getInstance().getResMoney().subtract(getInstance().getResMoney().
                                multiply(rebate.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP)))
                );
            }
        } else {
            getInstance().setTotalRebateMoney(rebate);
        }
        calcMoneys();
    }

    public BigDecimal getOrderRebate() {
        if (useScaleRebate) {
            return getInstance().getTotalRebate();
        } else {
            return getInstance().getTotalRebateMoney();
        }
    }

    public void setInputOrderMoney(BigDecimal money) {
        getInstance().setMoney(money);
        calcTotalResMoney();
        getInstance().setTotalRebateMoney(getInstance().getResMoney().subtract(money));
    }

    public BigDecimal getInputOrderMoney() {
        return getInstance().getMoney();
    }

    public String getToastMessages() {

        if (!isIdDefined()) {
            return "";
        }

        StringBuffer result = new StringBuffer();
        result.append(messages.get("OrderCode") + ":" + getInstance().getId() + "\n");

        result.append(messages.get("Customer") + ":" + getInstance().getCustomer().getName());
        result.append("\n");
        result.append(messages.get("order_field_reveiveContact") + ":" + getLastNeedRes().getReceivePerson());
        result.append(" ");
        result.append(messages.get("order_field_reveiveTel") + " " + getLastNeedRes().getReceiveTel());
        result.append("\n");
        result.append(messages.get("address") + ":" + getLastNeedRes().getAddress());
        result.append("\n");

        result.append(messages.get(getInstance().getPayType().name()) + "   ");

        if (getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
            result.append(DecimalFormat.getCurrencyInstance(Locale.CHINA).
                    format(getInstance().getMoney()) + "   ");
        }

        if (getLastNeedRes().isFareByCustomer()) {
            result.append(messages.get("order_fare_by_customer"));
        } else {
            result.append(messages.get("order_fare_by_company"));
        }


        for (TotalDataGroup<Store, OrderItem> group : getDispatchItemGroups()) {

            TotalDataGroup.sort(group, new Comparator<OrderItem>() {
                @Override
                public int compare(OrderItem o1, OrderItem o2) {
                    if (ResHelper.instance().sameFormat(o1.getFormats(), o2.getFormats())) {
                        if (o1.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                            return o1.getStoreRes().getFloatConversionRate().compareTo(o2.getFloatConvertRate());
                        }
                    }
                    return o1.getStoreRes().compareTo(o2.getStoreRes());
                }
            });

            result.append("\n" + group.getKey().getName());
            for (TotalDataGroup<?, OrderItem> sg : group.getChildGroup()) {
                result.append("\n\t" + sg.getKey().toString());
                for (OrderItem item : sg.getValues()) {

                    result.append("\n\t\t");
                    if (item.getStoreRes().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                        result.append(DataFormat.format(item.getStoreRes().getFloatConversionRate(),
                                item.getStoreRes().getRes().getUnitGroup().getFloatConvertRateFormat()) + item.getStoreRes().getRes().getUnitGroup().getName());
                    }
                    result.append(" " + DataFormat.format(item.getCountByResUnit(item.getRes().getResUnitByInDefault()), item.getRes().getResUnitByInDefault().getCountFormate()));
                    result.append(item.getRes().getResUnitByInDefault().getName());
                    if (item.getMemo() != null)
                        result.append(" " + item.getMemo());
                }
            }
            result.append("\n");
        }


        //List<OrderItem> createdItems = ;


        List<TotalDataGroup<SameFormatResGroupStrategy.StoreResFormatKey, OrderItem>> createdItems = TotalDataGroup.groupBy(getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.CREATED)),
                new SameFormatResGroupStrategy<OrderItem>());
        if (!createdItems.isEmpty()) {
            result.append("\n" + messages.get("no_dispatch_order_items"));
        }


        for (TotalDataGroup<?, OrderItem> sg : createdItems) {
            TotalDataGroup.sort(sg, new Comparator<OrderItem>() {
                @Override
                public int compare(OrderItem o1, OrderItem o2) {
                    if (ResHelper.instance().sameFormat(o1.getFormats(), o2.getFormats())) {
                        if (o1.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                            return o1.getStoreRes().getFloatConversionRate().compareTo(o2.getFloatConvertRate());
                        }
                    }
                    return o1.getStoreRes().compareTo(o2.getStoreRes());
                }
            });

            result.append("\n\t" + sg.getKey().toString());
            for (OrderItem item : sg.getValues()) {

                result.append("\n\t\t");
                if (item.getStoreRes().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                    result.append(DataFormat.format(item.getStoreRes().getFloatConversionRate(),
                            item.getStoreRes().getRes().getUnitGroup().getFloatConvertRateFormat()) + item.getStoreRes().getRes().getUnitGroup().getName());
                }
                result.append("  " + DataFormat.format(item.getCountByResUnit(item.getRes().getResUnitByInDefault()), item.getRes().getResUnitByInDefault().getCountFormate()));
                result.append(item.getRes().getResUnitByInDefault().getName());
                if (item.getMemo() != null)
                    result.append(" " + item.getMemo());
            }
        }

//        List<OweOut> oweOutItems = getNoAddOweItems();
//        if (!oweOutItems.isEmpty()) {
//            result.append(messages.get("sub_overly_oper") + "\n");
//        }
//        for (OweOut oweOut : oweOutItems) {
//            result.append("\t" + resHelper.generateStoreResTitle(oweOut.getStoreRes()) + " ");
//            result.append("  " + DataFormat.format(oweOut.getCountByResUnit(oweOut.getRes().getResUnitByInDefault()), oweOut.getRes().getResUnitByInDefault().getCountFormate()));
//            result.append(oweOut.getRes().getResUnitByInDefault().getName());
//        }


        return result.toString();
    }

    public List<TotalDataGroup<Store, OrderItem>> getDispatchItemGroups() {


        return TotalDataGroup.groupBy(getOrderItemByStatus(EnumSet.of(OrderItem.OrderItemStatus.DISPATCHED)),
                new TotalGroupStrategy<Store, OrderItem>() {
                    @Override
                    public Store getKey(OrderItem orderItem) {
                        return orderItem.getDispatch().getStore();
                    }

                    @Override
                    public Object totalGroupData(Collection<OrderItem> datas) {
                        return null;
                    }
                }, new SameFormatResGroupStrategy<OrderItem>()
        );
    }

    public void calcTotalResMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : getOrderItemByStatus(
                EnumSet.allOf(OrderItem.OrderItemStatus.class))) {
            if (item.getTotalMoney() != null)
                result = result.add(item.getTotalMoney());
        }
        getInstance().setResMoney(result);
    }


    public BigDecimal getEarnestScale() {
        if (getInstance().getEarnest() == null) {
            return BigDecimal.ZERO;
        }
        return getInstance().getEarnest().divide(getInstance().getMoney(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
    }

    public void calcMoneys() {

        setUseScaleRebate(false);
        BigDecimal result = BigDecimal.ZERO;
        for (ResSaleRebate item : resSaleRebates) {
            item.calcMoney();
            result = result.add(item.getRebateMoney());
        }
        getInstance().setTotalRebateMoney(result);


        calcTotalResMoney();

        getInstance().setMoney(getInstance().getResMoney().subtract(getInstance().getTotalRebateMoney()));
        //calcReceiveMoney();
    }

//    public void calcReceiveMoney(){
//        if (getInstance().isAllStoreOut()){
//            BigDecimal result = BigDecimal.ZERO;
//            for (AccountOper ap: getInstance().getAccountOpers()){
//                switch (ap.getOperType()){
//
//                    case ORDER_SAVINGS:
//                        result = result.add(ap.getAccountsReceivable()).add(ap.getProxcAccountsReceiveable()).add(ap.getAdvanceReceivable());
//                        break;
//                    case ORDER_FREE:
//                        result = result.add(ap.getAccountsReceivable());
//                        break;
//                    case ORDER_PAY:
//                        result = result.add(ap.getAdvanceReceivable());
//                        break;
//                    case MONEY_BACK_TO_PREPARE:
//                    case MONEY_BACK_TO_CUSTOMER:
//                        break;
//                    default:
//                        throw new IllegalArgumentException("unkonw operType:" + ap.getOperType());
//                }
//            }
//
//            getInstance().setReceiveMoney(result);
//        }else{
//            getInstance().setReceiveMoney(getInstance().getAdvanceMoney());
//        }
//    }

    public BigDecimal getAllFare() {
        BigDecimal result = BigDecimal.ZERO;

        for (NeedRes nr : getInstance().getNeedReses()) {
            result = result.add(nr.getTotalFare());
        }

        return result;
    }

    public BigDecimal getTotalFare() {
        BigDecimal result = BigDecimal.ZERO;
        for (NeedRes nr : getInstance().getNeedReses()) {
            result = result.add(nr.getTotalFare());
        }
        return result;
    }

    public NeedRes getLastNeedRes() {
        NeedRes result = null;
        for (NeedRes nr : getInstance().getNeedReses()) {
            if ((result == null) ||
                    (result.getCreateDate().getTime() < nr.getCreateDate().getTime()))
                result = nr;
        }
        return result;
    }

    public Date getLastShipDate() {
        Date result = null;
        for (Dispatch dispatch : getLastNeedRes().getDispatches()) {
            if ((result == null) ||
                    ((dispatch.getSendTime() != null) && (dispatch.getSendTime().compareTo(result) > 0))) {
                result = dispatch.getSendTime();
            }
        }
        return result;
    }


    public BigDecimal getLastResTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : getLastNeedRes().getOrderItems()) {
            if (item.getTotalMoney() != null)
                result = result.add(item.getTotalMoney());
        }
        return result;
    }

    public NeedRes getMasterNeedRes() {
        for (NeedRes nr : getInstance().getNeedReses()) {
            if (nr.getType().equals(NeedRes.NeedResType.ORDER_SEND)) {
                return nr;
            }
        }
        return null;
    }

    public boolean isHavePayFee() {
        for (OrderFee fee : getInstance().getOrderFees()) {
            if (!fee.isPay()) {
                return true;
            }
        }

        return false;
    }


    public boolean isZeroPriceOrder() {
        return !(getInstance().getMoney().compareTo(BigDecimal.ZERO) > 0);
    }

    @DataModel
    public List<Map.Entry<StoreRes, StoreResCount>> getAllShipStoreResEntrySet() {
        List<Map.Entry<StoreRes, StoreResCount>> result = new ArrayList<Map.Entry<StoreRes, StoreResCount>>(allShipStoreReses().entrySet());

        Collections.sort(result, new Comparator<Map.Entry<StoreRes, StoreResCount>>() {
            @Override
            public int compare(Map.Entry<StoreRes, StoreResCount> o1, Map.Entry<StoreRes, StoreResCount> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return result;
    }

    public BigDecimal getTotalItemMiddleMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (NeedRes needRes : getInstance().getNeedReses())
            for (OrderItem orderItem : needRes.getOrderItems()) {
                if ((orderItem.getMiddleMoneyCalcType() != null) &&
                        (orderItem.getMiddleMoney() != null))
                    result = result.add(orderItem.getMiddleMoney());
            }

        return result;
    }

    public BigDecimal getTotalOrderFeeMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderFee orderFee : getInstance().getOrderFees()) {
            result = result.add(orderFee.getMoney());
        }
        return result;
    }


    public Map<StoreRes, StoreResCount> allShipStoreReses() {
        return getInstance().getAllShipStoreReses();
    }

    public boolean isAnyOneStoreOut() {
        for (NeedRes needRes : getInstance().getNeedReses()) {
            for (Dispatch dispatch : needRes.getDispatches()) {
                if (dispatch.isStoreOut()) {
                    return true;
                }
            }
        }
        return false;
    }


    public OrderItem getFirstResOrderItem(StoreRes storeRes) {
        for (NeedRes needRes : getInstance().getNeedReses()) {
            for (OrderItem oi : needRes.getOrderItems()) {
                if (oi.getStoreRes().equals(storeRes) && (oi.getMoney().compareTo(BigDecimal.ZERO) > 0)) {
                    return oi;
                }
            }
        }
        return null;
    }


//    public  getFirstPrice(StoreRes storeRes){
//
//        for (NeedRes needRes: getInstance().getNeedReses()){
//            for (OrderItem orderItem: needRes.getOrderItems()){
//                if (orderItem.getStoreRes().equals(storeRes)){
//                    return orderItem.getPrice();
//                }
//            }
//        }
//        return null;
//    }

    public boolean isComplete() {
        return !getInstance().isCanceled() && getInstance().isAllStoreOut() && getInstance().isMoneyComplete() && getInstance().isResReceived();
    }

    public boolean isHaveShip() {
        for (NeedRes needRes : getInstance().getNeedReses()) {
            for (Dispatch dispatch : needRes.getDispatches()) {
                if (dispatch.getDeliveryType().isShip()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Deprecated
    public boolean isHaveOverlyOut() {
        return !getInstance().isAllStoreOut();
    }


    public boolean isInAccount() {
        if (getInstance().isAllStoreOut()) {
            for (AccountOper ao : getInstance().getAccountOpers()) {
                if (ao.getSaleCertificate() != null) {
                    return true;
                }
            }
        }
        return false;
    }
}
