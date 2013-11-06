package com.dgsoft.erp.action;

import com.dgsoft.common.system.model.Organization;
import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.Accounting;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Roles;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 2:04 PM
 */
@Name("accountingHome")
@Roles({
        @Role(name = "debitAccountHome", scope = ScopeType.CONVERSATION),
        @Role(name = "creditAccountHome", scope = ScopeType.CONVERSATION)
})
public class AccountingHome extends ErpSimpleEntityHome<Accounting> {

    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        UITree tree = (UITree) selectionChangeEvent.getSource();

        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);
        setId(((Organization) tree.getRowData()).getId());
        tree.setRowKey(storedKey);
    }


    private String selectId;

    public String getSelectId() {
        return selectId;
    }

    public void setSelectId(String selectId) {
        this.selectId = selectId;
    }

    public Accounting.Direction[] getDirections(){
        return Accounting.Direction.values();
    }

    @Override
    public String createNew() {
        if (selectId == null || selectId.trim().equals(""))
            getInstance().setRoot(true);
        return super.createNew();
    }

    public void rootChangeListener() {
        if (!getInstance().isRoot()) {
            getInstance().setAccounting(getEntityManager().find(Accounting.class,selectId));
        }else{
            getInstance().setAccounting(null);
        }
        if (getInstance().getAccounting() != null){
            getInstance().setDirection(getInstance().getAccounting().getDirection());
            getInstance().setAccountingType(getInstance().getAccounting().getAccountingType());
        }
    }

}
