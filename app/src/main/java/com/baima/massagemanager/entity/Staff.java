package com.baima.massagemanager.entity;

import org.litepal.crud.LitePalSupport;

public class Staff extends Person{

    private double hoursOfCurrentMonth;

    public Staff() {
    }

    public Staff(int number, String name, String phoneNumber, double hoursOfCurrentMonth) {
        super(number, name, phoneNumber);
        this.hoursOfCurrentMonth = hoursOfCurrentMonth;
    }

    public Staff(int number, String name, String phoneNumber, double hoursOfCurrentMonth, String remark) {
        super(number, name, phoneNumber, remark);
        this.hoursOfCurrentMonth = hoursOfCurrentMonth;
    }

    public double getHoursOfCurrentMonth() {
        return hoursOfCurrentMonth;
    }

    public void setHoursOfCurrentMonth(double hoursOfCurrentMonth) {
        this.hoursOfCurrentMonth = hoursOfCurrentMonth;
    }

    }
