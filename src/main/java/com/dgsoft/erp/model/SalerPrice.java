package com.dgsoft.erp.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 9/29/14.
 */
@Entity
@Table(name = "SALER_PRICE", catalog = "MINI_ERP")
public class SalerPrice {

    private String id;
    private BigDecimal price;

    private ResUnit resUnit;
    private Res res;
    private MiddleMan middleMan;
    private Set<SalerStoreResPrice> salerStoreResPrices = new HashSet<SalerStoreResPrice>(0);

    public SalerPrice() {
    }

    public SalerPrice(BigDecimal price, ResUnit resUnit, Res res, MiddleMan middleMan) {
        this.price = price;
        this.resUnit = resUnit;
        this.res = res;
        this.middleMan = middleMan;
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

    @Column(name = "PRICE", nullable = false, scale = 3)
    @NotNull
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RES_UNIT", nullable = false)
    @NotNull
    public ResUnit getResUnit() {
        return resUnit;
    }

    public void setResUnit(ResUnit resUnit) {
        this.resUnit = resUnit;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RES", nullable = false)
    @NotNull
    public Res getRes() {
        return res;
    }

    public void setRes(Res res) {
        this.res = res;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MIDDLE_MAN", nullable = false)
    @NotNull
    public MiddleMan getMiddleMan() {
        return middleMan;
    }

    public void setMiddleMan(MiddleMan middleMan) {
        this.middleMan = middleMan;
    }

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "salerPrice", cascade = CascadeType.ALL)
    public Set<SalerStoreResPrice> getSalerStoreResPrices() {
        return salerStoreResPrices;
    }

    public void setSalerStoreResPrices(Set<SalerStoreResPrice> salerStoreResPrices) {
        this.salerStoreResPrices = salerStoreResPrices;
    }

    @Transient
    public List<SalerStoreResPrice> getSalerStoreResPriceList(){
        List<SalerStoreResPrice> result = new ArrayList<SalerStoreResPrice>(getSalerStoreResPrices());
        Collections.sort(result,new Comparator<SalerStoreResPrice>() {
            @Override
            public int compare(SalerStoreResPrice o1, SalerStoreResPrice o2) {
                return o1.getStoreRes().compareTo(o2.getStoreRes());
            }
        });
        return result;
    }
}
