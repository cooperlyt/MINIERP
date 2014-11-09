package com.dgsoft.erp.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/16/14
 * Time: 9:08 PM
 */

@Entity
@Table(name = "CHECK_OUT", catalog = "MINI_ERP")
public class Checkout implements java.io.Serializable {

    private long id;

    private Date operDate;

    private String operEmp;

    private Integer version;


    private Date beginDate;

    private Date closeDate;

    private Set<AccountCheckout> accountCheckouts = new HashSet<AccountCheckout>(0);

    public Checkout() {
    }

    public Checkout(long id, Date operDate, String operEmp, Date beginDate, Date closeDate) {
        this.id = id;
        this.operDate = operDate;
        this.operEmp = operEmp;
        this.beginDate = beginDate;
        this.closeDate = closeDate;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @NotNull
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CHECKOUT_TIME", nullable = false, length = 19)
    @NotNull
    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    @Column(name = "OPER_EMP", nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getOperEmp() {
        return operEmp;
    }

    public void setOperEmp(String operEmp) {
        this.operEmp = operEmp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="BEGIN_DATE",nullable = false,length = 19)
    @NotNull
    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CLOSE_DATE",nullable = false,length = 19)
    @NotNull
    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "checkout",cascade = {CascadeType.ALL})
    public Set<AccountCheckout> getAccountCheckouts() {
        return accountCheckouts;
    }

    public void setAccountCheckouts(Set<AccountCheckout> accountCheckouts) {
        this.accountCheckouts = accountCheckouts;
    }

    @Version
    @Column(name = "VERSION")
    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


    @Transient
    public Map<String,AccountCheckout> getAccountCheckOutMap(){
        Map<String,AccountCheckout> accountCheckouts = new HashMap<String, AccountCheckout>();
        for (AccountCheckout accountCheckout: getAccountCheckouts()){
            accountCheckouts.put(accountCheckout.getAccountCode(),accountCheckout);
        }
        return accountCheckouts;
    }

}
