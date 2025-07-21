package org.example.common.file.io;


import org.junit.Test;

import java.io.*;

//包装 字符流
public class BufferedWriterPart {


    @Test
    public void test() throws IOException {
        String path = "src/main/resources/writer01.txt";
        //覆盖的方式
        /*BufferedWriter writer1 = new BufferedWriter(new FileWriter(path));
        writer1.write("\n hello, this is BufferedWriterPart1");
        writer1.write("\n hello, this is BufferedWriterPart2");
        writer1.close();*/
        //追加的方式
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(path, true));
        writer2.write("\n hello, this is BufferedWriterPart3");
        //底层是关闭的 FileWriter
        writer2.close();
    }
}
