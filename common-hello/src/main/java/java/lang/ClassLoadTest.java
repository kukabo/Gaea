package java.lang;

import org.example.common.classLoader.classInitOrder.ChildClass;

import java.util.ServiceLoader;

public class ClassLoadTest {

    public static void main(String[] args) throws ClassNotFoundException {

        ChildClass childClass = new ChildClass();


        childClass.getClass().getClassLoader().loadClass("");
        //根据类的全限定名，获取字节码二进制流，并创建对应的 Class 对象
        Class<?> aClass = childClass.getClass().getClassLoader().findClass("");
    }
}
