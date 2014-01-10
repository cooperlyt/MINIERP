package com.dgsoft.erp.model;

import com.google.common.collect.Iterators;

import javax.persistence.*;
import javax.swing.tree.TreeNode;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/10/14
 * Time: 1:49 PM
 */
@Entity
@Table(name = "PRODUCT_GROUP", catalog = "MINI_ERP")
public class ProductGroup implements java.io.Serializable, TreeNode {

    private String id;

    private String name;

    private boolean enable;

    private ProductGroup parentGroup;

    private Set<ProductGroup> childrenGroups = new HashSet<ProductGroup>(0);

    private Factory factory;

    private Set<ProductStoreIn> productStoreIns = new HashSet<ProductStoreIn>(0);


    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Column(name = "NAME", nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT")
    public ProductGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(ProductGroup parentGroup) {
        this.parentGroup = parentGroup;
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentGroup", orphanRemoval = true, cascade = {CascadeType.ALL})
    public Set<ProductGroup> getChildrenGroups() {
        return childrenGroups;
    }

    public void setChildrenGroups(Set<ProductGroup> childrenGroup) {
        this.childrenGroups = childrenGroup;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "FACTORY", nullable = false)
    @NotNull
    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productGroup")
    public Set<ProductStoreIn> getProductStoreIns() {
        return productStoreIns;
    }

    public void setProductStoreIns(Set<ProductStoreIn> productStoreIns) {
        this.productStoreIns = productStoreIns;
    }

    @Column(name = "ENABLE", nullable = false)
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Transient
    private boolean containDisable = false;

    @Transient
    public boolean isContainDisable() {
        return containDisable;
    }

    @Transient
    public void setContainDisable(boolean containDisable) {
        this.containDisable = containDisable;
    }

    @Transient
    private List<ProductGroup> getChildrenGroupList() {
        List<ProductGroup> result = new ArrayList<ProductGroup>();
        for (ProductGroup pg : getChildrenGroups()) {
            if (containDisable || pg.isEnable()) {
                pg.setContainDisable(containDisable);
                result.add(pg);
            }
        }
        Collections.sort(result, new Comparator<ProductGroup>() {
            @Override
            public int compare(ProductGroup o1, ProductGroup o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        return result;
    }

    @Transient
    @Override
    public TreeNode getChildAt(int childIndex) {
        return getChildrenGroupList().get(childIndex);
    }

    @Transient
    @Override
    public int getChildCount() {
        return getChildrenGroupList().size();
    }

    @Transient
    @Override
    public TreeNode getParent() {
        return (getParentGroup() == null) ? getFactory() : getParentGroup();
    }

    @Transient
    @Override
    public int getIndex(TreeNode node) {
        return getChildrenGroupList().indexOf(node);
    }

    @Transient
    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Transient
    @Override
    public boolean isLeaf() {
        return getChildrenGroupList().isEmpty();
    }

    @Transient
    @Override
    public Enumeration children() {
        return Iterators.asEnumeration(getChildrenGroupList().iterator());
    }
}
