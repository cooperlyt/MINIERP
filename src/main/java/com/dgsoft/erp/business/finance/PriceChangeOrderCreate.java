package com.dgsoft.erp.business.finance;

import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;

/**
 * Created by cooper on 2/8/15.
 */


@Name("priceChangeOrderCreate")
public class PriceChangeOrderCreate {

    private boolean type = true;

    private BigDecimal money;

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    private void createNoItemOrder(){

    }

    public String create(){
        if (type){

            return "createItem";
        }else{
            createNoItemOrder();
            return "complete";
        }


    }
}
