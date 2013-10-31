package com.dgsoft.common.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/31/13
 * Time: 10:13 AM
 */
@Entity
@Table(name = "NUMBER_POOL", catalog = "DG_SYSTEM")
public class NumberPool {

    private String id;
    private long number;
    private int poolSize;

    public NumberPool() {
    }

    public NumberPool(String id, long number,int poolSize) {
        this.id = id;
        this.number = number;
        this.poolSize = poolSize;
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

    @Column(name = "NUMBER", nullable = false)
    public long getNumber() {
        return this.number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    @Column(name="POOL_SIZE",nullable = false)
    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
