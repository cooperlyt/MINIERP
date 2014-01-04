package com.dgsoft.erp.model;
// Generated Sep 25, 2013 4:34:50 PM by Hibernate Tools 4.0.0

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * ResCategory generated by hbm2java
 */
@Entity
@Table(name = "RES_CATEGORY", catalog = "MINI_ERP")
public class ResCategory implements java.io.Serializable {

    public enum ResType {
        PRODUCT, SEMI_PRODUCT, MATERIAL, WORK_IN_PROCESS,CONSUMABLE,OUTER_MATERIAL;
    }

    private String id;
    private ResCategory resCategory;
    private String name;
    private String description;
    private boolean root;
    private boolean enable;
    private Set<Res> reses = new HashSet<Res>(0);
    private Set<ResCategory> resCategories = new HashSet<ResCategory>(0);
    private ResType type;

    public ResCategory() {
    }

    public ResCategory(boolean enable) {
        this.enable = enable;
    }

    public ResCategory(String id, ResType type, String name, boolean root) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.root = root;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
    @NotNull
    @Size(max = 32)
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY")
    public ResCategory getResCategory() {
        return this.resCategory;
    }

    public void setResCategory(ResCategory resCategory) {
        this.resCategory = resCategory;
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

    @Column(name = "_ROOT", nullable = false)
    public boolean isRoot() {
        return this.root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }


    @Column(name = "ENABLE", nullable = false)
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resCategory")
    public Set<Res> getReses() {
        return this.reses;
    }

    public void setReses(Set<Res> reses) {
        this.reses = reses;
    }

    @Transient
    public List<Res> getResList() {
        List<Res> result = new ArrayList<Res>(getReses());
        Collections.sort(result, new Comparator<Res>() {
            @Override
            public int compare(Res o1, Res o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        return result;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resCategory")
    public Set<ResCategory> getResCategories() {
        return this.resCategories;
    }

    public void setResCategories(Set<ResCategory> resCategories) {
        this.resCategories = resCategories;
    }

    @Transient
    public List<ResCategory> getResCategoryList() {
        List<ResCategory> result = new ArrayList<ResCategory>(getResCategories());
        Collections.sort(result, new Comparator<ResCategory>() {
            @Override
            public int compare(ResCategory o1, ResCategory o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        return result;
    }

    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    public ResType getType() {
        return type;
    }

    public void setType(ResType type) {
        this.type = type;
    }


}
