package com.dgsoft.erp.model;
// Generated Oct 1, 2013 5:41:32 PM by Hibernate Tools 4.0.0

import com.google.common.collect.Iterators;

import javax.persistence.*;
import javax.swing.tree.TreeNode;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * Store generated by hbm2java
 */
@Entity
@Table(name = "STORE", catalog = "MINI_ERP")
public class Store implements java.io.Serializable, TreeNode {

    private String id;
    private String name;
    private String description;
    private String address;
    private String tel;
    private boolean enable;
    private boolean open;
    private Integer version;
    private String roleId;
    private String shipRole;
    private Set<StoreArea> storeAreas = new HashSet<StoreArea>(0);
    private Set<Inventory> inventories = new HashSet<Inventory>(0);
    private Set<StockChange> stockChanges = new HashSet<StockChange>(0);


    private Set<Stock> stocks = new HashSet<Stock>(0);
    private Set<Allocation> allocationsForApplyStore = new HashSet<Allocation>(0);
    private Set<Allocation> allocationsForTargetStore = new HashSet<Allocation>(0);
    private Set<Dispatch> dispatches = new HashSet<Dispatch>(0);
    private Set<ProductBackStoreIn> productBackStoreIns = new HashSet<ProductBackStoreIn>(0);

    public Store() {
    }

    public Store(boolean enable) {
        this.enable = enable;
    }

    public Store(String id, String name, boolean enable) {
        this.id = id;
        this.name = name;
        this.enable = enable;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Version
    @Column(name = "VERSION")
    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Column(name = "OPEN", nullable = false)
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Column(name = "NAME", nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION", length = 200)
    @Size(max = 200)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "ADDRESS", length = 200)
    @Size(max = 200)
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "TEL", length = 50)
    @Size(max = 50)
    public String getTel() {
        return this.tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Column(name = "ENABLE", nullable = false)
    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Column(name="ROLE_ID",nullable = false,length = 32)
    @NotNull
    @Size(max = 32)
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Column(name = "SHIP_ROLE",nullable = false,length = 32)
    @NotNull
    @Size(max = 32)
    public String getShipRole() {
        return shipRole;
    }

    public void setShipRole(String shipRole) {
        this.shipRole = shipRole;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "store", orphanRemoval = true, cascade = {CascadeType.ALL})
    public Set<StoreArea> getStoreAreas() {
        return this.storeAreas;
    }

    public void setStoreAreas(Set<StoreArea> storeAreas) {
        this.storeAreas = storeAreas;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "store")
    public Set<Inventory> getInventories() {
        return this.inventories;
    }

    public void setInventories(Set<Inventory> inventories) {
        this.inventories = inventories;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "store")
    public Set<StockChange> getStockChanges() {
        return this.stockChanges;
    }

    public void setStockChanges(Set<StockChange> stockChanges) {
        this.stockChanges = stockChanges;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "store")
    public Set<Stock> getStocks() {
        return this.stocks;
    }

    public void setStocks(Set<Stock> stocks) {
        this.stocks = stocks;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storeByApplyStore")
    public Set<Allocation> getAllocationsForApplyStore() {
        return this.allocationsForApplyStore;
    }

    public void setAllocationsForApplyStore(
            Set<Allocation> allocationsForApplyStore) {
        this.allocationsForApplyStore = allocationsForApplyStore;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storeByTargetStore")
    public Set<Allocation> getAllocationsForTargetStore() {
        return this.allocationsForTargetStore;
    }

    public void setAllocationsForTargetStore(
            Set<Allocation> allocationsForTargetStore) {
        this.allocationsForTargetStore = allocationsForTargetStore;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "store")
    public Set<Dispatch> getDispatches() {
        return this.dispatches;
    }

    public void setDispatches(Set<Dispatch> dispatches) {
        this.dispatches = dispatches;
    }

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "store")
    public Set<ProductBackStoreIn> getProductBackStoreIns() {
        return productBackStoreIns;
    }

    public void setProductBackStoreIns(Set<ProductBackStoreIn> productBackStoreIns) {
        this.productBackStoreIns = productBackStoreIns;
    }

    @Transient
    public List<StoreArea> getRootStoreAreaList(boolean containsDisable) {
        List<StoreArea> result = new ArrayList<StoreArea>();
        for (StoreArea storeArea : getStoreAreas()) {
            if (storeArea.getStoreArea() == null) {
                if (containsDisable || storeArea.isEnable())
                    result.add(storeArea);
            }
        }

        Collections.sort(result, new Comparator<StoreArea>() {
            @Override
            public int compare(StoreArea o1, StoreArea o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        return result;
    }

    @Override
    @Transient
    public TreeNode getChildAt(int childIndex) {
        return getRootStoreAreaList(false).get(childIndex);
    }

    @Override
    @Transient
    public int getChildCount() {
        return getStoreAreas().size();
    }

    @Override
    @Transient
    public TreeNode getParent() {
        return null;
    }

    @Override
    @Transient
    public int getIndex(TreeNode node) {
        return getRootStoreAreaList(false).indexOf(node);
    }

    @Override
    @Transient
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    @Transient
    public boolean isLeaf() {
        return false;
    }

    @Override
    @Transient
    public Enumeration children() {
        return Iterators.asEnumeration(getRootStoreAreaList(false).iterator());
    }
}
