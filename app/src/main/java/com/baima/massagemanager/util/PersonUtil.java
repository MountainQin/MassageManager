package com.baima.massagemanager.util;

import com.baima.massagemanager.entity.Person;

import org.litepal.LitePal;

import java.util.List;

public class PersonUtil {
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
