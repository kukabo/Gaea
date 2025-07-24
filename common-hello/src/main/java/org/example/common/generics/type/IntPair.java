package org.example.common.generics.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class IntPair extends Pair<Integer> {
    public IntPair(Integer first, Integer last) {
        super(first, last);
    }

    public static void main(String[] args) {
        IntPair integerIntPair = new IntPair(1, 2);
        Class<? extends IntPair> clazz = integerIntPair.getClass();
        // getSuperclass() 获得该类的父类
        Class<?> superclass = clazz.getSuperclass();
        System.out.println(superclass);

        // getGenericSuperclass() 获得该类带有泛型的父类
        Type genericSuperclass = clazz.getGenericSuperclass();
        System.out.println(genericSuperclass);

        if (genericSuperclass instanceof ParameterizedType) {
            // ParameterizedType 参数化类型，即泛型
            // 将Type转化为参数化类型(即泛型)
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            Type[] types = pt.getActualTypeArguments(); // 可能有多个泛型类型
            Type firstType = types[0]; // 取第一个泛型类型
            Class<?> typeClass = (Class<?>) firstType;
            System.out.println(typeClass); // Integer
        }
    }
}
