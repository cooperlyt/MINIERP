package com.dgsoft.erp.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 26/03/14
 * Time: 14:27
 */
@Entity
@Table(name = "QUOTED_PRICE", catalog = "MINI_ERP")
public class QuotedPrice implements Serializable {

    private String id;

    private Date createDate;

    private String createEmp;

    private Customer customer;

    private String type;

    private String Memo;

    private Set<PriceItem> priceItems = new HashSet<PriceItem>(0);


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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE", nullable = false, length = 19)
    @NotNull
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "CREATE_EMP", nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getCreateEmp() {
        return createEmp;
    }

    public void setCreateEmp(String createEmp) {
        this.createEmp = createEmp;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "CUSTOMER", nullable = false)
    @NotNull
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Column(name = "TYPE", nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "MEMO", length = 200)
    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "quotedPrice", cascade = {CascadeType.ALL})
    public Set<PriceItem> getPriceItems() {
        return priceItems;
    }

    public void setPriceItems(Set<PriceItem> priceItems) {
        this.priceItems = priceItems;
    }
}
