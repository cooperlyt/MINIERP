package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityLoader;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by cooper on 8/27/15.
 */
@Name("monthMoneyChartData")
public class MonthMoneyChartData {

    @In(create = true)
    private ErpEntityLoader erpEntityLoader;

    private List<TotalData> resultList = new ArrayList<TotalData>(0);


    private TotalData resultTotal = new TotalData(0);

    private Integer totalYear;

    public Integer getTotalYear() {
        return totalYear;
    }

    public void setTotalYear(Integer totalYear) {
        this.totalYear = totalYear;
    }


    public List<TotalData> getResultList() {
        return resultList;
    }


    public TotalData getResultTotal() {
        return resultTotal;
    }

    public void totalData(){

        resultList = new ArrayList<TotalData>();
        resultTotal = new TotalData(0);

        Calendar calendar = Calendar.getInstance(Locale.CHINA);

        if (RunParam.instance().getBooleanParamValue("erp.finance.beginUpMonth")){
            calendar.set(Calendar.YEAR,totalYear - 1);
            calendar.set(Calendar.MONTH, 11);
        }else{
            calendar.set(Calendar.YEAR,totalYear);
            calendar.set(Calendar.MONTH, 0);
        }
        calendar.set(Calendar.DATE,
                Math.min(calendar.getActualMaximum(Calendar.DATE), RunParam.instance().getIntParamValue("erp.finance.beginningDay")));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        for (int i = 0 ; i < 12; i++){

            Date beginDate = calendar.getTime();

            calendar.add(Calendar.MONTH, 1);

            Date endDate =  new Date(calendar.getTime().getTime() - 1);
            TotalData totalDate =  new TotalData(i + 1);

            totalDate.setResMoney(
                    erpEntityLoader.getEntityManager().createQuery("select sum(oi.totalMoney)  from OrderItem oi where oi.needRes.customerOrder.canceled <> true and oi.needRes.customerOrder.createDate >= :beginDate and oi.needRes.customerOrder.createDate <= :endDate", BigDecimal.class)
                            .setParameter("beginDate", beginDate).setParameter("endDate", endDate).getSingleResult());

            totalDate.setOrderReduce(

                    erpEntityLoader.getEntityManager().createQuery("select sum(orderReduce.money) from OrderReduce orderReduce where orderReduce.customerOrder.canceled <> true and orderReduce.customerOrder.createDate >= :beginDate and orderReduce.customerOrder.createDate <= :endDate", BigDecimal.class).setParameter("beginDate", beginDate).setParameter("endDate", endDate).getSingleResult()
            );

            totalDate.setOrderBack(erpEntityLoader.getEntityManager().createQuery("select sum(bi.totalMoney)" +
                    " from BackItem bi where bi.orderBack.confirmed = true and bi.orderBack.completeDate >= :beginDate and bi.orderBack.completeDate <= :endDate", BigDecimal.class)
                    .setParameter("beginDate", beginDate).setParameter("endDate", endDate).getSingleResult());

            totalDate.setOrderRebate(erpEntityLoader.getEntityManager().createQuery("select sum(rsr.rebateMoney) from ResSaleRebate rsr where rsr.customerOrder.canceled <> true and rsr.customerOrder.createDate >= :beginDate and rsr.customerOrder.createDate <= :endDate", BigDecimal.class)
                    .setParameter("beginDate", beginDate).setParameter("endDate", endDate).getSingleResult());


            resultList.add(totalDate);


            resultTotal.setResMoney(resultTotal.getResMoney().add(totalDate.getResMoney()));

            resultTotal.setOrderReduce(resultTotal.getOrderReduce().add(totalDate.getOrderReduce()));

            resultTotal.setOrderBack(resultTotal.getOrderBack().add(totalDate.getOrderBack()));

            resultTotal.setOrderRebate(resultTotal.getOrderRebate().add(totalDate.getOrderRebate()));


        }





    }

    public static class TotalData{

        private int month;
        private BigDecimal resMoney;
        private BigDecimal orderReduce;
        private BigDecimal orderBack;
        private BigDecimal orderRebate;

        public TotalData(int month) {
            this.month = month;
        }

        public int getMonth() {
            return month;
        }

        public BigDecimal getResMoney() {
            if (resMoney == null){
                return BigDecimal.ZERO;
            }
            return resMoney;
        }

        public void setResMoney(BigDecimal resMoney) {
            this.resMoney = resMoney;
        }

        public BigDecimal getOrderReduce() {
            if (orderReduce == null){
                return BigDecimal.ZERO;
            }
            return orderReduce;
        }

        public void setOrderReduce(BigDecimal orderReduce) {
            this.orderReduce = orderReduce;
        }

        public BigDecimal getOrderBack() {
            if (orderBack == null){
                return BigDecimal.ZERO;
            }
            return orderBack;
        }

        public void setOrderBack(BigDecimal orderBack) {
            this.orderBack = orderBack;
        }

        public BigDecimal getOrderRebate() {
            if (orderRebate == null){
                return BigDecimal.ZERO;
            }
            return orderRebate;
        }

        public void setOrderRebate(BigDecimal orderRebate) {
            this.orderRebate = orderRebate;
        }


        public BigDecimal getTotalMoney(){
            BigDecimal result = getResMoney();
            if (orderReduce != null){
                result = result.subtract(orderReduce);
            }
            if (orderBack != null){
                result = result.subtract(orderBack);
            }
            if (orderRebate != null){
                result = result.subtract(orderRebate);
            }


            return result;
        }
    }

    public static void main(String[] args){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);


            calendar.set(Calendar.YEAR,2015 - 1);
            calendar.set(Calendar.MONTH, 11);

        calendar.set(Calendar.DATE,
                Math.min(calendar.getActualMaximum(Calendar.DATE), 27));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);



        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(calendar.getTime().getTime() - 1)));
        calendar.add(Calendar.MONTH, 1);

        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
        calendar.add(Calendar.MONTH,1);

        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
        calendar.add(Calendar.MONTH,1);

        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));

    }
}
