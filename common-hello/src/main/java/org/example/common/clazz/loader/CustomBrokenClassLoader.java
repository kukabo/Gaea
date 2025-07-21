package org.example.common.clazz.loader;


import org.example.common.utils.InputStreamUtil;

/**
 * 目的：要打破双亲委派机制
 * 场景：类A引用了类B，类A是自定义类加载器C加载，那么B也让C加载，而不是系统类加载器加载
 * 参考：https://blog.csdn.net/fuzhongmin05/article/details/125120572
 * 类加载传导规则：JVM会选择当前类的类加载器来加载所有该类要引用的类
 * 实现：
 *  1、打破双亲委派，必须覆写loadClass方法
 *  2、核心类还是ExtClassLoader或BootStrapClassLoader加载，需要传入 ExtClassLoader
 *  3、自己创建的业务类由自定义类加载，
 * */

public class CustomBrokenClassLoader extends ClassLoader {

    private final ClassLoader jdkClassLoader;
    private final String filePath;

    public CustomBrokenClassLoader(ClassLoader jdkClassLoader, String filePath) {
        this.jdkClassLoader = jdkClassLoader;
        this.filePath = filePath;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        if (jdkClassLoader == null) {
            throw new ClassNotFoundException("ExtClassLoader 异常");
        }
        try {
            //核心类例如java.lang下的还是ExtClassLoader或BootStrapClassLoader加载；
            Class<?> aClass = jdkClassLoader.loadClass(name);
            if (aClass != null) {
                return aClass;
            }
        } catch (ClassNotFoundException e) {
            //忽略
            System.out.println("忽略" + name);
        }

        //自己创建的业务类由自定义类加载器加载
        byte[] data = InputStreamUtil.loadByte(name, filePath);
        return defineClass(name, data, 0, data.length);
    }

}
