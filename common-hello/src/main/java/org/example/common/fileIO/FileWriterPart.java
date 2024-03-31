package org.example.common.fileIO;

import org.junit.Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
* 字符写 FileWriter
*
* */
public class FileWriterPart {

    public static void main(String[] args) {

    }

    /**
     * FileWriter 按字符写；
     * */
    @Test
    public void testFileWriter01() {

        String pathName = "src/main/resources/writer01.txt";
        FileWriter fileWriter = null;

        try {
            //new FileWriter(pathName)是覆盖
            //new FileWriter(pathName, true)是追加
            fileWriter = new FileWriter(pathName);
            fileWriter.write("hello world. Test fileWriter");
            //要写的内容多的话，可以循环写
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("写入成功");
    }
}
