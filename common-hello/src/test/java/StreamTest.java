import org.junit.Test;

import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 * Stream的几个常见操作：map()、filter()、reduce()
 * 这些操作对Stream来说可以分为两类：
 * 一类是转换操作，即把一个Stream转换为另一个Stream，例如map()和filter()；
 * 另一类是聚合操作，即对Stream的每个元素进行计算，得到一个确定的结果，例如reduce()。
 * 区分这两种操作是非常重要的，因为对于Stream来说，对其进行转换操作并不会触发任何计算
 * .collect(Collectors.toList()) 和 .collect(Collectors.toMap()) 也是聚合操作
 * */
public class StreamTest {

    @Test
    public void FeiBoNaQi() {
        Stream.generate(new FeiBoNaQiSupplier()).limit(20).forEach(System.out::println);
    }

    //斐波那契数列
    public class FeiBoNaQiSupplier implements Supplier<Integer> {
        int pre = 0;
        int cur = 1;
        @Override
        public Integer get() {
            int next = pre + cur;
            pre = cur;
            cur = next;
            return pre;
        }
    }


}


