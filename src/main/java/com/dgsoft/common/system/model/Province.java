package com.dgsoft.common.system.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/28/13
 * Time: 12:24 PM
 */
@Entity
@Table(name = "PROVINCE", catalog = "DG_SYSTEM")
public class Province implements java.io.Serializable{

    private int id;
    private String name;
    private int priority;
    private Set<City> cities = new HashSet<City>(0);

    @Id
    @Column(name = "PID", unique = true, nullable = false)
    @NotNull
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Column(name = "SORT", nullable = false)
    @NotNull
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "province")
    public Set<City> getCities() {
        return cities;
    }

    public void setCities(Set<City> cities) {
        this.cities = cities;
    }
}
