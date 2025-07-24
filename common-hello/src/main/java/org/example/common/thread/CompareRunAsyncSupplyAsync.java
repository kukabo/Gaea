package org.example.common.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 比较 CompletableFuture.runAsync 和 CompletableFuture.supplyAsync()
 * 1.runAsync，没有返回值；supplyAsync，有返回值
 * 2.这两个方法都有重载版本，可以指定传入线程池 Executor executor
 *
 *
 * 扩展： 1.ForkJoinTask.invokeAll()，把拆分的子任务并行运行
 * java.util.Arrays.parallelSort(array)的并行排序，原理就是ForkJoinTask.invokeAll()
 * 2.ThreadLocal，它可以在一个线程中传递同一个对象，避免多个方法调用时参数上下文不断传递
 * 实际上可以把ThreadLocal看成一个全局Map<Thread, Object>：每个线程获取ThreadLocal变量时，总是使用Thread自身作为key：
 * Object threadLocalValue = threadLocalMap.get(Thread.currentThread());
 * ThreadLocal相当于给每个线程都开辟了一个独立的存储空间，各个线程的ThreadLocal关联的实例互不干扰。
 */

public class CompareRunAsyncSupplyAsync {

    static ThreadLocal<String> threadLocalUserName = new ThreadLocal<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //主线程添加，子线程无法获取
        threadLocalUserName.set("mainThread");

        // ----------------------- runAsync() -----------------------
        // Runnable: 没有返回值 (日志记录操作)
        CompletableFuture<Void> futureRun = CompletableFuture.runAsync(() -> {
            System.out.println("runAsync(): Doing some long-running background task (no result)...");
            // 模拟耗时操作
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            System.out.println("runAsync(): Background task finished.");
            //子线程无法获取
            System.out.println("sunThread---" + Thread.currentThread().getName() + "---" + threadLocalUserName.get());
        });

        // 等待runAsync的任务完成（无法获取"结果"，只关心完成与否）
        futureRun.get(); // 阻塞直到完成
        System.out.println("runAsync() future completed.");

        // ----------------------- supplyAsync() -----------------------
        // Supplier<String>: 返回一个字符串结果 (模拟计算结果)
        CompletableFuture<String> futureSupply = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync(): Calculating something important...");
            // 模拟耗时计算
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            return "The Result is 42";
        });

        // 处理supplyAsync的结果
        futureSupply.thenAccept(result -> System.out.println("supplyAsync() result processed asynchronously: " + result));
        // 或者直接获取（会阻塞）
        String result = futureSupply.get();
        System.out.println("supplyAsync() result retrieved (blocking): " + result);
        //主线程能获取到
        System.out.println("main---" + Thread.currentThread().getName() + "---" + threadLocalUserName.get());

        //注意：因为当前线程执行完相关代码后，很可能会被重新放入线程池中，如果ThreadLocal没有被清除，该线程执行其他代码时，会把上一次的状态带进去。
        threadLocalUserName.remove();
    }
}