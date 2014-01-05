package com.dgsoft.erp.action;

import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.FormatDefine;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.UnitGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

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

    public String generateStoreResTitle(StoreRes storeRes){

        String result = storeRes.getRes().getName() + "(" + storeRes.getRes().getCode() + ")  ";

        for (Format format: storeRes.getFormatList()){
            result += " " + format.getFormatDefine().getName() + " : ";
            if (format.getFormatDefine().getDataType().equals(FormatDefine.FormatType.WORD)){
                result += dictionary.getWordValue(format.getFormatValue());
            }else{
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




}
