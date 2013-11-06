package com.dgsoft.common;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/27/13
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleEntityHome<E extends NamedEntity> extends EntityHomeAdapter<E> {

    public void setPinyinSearchName(String searchStr) {
        String id = PinyinTools.splitPinyinId(searchStr);
        if (id == null) {
            if ((searchStr == null) || searchStr.trim().equals("")) {
                setId(null);
            }
            return;
        }
        try {
            setId(id);
        } catch (javax.persistence.EntityNotFoundException e) {
            setId(null);
        }
    }

    public String getPinyinSearchName() {
        if (isIdDefined()) {
            return getInstance().getName();
        }
        return "";
    }

    private boolean editing = false;

    @End
    public void cancel() {
        refresh();
        editing = false;
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public String edit() {
        editing = true;
        return "editBeginning";
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public String createNew() {
        editing = true;
        return "editBeginning";
    }

    @End
    public String save() {
        String result;
        if (isManaged()) {
            result = update();
            if (!"updated".equals(result)) {
                return result;
            }
        } else {
            result = persist();
            if (!"persisted".equals(result)) {
                return result;
            }
        }
        editing = false;
        return result;
    }


    @Override
    public void setId(Object id) {
        super.setId(id);
        if (isIdDefined())
            editing = false;

    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }
}
