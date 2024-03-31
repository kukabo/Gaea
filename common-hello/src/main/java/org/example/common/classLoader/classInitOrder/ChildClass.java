package org.example.common.classLoader.classInitOrder;

public class ChildClass extends ParentClass {
    //常量，准备阶段
    public static final int a = 10;

    //静态变量
    public static String b = "子类--静态变量";

    //变量
    public String d = "子类--变量";


    //静态代码块
    static {
        System.out.println(b);
        System.out.println("子类--静态初始化块");
    }

    //初始化块
    {
        System.out.println(d);
        System.out.println("子类--初始化块");
    }

    //构造器
    public ChildClass() {
        System.out.print("子类--构造器");
        System.out.println("i=" + i);
    }
}
