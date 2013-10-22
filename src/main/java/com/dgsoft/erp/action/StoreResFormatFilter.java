package com.dgsoft.erp.action;

import com.dgsoft.erp.model.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/3/13
 * Time: 10:59 PM
 */
@Name("storeResFormatFilter")
@Scope(ScopeType.CONVERSATION)
public class StoreResFormatFilter {

    public static class NoConvertAuxCount{

        private BigDecimal count;

        private ResUnit resUnit;

        public NoConvertAuxCount(ResUnit resUnit, BigDecimal count) {
            this.count = count;
            this.resUnit = resUnit;
        }

        public BigDecimal getCount() {
            return count;
        }

        public void setCount(BigDecimal count) {
            this.count = count;
        }

        public ResUnit getResUnit() {
            return resUnit;
        }

        public void setResUnit(ResUnit resUnit) {
            this.resUnit = resUnit;
        }
    }

    private static final int FLOAT_CONVERT_SCALE = 10;

    @In
    private EntityManager erpEntityManager;

    @Logger
    protected Log log;

    private Res res;

    private BigDecimal count;

    private ResUnit useUnit;

    private BigDecimal floatConvertRate;

    private List<Format> resFormatList = new ArrayList<Format>();

    private List<NoConvertAuxCount> noConvertCountList;

    private List<BigDecimal> floatConvertHistoryRates;

    private BigDecimal auxCount;

    private Map<String, List<Object>> historyValues = new HashMap<String, List<Object>>();

    public List<Format> getResFormatList() {
        return resFormatList;
    }

    public void setResFormatList(List<Format> resFormatList) {
        this.resFormatList = resFormatList;
    }

    public ResUnit getUseUnit() {
        return useUnit;
    }

    public void setUseUnit(ResUnit useUnit) {
        this.useUnit = useUnit;
    }

    public BigDecimal getFloatConvertRate() {
        return floatConvertRate;
    }

    public void setFloatConvertRate(BigDecimal floatConvertRate) {
        this.floatConvertRate = floatConvertRate;
    }

    public List<NoConvertAuxCount> getNoConvertCountList() {
        return noConvertCountList;
    }

    public void setNoConvertCountList(List<NoConvertAuxCount> noConvertCountList) {
        this.noConvertCountList = noConvertCountList;
    }

    public BigDecimal getCount() {
        return count;
    }

    public BigDecimal getFixConvertMasterCount(){
        return count.multiply(useUnit.getConversionRate());
    }

    public void clearCount(){
        noConvertCountList = null;
        count = new BigDecimal(0);
        auxCount = new BigDecimal(0);
        floatConvertRate = new BigDecimal(0);
        useUnit = null;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public Res getRes() {
        return res;
    }

    public BigDecimal getAuxCount() {
        return auxCount;
    }

    public void setAuxCount(BigDecimal auxCount) {
        this.auxCount = auxCount;
    }


    public void calcFloatQuantityByMasterUnit() {
        if ((count == null) || (count.doubleValue() == 0)) {
            auxCount = new BigDecimal(0);
            floatConvertRate = new BigDecimal(0);
            return;
        }

        if ((floatConvertRate != null) && (floatConvertRate.doubleValue() != 0)) {
            auxCount = count.multiply(floatConvertRate);
        } else if ((auxCount != null) && (auxCount.doubleValue() != 0)) {
            floatConvertRate = auxCount.divide(count, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP);
        }
    }

    public void calcFloatQuantityByRate() {
        if ((floatConvertRate == null) || (floatConvertRate.doubleValue() == 0)) {
            count = new BigDecimal(0);
            auxCount = new BigDecimal(0);
            return;
        }

        if ((count != null) && (count.doubleValue() != 0)) {
            auxCount = count.multiply(floatConvertRate);
        } else if ((auxCount != null) && (auxCount.doubleValue() != 0)) {
            count = auxCount.divide(floatConvertRate, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP);
        }

    }

    public void calcFloatQuantityByAuxUnit() {
        if ((auxCount == null) || (auxCount.doubleValue() == 0)) {
            count = new BigDecimal(0);
            floatConvertRate = new BigDecimal(0);
        }

        if ((count != null) && (count.doubleValue() != 0)) {
            floatConvertRate = auxCount.divide(count, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP);
        } else if ((floatConvertRate != null) && (floatConvertRate.doubleValue() != 0)) {
            count = auxCount.divide(floatConvertRate, FLOAT_CONVERT_SCALE, BigDecimal.ROUND_HALF_UP);
        }
    }


    public List<BigDecimal> getFloatConvertHistoryRateList() {
        return floatConvertHistoryRates;
    }

    private void generateFloatConvertRateHistory() {
        Set<BigDecimal> result = new HashSet<BigDecimal>();
        for (StoreRes storeRes : res.getStoreReses()) {
            result.add(storeRes.getFloatConversionRate());
        }
        floatConvertHistoryRates = new ArrayList<BigDecimal>(result);
        Collections.sort(floatConvertHistoryRates);
    }

    private void generateCounts(Boolean out) {
        count = new BigDecimal(0);
        noConvertCountList = null;
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            floatConvertRate = res.getUnitGroup().getFloatAuxiliaryUnit().getConversionRate();

        } else if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)) {
            if (res.getUnitGroup().getResUnits().size() > 1) {
                noConvertCountList = new ArrayList<NoConvertAuxCount>(res.getUnitGroup().getResUnits().size() - 1);
                for (ResUnit unit : res.getUnitGroup().getResUnitList()) {
                    if (!unit.equals(res.getResUnitByMasterUnit())) {
                        noConvertCountList.add(new NoConvertAuxCount(unit, new BigDecimal(0)));
                    }
                }
            }
        }

        if (out == null) {
            useUnit = res.getResUnitByMasterUnit();
        } else if (out) {
            useUnit = res.getResUnitByOutDefault();
        } else {
            useUnit = res.getResUnitByInDefault();
        }

    }

    public void selectedRes(Res res, Boolean out) {
        this.res = res;
        generateCounts(out);
        resFormatList = new ArrayList<Format>();
        generateFloatConvertRateHistory();
        for (FormatDefine formatDefine : res.getFormatDefineList()) {
            resFormatList.add(new Format(formatDefine));
            historyValues.put(formatDefine.getId(), searchHistoryValues(formatDefine));
        }
    }

    public void clearRes() {
        res = null;
        historyValues.clear();
        resFormatList.clear();

    }

    public List<Object> getFormatHistoryList(String defineId) {
        List<Object> result = historyValues.get(defineId);
        if (result == null) {
            return new ArrayList<Object>(0);
        } else
            return result;
    }

    public boolean typedFormat() {
        if (res == null) {
            return false;
        }

        for (Format format : resFormatList) {
            if ((format.getFormatValue() != null) && (!format.getFormatValue().trim().equals(""))) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getAgreeStoreResIds() {
        if (res != null) {
            Set<String> result = new HashSet<String>();
            result.add("none_none_none_if_collection_empty");
            List<StoreRes> storeReses = erpEntityManager.createQuery("select storeRes from StoreRes storeRes where storeRes.res.id = :resId").setParameter("resId", res.getId()).getResultList();

            for (StoreRes storeRes : storeReses) {
                boolean agree = true;
                for (Format format : resFormatList) {

                    log.debug("getAgreeStoreResIds test: " + format);
                    if (format != null) {
                        log.debug("getAgreeStoreResIds test: " + format.getFormatValue());
                    }
                    if ((format.getFormatValue() != null) && (!format.getFormatValue().trim().equals("")) &&
                            (!format.getFormatValue().equals(storeRes.getFormat(format.getFormatDefine().getId()).getFormatValue()))) {
                        agree = false;
                        break;
                    }
                }
                if (agree) {
                    result.add(storeRes.getId());
                }
            }

            log.debug("getAgreeStoreResIds return: " + result.size());

            return result;
        } else {
            return null;
        }
    }


    private List<Object> searchHistoryValues(FormatDefine define) {
        List<Format> formats = erpEntityManager.createQuery("select distinct format from Format format where format.formatDefine.id = :defineId").setParameter("defineId", define.getId()).getResultList();
        formats.addAll(resFormatList);
        List<Object> result = new ArrayList<Object>();
        for (Format format : formats) {
            if (format.getFormatDefine().getId().equals(define.getId())
                    && (format.getFormatValue() != null) &&
                    !format.getFormatValue().trim().equals("")) {
                if (define.getDataType().equals(FormatDefine.FormatType.INTEGER)) {
                    Integer value = Integer.parseInt(format.getFormatValue());
                    if (!result.contains(value)) {
                        result.add(value);
                    }
                } else if (define.getDataType().equals(FormatDefine.FormatType.FLOAT)) {
                    BigDecimal value = new BigDecimal(format.getFormatValue());
                    if (!result.contains(value)) {
                        result.add(value);
                    }
                }
            }

        }


        Collections.sort(result, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                if ((o1 instanceof Integer) && (o2 instanceof Integer)) {
                    return ((Integer) o1).compareTo((Integer) o2);
                } else if ((o1 instanceof BigDecimal) && (o2 instanceof BigDecimal)) {
                    return ((BigDecimal) o1).compareTo((BigDecimal) o2);
                }
                return 0;
            }
        });
        return result;
    }

}
