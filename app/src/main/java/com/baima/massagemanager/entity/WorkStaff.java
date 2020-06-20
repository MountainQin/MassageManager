package com.baima.massagemanager.entity;

import org.litepal.crud.LitePalSupport;

/**
 * 保存每次消费对应的员工
 */
public class WorkStaff extends LitePalSupport {

    private long id;
    private long consumeRecordId;
private long staffId;
private double workTime;
private double currentMonthTime;
private long consumeTimestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConsumeRecordId() {
        return consumeRecordId;
    }

    public void setConsumeRecordId(long consumeRecordId) {
        this.consumeRecordId = consumeRecordId;
    }

    public long getStaffId() {
        return staffId;
    }

    public void setStaffId(long staffId) {
        this.staffId = staffId;
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

    public long getConsumeTimestamp() {
        return consumeTimestamp;
    }

    public void setConsumeTimestamp(long consumeTimestamp) {
        this.consumeTimestamp = consumeTimestamp;
    }

    @Override
    public String toString() {
        return "WorkStaff{" +
                "id=" + id +
                ", consumeRecordId=" + consumeRecordId +
                ", staffId=" + staffId +
                ", workTime=" + workTime +
                '}';
    }
}