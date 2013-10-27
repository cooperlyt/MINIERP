package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.model.BusinessDefine;
import com.dgsoft.common.system.model.Role;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/11/13
 * Time: 3:25 PM
 */
@Name("roleHome")
public class RoleHome extends SystemEntityHome<Role> {

    private boolean editing = false;


    private List<BusinessDefine> selectBusinessList = new ArrayList<BusinessDefine>();

    public List<BusinessDefine> getSelectBusinessList() {
        return selectBusinessList;
    }

    public void setSelectBusinessList(List<BusinessDefine> selectBusinessList) {
        this.selectBusinessList = selectBusinessList;
    }

    @Override
    public void setId(Object id) {
        super.setId(id);
        if (isIdDefined())
            editing = false;

    }

    @Override
    protected void initInstance() {
        super.initInstance();
        selectBusinessList.clear();
        selectBusinessList.addAll(getInstance().getBusinessDefines());
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    @End
    public void cancel() {
        refresh();
        editing = false;
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public void edit() {
        editing = true;
    }

    @End
    public String save() {
        String result;
        getInstance().getBusinessDefines().clear();
        getInstance().getBusinessDefines().addAll(selectBusinessList);
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
}
