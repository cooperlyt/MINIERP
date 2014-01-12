package com.dgsoft.erp.model.api;

import javax.persistence.Transient;

/**
 * Created by cooper on 1/12/14.
 */
public abstract class BatchOperEntity implements java.io.Serializable{

    @Transient
    private boolean selected = false;

    @Transient
    public boolean isSelected() {
        return selected;
    }

    @Transient
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
