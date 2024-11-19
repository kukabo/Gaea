package org.example.common.classLoader;

//https://blog.csdn.net/qq_32907195/article/details/109153644
//https://blog.csdn.net/weixin_36586120/article/details/117457014

import java.io.*;

/*
* 自定义类加载器，验证双亲委派
* */
public class MyClassLoader extends ClassLoader {
    public MyClassLoader(ClassLoader parent) {
        super(parent);
    }

    //想自定义类加载器，就需要继承ClassLoader，并重写findClass
    @Override
    protected Class<?> findClass(String name) {
        // 1、获取class文件二进制字节数组
        byte[] data = null;
        try {
            String classFile = "src/main/java/" + name.replace('.', File.separatorChar) + ".class";
            System.out.println(classFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(classFile);
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fis.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 2、字节码加载到 JVM 的方法区，
        // 并在 JVM 的堆区建立一个java.lang.Class对象的实例
        // 用来封装 Java 类相关的数据和方法
        return this.defineClass(name, data, 0, data.length);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> clazz = null;
        // 直接自己加载
        clazz = this.findClass(name);
        if (clazz != null) {
            return clazz;
        }

        // 自己加载不了，再调用父类loadClass，保持双亲委托模式
        return super.loadClass(name);
    }


    protected Class<?> findClassOther(String name) {
        // 1、获取class文件二进制字节数组
        byte[] data = null;
        try {
            String dir = "/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/rt.jar";
            String classFile = dir + name.replace('.', File.separatorChar) + ".class";
            System.out.println(classFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(classFile);
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fis.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 2、字节码加载到 JVM 的方法区，
        // 并在 JVM 的堆区建立一个java.lang.Class对象的实例
        // 用来封装 Java 类相关的数据和方法
        return this.defineClass(name, data, 0, data.length);
    }


    public Class<?> loadClassOther(String name) throws ClassNotFoundException {
        Class<?> clazz = null;
        // 直接自己加载
        clazz = this.findClassOther(name);
        if (clazz != null) {
            return clazz;
        }

        // 自己加载不了，再调用父类loadClass，保持双亲委托模式
        return super.loadClass(name);
    }
}
