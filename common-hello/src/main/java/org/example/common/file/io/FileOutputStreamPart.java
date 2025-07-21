package org.example.common.file.io;


import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

/*
*
* 输出流分为：字节输出流、字符输出流
* OutputStream是字节输出流，抽象类，是其他输出流的超类
*
* */
public class FileOutputStreamPart {

    public static void main(String[] args) {

    }

    /**
     * FileOutputStream是字节输出流，用流操作，将数据写入文件；
     * 文件不存在就会创建文件
     */
    @Test
    public void testFileOutputStream01() {
        String pathName = "src/main/resources/test01.txt";
        OutputStream outputStream = null;
        try {
            // 创建 FileOutputStream 用于写数据到文件，会覆盖原文件里的所有内容；
            // new FileOutputStream(pathName, true)，追加内容在最后
            outputStream = new FileOutputStream(pathName);
            //写入一个字节
            //outputStream.write('H');
            //写入字符串
            String str = "hello world";
            //outputStream.write(str.getBytes(StandardCharsets.UTF_8));
            //截取字符串长度写入文件
            String str01 = "okkkkk";
            outputStream.write(str01.getBytes(StandardCharsets.UTF_8), 0, 3);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (outputStream != null) {
                try {
                    //关闭文件流，释放资源
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
