package com.baima.massagemanager.entity;

import org.litepal.crud.LitePalSupport;

import java.util.Objects;

public class ConsumeRecord extends LitePalSupport {
    private long id;
    private long consumeTimestamp; //消费的时间戳
    private long customerId;
    private double consumeTime; //消费的时间
    private double remainder; //剩余小时数
    private String customeName; //如果 是普通 顾客 可以设置姓名

    private long staffId;
    private String staffName; //员工姓名如果 多个连一起
    private double workTime;
    private double currentMonthTime;
    private String remark;
    private long timestampFlag; //记录时间戳标记，用于判断 是否是同一次消费

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConsumeTimestamp() {
        return consumeTimestamp;
    }

    public void setConsumeTimestamp(long consumeTimestamp) {
        this.consumeTimestamp = consumeTimestamp;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public double getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(double consumeTime) {
        this.consumeTime = consumeTime;
    }

    public double getRemainder() {
        return remainder;
    }

    public void setRemainder(double remainder) {
        this.remainder = remainder;
    }

    public String getCustomeName() {
        return customeName;
    }

    public void setCustomeName(String customeName) {
        this.customeName = customeName;
    }

    public long getStaffId() {
        return staffId;
    }

    public void setStaffId(long staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public double getWorkTime() {
        return workTime;
    }

    public void setWorkTime(double workTime) {
        this.workTime = workTime;
    }

    public double getCurrentMonthTime() {
        return currentMonthTime;
    }

    public void setCurrentMonthTime(double currentMonthTime) {
        this.currentMonthTime = currentMonthTime;
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
        return "ConsumeRecord{" +
                "id=" + id +
                ", consumeTimestamp=" + consumeTimestamp +
                ", customerId=" + customerId +
                ", consumeTime=" + consumeTime +
                ", remainder=" + remainder +
                ", customeName='" + customeName + '\'' +
                ", staffId=" + staffId +
                ", staffName='" + staffName + '\'' +
                ", workTime=" + workTime +
                ", currentMonthTime=" + currentMonthTime +
                ", remark='" + remark + '\'' +
                ", timestampFlag=" + timestampFlag +
                '}';
    }
}
