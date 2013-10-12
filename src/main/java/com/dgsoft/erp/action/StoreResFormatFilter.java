package com.dgsoft.erp.action;

import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.FormatDefine;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

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

    @In
    private EntityManager erpEntityManager;

    private Res res;

    private List<Format> resFormatList = new ArrayList<Format>();

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

    public void setRes(Res res) {
        this.res = res;
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

    public void selectRes(Res res) {
        this.res = res;
        resFormatList = new ArrayList<Format>();
        for (FormatDefine formatDefine : res.getFormatDefineList()) {
            resFormatList.add(new Format(formatDefine));
            historyValues.put(formatDefine.getId(), searchHistoryValues(formatDefine));
        }

    }

    public List<Object> getFormatHistoryList(String defineId) {
        List<Object> result = historyValues.get(defineId);
        if (result == null) {
            return new ArrayList<Object>(0);
        } else
            return result;
    }

    public boolean typedFormat(){
        if (res == null){
            return false;
        }

        for (Format format : resFormatList) {
           if ((format.getFormatValue() != null) && (!format.getFormatValue().trim().equals(""))){
                return true;
           }
        }
        return false;
    }

    public Set<String> getAgreeStoreResIds() {
        if (res != null) {
            Set<String> result = new HashSet<String>();
            List<StoreRes> storeReses = erpEntityManager.createQuery("select storeRes from StoreRes storeRes where storeRes.res.id = :resId").setParameter("resId", res.getId()).getResultList();

            for (StoreRes storeRes : storeReses) {
                boolean agree = true;
                for (Format format : resFormatList) {
                    if ((format.getFormatValue() != null) && (!format.getFormatValue().trim().equals("")) &&
                            (!format.getFormatValue().equals(storeRes.getFormat(format.getFormatDefine().getId())))) {
                        agree = false;
                        break;
                    }
                }
                if (agree){
                    result.add(storeRes.getId());
                }
            }

            return result;
        } else
            return null;
    }

}
