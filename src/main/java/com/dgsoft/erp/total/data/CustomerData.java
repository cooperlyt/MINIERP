package com.dgsoft.erp.total.data;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-22
 * Time: 下午1:36
 */


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

    private BigDecimal noProxyBalance;

    private Long orderCount;

    private BigDecimal orderMoney;

    private Long curMonthCount;

    private BigDecimal curMonthMoney;

    private Long curYearCount;

    private BigDecimal curYearMoney;

    private Long beforYearCount;

    private BigDecimal beforYearMoney;

    private Long beforMonthCount;

    private BigDecimal befroMonthMoney;


    public CustomerData(String id, String name,  Long curMonthCount, BigDecimal curMonthMoney, Long beforYearCount, BigDecimal beforYearMoney) {
        this.name = name;
        this.id = id;
        this.curMonthCount = curMonthCount;
        this.curMonthMoney = curMonthMoney;
        this.beforYearCount = beforYearCount;
        this.beforYearMoney = beforYearMoney;
    }

    public CustomerData(String id, String name, String type, String area,
                        String levelName, int level, int cityCode, Date createDate,
                        BigDecimal noProxyBalance, BigDecimal balance,  Long orderCount,
                        BigDecimal orderMoney, Long curMonthCount, BigDecimal curMonthMoney,
                        Long beforMonthCount, BigDecimal befroMonthMoney,
                        Long curYearCount, BigDecimal curYearMoney, Long beforYearCount, BigDecimal beforYearMoney) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.area = area;
        this.level = level;
        this.levelName = levelName;
        this.cityCode = cityCode;
        this.createDate = createDate;
        this.balance = balance;
        this.noProxyBalance = noProxyBalance;
        this.orderCount = orderCount;
        this.orderMoney = orderMoney;
        this.curMonthCount = curMonthCount;
        this.curMonthMoney = curMonthMoney;
        this.curYearCount = curYearCount;
        this.curYearMoney = curYearMoney;
        this.beforYearCount = beforYearCount;
        this.beforYearMoney = beforYearMoney;
        this.beforMonthCount = beforMonthCount;
        this.befroMonthMoney = befroMonthMoney;
    }

    public CustomerData() {
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

    public BigDecimal getNoProxyBalance() {
        return noProxyBalance;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public BigDecimal getOrderMoney() {
        return orderMoney;
    }

    public Long getCurMonthCount() {
        return curMonthCount;
    }

    public BigDecimal getCurMonthMoney() {
        return curMonthMoney;
    }

    public Long getCurYearCount() {
        return curYearCount;
    }

    public BigDecimal getCurYearMoney() {
        return curYearMoney;
    }

    public BigDecimal getBeforYearMoney() {
        return beforYearMoney;
    }

    public Long getBeforYearCount() {
        return beforYearCount;
    }

    public Long getBeforMonthCount() {
        return beforMonthCount;
    }

    public BigDecimal getBefroMonthMoney() {
        return befroMonthMoney;
    }
}