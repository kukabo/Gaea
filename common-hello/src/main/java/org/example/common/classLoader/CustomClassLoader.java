package org.example.common.classLoader;

import java.io.FileInputStream;

/**
 * 自定义类加载器是必须要覆写findClass方法的，是否要覆写loadClass方法则取决于是否想要打破双亲委派。
 * 如果不想打破双亲委派机制，自定义类加载器覆写findClass方法即可，如果要打破双亲委派机制，就要覆写整个loadClass方法。
 * */

//https://blog.csdn.net/qq_32907195/article/details/109153644
//https://blog.csdn.net/weixin_36586120/article/details/117457014

public class CustomClassLoader extends ClassLoader {
    private final String classPath;

    public CustomClassLoader(String classPath) {
        this.classPath = classPath;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] data = loadByte(name);
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

    private byte[] loadByte(String name) throws Exception {
        name = name.replaceAll("\\.", "/");
        FileInputStream fileInputStream = new FileInputStream(classPath + "/" + name + ".class");
        int len = fileInputStream.available();
        byte[] data = new byte[len];
        int dataLen = fileInputStream.read(data);
        fileInputStream.close();
        return data;
    }

}
