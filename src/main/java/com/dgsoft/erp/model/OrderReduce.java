package com.dgsoft.erp.model;

import org.hibernate.annotations.GenericGenerator;
import org.jboss.seam.annotations.Name;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by cooper on 6/20/14.
 */
@Entity
@Table(name = "ORDER_REDUCE", catalog = "MINI_ERP")
public class OrderReduce implements Serializable {

    private String id;
    private String description;
    private BigDecimal money;
    private CustomerOrder customerOrder;

    public OrderReduce() {
    }

    public OrderReduce(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
    @NotNull
    @Size(max = 32)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "DESCRIPTION",nullable = true,length = 200)
    @Size(max = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="MONEY",nullable = false,precision = 19,scale = 4)
    @NotNull
    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "CUSTOMER_ORDER",nullable = false)
    @NotNull
    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }
}
