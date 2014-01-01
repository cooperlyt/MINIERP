package com.dgsoft.erp.model;
// Generated Oct 30, 2013 1:46:18 PM by Hibernate Tools 4.0.0

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * NeedRes generated by hbm2java
 */
@Entity
@Table(name = "NEED_RES", catalog = "MINI_ERP")
public class NeedRes implements java.io.Serializable {

    public enum NeedResType {
        ORDER_SEND, SUPPLEMENT_SEND;
    }

    private String id;
    private CustomerOrder customerOrder;
    private NeedResType type;
    private Date limitTime;
    private String reason;
    private String memo;
    private Date createDate;
    private boolean dispatched;
    private BigDecimal proxyFare;
    private boolean fareByCustomer;
    private String address;
    private String postCode;
    private String receivePerson;
    private String receiveTel;

    private Set<OrderItem> orderItems = new HashSet<OrderItem>(0);
    private Set<Dispatch> dispatches = new HashSet<Dispatch>(0);

    public NeedRes() {
    }

    public NeedRes(CustomerOrder customerOrder, NeedResType type,
                   String reason, Date createDate,boolean dispatched) {
        this.customerOrder = customerOrder;
        this.type = type;
        this.createDate = createDate;
        this.reason = reason;
        this.dispatched = dispatched;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ORDER", nullable = false)
    @NotNull
    public CustomerOrder getCustomerOrder() {
        return this.customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 32)
    @NotNull
    public NeedResType getType() {
        return this.type;
    }

    public void setType(NeedResType type) {
        this.type = type;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LIMIT_TIME", nullable = false, length = 19)
    @NotNull
    public Date getLimitTime() {
        return this.limitTime;
    }

    public void setLimitTime(Date limitTime) {
        this.limitTime = limitTime;
    }

    @Column(name = "REASON", nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Column(name = "MEMO", length = 200)
    @Size(max = 200)
    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Column(name="DISPATCHED", nullable = false)
    public boolean isDispatched() {
        return dispatched;
    }

    public void setDispatched(boolean dispatched) {
        this.dispatched = dispatched;
    }

    @Column(name="ADDRESS",length = 200,nullable = false)
    @NotNull
    @Size(max = 200)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "POST_CODE",length = 10, nullable = true)
    @Size(max = 10)
    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Column(name = "RECEIVE_PERSON",length = 50)
    @Size(max = 50)
    public String getReceivePerson() {
        return receivePerson;
    }

    public void setReceivePerson(String receivePerson) {
        this.receivePerson = receivePerson;
    }

    @Column(name = "RECEIVE_TEL",length = 50)
    @Size(max = 50)
    public String getReceiveTel() {
        return receiveTel;
    }

    public void setReceiveTel(String receiveTel) {
        this.receiveTel = receiveTel;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "needRes", orphanRemoval = true, cascade = {CascadeType.ALL})
    public Set<OrderItem> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Transient
    public List<OrderItem> getOrderItemList(){
        List<OrderItem> result = new ArrayList<OrderItem>(getOrderItems());
        Collections.sort(result, new Comparator<OrderItem>() {
            @Override
            public int compare(OrderItem o1, OrderItem o2) {
                int result = o1.getUseRes().getId().compareTo(o2.getUseRes().getId());
                if (result == 0){
                    result = o1.getId().compareTo(o2.getId());
                }
                return result;
            }
        });

        return result;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "needRes", cascade = {CascadeType.ALL})
    public Set<Dispatch> getDispatches() {
        return this.dispatches;
    }

    public void setDispatches(Set<Dispatch> dispatches) {
        this.dispatches = dispatches;
    }

    @Transient
    public List<Dispatch> getDispatchList(){
        List<Dispatch> result = new ArrayList<Dispatch>(getDispatches());
        Collections.sort(result,new Comparator<Dispatch>() {
            @Override
            public int compare(Dispatch o1, Dispatch o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        return result;
    }

    @Column(name = "PROXY_FARE",nullable = true,scale = 3)
    public BigDecimal getProxyFare() {
        return proxyFare;
    }

    public void setProxyFare(BigDecimal fare) {
        this.proxyFare = fare;
    }

    @Column(name="FARE_BY_CUSTOMER", nullable = false)
    public boolean isFareByCustomer() {
        return fareByCustomer;
    }

    public void setFareByCustomer(boolean fareByCustomer) {
        this.fareByCustomer = fareByCustomer;
    }

    @Transient
    public boolean isAllCustomerSelfTake(){
        for (Dispatch dispatch: getDispatches()){
           if (!dispatch.getDeliveryType().equals(Dispatch.DeliveryType.CUSTOMER_SELF)){
               return false;
           }
        }
        return true;
    }

    @Transient
    public boolean isAllDispatchComplete(){
        for (Dispatch dispatch: getDispatches()){
            if (!dispatch.getState().equals(Dispatch.DispatchState.ALL_COMPLETE)){
                return false;
            }
        }
        return true;
    }

}
