package com.dgsoft.erp.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/16/14
 * Time: 9:08 PM
 */

@Entity
@Table(name = "CHECK_OUT", catalog = "MINI_ERP")
public class Checkout implements java.io.Serializable {

    private String id;

    private int year;

    private int month;

    private Date operDate;

    private String operEmp;

    private int beginDay;

    private Integer version;

    private Set<AccountCheckout> accountCheckouts = new HashSet<AccountCheckout>(0);

    public Checkout() {
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


    @Column(name = "C_YEAR", nullable = false)
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Column(name = "C_MONTH", nullable = false)
    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    @Column(name = "BEGIN_DAY", nullable = false)
    public int getBeginDay() {
        return beginDay;
    }

    public void setBeginDay(int beginDay) {
        this.beginDay = beginDay;
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

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "checkout")
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

}
