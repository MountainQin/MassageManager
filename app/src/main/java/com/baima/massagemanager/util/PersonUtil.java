package com.baima.massagemanager.util;

import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.Person;
import com.baima.massagemanager.entity.Staff;

import org.litepal.LitePal;

import java.util.List;

public class PersonUtil {
    /**
     * 修改员工的本月时间
     *
     * @param staffId
     * @param currentMonthTime
     */
    public static void updateStaffTime(long staffId, double changeTime) {
        List<Staff> staffList = LitePal.where("id=?", String.valueOf(staffId)).find(Staff.class);
        if (staffList.size() > 0) {
            Staff staff = staffList.get(0);
            double currentMonthTime = staff.getHoursOfCurrentMonth() + changeTime;
            staff.setHoursOfCurrentMonth(currentMonthTime);
            if (currentMonthTime == 0) {
                staff.setToDefault("hoursOfCurrentMonth");
            }
            staff.update(staffId);
        }
    }

    public static void updateCustomerTime(long customerId, double changeTime) {
        List<Customer> customerList = LitePal.where("id=?", String.valueOf(customerId)).find(Customer.class);
        if (customerList.size() > 0) {
            Customer customer = customerList.get(0);
            double remainder = customer.getRemainder()+changeTime;
            customer.setRemainder(remainder);
            if (remainder == 0) {
                customer.setToDefault("remainder");
            }
            customer.update(customerId);
        }
    }

    /**
     * 获取顾客或者员工的新的编号
     * 添加顾客或者员工的时候可以调用
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T extends Person> int getNewNumber(Class<T> tClass) {
        int newNumber = 0;
        List<T> list = LitePal.order("number").find(tClass);
        for (int i = 0; i < list.size(); i++) {
            int number = list.get(i).getNumber();
            if ((newNumber = i + 1) != number) {
                return i + 1;
            }
        }
        return newNumber + 1;
    }

    /**
     * 判断指定编号的顾客或者员工是否存在
     *
     * @param number
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T extends Person> boolean isExists(int number, Class<T> tClass) {
        return LitePal.where("number=?", String.valueOf(number)).find(tClass).size() > 0;
    }

}
