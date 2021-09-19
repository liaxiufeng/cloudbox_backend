package com.lj.cloudbox.utils.file;

import java.io.*;
import java.util.Base64;

public class ImageUtils {
    /**
     * 通过totalPath获得img的base64编码字符串
     *
     * @param totalPath 图片的totalPath
     * @return 返回base64编码字符串，或者空字符串
     */
    public static String getBase64(String totalPath) {
        File file = new File(totalPath);
        if (!file.exists()) return "";
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            is = new FileInputStream(file);
            os = new ByteArrayOutputStream();
            int len;
            byte[] flush = new byte[1024];
            while ((len = is.read(flush)) != -1) {
                os.write(flush, 0, len);
            }
            os.flush();
            byte[] bytes = os.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(is, os);
        }
        return null;
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


    private static boolean isText(File file) {
        boolean isText = true;
        try {
            FileInputStream fin = new FileInputStream(file);
            long len = file.length();
            for (int j = 0; j < (int) len; j++) {
                int t = fin.read();
                if (t < 32 && t != 9 && t != 10 && t != 13) {
                    isText = false;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isText;
    }


}
