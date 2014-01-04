package com.dgsoft.erp.model;
// Generated Oct 24, 2013 3:27:02 PM by Hibernate Tools 4.0.0

import com.dgsoft.common.NamedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * CustomerArea generated by hbm2java
 */
@Entity
@Table(name = "CUSTOMER_AREA", catalog = "MINI_ERP")
public class CustomerArea implements java.io.Serializable,NamedEntity {

	private String id;
	private String name;
    private String role;
	private Set<Customer> customers = new HashSet<Customer>(0);

	public CustomerArea() {
	}

	public CustomerArea(String id, String name) {
		this.id = id;
		this.name = name;
	}
	public CustomerArea(String id, String name, Set<Customer> customers) {
		this.id = id;
		this.name = name;
		this.customers = customers;
	}

	@Id
	@Column(name = "ID", unique = true, nullable = false, length = 20)
	@NotNull
	@Size(max = 20)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
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

    @Column(name= "AREA_ROLE",nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customerArea")
	public Set<Customer> getCustomers() {
		return this.customers;
	}

	public void setCustomers(Set<Customer> customers) {
		this.customers = customers;
	}

}
