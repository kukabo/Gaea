package org.example.common.reflection;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/*
*
* 反射学习， 记住 Java 里一切皆对象
*   类是 Class 对象，构造器是 Constructor 对象，成员变量是 Field 对象，方式是 Method 对象
*
* 目标
*   实现在不改变代码的前提下，动态调用不同的类、不同方法
*
* */
public class ReflectionPartOne {

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream("src\\main\\resources\\reflection.properties");
        properties.load(fileInputStream);

        System.out.println(properties.getProperty("classPathName"));
        System.out.println(properties.getProperty("methodName"));
    }

}
