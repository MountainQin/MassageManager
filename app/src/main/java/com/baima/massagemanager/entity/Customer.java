package com.baima.massagemanager.entity;

public class Customer extends Person {
    private double remainder; //剩余

    public double getRemainder() {
        return remainder;
    }

    public void setRemainder(double remainder) {
        this.remainder = remainder;
    }
}