package com.baima.massagemanager.entity;

import org.litepal.crud.LitePalSupport;

public class RechargeRecord extends LitePalSupport {

    private long id;
    private long customerId; //顾客 ID
    private long timeStamp;
    private double rechargeAmount; //充值金额
    private double rechargeHour; //充值小时数
    private double remainder; //剩余小时数
    private String remark = ""; //备注

    public RechargeRecord() {
    }

    public RechargeRecord(long customerId, long timeStamp, double rechargeAmount, double rechargeHour, double remainder, String remark) {
        this.customerId = customerId;
        this.timeStamp = timeStamp;
        this.rechargeAmount = rechargeAmount;
        this.rechargeHour = rechargeHour;
        this.remainder = remainder;
        this.remark = remark;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(double rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public double getRechargeHour() {
        return rechargeHour;
    }

    public void setRechargeHour(double rechargeHour) {
        this.rechargeHour = rechargeHour;
    }

    public double getRemainder() {
        return remainder;
    }

    public void setRemainder(double remainder) {
        this.remainder = remainder;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}