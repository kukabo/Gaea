package org.example.common.clazz.loader;

import org.example.common.bean.clazz.loader.ChildClassBean;
import org.example.common.bean.clazz.loader.ParentClassBean;
import org.example.common.bean.clazz.loader.SwapClassBean;

public class ClassLoaderTest {
    public static void main(String[] args) {

        /*
         * 类变量：同一个类的不同实例变量是不一样的，但类变量是同一个
         * */
        System.out.println("-------同一个类的不同实例变量是不一样的，但类变量是同一个-------");
        SwapClassBean swapClassBean = new SwapClassBean();
        String name1= "111";
        String name2= "222";
        swapClassBean.swapString(name1, name2);
        System.out.println(name1 +"----"+ name2);

        Integer age1 = 111;
        Integer age2 = 222;
        swapClassBean.swapInteger(age1, age2);
        System.out.println(age1 +"----"+ age2);

        swapClassBean.a ++ ;
        swapClassBean.s = swapClassBean.s + "world";
        System.out.println(swapClassBean.a);
        System.out.println(swapClassBean.s);

        SwapClassBean swapClassBean1 = new SwapClassBean();
        System.out.println(swapClassBean1.a);
        System.out.println(swapClassBean1.s);

        /*
        * JVM 的三个类加载器
        * AppClassLoader：
        * ExtClassLoader：
        * BootStrapClassLoader：由C++开发，是JVM的一部分，本身不是Java类所以打印为null
        * */
        System.out.println("-------JVM 的三个类加载器-------");
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
        System.out.println(classLoader);
        System.out.println(classLoader.getParent());
        System.out.println(classLoader.getParent().getParent());
        //显然，classLoader1=classLoader.getParent().getParent()
        ClassLoader classLoader1 = String.class.getClassLoader();
        System.out.println(classLoader1);


        /*
         * 类加载器2中获取方式：
         * 通过类获取类加载器
         * 通过实例获取类对象再获取类加载器
         * */
        System.out.println("-------获取到 类加载器 的2种方式-------");
        ClassLoader classLoader2 = SwapClassBean.class.getClassLoader();
        ClassLoader classLoader3 = swapClassBean1.getClass().getClassLoader();

        if (classLoader2.hashCode() == classLoader3.hashCode()) {
            System.out.println("classLoader2==classLoader3");
        }



        /*
        * 类加载：
        * 第一步类加载器加载类，生成 Class对象；
        * 第二步链接（校验、准备、解析）Class对象；
        * 第三步初始化Class对象
        *
        * */
        System.out.println("-------第一步类加载的方式-------");
        try {
            classLoader2.loadClass("xxx");
        } catch (ClassNotFoundException e) {

        }


        /*
        * 第三步类初始化（类加载的第三个阶段）顺序：
        * 【注意：常亮是在准备阶段就赋值了】
        * 父类--静态变量
        * 父类--静态初始化块
        * 子类--静态变量
        * 子类--静态初始化块
        * 父类--变量
        * 父类--初始化块
        * 父类--构造器
        * 子类--变量
        * 子类--初始化块
        * 子类--构造器
        * */
        System.out.println("---------类初始化（类加载的第三个阶段）顺序，创建子类对象--------");
        ChildClassBean childClass = new ChildClassBean();
        System.out.println("---------类初始化（类加载的第三个阶段）顺序，创建父类对象--------");
        ParentClassBean parentClassBean = new ParentClassBean();
        System.out.println("证明：类只会加载一次");



        /*
        * 多线程验证类的<clinit>()方法会被执行几次：
        * 创建 2 个线程thread1 和 thread2，分别创建OnlyLoadOnceThread实例，若OnlyLoadOnceThread类的<clinit>()方法被执行多次，会打印"线程 1初始化 和 线程 2初始化"
        * 从结果上看，"线程 1初始化"和"线程 2初始化"只会打印一次，那么说明<clinit>()方法会被上锁，执行时先判断是否已经被执行过，若没有执行若有其他在线程在执行就不会再调用
        * */
        System.out.println("---------多线程验证类的<clinit>()方法会被执行几次--------");
        Runnable r = () -> {
            System.out.println(Thread.currentThread().getName()+"启动");
            OnlyLoadOnceThread thread = new OnlyLoadOnceThread();
            System.out.println(Thread.currentThread().getName()+"结束");
        };

        Thread thread1 = new Thread(r, "线程 1");
        Thread thread2 = new Thread(r, "线程 2");

        thread1.start();
        thread2.start();

    }

    /*
     * 静态代码块和静态变量是按顺序执行
     * 类加载阶段，number会默认赋值 0；静态代码库可以初始化但不能访问。
     *
     * <clinit>()方法是由编译器自动收集类中的所有类变量的赋值动作和静态语句块( static{}块）中的语句合并产生的，
     * 编译器收集的顺序是由语句在源文件中出现的顺序决定的，静态语句块中只能访问到定义在静态语句块之前的变量，定义在它之后的变量，
     * 在前面的静态语句块可以赋值，但是不能访问。    -------《深入了解java虚拟机》
     * */
    static {
        number = 11;//可以初始化
        //System.out.println(number);//不能访问
    }
    private static int number = 10;


}