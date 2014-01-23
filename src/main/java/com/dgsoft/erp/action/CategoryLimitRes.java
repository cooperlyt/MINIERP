package com.dgsoft.erp.action;

import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResCategory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/24/13
 * Time: 11:37 AM
 */

@Name("categoryLimitRes")
public class CategoryLimitRes {

    @In
    private ResCategoryHome resCategoryHome;

    @Out(scope = ScopeType.PAGE)
    private List<Res> categoryRes = new ArrayList<Res>();


    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        UITree tree = (UITree) selectionChangeEvent.getSource();

        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);


        resCategoryHome.setId(((ResCategory) tree.getRowData()).getId());
        refresh();
        tree.setRowKey(storedKey);
    }


    @Observer("org.jboss.seam.afterTransactionSuccess.Res")
    public void refresh() {
        if (resCategoryHome.isIdDefined()){
            categoryRes = new ArrayList<Res>(resCategoryHome.getInstance().getReses());
            Collections.sort(categoryRes,new Comparator<Res>() {
                @Override
                public int compare(Res o1, Res o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
    }
}
