package com.dgsoft.erp.model;
// Generated Oct 30, 2013 1:46:18 PM by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * ProductToDoor generated by hbm2java
 */
@Entity
@Table(name = "PRODUCT_TO_DOOR", catalog = "MINI_ERP")
public class ProductToDoor implements java.io.Serializable {

	private String id;
	private Employee employee;
	private Cars cars;
	private Set<Dispatch> dispatches = new HashSet<Dispatch>(0);

	public ProductToDoor() {
	}

	public ProductToDoor(String id, Employee employee, Cars cars) {
		this.id = id;
		this.employee = employee;
		this.cars = cars;
	}
	public ProductToDoor(String id, Employee employee, Cars cars,
			Set<Dispatch> dispatches) {
		this.id = id;
		this.employee = employee;
		this.cars = cars;
		this.dispatches = dispatches;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DRIVER", nullable = false)
	@NotNull
	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAR", nullable = false)
	@NotNull
	public Cars getCars() {
		return this.cars;
	}

	public void setCars(Cars cars) {
		this.cars = cars;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productToDoor")
	public Set<Dispatch> getDispatches() {
		return this.dispatches;
	}

	public void setDispatches(Set<Dispatch> dispatches) {
		this.dispatches = dispatches;
	}

}