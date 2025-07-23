package org.example.common.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 比较 CompletableFuture.runAsync 和 CompletableFuture.supplyAsync()
 * 1.runAsync，没有返回值；supplyAsync，有返回值
 * 2.这两个方法都有重载版本，可以指定传入线程池 Executor executor
 */

public class CompareRunAsyncSupplyAsync {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // ----------------------- runAsync() -----------------------
        // Runnable: 没有返回值 (日志记录操作)
        CompletableFuture<Void> futureRun = CompletableFuture.runAsync(() -> {
            System.out.println("runAsync(): Doing some long-running background task (no result)...");
            // 模拟耗时操作
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            System.out.println("runAsync(): Background task finished.");
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
    }
}