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

    public enum ReduceType{
        SYSTEM_TRUNC,OTHER_REDUCE;
    }

    private String id;
    private String description;
    private BigDecimal money;
    private CustomerOrder customerOrder;
    private ReduceType type;

    public OrderReduce() {
    }

    public OrderReduce(CustomerOrder customerOrder,ReduceType type) {
        this.type = type;
        this.customerOrder = customerOrder;
    }

    public OrderReduce(CustomerOrder customerOrder, BigDecimal money, ReduceType type) {
        this.customerOrder = customerOrder;
        this.money = money;
        this.type = type;
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

    @Enumerated(EnumType.STRING)
    @Column(name="TYPE",nullable = false,length = 20)
    @NotNull
    public ReduceType getType() {
        return type;
    }

    public void setType(ReduceType type) {
        this.type = type;
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
