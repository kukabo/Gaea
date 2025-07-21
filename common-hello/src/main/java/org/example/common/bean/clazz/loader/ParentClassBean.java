package org.example.common.bean.clazz.loader;

public class ParentClassBean {

    //常量，准备阶段
    public static final int a = 10;

    //静态变量
    public static String b = "父类--静态变量";

    //变量
    public String d = "父类--变量";

    public int i = 0;

    //静态代码块
    static {
        System.out.println(b);
        System.out.println("父类--静态初始化块");
    }

    //初始化块
    {
        System.out.println(d);
        System.out.println("父类--初始化块");
    }

    //构造器
    public ParentClassBean() {
        System.out.print("父类--构造器");
        System.out.println("i=" + i);
        i++;
    }
}
