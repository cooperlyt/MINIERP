package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.model.Organization;
import com.google.common.collect.Iterators;
import org.jboss.seam.annotations.Name;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 8/26/13
 * Time: 9:17 AM
 */
@Name("organizationHome")
public class OrganizationHome extends SystemEntityHome<Organization> {

    private String parentOrgId;

    public String getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(String parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    @Override
    protected Organization createInstance() {
        Organization result = new Organization();

        if ((parentOrgId == null) || parentOrgId.trim().equals("")){
            result.setRoot(true);
        }else{

            result.setOrganization(getEntityManager().find(Organization.class,parentOrgId));
            if (result.getOrganization() != null){
                result.setRoot(false);
            }else
                result.setRoot(true);
        }

        return result;
    }


    @Override
    public String update() {
        if (getInstance().isRoot()){
            getInstance().setOrganization(null);
        }
        return super.update();
    }

    @Override
    public String persist() {
        if (getInstance().isRoot()){
            getInstance().setOrganization(null);
        }
        return super.persist();
    }


    public static class OrgNode implements TreeNode {

        private List<TreeNode> childList = new ArrayList<TreeNode>();

        private TreeNode parent;

        private Organization org;

        public OrgNode(TreeNode parent, Organization org) {
            this.parent = parent;
            this.org = org;
        }

        public String getType() {
            return "org";
        }

        public Organization getOrg() {
            return org;
        }

        public void setOrg(Organization org) {
            this.org = org;
        }

        public List<TreeNode> getChildList() {
            return childList;
        }

        public void setChildList(List<TreeNode> childList) {
            this.childList = childList;
        }

        public void setParent(TreeNode parent) {
            this.parent = parent;
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            return childList.get(childIndex);
        }

        @Override
        public int getChildCount() {
            return childList.size();
        }

        @Override
        public TreeNode getParent() {
            return parent;
        }

        @Override
        public int getIndex(TreeNode node) {
            return childList.indexOf(node);
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
            return Iterators.asEnumeration(childList.iterator());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof OrgNode)) {
                return false;
            }
            if (((OrgNode) obj).org.getId().equals(org.getId())) {
                return true;
            } else {
                return false;
            }

        }

        @Override
        public int hashCode() {
            return org.getId().hashCode();
        }
    }

}
