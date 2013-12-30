package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResCategory;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StoreRes;
import com.google.common.collect.Iterators;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.EnumSet;
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

    @Factory("resTypes")
    public ResCategory.ResType[] getResTypes() {
        return ResCategory.ResType.values();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public static class StoreResNode implements TreeNode {

        private TreeNode parent;

        private StoreRes storeRes;

        public StoreResNode(TreeNode parent, StoreRes storeRes) {
            this.parent = parent;
            this.storeRes = storeRes;
        }

        public StoreRes getStoreRes() {
            return storeRes;
        }

        public void setStoreRes(StoreRes storeRes) {
            this.storeRes = storeRes;
        }

        public String getType() {
            return "storeRes";
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

    public class ResNode implements TreeNode {

        private Res res;

        private TreeNode parent;

        private boolean addStoreResNode;

        private List<TreeNode> childNodes = null;

        public ResNode(TreeNode parent, Res res, boolean addStoreResNode, boolean hasDisable) {
            this.parent = parent;
            this.res = res;
            this.addStoreResNode = addStoreResNode;
            if (addStoreResNode) {
                childNodes = new ArrayList<TreeNode>();
                for (StoreRes storeRes : res.getStoreResList()) {
                    if (hasDisable || storeRes.isEnable()) {
                        childNodes.add(new StoreResNode(this, storeRes));
                    }
                }
            }
        }

        public String getType() {
            return "res";
        }

        public Res getRes() {
            return res;
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            if (addStoreResNode) {
                return childNodes.get(childIndex);
            } else
                return null;
        }

        @Override
        public int getChildCount() {
            if (addStoreResNode) {
                return childNodes.size();
            } else
                return 0;
        }

        @Override
        public TreeNode getParent() {
            return parent;
        }

        @Override
        public int getIndex(TreeNode node) {
            if (addStoreResNode) {
                return childNodes.indexOf(node);
            } else
                return 0;
        }

        @Override
        public boolean getAllowsChildren() {
            return addStoreResNode;
        }

        @Override
        public boolean isLeaf() {
            return !addStoreResNode;
        }

        @Override
        public Enumeration children() {
            if (addStoreResNode) {
                return Iterators.asEnumeration(childNodes.iterator());
            } else
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
        }

        return result;
    }

    private void generateChildrenNode(ResCategoryNode node, boolean addRes, boolean addStoreRes, boolean hasDisable, EnumSet<ResCategory.ResType> contains) {
        if (node.resCategory.getResCategories().isEmpty() && node.resCategory.getReses().isEmpty())
            return;

        if (!addRes && node.resCategory.getResCategories().isEmpty()) {
            return;
        }


        if (addRes) {
            for (Res res : node.resCategory.getResList()) {
                if (res.isEnable() || hasDisable) {
                    node.childList.add(new ResNode(node, res, addStoreRes, hasDisable));
                }
            }
        }


        for (ResCategory category : node.resCategory.getResCategoryList()) {
            if (category.isEnable() || hasDisable) {
                if (contains.contains(category.getType())) {
                    ResCategoryNode childrenNode = new ResCategoryNode(node, category);
                    node.childList.add(childrenNode);
                    generateChildrenNode(childrenNode, addRes, addStoreRes, hasDisable, contains);
                }
            }
        }

    }

    //private static final ResCategory.ResType[] RESTRICTIONS = {};

    @Factory(value = "categoryManagerTree", scope = ScopeType.CONVERSATION)
    public List<ResCategoryNode> getResCategoryManagerTree() {
        List<ResCategoryNode> result = new ArrayList<ResCategoryNode>();
        List<ResCategory> rootCategories = getEntityManager().createQuery("select resCategory from ResCategory resCategory where resCategory.root = true and resCategory.enable = true").getResultList();
        for (ResCategory resCategory : rootCategories) {
            ResCategoryNode rootNode = new ResCategoryNode(null, resCategory);
            generateChildrenNode(rootNode, false, false, true, EnumSet.allOf(ResCategory.ResType.class));
            result.add(rootNode);
        }
        return result;
    }

    @Factory(value = "resCategoryTree", scope = ScopeType.CONVERSATION)
    public List<ResCategoryNode> getResCategoryTree() {
        List<ResCategoryNode> result = new ArrayList<ResCategoryNode>();
        List<ResCategory> rootCategories = getEntityManager().createQuery("select resCategory from ResCategory resCategory where resCategory.root = true and resCategory.enable = true").getResultList();
        for (ResCategory resCategory : rootCategories) {
            ResCategoryNode rootNode = new ResCategoryNode(null, resCategory);
            generateChildrenNode(rootNode, true, false, false, EnumSet.allOf(ResCategory.ResType.class));
            result.add(rootNode);
        }
        return result;
    }


    @Factory(value = "allStoreResTree", scope = ScopeType.CONVERSATION)
    public List<ResCategoryNode> getAllStoreResTree(){
        List<ResCategoryNode> result = new ArrayList<ResCategoryNode>();
        List<ResCategory> rootCategories = getEntityManager().createQuery("select resCategory from ResCategory resCategory where resCategory.root = true and resCategory.enable = true").getResultList();
        for (ResCategory resCategory : rootCategories) {
            ResCategoryNode rootNode = new ResCategoryNode(null, resCategory);
            generateChildrenNode(rootNode, true, true, false,  EnumSet.allOf(ResCategory.ResType.class));
            result.add(rootNode);
        }
        return result;
    }

    @Factory(value = "allResTree", scope = ScopeType.CONVERSATION)
    public List<ResCategoryNode> getAllResTree(){
        List<ResCategoryNode> result = new ArrayList<ResCategoryNode>();
        List<ResCategory> rootCategories = getEntityManager().createQuery("select resCategory from ResCategory resCategory where resCategory.root = true and resCategory.enable = true").getResultList();
        for (ResCategory resCategory : rootCategories) {
            ResCategoryNode rootNode = new ResCategoryNode(null, resCategory);
            generateChildrenNode(rootNode, true, false, false,  EnumSet.allOf(ResCategory.ResType.class));
            result.add(rootNode);
        }
        return result;
    }

    private List<ResCategoryNode> getStoreChangeResLimitTree(StockChange.StoreChangeType changeType, boolean addStoreRes){
        List<ResCategoryNode> result = new ArrayList<ResCategoryNode>();
        List<ResCategory> rootCategories = getEntityManager().createQuery("select resCategory from ResCategory resCategory where resCategory.root = true and resCategory.enable = true and resCategory.type in (:changeTypes)").setParameter("changeTypes",changeType.getResTypes()).getResultList();
        for (ResCategory resCategory : rootCategories) {
            ResCategoryNode rootNode = new ResCategoryNode(null, resCategory);
            generateChildrenNode(rootNode, true, addStoreRes, false, changeType.getResTypes());
            result.add(rootNode);
        }
        return result;
    }

    @Factory(value="produceInResTree",scope = ScopeType.CONVERSATION)
    public List<ResCategoryNode> getProduceInResTree(){
        return  getStoreChangeResLimitTree(StockChange.StoreChangeType.PRODUCE_IN,false);
    }

    @Factory(value = "produceInStoreResTree",scope = ScopeType.CONVERSATION)
    public List<ResCategoryNode> getProduceInStoreResTree(){
        return  getStoreChangeResLimitTree(StockChange.StoreChangeType.PRODUCE_IN,true);
    }

    @Factory(value = "saleResTree",scope = ScopeType.CONVERSATION)
    public List<ResCategoryNode> getSaleResTree(){
        return  getStoreChangeResLimitTree(StockChange.StoreChangeType.SELL_OUT,false);
    }

    @Factory(value = "saleStoreResTree", scope = ScopeType.CONVERSATION)
    public List<ResCategoryNode> getSaleStoreTree(){
        return  getStoreChangeResLimitTree(StockChange.StoreChangeType.SELL_OUT,true);
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
            ResCategory parentResCategory = getEntityManager().find(ResCategory.class, parentId);
            getInstance().setResCategory(parentResCategory);
            getInstance().setType(parentResCategory.getType());
        }
        return true;
    }

    public void assignParentType() {
        if (!getInstance().isRoot()) {
            wire();
            getInstance().setType(getInstance().getResCategory().getType());
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
