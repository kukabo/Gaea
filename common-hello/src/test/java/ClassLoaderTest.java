import org.example.common.classLoader.MyClassLoader;
import org.junit.Test;

public class ClassLoaderTest {

    /*
    * 运行如下测试代码，会报错java.io.FileNotFoundException: src/main/java/java/lang/Object.class (No such file or directory)
    * 原因是：Object作为所有类的父类，加载Hello时会先加载Object，Object并不在目录src/main/java/下
    * */
    @Test
    public void testMyClassLoader() throws ClassNotFoundException {

        //获取加载ClassLoaderTest的父加载器
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
        System.out.println(classLoader);
        System.out.println(classLoader.getParent());

        MyClassLoader myClassLoader = new MyClassLoader(classLoader.getParent());

        Class<?> swapTestBean = myClassLoader.loadClass("org.example.common.bean.Hello");
        System.out.println(swapTestBean.getClassLoader());
    }

    /*
    * 验证 java禁止用户用自定义的类加载器加载java.开头的官方类
    * 路径问题，代码运行不成功
    * */
    @Test
    public void testMyClassLoaderOther() throws ClassNotFoundException {

        //获取加载ClassLoaderTest的父加载器
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
        System.out.println(classLoader);
        System.out.println(classLoader.getParent());

        MyClassLoader myClassLoader = new MyClassLoader(classLoader.getParent());

        Class<?> swapTestBean = myClassLoader.loadClassOther("java.lang.Object");
        System.out.println(swapTestBean.getClassLoader());
    }

    @Test
    public void findPath() {
        System.out.println(System.getProperty("sun.boot.class.path"));
        //     /Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/rt.jar
    }


}
