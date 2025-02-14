package org.example.common.functionIface;


/**
 * 什么是函数式接口？
 * 如果在Java的接口中，有且只有一个抽象方法，那么这种接口就是函数式接口。
 * 函数式接口是使用Lambda表达式的前提条件
 * */
@FunctionalInterface
public interface MyFunctionInterface {
    void show();
}
