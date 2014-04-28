package com.dgsoft.erp.action;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.common.system.model.Word;
import com.dgsoft.erp.ResFormatCache;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Logging;

import javax.persistence.EntityManager;
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

        String result = storeRes.getRes().getName() + "(" + storeRes.getRes().getCode() + ")";

        result += getFormatsTitle(ResFormatCache.instance().getFormats(storeRes), true);

        if (storeRes.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            result += " " + DataFormat.format(storeRes.getFloatConversionRate(),
                    storeRes.getRes().getUnitGroup().getFloatConvertRateFormat()).toString();
            result += storeRes.getRes().getUnitGroup().getName();
        }


        return result;
    }

    public String getFormatsTitle(List<Format> formats, boolean showKey) {
        String result = "";
        for (Format format : formats) {
            if (showKey)
                result += " " + format.getFormatDefine().getName() + ":";
            if (format.getFormatDefine().getDataType().equals(FormatDefine.FormatType.WORD)) {
                if (!showKey) {
                    result += " ";
                }
                result += dictionary.getWordValue(format.getFormatValue());
            } else {
                result += format.getFormatValue();
            }
        }
        return result;
    }


//    public static List<OrderItem> unionSeamOrderItem(List<OrderItem> orderItems) {
//        Logging.getLog(ResHelper.class).debug("call unionSeamOrderItem:" + orderItems.size() );
//        List<OrderItem> result = new ArrayList<OrderItem>();
//        for (OrderItem orderItem : orderItems) {
//            boolean finded = false;
//            for (OrderItem item : result) {
//                if (item.isSameItem(orderItem)) {
//                    finded = true;
//                    item.setCount(item.getCount().add(orderItem.getCount()));
//                    Logging.getLog(ResHelper.class).debug("fount add count:" + item.getCount() + "|" + orderItem.getCount());
//                    break;
//                }
//            }
//            if (!finded) {
//                Logging.getLog(ResHelper.class).debug("not fount add:" + orderItem.getCount());
//                result.add(orderItem);
//            }
//        }
//        return result;
//    }


    public boolean matchFormat(Collection<Format> formats, StoreRes storeRes) {
        for (Format cFormat : formats) {
            if (cFormat.getFormatValue() != null) {
                boolean find = false;
                for (Format f : ResFormatCache.instance().getFormats(storeRes)) {
                    if (cFormat.equals(f)) {
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean sameFormat(Collection<Format> formatList1, Collection<Format> formatList2) {
        if (formatList1.size() != formatList2.size()) {
            return false;
        }

        if (formatList1.isEmpty() && formatList2.isEmpty()) {
            return true;
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

    public String formatDisplayValue(Format format) {
        if (format != null)
            switch (format.getFormatDefine().getDataType()) {
                case INTEGER:
                    return format.getFormatDefine().getName() + " " + format.getIntValue();
                case WORD:
                    return format.getFormatDefine().getName() + " " + dictionary.getWordValue(format.getFormatValue());
                case FLOAT:
                    return format.getFormatDefine().getName() + " " + format.getFloatValue();
            }

        return "";
    }

    public String genStoreResCode(String resCode, List<Format> formats, String floatConvertRate) {
        String result = resCode.trim() + "-";
        for (Format format : formats) {
            if (format.getFormatDefine().getDataType().equals(FormatDefine.FormatType.WORD)) {
                Word word = dictionary.getWord(format.getFormatValue());
                if (word != null)
                    result += word.getKey();
            }
            if (format.getFormatDefine().getDataType().equals(FormatDefine.FormatType.INTEGER)) {
                result += format.getIntValue();
            }
            if (format.getFormatDefine().getDataType().equals(FormatDefine.FormatType.FLOAT)) {
                result += format.getFloatValue();
            }
        }
        if (floatConvertRate != null) {
            result += floatConvertRate;
        }

        return result;
    }

    public String genStoreResCode(StoreRes storeRes) {
        return genStoreResCode(storeRes.getRes().getCode(), ResFormatCache.instance().getFormats(storeRes),
                (storeRes.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) ?
                        DataFormat.format(storeRes.getFloatConversionRate(), storeRes.getRes().getUnitGroup().getFloatConvertRateFormat()).toString() : null
        );
    }

    @In
    private EntityManager erpEntityManager;

    public Map<String, Set<Object>> getFormatHistory(Res res) {
        Map<String, Set<Object>> result = new HashMap<String, Set<Object>>();

        List<Format> formats = erpEntityManager.createQuery("select format from Format format where format.storeRes.res.id = :resId and format.formatDefine.dataType != :dataType and format.storeRes.enable = true", Format.class).
                setParameter("resId", res.getId()).setParameter("dataType", FormatDefine.FormatType.WORD).getResultList();

        for (Format format : formats) {
            Set<Object> historys = result.get(format.getFormatDefine().getId());
            if (historys == null) {
                historys = new HashSet<Object>();
                result.put(format.getFormatDefine().getId(), historys);
            }

            if (format.getFormatDefine().getDataType().equals(FormatDefine.FormatType.INTEGER)) {

                historys.add(format.getIntValue());
            } else if (format.getFormatDefine().getDataType().equals(FormatDefine.FormatType.FLOAT)) {
                historys.add(format.getFloatValue());
            }
        }

        return result;
    }

    public List<BigDecimal> getFloatConvertRateHistory(Res res) {
        List<BigDecimal> queryResult = erpEntityManager.createQuery("select distinct storeRes.floatConversionRate from StoreRes storeRes where storeRes.enable = true and storeRes.res.unitGroup.type = :floatConvertType and storeRes.res.id = :resId", BigDecimal.class).
                setParameter("floatConvertType", UnitGroup.UnitGroupType.FLOAT_CONVERT).setParameter("resId", res.getId()).getResultList();
        List<BigDecimal> result = new ArrayList<BigDecimal>(queryResult.size());
        for (BigDecimal rate : queryResult) {
            result.add(DataFormat.format(rate, res.getUnitGroup().getFloatConvertRateFormat()));
        }
        return result;
    }


    public static ResHelper instance() {
        if (!Contexts.isEventContextActive()) {
            throw new IllegalStateException("no active event context");
        }
        return (ResHelper) Component.getInstance(ResHelper.class, ScopeType.STATELESS, true);
    }

}
