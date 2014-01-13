package com.dgsoft.erp.action;

import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/13/13
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("resHelper")
@AutoCreate
@Scope(ScopeType.STATELESS)
public class ResHelper {


    @In
    private DictionaryWord dictionary;

    public String generateStoreResTitle(StoreRes storeRes) {

        String result = storeRes.getRes().getName() + "(" + storeRes.getRes().getCode() + ")  ";

        for (Format format : storeRes.getFormatList()) {
            result += " " + format.getFormatDefine().getName() + " : ";
            if (format.getFormatDefine().getDataType().equals(FormatDefine.FormatType.WORD)) {
                result += dictionary.getWordValue(format.getFormatValue());
            } else {
                result += format.getFormatValue();
            }
        }


        if (storeRes.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            result += BigDecimalFormat.format(storeRes.getFloatConversionRate(),
                    storeRes.getRes().getUnitGroup().getFloatConvertRateFormat()).toString();
            result += storeRes.getRes().getUnitGroup().getName();
        }


        return result;
    }

    public static Map<StoreRes, ResCount> totalStockChangeItem(List<StockChangeItem> items) {
        Map<StoreRes, ResCount> result = new HashMap<StoreRes, ResCount>();
        for (StockChangeItem item : items) {
            ResCount count = result.get(item.getStoreRes());
            if (count == null) {
                result.put(item.getStoreRes(), item.getResCount());
            } else {
                count.add(item.getResCount());
            }
        }
        return result;
    }

    public List<OrderItem> totalOrderItem(List<OrderItem> items) {
        List<OrderItem> result = new ArrayList<OrderItem>();
        for (OrderItem item : items) {
            boolean find = false;
            for (OrderItem totalItem : result) {
                if ((totalItem.isStoreResItem() == item.isStoreResItem()) &&
                        totalItem.getMoneyUnit().getId().equals(item.getMoneyUnit().getId()) &&
                        (totalItem.getMoney().compareTo(item.getMoney()) == 0) &&
                        (totalItem.getRebate().compareTo(item.getRebate()) == 0)) {
                    if (totalItem.isStoreResItem()) {
                        if (totalItem.getStoreRes().equals(item.getStoreRes())) {
                            find = true;
                            totalItem.addCount(item);
                            break;
                        }
                    } else if (totalItem.getRes().equals(item.getRes())) {
                        find = true;
                        totalItem.setCount(totalItem.getCount().add(item.getCount()));
                        break;
                    }
                }
            }
            if (!find) {
                if (item.isStoreResItem()) {
                    result.add(new OrderItem(item.getStoreRes(),
                            item.getMoneyUnit(), item.getCount(), item.getMoney(), item.getRebate()));
                } else {
                    result.add(new OrderItem(item.getRes(), item.getMoneyUnit(), item.getCount(), item.getMoney(), item.getRebate()));
                }

            }
        }
        return result;
    }

}
