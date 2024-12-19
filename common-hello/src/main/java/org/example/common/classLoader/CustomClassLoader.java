package org.example.common.classLoader;

import org.example.common.utils.InputStreamUtil;

/**
 * 目的：不打破双亲委派机制，用自定义类加载器加载类
 * 场景：类A引用了类B，类A是自定义类加载器C加载，B是AppClassLoader加载
 * 实现：
 * 1、自定义类加载器覆写findClass方法的，但不覆写loadClass方法
 * 2、如果不想打破双亲委派机制，自定义类加载器覆写findClass方法即可；如果要打破双亲委派机制，就要覆写整个loadClass方法。
 * */

//https://blog.csdn.net/qq_32907195/article/details/109153644
//https://blog.csdn.net/weixin_36586120/article/details/117457014

public class CustomClassLoader extends ClassLoader {
    private final String filePath;

    public CustomClassLoader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] data = InputStreamUtil.loadByte(name, filePath);
            /*
             * defineClass将一个字节数组转为Class对象
             * 字节数组加载到 JVM 的方法区，并在 JVM 的堆区建立一个java.lang.Class对象的实例，用来封装 Java 类相关的数据和方法
             * */
            return defineClass(name, data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.findClass(name);
    }

}
