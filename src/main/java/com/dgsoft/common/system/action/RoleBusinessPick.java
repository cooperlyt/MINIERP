package com.dgsoft.common.system.action;

import com.dgsoft.common.system.model.BusinessDefine;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/11/13
 * Time: 3:52 PM
 */
@Name("roleBusinessPick")
@Scope(ScopeType.CONVERSATION)
public class RoleBusinessPick {

    @In
    private RoleHome roleHome;

    private List<BusinessDefine> selectBusinessList;

    private List<BusinessDefine> businessList;

    public List<BusinessDefine> getSelectBusinessList() {
        return selectBusinessList;
    }

    public void setSelectBusinessList(List<BusinessDefine> selectBusinessList) {
        this.selectBusinessList = selectBusinessList;
    }

    public List<BusinessDefine> getBusinessList() {
        return businessList;
    }

    public void setBusinessList(List<BusinessDefine> businessList) {
        this.businessList = businessList;
    }

    public void roleSelected(){

    }

    public void save(){
        roleHome.getInstance().getBusinessDefines().clear();
        roleHome.getInstance().getBusinessDefines().addAll(selectBusinessList);
        if (roleHome.isManaged()){
            roleHome.update();
        }else{
            roleHome.persist();
        }
    }

}
