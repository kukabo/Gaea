package org.example.common.reflection;

//演示 Class的方法，和获取 Class对象的几种方式

import org.example.common.bean.classLoader.SwapClassBean;

public class Class01 {

    public static void main(String[] args) throws ClassNotFoundException {
        //方式 1：Class.forName()，应用场景多用于 配置文件的全类名加载
        String classPath = "org.example.common.bean.classLoader.SwapClassBean";//通过读取配置文件获取 s值
        Class<?> cls1 = Class.forName(classPath);

        //方式 2：类名.class，应用场景多用于 参数传递
        Class<SwapClassBean> cls2 = SwapClassBean.class;
        System.out.println(cls2);

        //方式 3：对象.getClass()，应用场景多用于 有对象实例
        SwapClassBean swapClassBean = new SwapClassBean();
        Class<? extends SwapClassBean> cls3 = swapClassBean.getClass();//运行时类型（多态）

        //方式 4：类加载器
        ClassLoader classLoader = swapClassBean.getClass().getClassLoader();//先获取类加载器
        Class<?> cls4 = classLoader.loadClass(classPath);//通过类加载器获取对象

    }


}
