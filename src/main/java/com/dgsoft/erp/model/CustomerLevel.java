package com.dgsoft.erp.model;
// Generated Oct 24, 2013 3:27:02 PM by Hibernate Tools 4.0.0

import com.dgsoft.common.NamedModel;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * CustomerLevel generated by hbm2java
 */
@Entity
@Table(name = "CUSTOMER_LEVEL", catalog = "MINI_ERP")
public class CustomerLevel implements java.io.Serializable,NamedModel {

	private String id;
	private int priority;
	private String name;
	private String memo;
	private Set<Customer> customers = new HashSet<Customer>(0);

	public CustomerLevel() {
	}

	public CustomerLevel(String id, int priority, String name) {
		this.id = id;
		this.priority = priority;
		this.name = name;
	}
	public CustomerLevel(String id, int priority, String name, String memo,
			Set<Customer> customers) {
		this.id = id;
		this.priority = priority;
		this.name = name;
		this.memo = memo;
		this.customers = customers;
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

	@Column(name = "PRIORITY", nullable = false)
	public int getPriority() {
		return this.priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
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

	@Column(name = "MEMO", length = 200)
	@Size(max = 200)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "customerLevel")
	public Set<Customer> getCustomers() {
		return this.customers;
	}

	public void setCustomers(Set<Customer> customers) {
		this.customers = customers;
	}

}