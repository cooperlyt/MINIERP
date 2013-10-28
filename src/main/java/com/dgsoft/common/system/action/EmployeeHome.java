package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.model.Employee;
import com.dgsoft.common.system.model.Organization;
import com.dgsoft.common.system.model.Role;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.faces.event.ValueChangeEvent;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/9/13
 * Time: 10:29 AM
 */
@Name("employeeHome")
public class EmployeeHome extends SystemEntityHome<Employee> {

    @In(create = true)
    private OrganizationHome organizationHome;

    @In(create = true)
    private PersonHome personHome;

    @In
    private FacesMessages facesMessages;

    private List<Role> selectedRoles = new ArrayList<Role>();

    public List<Role> getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(List<Role> selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    @Override
    public Employee createInstance() {
        return new Employee(organizationHome.getInstance());
    }

    @Override
    public String persist() {
        getInstance().setPerson(personHome.getInstance());
        return super.persist();
    }

    public void verifyIdAvailable(ValueChangeEvent e) {
        String id = (String) e.getNewValue();
        if (!isIdAvailable(id)) {
            log.info("add confirm message");
            facesMessages.addToControlFromResourceBundle(e.getComponent().getId(), StatusMessage.Severity.ERROR, "fieldConflict", id);
        }
    }

    @Override
    protected boolean verifyPersistAvailable() {
        String id = this.getInstance().getId();
        if (!isIdAvailable(id)) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "fieldConflict", id);
            return false;
        } else if (!isPersonAvailable()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "employee_person_exists");
            return false;
        } else
            return true;

    }

    public boolean isPersonAvailable() {
        return getEntityManager().createQuery("select emp from Employee emp where emp.person.id =?1").setParameter(1, personHome.getInstance().getId()).getResultList().isEmpty();
    }

    public boolean isIdAvailable(String id) {
        return getEntityManager().createQuery("select emp from Employee emp where emp.id = ?1").setParameter(1, id).getResultList().size() == 0;
    }


    public void readPower() {
        selectedRoles.clear();
        selectedRoles.addAll(getInstance().getRoles());
    }


    @End
    public String savePowerAssign() {
        getInstance().getRoles().clear();
        getInstance().getRoles().addAll(selectedRoles);

        return super.update();
    }


    private void generateChildrenNode(OrganizationHome.OrgNode orgNode){
        if (orgNode.getOrg().getOrganizations().isEmpty() && orgNode.getOrg().getEmployees().isEmpty()){
            return;
        }
        for (Employee emp: orgNode.getOrg().getEmployeeList()){
            if (emp.isEnable()){
                orgNode.getChildList().add(new EmpNode(orgNode,emp));
            }
        }

        for (Organization org: orgNode.getOrg().getOrganizationList()){
            OrganizationHome.OrgNode childOrg = new OrganizationHome.OrgNode(orgNode,org);
            orgNode.getChildList().add(childOrg);
            generateChildrenNode(childOrg);
        }


    }

    @Factory(value = "employeeTree", scope = ScopeType.CONVERSATION)
    public List<TreeNode> getEmployeeTree() {
        List<TreeNode> result = new ArrayList<TreeNode>();
        List<Organization> rootOrg = getEntityManager().createQuery("select org from Organization org where org.root = true").getResultList();
        for (Organization org: rootOrg){
            OrganizationHome.OrgNode orgRootNode = new OrganizationHome.OrgNode(null,org);
            generateChildrenNode(orgRootNode);
            result.add(orgRootNode);
        }

        return result;
    }

    public class EmpNode implements TreeNode{

        private OrganizationHome.OrgNode orgNode;

        private Employee emp;

        public EmpNode(OrganizationHome.OrgNode orgNode,Employee emp){
            this.orgNode = orgNode;
            this.emp = emp;
        }


        public String getType(){
            return "emp";
        }

        public Employee getEmp() {
            return emp;
        }

        public void setEmp(Employee emp) {
            this.emp = emp;
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            return null;
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        public TreeNode getParent() {
            return orgNode;
        }

        @Override
        public int getIndex(TreeNode node) {
            return 0;
        }

        @Override
        public boolean getAllowsChildren() {
            return false;
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

        @Override
        public Enumeration children() {
            return null;
        }
    }

}
