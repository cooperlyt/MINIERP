package com.dgsoft.erp.action;

import com.dgsoft.common.OrderBeanComparator;
import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.ResUnit;
import com.dgsoft.erp.model.UnitGroup;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.richfaces.component.UIExtendedDataTable;

import javax.faces.event.AjaxBehaviorEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/19/13
 * Time: 6:27 PM
 */
@Name("unitGroupHome")
public class UnitGroupHome extends ErpEntityHome<UnitGroup> {

    private boolean editing = false;

    private Boolean editResUnit = null;

    @In
    private FacesMessages facesMessages;

    @In
    private ActionExecuteState actionExecuteState;

    @DataModelSelection
    private ResUnit selectResUnit;

    @DataModel("resUnitLists")
    private List<ResUnit> unitList = new ArrayList<ResUnit>();


    public Boolean getEditResUnit() {
        return editResUnit;
    }

    public void setEditResUnit(Boolean editResUnit) {
        this.editResUnit = editResUnit;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public ResUnit getSelectResUnit() {
        return selectResUnit;
    }

    public void setSelectResUnit(ResUnit selectResUnit) {
        this.selectResUnit = selectResUnit;
    }

    @Factory("unitGroupTypes")
    public UnitGroup.UnitGroupType[] getUnitGroupTypes() {
        return UnitGroup.UnitGroupType.values();
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void beginCreateResUnit() {
        selectResUnit = new ResUnit(getInstance());
        actionExecuteState.clearState();
        editResUnit = false;
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void beginEditResUnit() {
        actionExecuteState.clearState();
        editResUnit = true;
    }

    public void saveResUnit() {
        if (!editResUnit){
            OrderBeanComparator.addToLast(selectResUnit, unitList);
            getInstance().getResUnits().add(selectResUnit);
        }
        actionExecuteState.setLastState("success");
    }

    public void upResUnit() {
        OrderBeanComparator.up(selectResUnit, unitList);
    }

    public void downResUnit() {
        OrderBeanComparator.down(selectResUnit, unitList);
    }

    public void removeResUnit() {
        unitList.remove(selectResUnit);
        getInstance().getResUnits().remove(selectResUnit);
    }

    @Override
    public void setId(Object id) {
        super.setId(id);
        if (isIdDefined())
            editing = false;
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public void beginUnitGroupCU() {
        editing = true;
    }

    @End
    public void cancel() {
        editing = false;
    }

    private boolean verifyResUnit() {
        if (unitList.isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "ResUnitGroupEmptyError");
            return false;
        }
        if (getInstance().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            if ((unitList.size() != 2)) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "FloatConvertUnitCountError");
                return false;
            }
            if ((unitList.get(0).getConversionRate().intValue() != 1) && (unitList.get(1).getConversionRate().intValue() != 1)){
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "FloatConvertNoMainError");
                return false;
            }

        }


        return true;
    }

    @Override
    protected boolean verifyUpdateAvailable() {
        return verifyResUnit();
    }

    @Override
    protected boolean verifyPersistAvailable() {
        return verifyResUnit();
    }

    @Override
    protected boolean verifyRemoveAvailable() {
        if (!getInstance().getReses().isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "CantDeleteUnitGroup");
            return false;
        }
        return true;
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
    protected void initInstance() {
        super.initInstance();
        if (isIdDefined()) {
            unitList =  getInstance().getResUnitList();
        } else {
            unitList = new ArrayList<ResUnit>();
        }
    }

}
