package org.example.common.utils;

import java.io.FileInputStream;

public class InputStreamUtil {

    public static byte[] loadByte(String classPath, String filePath) {
        byte[] data = null;
        try {
            classPath = classPath.replaceAll("\\.", "/");
            FileInputStream fileInputStream = new FileInputStream(filePath + "/" + classPath + ".class");
            int len = fileInputStream.available();
            data = new byte[len];
            int dataLen = fileInputStream.read(data);
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }

}
