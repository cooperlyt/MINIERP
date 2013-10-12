package com.dgsoft.erp.action;

import com.dgsoft.common.system.AuthenticationInfo;
import com.dgsoft.common.system.action.EmployeeHome;
import com.dgsoft.common.system.model.Employee;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreArea;
import com.dgsoft.erp.model.StoreManager;
import com.google.common.collect.Iterators;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import javax.persistence.EntityManager;
import javax.swing.tree.TreeNode;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/25/13
 * Time: 7:29 PM
 */
@Name("storeHome")
public class StoreHome extends ErpEntityHome<Store> {

    @In
    private EntityManager systemEntityManager;

    @In
    private FacesMessages facesMessages;

    @In
    private AuthenticationInfo authInfo;

    private List<Employee> employees = new ArrayList<Employee>();

    private String operEmpId;

    private boolean editing = false;

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public String getOperEmpId() {
        return operEmpId;
    }

    public void setOperEmpId(String operEmpId) {
        this.operEmpId = operEmpId;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public void removeEmp() {
        if ((operEmpId != null) && (!"".equals(operEmpId.trim()))) {
            for (Employee emp : employees) {
                if (emp.getId().equals(operEmpId)) {
                    log.debug("emp is remove:" + operEmpId);
                    employees.remove(emp);
                    return;
                }
            }
        }
    }

    public void selectEmployee() {
        Employee emp = systemEntityManager.find(Employee.class, operEmpId);
        if (employees.contains(emp)) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "employeeAlreadyStoreManager", emp.getPerson().getName());
        } else {
            employees.add(emp);
            log.debug("add employee :" + employees.size());
        }
    }


    @Override
    protected Store createInstance() {
        return new Store(true);
    }

    @Override
    public void setId(Object id) {
        super.setId(id);
        if (this.isIdDefined())
            editing = false;

    }

    @Override
    protected void initInstance() {

        super.initInstance();
        //selectStoreArea = null;
        List<StoreManager> storeManagerList = new ArrayList<StoreManager>(getInstance().getStoreManagers());
        if (storeManagerList.isEmpty()) {
            log.debug("call employee clear for initInstance");
            employees.clear();
            return;
        }

        List<String> empIds = new ArrayList<String>(storeManagerList.size());
        for (StoreManager storeManager : storeManagerList) {
            empIds.add(storeManager.getEmpId());
        }

        employees = systemEntityManager.createQuery("select emp from Employee emp left join fetch emp.person where emp.id in (:empIds)").setParameter("empIds", empIds).getResultList();
    }

    @Override
    protected boolean wire() {
        getInstance().getStoreManagers().clear();

        for (Employee emp : employees) {
            getInstance().getStoreManagers().add(new StoreManager(getInstance(), emp.getId()));
        }
        return true;
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

    @Factory(value = "myStoreAreaTree", scope = ScopeType.CONVERSATION)
    public List<StoreTreeNode> getStoreAreaTree() {
        List<Store> stores = getMyStoreList();
        List<StoreTreeNode> result = new ArrayList<StoreTreeNode>(stores.size());
        for(Store store: stores){
            result.add(new StoreTreeNode(store));
        }
        return result;
    }

    @Factory(value = "myStores", scope = ScopeType.CONVERSATION)
    public List<Store> getMyStoreList() {
        List<StoreManager> storeManagerList = getEntityManager().createQuery("select storeManager from StoreManager storeManager join fetch storeManager.store where storeManager.empId = :empId and storeManager.store.enable = true").setParameter("empId", authInfo.getLoginEmployee().getId()).getResultList();
        List<Store> myStores = new ArrayList();
        for (StoreManager storeManager : storeManagerList) {
            myStores.add(storeManager.getStore());
        }
        return myStores;
    }

    public static class StoreAreaTreeNode implements TreeNode {

        private StoreArea area;

        private List<StoreAreaTreeNode> childs;

        private TreeNode parent;

        public StoreAreaTreeNode(StoreArea area, TreeNode parent) {
            this.area = area;
            this.parent = parent;
            childs = new ArrayList<StoreAreaTreeNode>(area.getStoreAreas().size());
            for (StoreArea storeArea : area.getStoreAreas()) {
                childs.add(new StoreAreaTreeNode(storeArea, this));
            }
            Collections.sort(childs, new Comparator<StoreAreaTreeNode>() {
                @Override
                public int compare(StoreAreaTreeNode o1, StoreAreaTreeNode o2) {
                    return o1.area.getId().compareTo(o2.area.getId());
                }
            });
        }

        public StoreArea getArea() {
            return area;
        }

        public void setArea(StoreArea area) {
            this.area = area;
        }

        public String getType() {
            return "area";
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            return childs.get(childIndex);
        }

        @Override
        public int getChildCount() {
            return childs.size();
        }

        @Override
        public TreeNode getParent() {
            return parent;
        }

        @Override
        public int getIndex(TreeNode node) {
            return childs.indexOf(node);
        }

        @Override
        public boolean getAllowsChildren() {
            return !childs.isEmpty();
        }

        @Override
        public boolean isLeaf() {
            return childs.isEmpty();
        }

        @Override
        public Enumeration children() {
            return Iterators.asEnumeration(childs.iterator());
        }
    }

    public static class StoreTreeNode implements TreeNode {

        private Store store;

        private List<StoreAreaTreeNode> childs;

        public StoreTreeNode(Store store){
            this.store = store;
            childs = new ArrayList<StoreAreaTreeNode>();
            for (StoreArea storeArea : store.getRootStoreAreaList(true)) {
                childs.add(new StoreAreaTreeNode(storeArea, this));
            }
            Collections.sort(childs, new Comparator<StoreAreaTreeNode>() {
                @Override
                public int compare(StoreAreaTreeNode o1, StoreAreaTreeNode o2) {
                    return o1.area.getId().compareTo(o2.area.getId());
                }
            });
        }

        public Store getStore() {
            return store;
        }

        public void setStore(Store store) {
            this.store = store;
        }

        public String getType() {
            return "store";
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            return childs.get(childIndex);
        }

        @Override
        public int getChildCount() {
            return childs.size();
        }

        @Override
        public TreeNode getParent() {
            return null;
        }

        @Override
        public int getIndex(TreeNode node) {
            return childs.indexOf(node);
        }

        @Override
        public boolean getAllowsChildren() {
            return true;
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

        @Override
        public Enumeration children() {
            return Iterators.asEnumeration(childs.iterator());
        }
    }

}
