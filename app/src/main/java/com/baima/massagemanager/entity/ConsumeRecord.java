package com.baima.massagemanager.entity;

import org.litepal.crud.LitePalSupport;

public class ConsumeRecord extends LitePalSupport {
    private long id;
    //如果 不同的记录的消费时间戳和顾客 ID都相同就是同一次消费
    private long consumeTimestamp; //消费的时间戳
    private long customerId;
    private double consumeHour; //消费的小时数
    private double remainder; //剩余小时数

    private long staffId;
    private double hourOfCurrentMonth;
    private String staffNames;
    private String otherStaffName;
    private int staffCount = 1; //本次消费参与的员工的个数
    private String remark;

    public ConsumeRecord() {
    }

    public ConsumeRecord(long consumeTimestamp, long customerId, double consumeHour, double remainder, long staffId, double hourOfCurrentMonth, String staffNames, int staffCount) {
        this.consumeTimestamp = consumeTimestamp;
        this.customerId = customerId;
        this.consumeHour = consumeHour;
        this.remainder = remainder;
        this.staffId = staffId;
        this.hourOfCurrentMonth = hourOfCurrentMonth;
        this.staffNames = staffNames;
        this.staffCount = staffCount;
    }

    public ConsumeRecord(long consumeTimestamp, long customerId, double consumeHour, double remainder, long staffId, double hourOfCurrentMonth, String staffNames, String otherStaffName, int staffCount, String remark) {
        this.consumeTimestamp = consumeTimestamp;
        this.customerId = customerId;
        this.consumeHour = consumeHour;
        this.remainder = remainder;
        this.staffId = staffId;
        this.hourOfCurrentMonth = hourOfCurrentMonth;
        this.staffNames = staffNames;
        this.otherStaffName = otherStaffName;
        this.staffCount = staffCount;
        this.remark = remark;
    }

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

    public double getConsumeHour() {
        return consumeHour;
    }

    public void setConsumeHour(double consumeHour) {
        this.consumeHour = consumeHour;
    }

    public double getRemainder() {
        return remainder;
    }

    public void setRemainder(double remainder) {
        this.remainder = remainder;
    }

    public long getStaffId() {
        return staffId;
    }

    public void setStaffId(long staffId) {
        this.staffId = staffId;
    }

    public double getHourOfCurrentMonth() {
        return hourOfCurrentMonth;
    }

    public void setHourOfCurrentMonth(double hourOfCurrentMonth) {
        this.hourOfCurrentMonth = hourOfCurrentMonth;
    }

    public String getStaffNames() {
        return staffNames;
    }

    public void setStaffNames(String staffNames) {
        this.staffNames = staffNames;
    }

    public String getOtherStaffName() {
        return otherStaffName;
    }

    public void setOtherStaffName(String otherStaffName) {
        this.otherStaffName = otherStaffName;
    }

    public int getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(int staffCount) {
        this.staffCount = staffCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "ConsumeRecord{" +
                "id=" + id +
                ", consumeTimestamp=" + consumeTimestamp +
                ", customerId=" + customerId +
                ", consumeHour=" + consumeHour +
                ", remainder=" + remainder +
                ", staffId=" + staffId +
                ", hourOfCurrentMonth=" + hourOfCurrentMonth +
                ", staffNames='" + staffNames + '\'' +
                ", otherStaffName='" + otherStaffName + '\'' +
                ", staffCount=" + staffCount +
                ", remark='" + remark + '\'' +
                '}';
    }
}
