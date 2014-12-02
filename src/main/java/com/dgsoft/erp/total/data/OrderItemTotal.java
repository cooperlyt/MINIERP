package com.dgsoft.erp.total.data;

import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResPriceEntity;
import com.dgsoft.erp.total.ResFormatGroupStrategy;
import com.dgsoft.erp.total.ResPriceGroupStrategy;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by cooper on 11/15/14.
 */
public class OrderItemTotal extends ResPriceTotal {


    public static OrderItemTotal totalOrderItems(Collection<OrderItem> datas) {
        OrderItemTotal result = null;
        for (OrderItem data : datas) {
            if (result == null) {
                result = new OrderItemTotal(data.getRes());
            }
            result.putItem(data);
        }
        return result;
    }


    private BigDecimal needCount = BigDecimal.ZERO;

    private BigDecimal needAddCount = BigDecimal.ZERO;

    public OrderItemTotal(Res res) {
        super(res);
    }

    public BigDecimal getNeedCount() {
        return needCount;
    }

    public BigDecimal getNeedAddCount() {
        return needAddCount;
    }

    public void putItem(OrderItem other) {
        super.putItem(other);
        if(getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            needAddCount = needAddCount.add(other.getNeedAddCount());
            needCount = needCount.add(other.getNeedCount());
        }
    }


    @Deprecated
    public BigDecimal getNeedMoney() {
        return BigDecimal.ZERO;
    }

    @Deprecated
    public BigDecimal getNeedAddMoney() {
        return BigDecimal.ZERO;
    }


    public static class FormatOrderItemGroupStrategy extends ResFormatGroupStrategy<OrderItem, OrderItemTotal> {
        @Override
        public OrderItemTotal totalGroupData(Collection<OrderItem> datas) {
            return totalOrderItems(datas);
        }
    }


    public static class ResOrderItemGroupStrategy implements TotalGroupStrategy<OrderItemResKey,OrderItem,OrderItemTotal> {
        @Override
        public OrderItemResKey getKey(OrderItem orderItem) {
            Res res = orderItem.getStoreRes().getRes();
            ResUnit resUnit = orderItem.getResUnit();
            ResSaleRebate resSaleRebate = null;
            for(ResSaleRebate rebate: orderItem.getNeedRes().getCustomerOrder().getResSaleRebates()){
                if (rebate.getRes().equals(res) && rebate.getResUnit().equals(resUnit)){
                    resSaleRebate = rebate;
                    break;
                }
            }

            return new OrderItemResKey(res,resUnit,resSaleRebate);
        }

        @Override
        public OrderItemTotal totalGroupData(Collection<OrderItem> datas) {
            return totalOrderItems(datas);
        }
    }


    public static class OrderItemResKey extends ResPriceGroupStrategy.PriceItemKey{

        private ResSaleRebate resSaleRebate;

        public OrderItemResKey(Res res, ResUnit resUnit, ResSaleRebate resSaleRebate) {
            super(res, resUnit);
            this.resSaleRebate = resSaleRebate;
        }

        public ResSaleRebate getResSaleRebate() {
            return resSaleRebate;
        }
    }


}
