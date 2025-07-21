package org.example.common.clazz.loader;

//演示 类加载 连接的准备阶段
public class Load_LinkPrepare {
    //分析准备阶段，属性是如何处理的
    //n1是成员变量，准备阶段是不会分配内存
    //n2是静态变量，准备阶段会分配内存，默认初始化是 0，不是 20
    //n3是常量，一旦赋值就不变，准备阶段会分配内存，默认初始化是 30
    private int n1 = 10;
    private static int n2 = 20;
    private static final long n3 = 30;
}
