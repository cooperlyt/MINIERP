package com.dgsoft.erp.action;

import com.dgsoft.common.SetLinkList;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.MoneySave;
import com.dgsoft.erp.model.TransCorp;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-9
 * Time: 下午4:51
 */
@Name("moneySaveHome")
public class MoneySaveHome extends ErpEntityHome<MoneySave> {

    public enum SaveToAccountType{
        BY_CURR,BY_ADVANCE, BY_ACCOUNT;
    }

    private AccountOper.AccountOperType type;

    private SaveToAccountType saveType;

    private SetLinkList<AccountOper> accountOperList;

    private BigDecimal toAccountMoney;

    private Date operDate;

    private TransCorp transCorp;

    @In
    private CustomerHome customerHome;

    @In
    private Credentials credentials;

    @Factory(value = "moneySaveTypes",scope = ScopeType.CONVERSATION)
    public EnumSet<AccountOper.AccountOperType> getMoneySaveTypes(){
         return EnumSet.of(AccountOper.AccountOperType.PROXY_SAVINGS,
                 AccountOper.AccountOperType.CUSTOMER_SAVINGS,
                 AccountOper.AccountOperType.MONEY_FREE,
                 AccountOper.AccountOperType.DEPOSIT_PAY);
    }

    @Factory(value = "moneySaveToTypes",scope = ScopeType.CONVERSATION)
    public EnumSet<SaveToAccountType> getMoneySaveToTypes(){
        return EnumSet.allOf(SaveToAccountType.class);
    }

    @Override
    protected void initInstance(){
        super.initInstance();
        accountOperList = new SetLinkList<AccountOper>(getInstance().getAccountOpers());
    }

    public AccountOper.AccountOperType getType() {
        return type;
    }

    public void setType(AccountOper.AccountOperType type) {
        this.type = type;
    }

    public SaveToAccountType getSaveType() {
        return saveType;
    }

    public void setSaveType(SaveToAccountType saveType) {
        this.saveType = saveType;
    }

    public SetLinkList<AccountOper> getAccountOperList() {
        return accountOperList;
    }

    public AccountOper getSingleAccountOper(){
        return accountOperList.get(0);
    }

    public BigDecimal getToAccountMoney() {
        return toAccountMoney;
    }

    public void setToAccountMoney(BigDecimal toAccountMoney) {
        this.toAccountMoney = toAccountMoney;
    }

    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    public TransCorp getTransCorp() {
        return transCorp;
    }

    public void setTransCorp(TransCorp transCorp) {
        this.transCorp = transCorp;
    }

    public void onOperTypeChange(){
        getAccountOperList().clear();

        switch (type){

            case DEPOSIT_BACK:
                break;
            case PROXY_SAVINGS:
                break;
            case CUSTOMER_SAVINGS:
                break;
            case DEPOSIT_PAY:
                break;
            case MONEY_FREE:
                break;
            case ORDER_PAY:
                break;
            case ORDER_BACK:
                break;
        }
    }

    public void depositPay(){
      //DEPOSIT_PAY
        //MONEY_FREE
    }

    @Override
    protected boolean wire(){

        if (!getType().equals(AccountOper.AccountOperType.PROXY_SAVINGS) &&
                (getAccountOperList().size() != 1) ){
            if (getAccountOperList().size() != 1){
                throw new IllegalArgumentException(getType() + "error accountOperList:" + getAccountOperList().size());
            }
        }



        for(AccountOper accountOper: getAccountOperList()){
            accountOper.setOperType(getType());
            accountOper.setOperEmp(credentials.getUsername());
            accountOper.setOperDate(getOperDate());
            accountOper.setMoneySave(getInstance());
            accountOper.setAdvanceReceivable(BigDecimal.ZERO);
            accountOper.setAccountsReceivable(BigDecimal.ZERO);
            if (!getType().equals(AccountOper.AccountOperType.PROXY_SAVINGS)){
                accountOper.setProxcAccountsReceiveable(BigDecimal.ZERO);
            }
        }

        if (getType().equals(AccountOper.AccountOperType.PROXY_SAVINGS)){
            getInstance().setTransCorp(transCorp);
        }else{
            getInstance().setTransCorp(null);
            getSingleAccountOper().setCustomer(customerHome.getReadyInstance());
            if (getType().equals(AccountOper.AccountOperType.DEPOSIT_BACK)){
                getSingleAccountOper().setAdvanceReceivable(getInstance().getMoney());
            } else {
                if (getType().equals(AccountOper.AccountOperType.CUSTOMER_SAVINGS)) {
                    switch (saveType) {

                        case BY_CURR:
                            break;
                        case BY_ADVANCE:
                            getSingleAccountOper().setAdvanceReceivable(getInstance().getMoney());
                            break;
                        case BY_ACCOUNT:
                            getSingleAccountOper().set
                            break;
                    }
                } else {
                    throw new IllegalArgumentException("unkonw type:" + type);
                }
            }
        }

        return true;
    }
}
