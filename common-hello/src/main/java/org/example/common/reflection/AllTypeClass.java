package org.example.common.reflection;

import java.io.Serializable;

//哪些类型有 Class 对象
public class AllTypeClass {

    public static void main(String[] args) {

        //这些类型都有 Class 对象
        Class<Serializable> cls1 = Serializable.class;//接口
        Class<Integer[]> cls2 = Integer[].class;//数组
        Class<Integer> cls3 = int.class;//基础类
        Class<Void> cls4 = void.class;//万物皆对象
        Class<Class> cls5 = Class.class;
        Class<Enum> cls6 = Enum.class;//抽象类
        Class<String> cls7 = String.class;//外部类
        Class<Thread.State> cls8 = Thread.State.class;//枚举类
        Class<Deprecated> cls9 = Deprecated.class;//注解类
        Class<Float[][]> cls10 = Float[][].class;//二维数组

    }

}
