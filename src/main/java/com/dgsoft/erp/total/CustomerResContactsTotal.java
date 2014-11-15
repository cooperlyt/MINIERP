package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.BackItem;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.api.StoreResPriceEntity;
import com.dgsoft.erp.total.data.ResPriceTotal;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 15/04/14
 * Time: 14:16
 */
@Name("customerResContactsTotal")
public class CustomerResContactsTotal {

    @In(create = true)
    private BackResContactsTotal backResContactsTotal;

    @In(create = true)
    private OrderResContactsTotal orderResContactsTotal;

    @In(create = true)
    private CustomerResCondition customerResCondition;

    private boolean onlyModel = false;

    private boolean onlyStoreOut = true;

    private boolean groupByDay = true;

    public boolean isGroupByDay() {
        return groupByDay;
    }

    public void setGroupByDay(boolean groupByDay) {
        this.groupByDay = groupByDay;
    }

    public boolean isOnlyStoreOut() {
        return onlyStoreOut;
    }

    public void setOnlyStoreOut(boolean onlyStoreOut) {
        this.onlyStoreOut = onlyStoreOut;
    }

    public boolean isOnlyModel() {
        return onlyModel;
    }

    public void setOnlyModel(boolean onlyModel) {
        this.onlyModel = onlyModel;
    }

    public RebateMoney getTotalPrice(){
//        BigDecimal result = BigDecimal.ZERO;
//        for (StoreResPriceEntity item: getResultList()){
//            result = result.add(item.getTotalMoney());
//        }
        return genRebateMoney(getResultList());
    }


    public List<StoreResPriceEntity> getResultList() {
        final List<StoreResPriceEntity> result = new ArrayList<StoreResPriceEntity>();
        if (!onlyModel) {

            if (customerResCondition.isContainStoreOut()) {
                result.addAll(orderResContactsTotal.getResultList());
            }
            if (customerResCondition.isContainResBack()) {
                result.addAll(backResContactsTotal.getResultList());
            }
        }else{
            if (onlyStoreOut){
                customerResCondition.setContainStoreOut(true);
                customerResCondition.setContainResBack(false);
                result.addAll(orderResContactsTotal.getResultList());
            }else{
                customerResCondition.setContainStoreOut(false);
                customerResCondition.setContainResBack(true);
                result.addAll(backResContactsTotal.getResultList());
            }
        }
        Collections.sort(result, new SaleBackItemComparator());

        return result;
    }

    public TotalDataGroup<?, StoreResPriceEntity,?> getCustomerResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(), new CustomerGroupStrategy(),
                new ResPriceTotal.ResMoneyGroupStrategy<StoreResPriceEntity>(),
                new ResPriceTotal.FormatMoneyGroupStrategy<StoreResPriceEntity>());
    }

    public TotalDataGroup<?, StoreResPriceEntity,?> getDayResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(), new TotalGroupStrategy<TotalDataGroup.DateKey, StoreResPriceEntity,RebateMoney>() {
            @Override
            public TotalDataGroup.DateKey getKey(StoreResPriceEntity storeResPriceEntity) {
                if (storeResPriceEntity instanceof OrderItem) {
                    return new TotalDataGroup.DateKey(DataFormat.halfTime(((OrderItem) storeResPriceEntity).getDispatch().getStockChange().getOperDate()));
                } else if (storeResPriceEntity instanceof BackItem) {
                    return new TotalDataGroup.DateKey(DataFormat.halfTime(((BackItem) storeResPriceEntity).getDispatch().getStockChange().getOperDate()));
                } else
                    return null;
            }

            @Override
            public RebateMoney totalGroupData(Collection<StoreResPriceEntity> datas) {
                return genRebateMoney(datas);
            }
        }, new CustomerGroupStrategy(), new ResPriceTotal.ResMoneyGroupStrategy<StoreResPriceEntity>(), new ResPriceTotal.FormatMoneyGroupStrategy<StoreResPriceEntity>());
    }


    private static class CustomerGroupStrategy implements TotalGroupStrategy<Customer, StoreResPriceEntity,RebateMoney> {
        @Override
        public Customer getKey(StoreResPriceEntity storeResPriceEntity) {
            if (storeResPriceEntity instanceof OrderItem) {
                return ((OrderItem) storeResPriceEntity).getNeedRes().getCustomerOrder().getCustomer();
            } else if (storeResPriceEntity instanceof BackItem) {
                return ((BackItem) storeResPriceEntity).getOrderBack().getCustomer();
            } else
                return null;
        }

        @Override
        public RebateMoney totalGroupData(Collection<StoreResPriceEntity> datas) {
            return genRebateMoney(datas);
        }
    }

    private static RebateMoney genRebateMoney(Collection<StoreResPriceEntity> datas){
        Map<String,BigDecimal> orderRebateMap = new HashMap<String, BigDecimal>();

        BigDecimal money = BigDecimal.ZERO;
        for(StoreResPriceEntity item: datas){
            if (item instanceof OrderItem) {
                CustomerOrder order = ((OrderItem)item).getNeedRes().getCustomerOrder();
                if (orderRebateMap.get(order.getId()) == null){
                    orderRebateMap.put(order.getId(),order.getTotalRebateMoney());
                }
            }
            money = money.add(item.getTotalMoney());
        }

        BigDecimal rebate = BigDecimal.ZERO;
        for (BigDecimal r: orderRebateMap.values()){
            rebate = rebate.add(r);
        }

        return new RebateMoney(money,rebate);
    }

    public static class RebateMoney implements TotalDataGroup.GroupTotalData{

        private BigDecimal money;

        private BigDecimal rebate;

        public RebateMoney(BigDecimal money, BigDecimal rebate) {
            this.money = money;
            this.rebate = rebate;
        }

        public RebateMoney(BigDecimal money) {
            this.money = money;
            rebate = BigDecimal.ZERO;
        }

        public BigDecimal getMoney() {
            return money;
        }

        public BigDecimal getRebate() {
            return rebate;
        }

        public BigDecimal getCalcMoney(){
            return money.subtract(rebate);
        }
    }

}
