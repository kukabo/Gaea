package org.example.common.reflection;

import org.example.common.bean.clazz.loader.SwapClassBean;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/*
*
* 目标：
*   Class的创建过程
*
* */
public class ReflectionPartTwo {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get("/Users/webster/project/github/kukabo.github.io/common-hello/src/main/resources/reflection.properties")));
        String classPathName = properties.getProperty("classPathName");

        //(1)例如下面代码，Class 对象不是 new出来的，而是类加载器通过 loadClass()方法加载类生成 Class 对象（断点可发现）
        SwapClassBean swapClassBean = new SwapClassBean();
        /*(2)反射方式最终也是 类加载器通过 loadClass()方法加载类生成 Class 对象，前提是先注释掉 SwapClassBean swapClassBean = new SwapClassBean();
         因为类加载只会做一次*/
        Class<?> cls = Class.forName(classPathName);

    }


}
