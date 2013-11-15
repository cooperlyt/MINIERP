package com.dgsoft.erp.action;

import com.dgsoft.common.system.AuthenticationInfo;
import com.dgsoft.common.system.action.RoleHome;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.Store;
import com.dgsoft.erp.model.StoreArea;
import com.google.common.collect.Iterators;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityNotFoundException;
import org.jboss.seam.security.Identity;

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

    @Transactional
    public synchronized String getStoreRole(String id) {
        this.setId(id);
        try {
            return getInstance().getRoleId();
        } catch (EntityNotFoundException e) {
            setId(null);
            return "erp.storage.manager";
        }
    }

    @Transactional
    public synchronized String getStoreShipRole(String id){
        this.setId(id);
        try {
            return getInstance().getShipRole();
        } catch (EntityNotFoundException e) {
            setId(null);
            return "erp.storage.manager";
        }
    }

    @In
    private Identity identity;


    private boolean editing = false;

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
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
        for (Store store : stores) {
            result.add(new StoreTreeNode(store));
        }
        return result;
    }

    public String getStoreRoleId(String storeId) {
        Store store = getEntityManager().find(Store.class, storeId);
        if (store == null) {
            return null;
        } else {
            return store.getRoleId();
        }
    }


    @Factory(value = "myStores", scope = ScopeType.CONVERSATION)
    public List<Store> getMyStoreList() {

        List<Store> result = new ArrayList<Store>();

        for (Store store : getEntityManager().createQuery("select store from Store store where store.enable = true", Store.class).getResultList()) {
            if (identity.hasRole(store.getRoleId())) {
                result.add(store);
            }
        }

        return result;

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

        public StoreTreeNode(Store store) {
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
