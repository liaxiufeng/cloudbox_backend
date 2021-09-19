package com.lj.cloudbox.utils.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat sdf_total = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date parse(String strDate) throws ParseException {
        return sdf.parse(strDate);
    }

    public static String parse(Date date) {
        return sdf.format(date);
    }

    public static String parse(Long date) {
        return sdf.format(date);
    }

    public static Date parse_total(String strDate) throws ParseException {
        return sdf_total.parse(strDate);
    }

    public static String parse_total(Date date) {
        return sdf_total.format(date);
    }

    public static String parse_total(Long date) {
        return sdf_total.format(date);
    }

    public static int getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {//出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException("生日在当前日期之后，无法计算生日");
        }
        int yearNow = cal.get(Calendar.YEAR); //当前年份
        int monthNow = cal.get(Calendar.MONTH); //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);//当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;  //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//当前日期在生日之前，年龄减一
            } else {
                age--;//当前月份在生日之前，年龄减一
            }
        }
        return age;
    }
}
