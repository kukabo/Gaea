import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * //1、单个参数
 * (String s)->s.length()
 *
 * //2、单个对象
 * (Apple a)->a.getWeight()>150
 *
 * //3、多参数,多语句
 * (int a,int b)->{
 * 	System.out.println(a);
 * 	System.out.println(b);
 * }
 *
 * //4、空参数,返回int值42
 * ()->42
 *
 * //5、多对象参数
 * (Applea1,Applea2)->a1.getWeight().compareTo(a2.getWeight())
 *
 *
 * Lambda表达式并不能取代所有的匿名内部类，只能用来取代函数接口（Functional Interface）的简写
 * https://objcoding.com/2019/03/04/lambda/
 * */
public class LambdaTest {

    @Test
    public static void testLambada() {
        // JDK7 匿名内部类写法
        List<String> listA = Arrays.asList("I", "love", "you", "too");
        listA.sort(new Comparator<String>() {// 接口名
            @Override
            public int compare(String s1, String s2) {// 方法名
                if (s1 == null)
                    return -1;
                if (s2 == null)
                    return 1;
                return s1.length() - s2.length();
            }
        });

        // JDK8 Lambda表达式写法
        List<String> listB = Arrays.asList("I", "love", "you", "too");
        listB.sort((s1, s2) -> {// 省略参数表的类型
            if (s1 == null)
                return -1;
            if (s2 == null)
                return 1;
            return s1.length() - s2.length();
        });
    }

}
