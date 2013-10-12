package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResCategory;
import com.google.common.collect.Iterators;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/23/13
 * Time: 10:13 PM
 */
@Name("resCategoryHome")
public class ResCategoryHome extends ErpEntityHome<ResCategory> {

    private String parentId;

    @In
    private FacesMessages facesMessages;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public class ResNode implements TreeNode {

        private Res res;

        private TreeNode parent;

        public ResNode(TreeNode parent, Res res) {
            this.parent = parent;
            this.res = res;
        }

        public String getType() {
            return "res";
        }

        public Res getRes() {
            return res;
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
            return parent;
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

    public class ResCategoryNode implements TreeNode {


        private List<TreeNode> childList = new ArrayList<TreeNode>();

        private TreeNode parent;

        public ResCategoryNode(TreeNode parent, ResCategory resCategory) {
            this.parent = parent;
            this.resCategory = resCategory;
        }

        private ResCategory resCategory;

        public String getType() {
            return "category";
        }

        public ResCategory getResCategory() {
            return resCategory;
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
            if (!(obj instanceof ResCategoryNode)) {
                return false;
            }
            if (((ResCategoryNode) obj).resCategory.getId().equals(resCategory.getId())) {
                return true;
            } else {
                return false;
            }

        }

        @Override
        public int hashCode() {
            return resCategory.getId().hashCode();
        }
    }

    @Override
    protected ResCategory createInstance() {
        ResCategory result = new ResCategory(true);
        if (parentId == null || parentId.trim().equals("")) {
            result.setRoot(true);
        } else {
            result.setResCategory(getEntityManager().find(ResCategory.class, parentId));
            result.setCommodity(result.getResCategory().isCommodity());
            result.setMaterial(result.getResCategory().isMaterial());
            result.setSemiProduct(result.getResCategory().isSemiProduct());
        }

        return result;
    }

    private void generateChildrenNode(ResCategoryNode node, boolean addRes,
                                      boolean hasCommodity, boolean hasMaterial, boolean hasSemi, boolean hasDisable) {
        if (node.resCategory.getResCategories().isEmpty() && node.resCategory.getReses().isEmpty())
            return;

        if (!addRes && node.resCategory.getResCategories().isEmpty()) {
            return;
        }

        if (addRes) {
            for (Res res : node.resCategory.getResList()) {
                if (res.isEnable() || hasDisable) {
                    node.childList.add(new ResNode(node, res));
                }
            }
        }

        for (ResCategory category : node.resCategory.getResCategoryList()) {
            if (category.isEnable() || hasDisable) {
                if ((hasCommodity && category.isCommodity()) || (hasMaterial && category.isMaterial())
                        || (hasSemi && category.isSemiProduct())) {
                    ResCategoryNode childrenNode = new ResCategoryNode(node, category);
                    node.childList.add(childrenNode);
                    generateChildrenNode(childrenNode, addRes, hasCommodity, hasMaterial, hasSemi, hasDisable);
                }
            }
        }

    }

    @Factory(value = "categoryManagerTree", scope = ScopeType.CONVERSATION)
    public List<ResCategoryNode> getResCategoryManagerTree() {
        List<ResCategoryNode> result = new ArrayList<ResCategoryNode>();
        List<ResCategory> rootCategories = getEntityManager().createQuery("select resCategory from ResCategory resCategory where resCategory.root = true and resCategory.enable = true").getResultList();
        for (ResCategory resCategory : rootCategories) {
            ResCategoryNode rootNode = new ResCategoryNode(null, resCategory);
            generateChildrenNode(rootNode, false, true, true, true, true);
            result.add(rootNode);
        }
        return result;
    }

    @Factory(value="resCategoryTree", scope = ScopeType.CONVERSATION)
    public List<ResCategoryNode> getResCategoryTree(){
        List<ResCategoryNode> result = new ArrayList<ResCategoryNode>();
        List<ResCategory> rootCategories = getEntityManager().createQuery("select resCategory from ResCategory resCategory where resCategory.root = true and resCategory.enable = true").getResultList();
        for (ResCategory resCategory : rootCategories) {
            ResCategoryNode rootNode = new ResCategoryNode(null, resCategory);
            generateChildrenNode(rootNode, true, true, true, true, false);
            result.add(rootNode);
        }
        return result;
    }


    @Override
    protected boolean wire() {

        if (getInstance().isRoot()) {
            getInstance().setResCategory(null);
        } else if (!isManaged()) {
            if (parentId == null || "".equals(parentId.trim())) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "category_not_have_parent");
                return false;
            }
            getInstance().setResCategory(getEntityManager().find(ResCategory.class, parentId));
        }
        return true;
    }

    private boolean verifyData() {
        if (!getInstance().isSemiProduct() && !getInstance().isMaterial() && !getInstance().isCommodity()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "resTypeRequest");
            return false;
        }

        return true;
    }

    @Override
    protected boolean verifyUpdateAvailable() {
        return verifyData();
    }

    @Override
    protected boolean verifyPersistAvailable() {
        return verifyData();
    }

    public void assignParentType() {
        if (!getInstance().isRoot()) {
            wire();
            getInstance().setCommodity(getInstance().getResCategory().isCommodity());
            getInstance().setMaterial(getInstance().getResCategory().isMaterial());
            getInstance().setSemiProduct(getInstance().getResCategory().isSemiProduct());
        }
    }

    public String getCategoryTitle() {
        String result = "";
        if (isIdDefined()) {
            ResCategory category = getInstance();
            result = category.getName();
            while (!category.isRoot()) {
                category = category.getResCategory();
                result = category.getName() + " > " + result;
            }
        }
        return result;
    }

}
