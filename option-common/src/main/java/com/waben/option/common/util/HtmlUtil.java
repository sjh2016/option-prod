package com.waben.option.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author Peter
 * @Date 2021/03/24 15:01
 * @Version 1.0
 */
public class HtmlUtil {

    public static String readInputStream(String fileName) {
        String data = "";
        try {
            InputStream inputStream = HtmlUtil.class.getClassLoader().getResourceAsStream(fileName);
            byte[] buffer = new byte[1024];
            int len = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.close();
            byte[] getData = bos.toByteArray();
            data = new String(getData, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
