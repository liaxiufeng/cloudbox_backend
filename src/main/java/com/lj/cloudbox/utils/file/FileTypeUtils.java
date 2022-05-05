package com.lj.cloudbox.utils.file;


import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class FileTypeUtils {
    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<>();

    private FileTypeUtils() {
    }

    static {
        getAllFileType();  //初始化文件类型信息
    }

    /**
     * <p>Discription:[getAllFileType,常见文件头信息]</p>
     */
    private static void getAllFileType() {
        FILE_TYPE_MAP.put("jpg", "FFD8FF"); //JPEG (jpg)
        FILE_TYPE_MAP.put("png", "89504E47");  //PNG (png)
        FILE_TYPE_MAP.put("gif", "47494638");  //GIF (gif)
        FILE_TYPE_MAP.put("tif", "49492A00");  //TIFF (tif)
        FILE_TYPE_MAP.put("bmp", "424D");  //Windows Bitmap (bmp)
        FILE_TYPE_MAP.put("dwg", "41433130"); //CAD (dwg)
        FILE_TYPE_MAP.put("html", "68746D6C3E");  //HTML (html)
        FILE_TYPE_MAP.put("rtf", "7B5C727466");  //Rich Text Format (rtf)
        FILE_TYPE_MAP.put("xml", "3C3F786D6C");
        FILE_TYPE_MAP.put("zip", "504B0304");
        FILE_TYPE_MAP.put("rar", "52617221");
        FILE_TYPE_MAP.put("psd", "38425053");  //Photoshop (psd)
        FILE_TYPE_MAP.put("eml", "44656C69766572792D646174653A");  //Email [thorough only] (eml)
        FILE_TYPE_MAP.put("dbx", "CFAD12FEC5FD746F");  //Outlook Express (dbx)
        FILE_TYPE_MAP.put("pst", "2142444E");  //Outlook (pst)
        FILE_TYPE_MAP.put("xls", "D0CF11E0");  //MS Word
        FILE_TYPE_MAP.put("doc", "D0CF11E0");  //MS Excel 注意：word 和 excel的文件头一样
        FILE_TYPE_MAP.put("mdb", "5374616E64617264204A");  //MS Access (mdb)
        FILE_TYPE_MAP.put("wpd", "FF575043"); //WordPerfect (wpd)
        FILE_TYPE_MAP.put("eps", "252150532D41646F6265");
        FILE_TYPE_MAP.put("ps", "252150532D41646F6265");
        FILE_TYPE_MAP.put("pdf", "255044462D312E");  //Adobe Acrobat (pdf)
        FILE_TYPE_MAP.put("qdf", "AC9EBD8F");  //Quicken (qdf)
        FILE_TYPE_MAP.put("pwl", "E3828596");  //Windows Password (pwl)
        FILE_TYPE_MAP.put("wav", "57415645");  //Wave (wav)
        FILE_TYPE_MAP.put("avi", "41564920");
        FILE_TYPE_MAP.put("ram", "2E7261FD");  //Real Audio (ram)
        FILE_TYPE_MAP.put("rm", "2E524D46");  //Real Media (rm)
        FILE_TYPE_MAP.put("mpg", "000001BA");  //
        FILE_TYPE_MAP.put("mov", "6D6F6F76");  //Quicktime (mov)
        FILE_TYPE_MAP.put("asf", "3026B2758E66CF11"); //Windows Media (asf)
        FILE_TYPE_MAP.put("mid", "4D546864");  //MIDI (mid)
    }

    /**
     * <p>Discription:[getImageFileType,获取图片文件实际类型,若不是图片则返回null]</p>
     *
     * @param file 文件
     * @return fileType
     */
    public static String getImageFileType(File file) {
        if (isImage(file)) {
            try {
                ImageInputStream iis = ImageIO.createImageInputStream(file);
                Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
                if (!iter.hasNext()) {
                    return null;
                }
                ImageReader reader = iter.next();
                iis.close();
                return reader.getFormatName();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * <p>Discription:[getFileByFile,获取文件类型,包括图片,若格式不是已配置的,则返回null]</p>
     *
     * @param file 文件
     * @return fileType
     */
    public static String getFileByFile(File file) {
        String filetype = null;
        byte[] b = new byte[50];
        try (InputStream is = new FileInputStream(file)) {
            is.read(b);
            filetype = getFileTypeByStream(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filetype;
    }

    /**
     * <p>Discription:[getFileTypeByStream]</p>
     *
     * @param fileByte 文件字节数组
     * @return fileType
     */
    public static String getFileTypeByStream(byte[] fileByte) {
        String filetypeHex = String.valueOf(getFileHexString(fileByte));
        for (Entry<String, String> entry : FILE_TYPE_MAP.entrySet()) {
            String fileTypeHexValue = entry.getValue();
            if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * <p>Discription:[isImage,判断文件是否为图片]</p>
     *
     * @param file 文件
     * @return true 是 | false 否
     */
    public static boolean isImage(File file) {
        boolean flag;
        try {
            BufferedImage bufreader = ImageIO.read(file);
            int width = bufreader.getWidth();
            int height = bufreader.getHeight();
            flag = !(width == 0 || height == 0);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * <p>Discription:[isImage,判断文件是否为文本文件]</p>
     *
     * @param file 文件
     * @return true 是 | false 否
     */
    public static boolean isTxt(File file) {
        boolean isText = true;
        try (FileInputStream fin = new FileInputStream(file)){
            int len = (int)file.length();
            for (int j = 0; j < len; j++) {
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

    /**
     * 获取文件的编码格式
     *
     * @param file 文件
     * @return 问价类型字符串
     */

    public static String getFileCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){
            boolean checked = false;
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset; // 文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; // 文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; // 文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; // 文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        // (0x80- 0xBF),也可能在GB编码内
                        if (!(0x80 <= read && read <= 0xBF))// 双字节 (0xC0 - 0xDF)
                            break;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return charset;
    }


    /**
     * <p>Discription:[getFileHexString]</p>
     *
     * @param fileByte 文件字节数组
     * @return fileTypeHex
     */
    public static String getFileHexString(byte[] fileByte) {
        StringBuilder stringBuilder = new StringBuilder();
        if (fileByte == null || fileByte.length <= 0) {
            return null;
        }
        for (byte value : fileByte) {
            int v = value & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}