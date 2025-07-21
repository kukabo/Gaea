import org.example.common.clazz.loader.CustomBrokenClassLoader;
import org.example.common.clazz.loader.CustomClassLoader;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomClassLoaderTest {

    @Test
    public void testMyClassLoader() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        //编译后的.class文件目录
        String filePath = "D:/project/ims-proj/Gaea/common-hello/target/classes";

        //需要加载的类路径
        String classPath = "org.example.common.bean.Hello";
        String classPathNew = "org.example.common.bean.classLoader.Hello";

        // 初始化自定义类加载器，会先初始化父类ClassLoader，其中会把自定义类加载器的父类加载器设置为应用程序类加载器AppClassLoader
        CustomClassLoader classLoader1 = new CustomClassLoader(filePath);
        System.out.println("========自定义类加载器 CustomClassLoader 的父加载器========");
        System.out.println(classLoader1.getParent());
        System.out.println();

        // 自定义类加载classLoader1，加载类Hello.class
        Class<?> clazz1 = classLoader1.findClass(classPath);
        Object obj1 = clazz1.newInstance();
        System.out.println(obj1.toString());
        System.out.println(clazz1.getClassLoader());
        System.out.println();

        /*
        * Class.forName和classLoader2.findClass，效果是否一样？
        * 从结果上看：Class.forName并没有用传入的自定义类加载器，还是用的AppClassLoader，为什么？
        * 原因：
        * 在加载classPathNew类时确实是classLoader2发起加载流程的，但是在loadClass方法中，是先委托给父加载器来加载。
        * 如果父加载器找不到classPathNew类，才会调用classLoader2覆写的findClass方法进行加载。
        * 而classPathNew就这样被委托给了classLoader2的父加载器AppClassLoader来加载了。
        * 参考：https://blog.csdn.net/fuzhongmin05/article/details/125120572
        * */
        CustomClassLoader classLoader2 = new CustomClassLoader(filePath);
        Class<?> clazz2 = Class.forName(classPathNew, true, classLoader2);
        Object obj2 = clazz2.newInstance();
        System.out.println(obj2.toString());
        Method addMethod = clazz2.getMethod("add");
        addMethod.invoke(obj2);
        System.out.println(clazz2.getClassLoader());
        System.out.println();

        //进一步验证：没有重写loadClass，向上委派，就是AppClassLoader加载
        System.out.println("==========================");
        String classPath4 = "org.example.common.bean.classLoader.BrokenClassABean";
        CustomClassLoader classLoader4 = new CustomClassLoader(filePath);
        Class<?> aClass = classLoader4.loadClass(classPath4);
        Object aOb = aClass.newInstance();
        Method hello = aClass.getMethod("hello");
        hello.invoke(aOb);
        System.out.println();


        // 新建类加载器classLoader3，同样加载classPath
        CustomClassLoader classLoader3 = new CustomClassLoader(filePath);
        Class<?> clazz3 = classLoader3.findClass(classPath);
        Object obj3 = clazz3.newInstance();
        System.out.println(obj3.toString());
        System.out.println(clazz3.getClassLoader());
        System.out.println();

        // 同一个类，被不同类加载器加载，不一样
        if (clazz1 == clazz3) {
            System.out.println("clazz1 == clazz3");
            System.out.println();
        }

        // 类对象的唯一性
        if (obj1.getClass() == clazz1) {
            System.out.println("obj1.getClass() == clazz1");
        }
    }

    //打破双亲委派
    @Test
    public void testBrokenClassLoader() throws Exception {

        //编译后的.class文件目录
        String filePathNew = "D:/project/ims-proj/Gaea/common-hello/target/classes";
        //需要加载的类路径
        String classPathNew = "org.example.common.bean.classLoader.Hello";
        //ExtClassLoader
        ClassLoader extClassLoader = Thread.currentThread().getContextClassLoader().getParent();
        System.out.println("AppClassLoader的父加载器：" + extClassLoader);
        System.out.println();
        CustomBrokenClassLoader classLoader2 = new CustomBrokenClassLoader(extClassLoader, filePathNew);
        Class<?> clazz2 = Class.forName(classPathNew, true, classLoader2);
        Object obj2 = clazz2.newInstance();
        System.out.println(obj2.toString());
        Method addMethod = clazz2.getMethod("add");
        addMethod.invoke(obj2);
        System.out.println(clazz2.getClassLoader());
        System.out.println();


        classPathNew = "org.example.common.bean.classLoader.BrokenClassABean";
        Class<?> aClass = classLoader2.loadClass(classPathNew);
        Object aOb = aClass.newInstance();
        Method hello = aClass.getMethod("hello");
        hello.invoke(aOb);


    }


}
