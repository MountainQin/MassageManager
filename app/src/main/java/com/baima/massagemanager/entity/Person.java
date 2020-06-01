package com.baima.massagemanager.entity;

import org.litepal.crud.LitePalSupport;

public abstract class Person extends LitePalSupport {
    private long id;
    private int number; //编号
    private String name;
    private String phoneNumber;
    private String remark; //备注

    public Person() {
    }

    public Person(int number, String name, String phoneNumber) {
        this(number, name, phoneNumber, "");
    }

    public Person(int number, String name, String phoneNumber, String remark) {
        this.number = number;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.remark = remark;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", number=" + number +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
