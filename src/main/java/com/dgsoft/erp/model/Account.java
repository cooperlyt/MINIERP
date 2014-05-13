package com.dgsoft.erp.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-13
 * Time: 上午10:14
 */
@Entity
@Table(name = "ACCOUNT", catalog = "MINI_ERP")
public class Account implements Serializable {

    private String code;

    private Accounting.Direction direction;
    private int level;
    private String name;


    private Set<AccountCheckout> accountCheckouts = new HashSet<AccountCheckout>(0);

    public Account() {
    }

    @Id
    @Column(name = "ACCOUNT_CODE", unique = true, nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    public Set<AccountCheckout> getAccountCheckouts() {
        return accountCheckouts;
    }

    public void setAccountCheckouts(Set<AccountCheckout> accountCheckouts) {
        this.accountCheckouts = accountCheckouts;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "DIRECTION", nullable = false, length = 10)
    @NotNull
    public Accounting.Direction getDirection() {
        return direction;
    }

    public void setDirection(Accounting.Direction direction) {
        this.direction = direction;
    }

    @Column(name = "ACCOUNT_LEVEL", nullable = false)
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Column(name = "ACCOUNT_NAME", nullable = false, length = 50)
    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
