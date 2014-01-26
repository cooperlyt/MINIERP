package com.dgsoft.erp.action;

import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.common.system.model.Word;
import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Logging;
import org.jbpm.JbpmContext;

import java.math.BigDecimal;
import java.util.*;

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
            result += " " + BigDecimalFormat.format(storeRes.getFloatConversionRate(),
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


    public static boolean sameFormat(Collection<Format> formatList1, Collection<Format> formatList2) {
        if (formatList1.size() != formatList2.size()) {
            return false;
        }

        Map<FormatDefine, Format> format1Values = new HashMap<FormatDefine, Format>();
        for (Format format : formatList1) {
            format1Values.put(format.getFormatDefine(), format);
        }
        for (Format format : formatList2) {
            Format format1 = format1Values.get(format.getFormatDefine());
            if ((format1 == null) || !format1.equals(format)) {
                return false;
            }
        }
        return true;
    }

    public String formatDisplayValue(Format format){
        if (format != null)
        switch (format.getFormatDefine().getDataType()){
            case INTEGER:
                return format.getFormatDefine().getName() + " " + format.getIntValue();
            case WORD:
                return format.getFormatDefine().getName() + " " + dictionary.getWordValue(format.getFormatValue()) ;
            case FLOAT:
                return format.getFormatDefine().getName() + " " + format.getFloatValue();
        }

        return "";
    }

    public String genStoreResCode(String resCode, List<Format> formats, String floatConvertRate){
        String result = resCode.trim() + "-";
        for (Format format: formats){
            if (format.getFormatDefine().getDataType().equals(FormatDefine.FormatType.WORD)) {
                Word word = dictionary.getWord(format.getFormatValue());
                if (word != null)
                    result += word.getKey();
            }
            if (format.getFormatDefine().getDataType().equals(FormatDefine.FormatType.INTEGER)){
                result += format.getIntValue();
            }
            if (format.getFormatDefine().getDataType().equals(FormatDefine.FormatType.FLOAT)){
                result += format.getFloatValue();
            }
        }
        if (floatConvertRate != null){
            result += floatConvertRate;
        }

        return result;
    }

    public String genStoreResCode(StoreRes storeRes) {
        return genStoreResCode(storeRes.getRes().getCode(),storeRes.getFormatList(),
                (storeRes.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT))?
                BigDecimalFormat.format(storeRes.getFloatConversionRate(),storeRes.getRes().getUnitGroup().getFloatConvertRateFormat()).toString(): null);
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


    public static ResHelper instance()
    {
        if ( !Contexts.isEventContextActive() )
        {
            throw new IllegalStateException("no active event context");
        }
        return (ResHelper) Component.getInstance(ResHelper.class, ScopeType.STATELESS,true);
    }

}
