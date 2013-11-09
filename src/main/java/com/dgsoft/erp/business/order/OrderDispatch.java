package com.dgsoft.erp.business.order;

import com.dgsoft.erp.model.Dispatch;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.Store;
import org.jboss.seam.annotations.Name;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/9/13
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("orderDispatch")
public class OrderDispatch extends OrderTaskHandle {

    public static class ResOrderItem {

        private OrderItem orderItem;

        private List<Stock> stockList;

    }

    private List<OrderItem> storeResOrderItems;

    private List<ResOrderItem> resOrderItemList;

    private List<Dispatch> dispatchList;

   // private List<Di>


}
