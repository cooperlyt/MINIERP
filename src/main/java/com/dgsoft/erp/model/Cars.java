package com.dgsoft.erp.model;
// Generated Oct 24, 2013 3:27:02 PM by Hibernate Tools 4.0.0

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Cars generated by hbm2java
 */
@Entity
@Table(name = "CARS", catalog = "MINI_ERP")
public class Cars implements java.io.Serializable {

	private String id;
    private boolean enable;
	private String employeeId;
	private Set<ProductToDoor> productToDoors = new HashSet<ProductToDoor>(0);

	public Cars() {
        enable = true;
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

    @Column(name = "EMP_DRIVER", nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cars")
	public Set<ProductToDoor> getProductToDoors() {
		return this.productToDoors;
	}

	public void setProductToDoors(Set<ProductToDoor> productToDoors) {
		this.productToDoors = productToDoors;
	}

    @Column(name = "ENABLE", nullable = false)
    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
