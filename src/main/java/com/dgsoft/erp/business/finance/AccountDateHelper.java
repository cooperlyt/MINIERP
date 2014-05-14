package com.dgsoft.erp.business.finance;

import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.model.AccountCheckout;
import com.dgsoft.erp.model.Checkout;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;

import javax.persistence.EntityManager;
import java.io.Serializable;
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
            nextBeginDate = new Date(0);
        }else{

            Checkout checkout = erpEntityManager.find(Checkout.class, maxId);

            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            calendar.set(checkout.getYear(),checkout.getMonth(),checkout.getBeginDay());
            calendar.add(Calendar.MONTH,1);
            nextBeginDate = calendar.getTime();
        }
    }

    public Date getNextBeginDate() {
        return nextBeginDate;
    }


    public Date getNextCloseDate(){
        if (nextBeginDate == null){
            return null;
        }
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(nextBeginDate);
        calendar.set(Calendar.DATE, RunParam.instance().getIntParamValue("erp.finance.beginningDay"));
        calendar.add(Calendar.MONTH,1);
        return calendar.getTime();
    }



}
