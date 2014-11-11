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
        if (totalYear == null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(new Date());
            totalYear = gc.get(Calendar.YEAR);
        }

        return totalYear;
    }

    public void setTotalYear(Integer totalYear) {
        this.totalYear = totalYear;
    }


    private Map<Res, Map<Integer, ResSaleTotalData>> totalStoreResSaleData;


    public Map<Res, Map<Integer, ResSaleTotalData>> getTotalStoreResSaleData() {
        initTotalStoreResSaleData();
        return totalStoreResSaleData;
    }


    public List<YearTotalData<Res,ResSaleTotalData>> getResYearTotalData(){
        initTotalStoreResSaleData();
        List<YearTotalData<Res,ResSaleTotalData>> result = new ArrayList<YearTotalData<Res, ResSaleTotalData>>(totalStoreResSaleData.size());
        for (Map.Entry<Res, Map<Integer, ResSaleTotalData>> entry : totalStoreResSaleData.entrySet()) {
            result.add(new YearTotalData<Res, ResSaleTotalData>(entry.getKey(),entry.getValue()));
        }
        Collections.sort(result, new Comparator<YearTotalData<Res, ResSaleTotalData>>() {
            @Override
            public int compare(YearTotalData<Res, ResSaleTotalData> o1, YearTotalData<Res, ResSaleTotalData> o2) {
                return o1.getObj().compareTo(o2.getObj());
            }
        });
        return result;
    }

    public static class YearTotalData<T,E>{

        private T obj;

        private Map<Integer, E> data;

        public YearTotalData(T obj, Map<Integer, E> data) {
            this.obj = obj;
            this.data = data;
        }

        public T getObj() {
            return obj;
        }

        public E getMonth1(){
              return data.get(1);
        }

        public E getMonth2(){
            return data.get(2);
        }
        public E getMonth3(){
            return data.get(3);
        }
        public E getMonth4(){
            return data.get(4);
        }
        public E getMonth5(){
            return data.get(5);
        }
        public E getMonth6(){
            return data.get(6);
        }
        public E getMonth7(){
            return data.get(7);
        }
        public E getMonth8(){
            return data.get(8);
        }
        public E getMonth9(){
            return data.get(9);
        }
        public E getMonth10(){
            return data.get(10);
        }
        public E getMonth11(){
            return data.get(11);
        }
        public E getMonth12(){
            return data.get(12);
        }

    }

    private void initTotalStoreResSaleData() {
        if (totalStoreResSaleData == null) {

            Map<Res, Map<Integer, ResSaleTotalData>> result = new HashMap<Res, Map<Integer, ResSaleTotalData>>();
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

            totalStoreResSaleData = result;
        }
    }


}
