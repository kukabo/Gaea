package org.example.common.file.io;


import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/*
*
* 输入流分为：字节输入流、字符输入流
* InputStream是字节输入流，抽象类，是其他输入流的超类
*
* */
public class FileInputStreamPart {

    public static void main(String[] args) {

    }

    /**
     * FileInputStream是字节输入流，用流操作，将文件读取到内存；
     * read(byte[] b)可以提升效率
     */
    @Test
    public void testFileInputStream01() {
        String pathName = "src/main/resources/test01.txt";
        InputStream inputStream = null;
        int readData;
        try {
            //创建FileInputStream，用于读取文件
            inputStream = new FileInputStream(pathName);
            //read一个一个字节的读取，调用一次移到下一个字节；如果读取完了返回-1
            //一个汉子是三个字节，如果用 read() 读取会出现乱码
            while ( (readData = inputStream.read()) != -1) {
                //把当前读取的字节转换为 char
                System.out.print((char) readData);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    //关闭文件流，释放资源
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * read(byte[] bytes) 提升读取速度
     * new byte[2]定义每次最多读取的字节大小是 2
     * 解析汉字可以用字符输入流
     */
    @Test
    public void testFileInputStream02() {
        String pathName = "src/main/resources/test01.txt";
        InputStream inputStream = null;
        byte[] bytes = new byte[8];
        int readDataLength = 0;
        try {
            inputStream = new FileInputStream(pathName);
            // 如果读取正常，readDataLength是实际读取的字节长度；
            // 如果读取完，返回-1；
            // 读取到的内容会放在bytes里
            while ((readDataLength = inputStream.read(bytes)) != -1) {
                //把当前读取的字节转换为 String；
                // readDataLength 是读取的长度，如果最后一次还剩 2 个字节readDataLength就等于 2
                System.out.print(new String(bytes, 0, readDataLength));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    //关闭文件流，释放资源
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
