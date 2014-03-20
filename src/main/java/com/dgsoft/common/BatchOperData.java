package com.dgsoft.common;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 28/02/14
 * Time: 16:49
 */
public class BatchOperData<T> {

    public static <T> List<BatchOperData<T>> createBatchOperDataList(Collection<? extends T> datas, boolean selected) {
        List<BatchOperData<T>> result = new ArrayList<BatchOperData<T>>(datas.size());
        for (T data : datas) {
            result.add(new BatchOperData<T>(data, selected));
        }
        return result;
    }

    public static <T> BatchOperData<T> createBatchOperData(T data, boolean selected){
        return new BatchOperData<T>(data, selected);
    }

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
