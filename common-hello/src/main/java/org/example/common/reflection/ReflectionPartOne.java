package org.example.common.reflection;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get("/Users/webster/project/github/kukabo.github.io/common-hello/src/main/resources/reflection.properties")));
        String classPathName = properties.getProperty("classPathName");
        String methodName = properties.getProperty("methodName");
        System.out.println("classPathName is " + classPathName);
        System.out.println("methodName is " + methodName);

        //使用反射解决
        //(1)加载类，返回Class类型的对象 cls
        Class<?> cls = Class.forName(classPathName);
        //(2)创建org.example.common.SwapClassBean 的实例对象
        Object o = cls.newInstance();
        System.out.println("o的运行类型" + o.getClass());//运行类型
        //(3)获取方法对象 method
        Method method = cls.getMethod(methodName);
        //(4)通过方法对象来实现调用方法
        method.invoke(o);

    }


}
