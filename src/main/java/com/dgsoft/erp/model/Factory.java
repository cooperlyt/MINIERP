package com.dgsoft.erp.model;

import com.dgsoft.common.NamedEntity;
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
 * Time: 1:51 PM
 */

@Entity
@Table(name = "FACTORY", catalog = "MINI_ERP")
public class Factory implements java.io.Serializable, TreeNode, NamedEntity {

    private String id;
    private String name;
    private String factoryRole;
    private boolean enable;

    private Set<ProductGroup> productGroups = new HashSet<ProductGroup>(0);

    public Factory() {
    }

    public Factory(boolean enable) {
        this.enable = enable;
    }

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

    @Column(name="FACTORY_ROLE",nullable = false,length = 32)
    @NotNull
    @Size(max = 32)
    public String getFactoryRole() {
        return factoryRole;
    }

    public void setFactoryRole(String factoryRole) {
        this.factoryRole = factoryRole;
    }

    @Column(name = "ENABLE", nullable = false)
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "factory", orphanRemoval = true)
    public Set<ProductGroup> getProductGroups() {
        return productGroups;
    }

    public void setProductGroups(Set<ProductGroup> productGroups) {
        this.productGroups = productGroups;
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
    public List<ProductGroup> getRootProductGroupList() {
        List<ProductGroup> result = new ArrayList<ProductGroup>();
        for (ProductGroup pg : getProductGroups()) {
            if ((pg.getParentGroup() == null) && (containDisable || pg.isEnable())) {
                pg.setContainDisable(containDisable);
                result.add(pg);
            }

        }
        Collections.sort(result, new Comparator<ProductGroup>() {
            @Override
            public int compare(ProductGroup o1, ProductGroup o2) {
                if ((o1.getId() != null) && (o2.getId() != null)) {
                return o1.getId().compareTo(o2.getId());
                }else return 0;
            }
        });
        return result;
    }

    @Transient
    public String getType(){
        return "factory";
    }

    @Transient
    @Override
    public TreeNode getChildAt(int childIndex) {
        return getRootProductGroupList().get(childIndex);
    }

    @Transient
    @Override
    public int getChildCount() {
        return getRootProductGroupList().size();
    }

    @Transient
    @Override
    public TreeNode getParent() {
        return null;
    }

    @Transient
    @Override
    public int getIndex(TreeNode node) {
        return getRootProductGroupList().indexOf(node);
    }

    @Transient
    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Transient
    @Override
    public boolean isLeaf() {
        return false;
    }

    @Transient
    @Override
    public Enumeration children() {
        return Iterators.asEnumeration(getRootProductGroupList().iterator());
    }
}
