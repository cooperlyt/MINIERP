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

        Date minDate = erpEntityLoader.getEntityManager().createQuery("select min(customerOrder.createDate) from CustomerOrder customerOrder where not customerOrder.canceled ", Date.class).getSingleResult();

        int minYear;
        if (minDate == null) {
            minYear = maxYear;
        } else {
            gc.setTime(minDate);
            minYear = gc.get(Calendar.YEAR);
        }

        List<Integer> result = new ArrayList<Integer>();
        for(int i= minYear ; i <= maxYear ; i++){
           result.add(Integer.valueOf(i));
        }

        return result;
    }


    private Integer totalYear;

    public Integer getTotalYear() {
        if (totalYear == null){
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(new Date());
            totalYear = gc.get(Calendar.YEAR);
        }

        return totalYear;
    }

    public void setTotalYear(Integer totalYear) {
        this.totalYear = totalYear;
    }


    public Map<Res,Map<Integer,ResSaleTotalData>> totalStoreResSaleData(){
        Map<Res,Map<Integer,ResSaleTotalData>> result = new HashMap<Res, Map<Integer, ResSaleTotalData>>();
        for (StoreResMonthTotalData storeResData: erpEntityLoader.getEntityManager().createQuery("select new com.dgsoft.erp.total.data.StoreResMonthTotalData(item.storeRes,month(item.needRes.customerOrder.createDate),sum(item.count),sum(item.totalMoney)) from OrderItem item left join fetch item.storeRes where year(item.needRes.customerOrder.createDate) = :year and not item.needRes.customerOrder.canceled group by item.storeRes,month(item.needRes.customerOrder.createDate)",StoreResMonthTotalData.class).
                setParameter("year",totalYear).getResultList()){
            Map<Integer,ResSaleTotalData> totalData = result.get(storeResData.getStoreRes().getRes());
            if (totalData == null){
                totalData = new HashMap<Integer, ResSaleTotalData>();
                result.put(storeResData.getStoreRes().getRes(),totalData);
            }


            ResSaleTotalData data = totalData.get(storeResData.getMonth());
            if (data != null){
                totalData.put(storeResData.getMonth(), data.add(new ResSaleTotalData(new ResTotalCount(storeResData.getStoreResCount()),storeResData.getMoney())));
            }else{
                totalData.put(storeResData.getMonth(),new ResSaleTotalData(new ResTotalCount(storeResData.getStoreResCount()),storeResData.getMoney()));
            }

        }
        return result;
    }


}
