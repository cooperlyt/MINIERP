package com.dgsoft.erp.model;
// Generated Oct 28, 2013 12:46:39 PM by Hibernate Tools 4.0.0

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.*;

/**
 * CustomerOrder generated by hbm2java
 */
@Entity
@Table(name = "CUSTOMER_ORDER", catalog = "MINI_ERP")
public class CustomerOrder implements java.io.Serializable {

    public enum OrderPayType {
        COMPLETE_PAY, PAY_FIRST, EXPRESS_PROXY, OVERDRAFT;

    }

    public enum MiddleMoneyCalcType {
        NOT_CALC, CONSULT_FIX, TOTAL_MONEY_RATE;
    }

    private String id;
    private Integer version;
    private Customer customer;
    private String orderEmp;
    private OrderPayType payType;
    private Date createDate;

    private BigDecimal profit;
    private String memo;
    private String contact;
    private String tel;


    private boolean resReceived;
    private boolean canceled;
    private boolean allStoreOut;
    private Boolean arrears;

    private BigDecimal earnest;
    private BigDecimal totalRebate;
    private BigDecimal middleMoney;
    private BigDecimal totalCost;
    private BigDecimal middleRate;
    private BigDecimal money;


    private boolean middlePayed;
    private boolean includeMiddleMan;
    private boolean moneyComplete;
    private boolean earnestFirst;

    private MiddleMoneyCalcType middleMoneyCalcType;
    private OrderBack orderBack;
    private MiddleMoneyPay middleMoneyPay;

    private Set<AccountOper> accountOpers = new HashSet<AccountOper>(0);
    private Set<NeedRes> needReses = new HashSet<NeedRes>(0);
    private Set<OrderFee> orderFees = new HashSet<OrderFee>(0);

    public CustomerOrder() {
        arrears = null;
        resReceived = false;
        canceled = false;
        allStoreOut = false;
        moneyComplete = false;

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

    @Version
    @Column(name = "VERSION")
    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @NotNull
    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "PAY_TYPE", nullable = false, length = 32)
    @NotNull
    public OrderPayType getPayType() {
        return this.payType;
    }

    public void setPayType(OrderPayType payType) {
        this.payType = payType;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE", nullable = false, length = 19)
    @NotNull
    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "ORDER_EMPLOYEE", nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getOrderEmp() {
        return orderEmp;
    }

    public void setOrderEmp(String orderEmp) {
        this.orderEmp = orderEmp;
    }

    @Column(name = "EARNEST", scale = 3)
    public BigDecimal getEarnest() {
        return this.earnest;
    }

    public void setEarnest(BigDecimal realMoney) {
        this.earnest = realMoney;
    }

    @Column(name = "TOTAL_REBATE", nullable = false, scale = 4)
    @NotNull
    public BigDecimal getTotalRebate() {
        return this.totalRebate;
    }

    public void setTotalRebate(BigDecimal totalRebate) {
        this.totalRebate = totalRebate;
    }

    @Column(name = "MIDDLE_MONEY", scale = 3)
    public BigDecimal getMiddleMoney() {
        return this.middleMoney;
    }

    public void setMiddleMoney(BigDecimal middleMoney) {
        this.middleMoney = middleMoney;
    }

    @Column(name = "TOTAL_COST", nullable = false, scale = 3)
    @NotNull
    public BigDecimal getTotalCost() {
        return this.totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    @Column(name = "MIDDLE_RATE", nullable = true, scale = 4)
    public BigDecimal getMiddleRate() {
        return this.middleRate;
    }

    public void setMiddleRate(BigDecimal middleRate) {
        this.middleRate = middleRate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "MIDDLE_CALC_TYPE", nullable = true)
    public MiddleMoneyCalcType getMiddleMoneyCalcType() {
        return middleMoneyCalcType;
    }

    public void setMiddleMoneyCalcType(MiddleMoneyCalcType middleMoneyCalcType) {
        this.middleMoneyCalcType = middleMoneyCalcType;
    }

    @Column(name = "PROFIT", scale = 3)
    public BigDecimal getProfit() {
        return this.profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    @Column(name = "MEMO", length = 200)
    @Size(max = 200)
    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customerOrder", orphanRemoval = true, cascade = {CascadeType.ALL})
    public Set<NeedRes> getNeedReses() {
        return needReses;
    }

    public void setNeedReses(Set<NeedRes> needReses) {
        this.needReses = needReses;
    }

    @Transient
    public List<NeedRes> getNeedResList() {
        List<NeedRes> result = new ArrayList<NeedRes>(getNeedReses());
        Collections.sort(result, new Comparator<NeedRes>() {
            @Override
            public int compare(NeedRes o1, NeedRes o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        return result;
    }

    @Column(name = "CONTACT", length = 50, nullable = false)
    @NotNull
    @Size(max = 50)
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Column(name = "TEL", length = 50, nullable = false)
    @NotNull
    @Size(max = 50)
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }


    @Column(name = "MONEY_COMPLETE", nullable = false)
    public boolean isMoneyComplete() {
        return moneyComplete;
    }

    public void setMoneyComplete(boolean moneyComplete) {
        this.moneyComplete = moneyComplete;
    }

    @Column(name = "INCLUDE_MIDDLE_MAN", nullable = false)
    public boolean isIncludeMiddleMan() {
        return this.includeMiddleMan;
    }

    public void setIncludeMiddleMan(boolean middleManPay) {
        this.includeMiddleMan = middleManPay;
    }

    @OneToOne(optional = true,fetch = FetchType.LAZY,mappedBy = "customerOrder")
    public OrderBack getOrderBack() {
        return orderBack;
    }

    public void setOrderBack(OrderBack orderBack) {
        this.orderBack = orderBack;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "customerOrder", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    public MiddleMoneyPay getMiddleMoneyPay() {
        return this.middleMoneyPay;
    }

    public void setMiddleMoneyPay(MiddleMoneyPay middleMoneys) {
        this.middleMoneyPay = middleMoneys;
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customerOrder")
    public Set<AccountOper> getAccountOpers() {
        return accountOpers;
    }

    public void setAccountOpers(Set<AccountOper> accountOpers) {
        this.accountOpers = accountOpers;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customerOrder", orphanRemoval = true, cascade = {CascadeType.ALL})
    public Set<OrderFee> getOrderFees() {
        return orderFees;
    }

    public void setOrderFees(Set<OrderFee> orderFees) {
        this.orderFees = orderFees;
    }

    @Transient
    public List<OrderFee> getOrderFeeList() {
        List<OrderFee> result = new ArrayList<OrderFee>(getOrderFees());
        Collections.sort(result, new Comparator<OrderFee>() {
            @Override
            public int compare(OrderFee o1, OrderFee o2) {
                return o1.getApplyDate().compareTo(o2.getApplyDate());
            }
        });
        return result;
    }

    @Column(name = "RES_RECEIVED", nullable = false)
    public boolean isResReceived() {
        return resReceived;
    }

    public void setResReceived(boolean resReceived) {
        this.resReceived = resReceived;
    }

    @Column(name = "CANCELED", nullable = false)
    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    @Column(name = "MIDDLE_PAYED", nullable = false)
    public boolean isMiddlePayed() {
        return middlePayed;
    }

    public void setMiddlePayed(boolean middlePayed) {
        this.middlePayed = middlePayed;
    }

    @Column(name = "ALL_STORE_OUT", nullable = false)
    public boolean isAllStoreOut() {
        return allStoreOut;
    }

    public void setAllStoreOut(boolean allStoreOut) {
        this.allStoreOut = allStoreOut;
    }

    @Column(name = "ARREARS")
    public Boolean getArrears() {
        return arrears;
    }

    public void setArrears(Boolean arrears) {
        this.arrears = arrears;
    }

    @Column(name = "EARNEST_FIRST", nullable = false)
    public boolean isEarnestFirst() {
        return earnestFirst;
    }

    public void setEarnestFirst(boolean earnestFirst) {
        this.earnestFirst = earnestFirst;
    }

    @Column(name = "MONEY", scale = 3, nullable = false)
    @NotNull
    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Transient
    public List<AccountOper> getAccountOperList() {
        List<AccountOper> result = new ArrayList<AccountOper>(getAccountOpers());
        Collections.sort(result, new Comparator<AccountOper>() {
            @Override
            public int compare(AccountOper o1, AccountOper o2) {
                return o1.getOperDate().compareTo(o2.getOperDate());
            }
        });
        return result;
    }



}
