package com.dgsoft.common.system.action;

import com.dgsoft.common.system.model.Employee;
import com.dgsoft.common.system.model.Organization;
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
 * Date: 9/5/13
 * Time: 3:34 PM
 */
@Name("orgLimitEmployee")
public class OrgLimitEmployee {

    @Out(scope = ScopeType.PAGE)
    private List<Employee> orgEmployees = new ArrayList<Employee>(0);

    @In(create = true)
    private OrganizationHome organizationHome;

    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        UITree tree = (UITree) selectionChangeEvent.getSource();

        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);
        organizationHome.setId(((Organization) tree.getRowData()).getId());
        refresh();
        tree.setRowKey(storedKey);
    }

    @Observer("org.jboss.seam.afterTransactionSuccess.Employee")
    public void refresh() {
        if (organizationHome.isIdDefined()) {
            orgEmployees = new ArrayList<Employee>(organizationHome.getInstance().getEmployees());
            Collections.sort(orgEmployees, new Comparator<Employee>() {
                @Override
                public int compare(Employee o1, Employee o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
        }
    }
}
