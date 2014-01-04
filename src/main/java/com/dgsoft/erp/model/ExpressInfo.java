package com.dgsoft.erp.model;
// Generated Oct 30, 2013 1:46:18 PM by Hibernate Tools 4.0.0

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * ExpressInfo generated by hbm2java
 */
@Entity
@Table(name = "EXPRESS_INFO", catalog = "MINI_ERP")
public class ExpressInfo implements java.io.Serializable {

	private String id;
	private TransCorp transCorp;
	private String number;
	private Dispatch dispatch;

	public ExpressInfo() {
	}

	public ExpressInfo(Dispatch dispatch, TransCorp transCorp) {
		this.transCorp = transCorp;
        this.dispatch = dispatch;
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

	@ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST})
	@JoinColumn(name = "TRANS", nullable = false)
	@NotNull
	public TransCorp getTransCorp() {
		return this.transCorp;
	}

	public void setTransCorp(TransCorp transCorp) {
		this.transCorp = transCorp;
	}

	@Column(name = "NUMBER", length = 50)
	@Size(max = 50)
	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "expressInfo")
	public Dispatch getDispatch() {
		return this.dispatch;
	}

	public void setDispatch(Dispatch dispatches) {
		this.dispatch = dispatches;
	}

}
