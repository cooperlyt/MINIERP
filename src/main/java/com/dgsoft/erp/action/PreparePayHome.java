package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.PreparePay;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/22/13
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("preparePayHome")
public class PreparePayHome extends ErpEntityHome<PreparePay> {

    @In
    private CustomerHome customerHome;

    @In
    private Credentials credentials;

    @Override
    public PreparePay createInstance(){
        PreparePay result = new PreparePay();
        result.setAccountOper(new AccountOper(result,customerHome.getInstance(),credentials.getUsername(), BigDecimal.ZERO));
        return result;
    }

    @Override
    protected boolean wire(){
        if (!isManaged()){

            getInstance().getAccountOper().getCustomer().
                    setBalance(getInstance().getAccountOper().getCustomer().getBalance().add(getInstance().getAccountOper().getOperMoney()));

        }

        return true;
    }

}
