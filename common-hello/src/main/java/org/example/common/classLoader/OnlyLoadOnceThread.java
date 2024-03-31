package org.example.common.classLoader;

/*
 * 验证：一个类的 <clinit>()方法 只被执行一次，会通过执行加锁实现
 * 当OnlyLoadOnceThread被加载时会执行静态方法，Thread.sleep模拟线程一直未释放锁，那么其他线程无法进入
 * 当类的静态代码块被执行过，其他线程再执行静态代码块时会判断是否已有其他线程执行，若是自行过就不再执行
 * */
public class OnlyLoadOnceThread {
    static {
        System.out.println(Thread.currentThread().getName() + "初始化");
        if (true) {
            while (true) {
                try {
                    Thread.sleep(5000);
                    break;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Thread thread = new Thread();
        thread.getContextClassLoader();

    }
}
