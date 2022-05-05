package com.lj.cloudbox.utils.file;

import com.lj.cloudbox.utils.CommonUtils;

import java.io.*;
import java.util.Base64;

public class ImageUtils {
    /**
     * 通过totalPath获得img的base64编码字符串
     *
     * @param file 图片文件
     * @return 返回base64编码字符串，或者空字符串
     */
    public static String getBase64(File file) {
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
            return "data:image/png;base64," + encoder.encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommonUtils.closeResource(is, os);
        }
        return null;
    }

}
