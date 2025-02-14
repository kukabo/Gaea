import org.example.common.functionIface.MyFunctionInterface;
import org.junit.Test;

public class FunctionInterfaceTest {

    //方案2.定义一个方法以函数式接口作入参
    public static void test(MyFunctionInterface myFunctionInterface) {
        myFunctionInterface.show();
    }

    @Test
    public void testShow() {
        //方式1.使用匿名内部类的方式
        MyFunctionInterface myFunctionInterface = new MyFunctionInterface() {
            @Override
            public void show() {
                System.out.println("hello 1");
            }
        };
        test(myFunctionInterface);

        //方式2.直接传递匿名内部类
        test(new MyFunctionInterface() {
            @Override
            public void show() {
                System.out.println("hello 2");
            }
        });

    }
}
