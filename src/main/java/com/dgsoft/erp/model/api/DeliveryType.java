package com.dgsoft.erp.model.api;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/9/13
 * Time: 8:25 PM
 * To change this template use File | Settings | File Templates.
 */
public enum DeliveryType {

    FULL_CAR_SEND(true),
    EXPRESS_SEND(true),
    SEND_TO_DOOR(false),
    CUSTOMER_SELF(false);

    private boolean haveFare;

    public boolean isHaveFare() {
        return haveFare;
    }

    private DeliveryType(boolean haveFare){
        this.haveFare = haveFare;
    }

}
