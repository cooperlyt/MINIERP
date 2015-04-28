package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityLoader;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.total.data.ResSaleTotalData;
import com.dgsoft.erp.total.data.ResTotalCount;
import com.dgsoft.erp.total.data.StoreResMonthTotalData;
import com.dgsoft.erp.total.data.StoreResTotalData;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 11/10/14.
 */
@Name("yearSaleResChartData")
public class YearSaleResChartData {


    @In(create = true)
    private ErpEntityLoader erpEntityLoader;

    @Factory(value = "saleSearchYears", scope = ScopeType.SESSION)
    public List<Integer> getSaleSearchYears() {

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        int maxYear = gc.get(Calendar.YEAR);

        Date minDate = erpEntityLoader.getEntityManager().createQuery("select min(customerOrder.createDate) from CustomerOrder customerOrder where customerOrder.canceled = false", Date.class).getSingleResult();

        int minYear;
        if (minDate == null) {
            minYear = maxYear;
        } else {
            gc.setTime(minDate);
            minYear = gc.get(Calendar.YEAR);
        }

        List<Integer> result = new ArrayList<Integer>();
        for (int i = minYear; i <= maxYear; i++) {
            result.add(Integer.valueOf(i));
        }

        return result;
    }


    private Integer totalYear;

    public Integer getTotalYear() {

        return totalYear;
    }

    public void setTotalYear(Integer totalYear) {
        if ((this.totalYear == null) || !this.totalYear.equals(totalYear))
            totalStoreResSaleData = null;
        this.totalYear = totalYear;
    }


    private Map<Res, Map<Integer, ResSaleTotalData>> totalStoreResSaleData;


    public Map<Res, Map<Integer, ResSaleTotalData>> getTotalStoreResSaleData() {
        initTotalStoreResSaleData();
        return totalStoreResSaleData;
    }


    public List<YearTotalData<Res, ResSaleTotalData>> getResYearTotalData() {
        initTotalStoreResSaleData();
        List<YearTotalData<Res, ResSaleTotalData>> result = new ArrayList<YearTotalData<Res, ResSaleTotalData>>(totalStoreResSaleData.size());
        for (Map.Entry<Res, Map<Integer, ResSaleTotalData>> entry : totalStoreResSaleData.entrySet()) {
            result.add(new YearTotalData<Res, ResSaleTotalData>(entry.getKey(), entry.getValue()));
        }
        Collections.sort(result, new Comparator<YearTotalData<Res, ResSaleTotalData>>() {
            @Override
            public int compare(YearTotalData<Res, ResSaleTotalData> o1, YearTotalData<Res, ResSaleTotalData> o2) {
                return o1.getObj().compareTo(o2.getObj());
            }
        });
        return result;
    }


    public List<MonthTotalData<ResSaleTotalData>> getResMonthTotalData() {
        initTotalStoreResSaleData();
        Map<Integer, MonthTotalData<ResSaleTotalData>> result = new HashMap<Integer, MonthTotalData<ResSaleTotalData>>();

        for (Map.Entry<Res, Map<Integer, ResSaleTotalData>> entry : totalStoreResSaleData.entrySet()) {
            for (int i = 1; i <= 12; i++) {
                MonthTotalData<ResSaleTotalData> data = result.get(i);
                if (data == null) {
                    data = new MonthTotalData<ResSaleTotalData>(i);
                    result.put(i, data);
                }
                data.getData().add(entry.getValue().get(i));
            }
        }
        List<MonthTotalData<ResSaleTotalData>> listResult = new ArrayList<MonthTotalData<ResSaleTotalData>>(result.values());
        Collections.sort(listResult, new Comparator<MonthTotalData<ResSaleTotalData>>() {
            @Override
            public int compare(MonthTotalData<ResSaleTotalData> o1, MonthTotalData<ResSaleTotalData> o2) {
                return o1.getMonth().compareTo(o2.getMonth());
            }
        });
        return listResult;
    }

   // private List<String> resIds;

    public boolean isFirstId(String id){
        if (getResIds().isEmpty()){
            return false;
        }else{
            return getResIds().get(0).equals(id);
        }
    }


    public List<String> getResIds() {
        //if (resIds == null) {
            initTotalStoreResSaleData();
        List<String> resIds = new ArrayList<String>();

            for (Res res : totalStoreResSaleData.keySet()) {
                resIds.add(res.getId());
            }
            Collections.sort(resIds);
       // }
        return resIds;


    }

    public List<String> getResNames() {
        initTotalStoreResSaleData();
        List<String> result = new ArrayList<String>();
        Set<Res> reses = totalStoreResSaleData.keySet();
        for (String id : getResIds()) {
            for (Res res : reses) {
                if (res.getId().equals(id)) {
                    result.add(res.getName());
                    break;
                }
            }
        }
        return result;
    }

    public List<MonthData> getMonthDatas() {
        initTotalStoreResSaleData();
        List<MonthData> result = new ArrayList<MonthData>(12);

        for (int i = 1; i <= 12; i++) {

            MonthData md = new MonthData(i);
            result.add(md);

            for (Map.Entry<Res, Map<Integer, ResSaleTotalData>> entry : totalStoreResSaleData.entrySet()) {
                md.put(entry.getKey().getId(),entry.getValue().get(i));

            }
        }
        Collections.sort(result, new Comparator<MonthData>() {
            @Override
            public int compare(MonthData o1, MonthData o2) {
                return o1.getMonth().compareTo(o2.getMonth());
            }
        });
        return result;
    }


    public static class MonthData extends MonthTotalData<ResSaleTotalData> {

        private Map<String, ResSaleTotalData> datas = new HashMap<String, ResSaleTotalData>();

        public MonthData(Integer month) {
            super(month);
        }

        public ResSaleTotalData getDataByResId(String id) {
            return datas.get(id);
        }

        public void put(String resId,ResSaleTotalData data){
            datas.put(resId,data);
        }

        public BigDecimal getTotalMoney(){
            BigDecimal result = BigDecimal.ZERO;
            for(ResSaleTotalData data: datas.values()){
                result = result.add(data.getMoney());
            }
            return result;
        }
    }


    public static class MonthTotalData<E> {

        private Integer month;

        private List<E> data = new ArrayList<E>();

        public MonthTotalData(Integer month) {
            this.month = month;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public List<E> getData() {
            return data;
        }

        public void setData(List<E> data) {
            this.data = data;
        }
    }

    public static class YearTotalData<T, E> {

        private T obj;

        private Map<Integer, E> data;

        public YearTotalData(T obj, Map<Integer, E> data) {
            this.obj = obj;
            this.data = data;
        }

        public T getObj() {
            return obj;
        }

        public E getMonth1() {
            return data.get(1);
        }

        public E getMonth2() {
            return data.get(2);
        }

        public E getMonth3() {
            return data.get(3);
        }

        public E getMonth4() {
            return data.get(4);
        }

        public E getMonth5() {
            return data.get(5);
        }

        public E getMonth6() {
            return data.get(6);
        }

        public E getMonth7() {
            return data.get(7);
        }

        public E getMonth8() {
            return data.get(8);
        }

        public E getMonth9() {
            return data.get(9);
        }

        public E getMonth10() {
            return data.get(10);
        }

        public E getMonth11() {
            return data.get(11);
        }

        public E getMonth12() {
            return data.get(12);
        }

    }

    private void initTotalStoreResSaleData() {
        if (totalStoreResSaleData == null) {

            Map<Res, Map<Integer, ResSaleTotalData>> result = new HashMap<Res, Map<Integer, ResSaleTotalData>>();

            if (totalYear != null) {

                for (StoreResMonthTotalData storeResData : erpEntityLoader.getEntityManager().createQuery("select new com.dgsoft.erp.total.data.StoreResMonthTotalData(item.storeRes,month(item.needRes.customerOrder.createDate),sum(item.count),sum(item.totalMoney)) from OrderItem item  where year(item.needRes.customerOrder.createDate) = :year and item.storeRes.res.resCategory.type = 'PRODUCT' and item.needRes.customerOrder.canceled = false group by item.storeRes,month(item.needRes.customerOrder.createDate)", StoreResMonthTotalData.class).
                        setParameter("year", totalYear).getResultList()) {
                    Map<Integer, ResSaleTotalData> totalData = result.get(storeResData.getStoreRes().getRes());
                    if (totalData == null) {
                        totalData = new HashMap<Integer, ResSaleTotalData>();
                        result.put(storeResData.getStoreRes().getRes(), totalData);
                    }


                    ResSaleTotalData data = totalData.get(storeResData.getMonth());
                    if (data != null) {
                        totalData.put(storeResData.getMonth(), data.add(new ResSaleTotalData(new ResTotalCount(storeResData.getStoreResCount()), storeResData.getMoney())));
                    } else {
                        totalData.put(storeResData.getMonth(), new ResSaleTotalData(new ResTotalCount(storeResData.getStoreResCount()), storeResData.getMoney()));
                    }

                }

                for (Map.Entry<Res, Map<Integer, ResSaleTotalData>> entry : result.entrySet()) {
                    for (int i = 1; i <= 12; i++) {
                        if (entry.getValue().get(i) == null) {
                            entry.getValue().put(i, new ResSaleTotalData(ResTotalCount.ZERO(entry.getKey()), BigDecimal.ZERO));
                        }

                    }
                }
            }
            totalStoreResSaleData = result;
        }
    }


}
