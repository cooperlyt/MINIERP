package com.dgsoft.common;

import javax.persistence.Transient;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 28/02/14
 * Time: 16:49
 */
public class BatchOperData<T> {

    private T data;

    public BatchOperData(T data, boolean selected) {
        this.data = data;
        this.selected = selected;
    }

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
