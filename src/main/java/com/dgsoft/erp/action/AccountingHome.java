package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.Accounting;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Name;
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
public class AccountingHome extends ErpSimpleEntityHome<Accounting> {


    @End
    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        UITree tree = (UITree) selectionChangeEvent.getSource();

        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);
        setId(((Accounting) tree.getRowData()).getId());
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
    @Begin(flushMode = FlushModeType.MANUAL)
    public String createNew() {
        log.debug("call create new");
        if (selectId == null || selectId.trim().equals("")){
            getInstance().setRoot(true);
        }else{
            getInstance().setRoot(false);
        }
        rootChangeListener();
        return super.createNew();
    }

    @Override
    protected boolean wire(){
        if (getInstance().isRoot()){
            getInstance().setAccounting(null);
        }else{
            getInstance().setAccounting(getEntityManager().find(Accounting.class,selectId));
            getInstance().setDirection(getInstance().getAccounting().getDirection());
            getInstance().setAccountingType(getInstance().getAccounting().getAccountingType());
        }
        return true;
    }

    public void rootChangeListener() {
        if (selectId == null || selectId.trim().equals("")){
            return;
        }
        if (!getInstance().isRoot()) {
            getInstance().setAccounting(getEntityManager().find(Accounting.class,selectId));
            getInstance().setDirection(null);
            getInstance().setAccountingType(null);
        }else{
            getInstance().setAccounting(null);
        }
        if (getInstance().getAccounting() != null){
            getInstance().setDirection(getInstance().getAccounting().getDirection());
            getInstance().setAccountingType(getInstance().getAccounting().getAccountingType());
        }
    }

}
