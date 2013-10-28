package com.dgsoft.common.system.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/28/13
 * Time: 12:27 PM
 */
@Entity
@Table(name = "CITY", catalog = "DG_SYSTEM")
public class City implements java.io.Serializable {

    private int id;
    private String name;
    private Province province;

    @Id
    @Column(name = "PID", unique = true, nullable = false)
    @NotNull
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FID", nullable = false)
    @NotNull
    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }
}
