package com.baima.massagemanager.entity;

import org.litepal.crud.LitePalSupport;

public class RechargeRecord extends LitePalSupport {

    private long id;
    private long customerId; //顾客 ID
    private long timeStamp; //充值 时间
    private double rechargeAmount; //充值金额
    private double rechargeHour; //充值小时数
    private double remainder; //剩余小时数
    private String remark = ""; //备注
    private long timestampFlag; //时间戳标记

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

    public long getTimestampFlag() {
        return timestampFlag;
    }

    public void setTimestampFlag(long timestampFlag) {
        this.timestampFlag = timestampFlag;
    }

    @Override
    public String toString() {
        return "RechargeRecord{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", timeStamp=" + timeStamp +
                ", rechargeAmount=" + rechargeAmount +
                ", rechargeHour=" + rechargeHour +
                ", remainder=" + remainder +
                ", remark='" + remark + '\'' +
                ", timestampFlag=" + timestampFlag +
                '}';
    }
}