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

    public ResPriceTotal getTotalPrice(){
//        BigDecimal result = BigDecimal.ZERO;
//        for (StoreResPriceEntity item: getResultList()){
//            result = result.add(item.getTotalMoney());
//        }
        return ResPriceTotal.total(getResultList());
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

    @Deprecated
    public TotalDataGroup<?, StoreResPriceEntity,ResPriceTotal> getCustomerResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(), new CustomerGroupStrategy(),
                new ResPriceTotal.ResMoneyGroupStrategy<StoreResPriceEntity>(),
                new ResPriceTotal.FormatMoneyGroupStrategy<StoreResPriceEntity>());
    }


    public List<TotalDataGroup<Customer,StoreResPriceEntity,TotalDataGroup.SingleTotalData<BigDecimal>>> getCustomerResultGroups(){
        if(customerResCondition.isContainStoreOut() && customerResCondition.isContainResBack()){

            return TotalDataGroup.groupBy(getResultList(),
                    new TotalGroupStrategy<Customer, StoreResPriceEntity, TotalDataGroup.SingleTotalData<BigDecimal>>() {
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
                        public TotalDataGroup.SingleTotalData totalGroupData(Collection<StoreResPriceEntity> datas) {
                            return null;
                        }
                    }
                    ,

                    new TotalGroupStrategy<TotalDataGroup.StringKey, StoreResPriceEntity, TotalDataGroup.SingleTotalData<BigDecimal>>() {
                        @Override
                        public TotalDataGroup.StringKey getKey(StoreResPriceEntity storeResPriceEntity) {
                            if ((storeResPriceEntity instanceof OrderItem) &&
                                    ((OrderItem)storeResPriceEntity).isFree()) {
                                return new TotalDataGroup.StringKey("free");

                            }
                            return new TotalDataGroup.StringKey(storeResPriceEntity.getType()) ;

                        }

                        @Override
                        public TotalDataGroup.SingleTotalData<BigDecimal> totalGroupData(Collection<StoreResPriceEntity> datas) {
                            return new TotalDataGroup.SingleTotalData<BigDecimal>(totalMoney(datas)) ;
                        }
                    }
                    );
        }else{
            return TotalDataGroup.groupBy(getResultList(),
                    new TotalGroupStrategy<Customer, StoreResPriceEntity, TotalDataGroup.SingleTotalData<BigDecimal>>() {
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
                        public TotalDataGroup.SingleTotalData<BigDecimal> totalGroupData(Collection<StoreResPriceEntity> datas) {
                            return new TotalDataGroup.SingleTotalData<BigDecimal>(totalMoney(datas)) ;
                        }
                    }

            );
        }

    }

    private BigDecimal totalMoney(Collection<StoreResPriceEntity> datas){
        BigDecimal result = BigDecimal.ZERO;
        for (StoreResPriceEntity item: datas){
            if (! item.isFree())
            result = result.add(item.getTotalMoney());
        }
        return result;
    }

    public TotalDataGroup<?, StoreResPriceEntity,ResPriceTotal> getDayResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(), new TotalGroupStrategy<TotalDataGroup.DateKey, StoreResPriceEntity,ResPriceTotal>() {
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
            public ResPriceTotal totalGroupData(Collection<StoreResPriceEntity> datas) {
                return ResPriceTotal.total(datas);
            }
        }, new CustomerGroupStrategy(), new ResPriceTotal.ResMoneyGroupStrategy<StoreResPriceEntity>(), new ResPriceTotal.FormatMoneyGroupStrategy<StoreResPriceEntity>());
    }


    private static class CustomerGroupStrategy implements TotalGroupStrategy<Customer, StoreResPriceEntity,ResPriceTotal> {
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
        public ResPriceTotal totalGroupData(Collection<StoreResPriceEntity> datas) {
            return ResPriceTotal.total(datas);
        }
    }



}
