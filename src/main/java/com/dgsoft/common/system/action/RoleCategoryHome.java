package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.model.BusinessDefine;
import com.dgsoft.common.system.model.Function;
import com.dgsoft.common.system.model.Role;
import com.dgsoft.common.system.model.RoleCategory;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Name;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/16/13
 * Time: 3:21 PM
 */
@Name("roleCategoryHome")
public class RoleCategoryHome extends SystemEntityHome<RoleCategory> {


    private boolean editing = false;


    private List<Role> selectRoleList = new ArrayList<Role>();

    private List<Function> selectFunctionList = new ArrayList<Function>();

    public List<Role> getSelectRoleList() {
        return selectRoleList;
    }

    public void setSelectRoleList(List<Role> selectRoleList) {
        this.selectRoleList = selectRoleList;
    }

    public List<Function> getSelectFunctionList() {
        return selectFunctionList;
    }

    public void setSelectFunctionList(List<Function> selectFunctionList) {
        this.selectFunctionList = selectFunctionList;
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
        selectRoleList.clear();
        selectRoleList.addAll(getInstance().getRoles());
        selectFunctionList.clear();
        selectFunctionList.addAll(getInstance().getFunctions());
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
        getInstance().getRoles().clear();
        getInstance().getRoles().addAll(selectRoleList);
        getInstance().getFunctions().clear();
        getInstance().getFunctions().addAll(selectFunctionList);
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
