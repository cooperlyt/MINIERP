package com.dgsoft.erp.model.api;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by cooper on 4/20/14.
 */
public class CustomerData{

    private String id;

    private String name;

    private String type;

    private String area;

    private int level;

    private String levelName;

    private int cityCode;

    private Date createDate;

    private BigDecimal balance;

    private Long orderCount;

    private Long completeOrderCount;

    private Long runningOrderCount;

    private Long waitPayOrderCount;

    private Long waitReceiveOrderCount;

    private Long waitShipOrderCount;

    private Long arrearsOrderCount;

    private BigDecimal orderArrears;

    private BigDecimal orderTotalMoney;

    private BigDecimal completeOrderMoney;

    private BigDecimal lastMoney;

    private boolean enable;

    public CustomerData(String id, String name, String type, String area, String levelName,int level,
                        int cityCode, Date createDate, BigDecimal balance, boolean enable,
                        Long orderCount, Long completeOrderCount, Long runningOrderCount, Long waitPayOrderCount,
                        Long waitReceiveOrderCount, Long waitShipOrderCount,Long arrearsOrderCount,
                        BigDecimal orderArrears, BigDecimal orderTotalMoney,
                        BigDecimal completeOrderMoney, BigDecimal lastMoney) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.area = area;
        this.levelName = levelName;
        this.level = level;
        this.cityCode = cityCode;
        this.createDate = createDate;
        this.balance = balance;
        this.orderCount = orderCount;
        this.arrearsOrderCount = arrearsOrderCount;
        this.completeOrderCount = completeOrderCount;
        this.runningOrderCount = runningOrderCount;
        this.waitPayOrderCount = waitPayOrderCount;
        this.waitReceiveOrderCount = waitReceiveOrderCount;
        this.waitShipOrderCount = waitShipOrderCount;
        this.orderArrears = orderArrears;
        this.orderTotalMoney = orderTotalMoney;
        this.completeOrderMoney = completeOrderMoney;
        this.enable = enable;
        this.lastMoney = lastMoney;
    }

    public CustomerData() {
    }

    public boolean isEnable() {
        return enable;
    }

    public BigDecimal getCompleteOrderMoney() {
        return completeOrderMoney;
    }

    public Long getArrearsOrderCount() {
        return arrearsOrderCount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getArea() {
        return area;
    }

    public int getLevel() {
        return level;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public Long getCompleteOrderCount() {
        return completeOrderCount;
    }

    public Long getRunningOrderCount() {
        return runningOrderCount;
    }

    public Long getWaitPayOrderCount() {
        return waitPayOrderCount;
    }

    public Long getWaitReceiveOrderCount() {
        return waitReceiveOrderCount;
    }

    public Long getWaitShipOrderCount() {
        return waitShipOrderCount;
    }

    public BigDecimal getOrderArrears() {
        return orderArrears;
    }

    public BigDecimal getOrderTotalMoney() {
        return orderTotalMoney;
    }

    public BigDecimal getLastMoney() {
        return lastMoney;
    }
}
