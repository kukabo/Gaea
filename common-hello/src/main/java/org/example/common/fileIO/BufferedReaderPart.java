package org.example.common.fileIO;


import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//包装 字符流
public class BufferedReaderPart {


    @Test
    public void test() throws IOException {
        String path = "src/main/resources/test01.txt";
        BufferedReader reader = new BufferedReader(new FileReader(path));
        //一行行的读取，效率高
        String line;
        //读取完最后一行就会返回空
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        //底层是关闭的FileReader
        reader.close();
    }
}
