package com.dgsoft.erp.business.finance;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.model.AccountCheckout;
import com.dgsoft.erp.model.Checkout;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-14
 * Time: 下午4:42
 */
@Name("accountDateHelper")
@AutoCreate
@Scope(ScopeType.APPLICATION)
@Startup
public class AccountDateHelper implements Serializable {

    @In
    private EntityManager erpEntityManager;

    private Date nextBeginDate;

    @Create
    public void load(){
        Long maxId = erpEntityManager.createQuery("select max(checkout.id) from Checkout checkout",Long.class).getSingleResult();
        if (maxId == null) {
            nextBeginDate = null;
        }else{

            Checkout checkout = erpEntityManager.find(Checkout.class, maxId);

            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            calendar.set(checkout.getYear(),checkout.getMonth(),checkout.getBeginDay());
            calendar.add(Calendar.MONTH,1);
            nextBeginDate = calendar.getTime();
        }
    }

    public Date getNextBeginDate() {
        if (nextBeginDate == null){
            Date saleMinDate = erpEntityManager.createQuery("select min(accountoper.operDate) from AccountOper accountoper",Date.class).getSingleResult();
            Date storeMinDate = erpEntityManager.createQuery("select min(stockChange.operDate) from StockChange stockChange ", Date.class).getSingleResult();
            Date minDate = null;
            if (saleMinDate == null){
                minDate = storeMinDate;
            }else if (storeMinDate != null){
                minDate = (storeMinDate.compareTo(saleMinDate) > 0) ? saleMinDate : storeMinDate;
            }

            if (minDate == null){
                minDate = new Date();
            }
            minDate = DataFormat.halfTime(minDate);
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            calendar.setTime(minDate);
            if (calendar.get(Calendar.DATE) >= RunParam.instance().getIntParamValue("erp.finance.beginningDay")){
                calendar.set(Calendar.DATE,RunParam.instance().getIntParamValue("erp.finance.beginningDay"));
            }else{
                calendar.set(Calendar.DATE,RunParam.instance().getIntParamValue("erp.finance.beginningDay"));
                calendar.add(Calendar.MONTH,-1);
            }
            return calendar.getTime();
        }
        return nextBeginDate;
    }

    public String getDisplayBeginDate(){

        return new SimpleDateFormat("yyyy-MM-dd").format(getNextBeginDate());
    }


    public Date getNextCloseDate(){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(getNextBeginDate());
        calendar.set(Calendar.DATE, RunParam.instance().getIntParamValue("erp.finance.beginningDay"));
        calendar.add(Calendar.MONTH,1);
        return new Date(calendar.getTime().getTime() - 1);
    }



}
