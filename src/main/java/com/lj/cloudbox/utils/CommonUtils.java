package com.lj.cloudbox.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CommonUtils {
    /**
     * 判断一个或多个java基础对象是否有非0的值
     *
     * @param args 一个或多个需要判断的java基础对象
     * @return （1）全部符合，返回true （2）至少一个不符合，则返回false
     */
    public static Boolean haveValue(Object... args) {
        for (Object obj : args) {
            if (obj == null) return false;
            if (obj instanceof String){
                String temp = (String) obj;
                if (temp.equals("")) return false;
            }else if (obj instanceof Number){
                Number temp = (Number) obj;
                if (temp.toString().equals("0"))
                    return false;
            }
        }
        return true;
    }


    /**
     * 关闭资源
     *
     * @param args 需要关闭的一个或多个资源
     */
    public static void closeResource(AutoCloseable... args) {
        try {
            for (AutoCloseable co : args) {
                if (co != null)
                    co.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void write(InputStream is, OutputStream os) throws IOException {
        int len;
        byte[] flush = new byte[1024];
        while ((len = is.read(flush)) != -1) {
            os.write(flush, 0, len);
        }
        os.flush();
    }
}
