package com.baima.massagemanager.util;

import java.text.DecimalFormat;

public class StringUtil {
    /**
     * 如果 double的小数部分是0就取整数 部分，如果 不是0就保持不变。
     *
     * @param d
     * @return
     */
    public static String doubleTrans(double d) {
        return doubleTrans(d, false);
    }

    /**
     * 如果 指定的double是金额，小数部分保留两位
     * @param d
     * @param isMoney
     * @return
     */
    public static String doubleTrans(double d, boolean isMoney) {
        if (isMoney) {
            return new DecimalFormat("#.00").format(d);
        } else {
            if (Math.floor(d) == d) {
                return String.valueOf((long) d);
            } else {
                return String.valueOf(d);
            }
        }
//        return "";
    }
}
