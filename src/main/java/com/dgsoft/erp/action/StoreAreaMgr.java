package com.dgsoft.erp.action;

import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreArea;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/2/13
 * Time: 10:27 PM
 */
@Name("storeAreaMgr")
@Scope(ScopeType.PAGE)
public class StoreAreaMgr {

    @In(create = true)
    private StoreAreaHome storeAreaHome;

    private String selectStoreId;

    private String selectStoreAreaId;

    private String selectTitle;

    private boolean editing;

    public String getSelectTitle() {
        return selectTitle;
    }

    public void setSelectTitle(String selectTitle) {
        this.selectTitle = selectTitle;
    }

    public boolean isStoreAreaSelected(){
        return (selectStoreAreaId != null) && (!"".equals(selectStoreAreaId));
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    @End
    public void treeSectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        UITree tree = (UITree) selectionChangeEvent.getSource();

        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);

        selectChanged(tree.getRowData());

        tree.setRowKey(storedKey);
        editing = false;
    }


    public void selectChanged(Object selectData) {
        String storeTitle = "";
        String areaTitle = "";
        if (selectData != null) {
            if (selectData instanceof StoreHome.StoreTreeNode) {
                storeTitle = ((StoreHome.StoreTreeNode) selectData).getStore().getName();
                selectStoreId = ((StoreHome.StoreTreeNode) selectData).getStore().getId();
                selectStoreAreaId = null;
            } else if (selectData instanceof StoreHome.StoreAreaTreeNode) {
                StoreArea storeArea = ((StoreHome.StoreAreaTreeNode) selectData).getArea();
                selectStoreId = storeArea.getStore().getId();
                selectStoreAreaId = storeArea.getId();
                storeAreaHome.setId(selectStoreAreaId);
                areaTitle = storeArea.getName();
                while (storeArea.getStoreArea() != null) {
                    storeArea = storeArea.getStoreArea();
                    areaTitle = storeArea.getName() + " > " + areaTitle;
                }
                storeTitle = storeArea.getStore().getName();
            }
        }

        selectTitle = storeTitle + ": " + areaTitle;
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public void createStoreArea() {
        //storeHome.setId(selectStoreId);
        StoreArea parentStoreArea = null;
        if ((selectStoreAreaId != null) && (!"".equals(selectStoreAreaId))) {
            storeAreaHome.setId(selectStoreAreaId);
            parentStoreArea = storeAreaHome.getInstance();
        }
        storeAreaHome.clearInstance();
        storeAreaHome.getInstance().setStoreArea(parentStoreArea);
        storeAreaHome.getInstance().setStore(storeAreaHome.getEntityManager().find(Store.class, selectStoreId));
        storeAreaHome.getInstance().setEnable(true);
        editing = true;
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public void editStoreArea() {
        storeAreaHome.setId(selectStoreAreaId);
        editing = true;
    }

    @End
    public void cancel(){
        storeAreaHome.refresh();
        editing = false;
    }

    @End
    public String save() {
        String result;
        if (storeAreaHome.isManaged()) {
            result = storeAreaHome.update();
            if (!"updated".equals(result)) {
                return result;
            }
        } else {
            result = storeAreaHome.persist();
            if (!"persisted".equals(result)) {
                return result;
            }
        }
        editing = false;
        return result;
    }

}
