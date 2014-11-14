package com.dgsoft.erp.total.data;

import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResPriceEntity;
import com.dgsoft.erp.total.ResFormatGroupStrategy;
import com.dgsoft.erp.total.ResPriceGroupStrategy;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by cooper on 11/15/14.
 */
public class OrderItemTotal extends ResPriceTotal {


    public static OrderItemTotal total(Collection<OrderItem> datas) {
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
        needAddCount = needAddCount.add(other.getNeedAddCount());
        needCount = needCount.add(other.getNeedCount());
    }


    @Deprecated
    public BigDecimal getNeedMoney() {
        return BigDecimal.ZERO;
    }

    @Deprecated
    public BigDecimal getNeedAddMoney() {
        return BigDecimal.ZERO;
    }


    public static class FormatOrderItemGroupStrategy extends ResFormatGroupStrategy<OrderItem, ResPriceTotal> {
        @Override
        public ResPriceTotal totalGroupData(Collection<OrderItem> datas) {
            return ResPriceTotal.total(datas);
        }
    }


    public static class ResOrderItemGroupStrategy extends ResPriceGroupStrategy<OrderItem,ResPriceTotal> {
        @Override
        public ResPriceTotal totalGroupData(Collection<OrderItem> datas) {
            return ResPriceTotal.total(datas);
        }
    }

    public static class StoreResOrderItemGroupStrategy implements TotalGroupStrategy<StoreRes, OrderItem, ResPriceTotal> {

        @Override
        public StoreRes getKey(OrderItem e) {
            return e.getStoreRes();
        }

        @Override
        public ResPriceTotal totalGroupData(Collection<OrderItem> datas) {
            return ResPriceTotal.total(datas);
        }
    }


}
