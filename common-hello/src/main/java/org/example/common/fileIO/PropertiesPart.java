package org.example.common.fileIO;

import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

//Properties 父类 是 Hashtable
public class PropertiesPart {

    public static void main(String[] args) {

    }

    /*
    * 读取配置文件
    *
    * */

    @Test
    public void testRead() throws IOException {
        //
        Properties properties = new Properties();
        // 加载配置文件
        properties.load(Files.newInputStream(Paths.get("src/main/resources/dataSource.properties")));
        //k-v显示在控制台
        properties.list(System.out);
        //根据 k 获取对应的值
        String password = properties.getProperty("password");
        System.out.println("password is     " + password);
    }

    /*
    * 修改配置文件
    *
    * */
    @Test
    public void testWrite() {

    }



}
