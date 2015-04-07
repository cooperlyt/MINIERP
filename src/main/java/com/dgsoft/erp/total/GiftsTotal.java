package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.erp.ErpEntityLoader;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.total.data.OrderItemTotal;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.util.List;

/**
 * Created by cooper on 4/7/15.
 */
@Name("giftsTotal")
public class GiftsTotal {


    @In(create = true)
    private ErpEntityLoader erpEntityLoader;

    private boolean contaionZero = false;

    private SearchDateArea searchDateArea = new SearchDateArea();

    public boolean isContaionZero() {
        return contaionZero;
    }

    public void setContaionZero(boolean contaionZero) {
        this.contaionZero = contaionZero;
    }

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public void setSearchDateArea(SearchDateArea searchDateArea) {
        this.searchDateArea = searchDateArea;
    }


    public List<TotalDataGroup<OrderItemTotal.OrderItemResKey,OrderItem,OrderItemTotal>> getResultTotal(){

        String sql = "select orderItem from OrderItem orderItem where orderItem.needRes.customerOrder.canceled = false and orderItem.needRes.customerOrder.payType <> 'PRICE_CHANGE' "
                + searchDateArea.genConditionSQL("orderItem.needRes.customerOrder.createDate",true);

        if (contaionZero){
            sql += " and (orderItem.presentation = true  or orderItem.totalMoney = 0) ";
        }else{
            sql += " and orderItem.presentation = true";
        }

        return TotalDataGroup.groupBy(searchDateArea.setQueryParam(erpEntityLoader.getEntityManager().createQuery(sql, OrderItem.class)).getResultList(),new OrderItemTotal.ResOrderItemGroupStrategy(),new OrderItemTotal.FormatOrderItemGroupStrategy());
    }

}
