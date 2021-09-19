package com.lj.cloudbox.utils.file;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSizeFormatUtil {
    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值
    public static final int SIZETYPE_TB = 5;//获取文件大小单位为GB的double值
    /**
     * 转换文件大小
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        } else if (fileS < 1024 && fileS > -1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576 && fileS > -1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824 && fileS > -1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else if (fileS < 1099511627776L && fileS > -1099511627776L) {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        } else {
            fileSizeString = df.format((double) fileS / 1099511627776L) + "TB";
        }
        return fileSizeString;
    }


    /**
     * 转换文件大小,指定转换的类型
     */
    public static double formatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            case SIZETYPE_TB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1099511627776L));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    public static Long formatFileSize(String fileS) {
        Long fileSize = 0L;
        String fileSizeStr = fileS.toUpperCase();
        Matcher matcher = Pattern.compile("([-0-9.]+)([BKGT]+)$").matcher(fileSizeStr);
        if (matcher.find()) {
            Double number = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(2);
            switch (unit) {
                case "B":
                case "":
                    fileSize = number.longValue();
                    break;
                case "K":
                case "KB":
                    fileSize = ((Double) (number * 1024)).longValue();
                    break;
                case "M":
                case "MB":
                    fileSize = ((Double) (number * 1048576)).longValue();
                    break;
                case "G":
                case "GB":
                    fileSize = ((Double) (number * 1073741824)).longValue();
                    break;
                case "T":
                case "TB":
                    fileSize = ((Double) (number * 1099511627776L)).longValue();
                    break;
            }
        }
        return fileSize;
    }
}
