package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
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

    @In
    private EntityManager erpEntityManager;

    @Logger
    protected Log log;

    private Res res;

    private List<Format> resFormatList = new ArrayList<Format>();

    private List<BigDecimal> floatConvertHistoryRates;

    private Map<String, List<Object>> historyValues = new HashMap<String, List<Object>>();

    public List<Format> getResFormatList() {
        return resFormatList;
    }

    public void setResFormatList(List<Format> resFormatList) {
        this.resFormatList = resFormatList;
    }

    public Res getRes() {
        return res;
    }

    public List<BigDecimal> getFloatConvertHistoryRateList() {
        return floatConvertHistoryRates;
    }

    private void generateFloatConvertRateHistory() {
        Set<BigDecimal> result = new HashSet<BigDecimal>();
        if (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            return;
        }
        DecimalFormat df = new DecimalFormat(res.getUnitGroup().getFloatConvertRateFormat());

        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setGroupingUsed(false);

        for (StoreRes storeRes : res.getStoreReses()) {

            try {
                result.add(new BigDecimal(df.parse(df.format(storeRes.getFloatConversionRate())).toString()));
            } catch (ParseException e) {
                result.add(storeRes.getFloatConversionRate());
                Logging.getLog(this.getClass()).warn("floatConvertRate cant be format:" + storeRes.getFloatConversionRate());
            }

        }
        floatConvertHistoryRates = new ArrayList<BigDecimal>(result);
        Collections.sort(floatConvertHistoryRates);
    }

    public void selectedStoreRes(StoreRes storeRes, boolean var) {
        this.res = storeRes.getRes();
        generateFloatConvertRateHistory();
        resFormatList = new ArrayList<Format>();
        for (Format format : storeRes.getFormatList()) {
            if (var) {
                resFormatList.add(format);
            } else
                resFormatList.add(new Format(format.getFormatDefine(), format.getFormatValue()));

            historyValues.put(format.getFormatDefine().getId(), searchHistoryValues(format.getFormatDefine()));
        }
        log.debug("storeResFormatFilter resSelect event comp");
    }


    @Observer(value = "erp.storeResLocateSelected", create = false)
    public void selectedStoreRes(StoreRes storeRes) {
        selectedStoreRes(storeRes, false);
    }


    @Observer(value = "erp.resLocateSelected", create = false)
    public void selectedRes(Res res) {
        this.res = res;
        generateFloatConvertRateHistory();
        resFormatList = new ArrayList<Format>();
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
