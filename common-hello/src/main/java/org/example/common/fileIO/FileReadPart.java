package org.example.common.fileIO;

import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;

/*
* 字符读取 FileReader
* FileReader和FileWriter  InputStream和OutputStream 都是节点流
* 节点流：从一个特定的数据源（比如文件）读取/写入数据
*
* 包装流：里面有一个属性是 Reader（可以是FileReader或FileWriter），包装了一下增大功能。比如 BufferedReader 和 BufferedWriter
* */
public class FileReadPart {

    public static void main(String[] args) {

    }

    /**
     * FileReader 按字符读取； FileInputStream 是按字节读取
     * 演示单个字符读取
     * */
    @Test
    public void testFileRead01() {

        String pathName = "src/main/resources/test01.txt";
        int readData = 0;
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(pathName);
            //循环一个字符一个字符的读取，读取完了返回-1
            while ( (readData = fileReader.read()) != -1) {
                System.out.print((char) readData);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fileReader!= null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 字符数组读取文件
     * */
    @Test
    public void testFileRead02() {

        String pathName = "src/main/resources/test02.txt";
        FileReader fileReader = null;
        //一次读取 10个字符
        char[] readData = new char[10];
        //注意：读取多个一定要获取长度
        int readLength = 0;

        try {
            fileReader = new FileReader(pathName);
            //循环一次读取多个字符，读取完了返回-1
            while ( (readLength = fileReader.read(readData)) != -1) {
                //偏移量每次从 0 开始到当前读取到的长度（最后一次读取readLength可能不是 10 ）
                System.out.print(new String(readData, 0, readLength));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fileReader!= null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
