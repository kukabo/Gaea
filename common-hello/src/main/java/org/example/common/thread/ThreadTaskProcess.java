package org.example.common.thread;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 线程池业务任务
 */
public class ThreadTaskProcess {

    // 业务主表记录
    static class MasterRecord {
        String name;        // 业务名称
        String status;      // 业务状态
        List<SlaveRecord> slaveTasks = new ArrayList<>(); // 子任务列表

        MasterRecord(String name, String status) {
            this.name = name;
            this.status = status;
        }

        void addSubTask(String subName) {
            slaveTasks.add(new SlaveRecord(name, subName));
        }
    }

    // 业务子表记录
    static class SlaveRecord {
        String masterName;  // 所属业务名称
        String subName;     // 子任务名称

        SlaveRecord(String masterName, String subName) {
            this.masterName = masterName;
            this.subName = subName;
        }
    }

    // 数据库模拟器
    static class DatabaseSimulator {
        private final Map<String, MasterRecord> masterData = new HashMap<>();

        // 初始化测试数据
        public void initTestData() {
            createMasterRecord("A", "待处理", "A1", "A2", "A3");
            createMasterRecord("B", "待处理", "B1", "B2", "B3");
            createMasterRecord("C", "待处理", "C1", "C2", "C3");
        }

        private void createMasterRecord(String name, String status, String... tasks) {
            MasterRecord record = new MasterRecord(name, status);
            for (String task : tasks) {
                record.addSubTask(task);
            }
            masterData.put(name, record);
        }

        List<MasterRecord> fetchPendingTasks() {
            return new ArrayList<>(masterData.values());
        }

        void updateMasterStatus(String name, String status) {
            MasterRecord record = masterData.get(name);
            if (record != null) {
                record.status = status;
                System.out.printf("[%s] 主业务 %s 状态更新: %s%n",
                        Thread.currentThread().getName(), name, status);
            }
        }

        void updateSubTaskStatus(String masterName, String subName, String status) {
            System.out.printf("[%s] 子任务 %s-%s 状态更新: %s%n",
                    Thread.currentThread().getName(), masterName, subName, status);
        }
    }

    // 业务处理器
    static class BusinessProcessor {
        private final ExecutorService executor;
        private final DatabaseSimulator db = new DatabaseSimulator();

        // 业务任务跟踪
        private final Map<String, BlockingQueue<SlaveRecord>> taskQueues = new ConcurrentHashMap<>();
        private final Map<String, CompletableFuture<Void>> activeFutures = new ConcurrentHashMap<>();

        public BusinessProcessor() {
            executor = createConfiguredThreadPool();
            db.initTestData();
        }

        /**
         * 创建配置线程池
         */
        private ExecutorService createConfiguredThreadPool() {
            final AtomicInteger counter = new AtomicInteger(1);

            ThreadFactory factory = r -> {
                Thread t = new Thread(r);
                t.setName("BIZ-THREAD-" + counter.getAndIncrement());
                return t;
            };

            return new ThreadPoolExecutor(
                    4,                        // 核心线程数
                    8,                        // 最大线程数
                    30, TimeUnit.SECONDS,     // 空闲超时时间
                    new LinkedBlockingQueue<>(20), // 任务队列
                    factory,                  // 自定义线程工厂
                    new ThreadPoolExecutor.CallerRunsPolicy() // 饱和策略
            );
        }

        /**
         * 主处理流程
         */
        public void execute() {
            try {
                // 1. 加载待处理业务
                List<MasterRecord> pendingTasks = fetchPendingTasks();
                initializeTaskQueues(pendingTasks);

                // 2. 启动所有业务处理
                launchBusinessProcessors();

                // 3. 等待所有业务完成
                awaitCompletion();

                System.out.printf("[%s] 所有业务处理完成%n", Thread.currentThread().getName());
            } catch (Exception e) {
                System.err.printf("[%s] 业务处理异常: %s%n",
                        Thread.currentThread().getName(), e.getMessage());
            } finally {
                shutdownExecutorGracefully();
            }
        }

        /**
         * 从数据库加载待处理业务
         */
        private List<MasterRecord> fetchPendingTasks() {
            List<MasterRecord> tasks = db.fetchPendingTasks();
            String taskNames = tasks.stream()
                    .map(m -> m.name)
                    .collect(Collectors.joining(", "));

            System.out.printf("[%s] 加载到 %d 个待处理业务: %s%n",
                    Thread.currentThread().getName(), tasks.size(), taskNames);
            return tasks;
        }

        /**
         * 初始化各业务的任务队列
         */
        private void initializeTaskQueues(List<MasterRecord> records) {
            for (MasterRecord record : records) {
                BlockingQueue<SlaveRecord> queue = new LinkedBlockingQueue<>(record.slaveTasks);
                taskQueues.put(record.name, queue);
                System.out.printf("[%s] 业务 %s 初始化 %d 个子任务%n",
                        Thread.currentThread().getName(), record.name, record.slaveTasks.size());
            }
        }

        /**
         * 启动所有业务处理器
         */
        private void launchBusinessProcessors() {
            for (String business : taskQueues.keySet()) {
                //处理业务的第一项任务
                CompletableFuture<Void> future = CompletableFuture.runAsync(
                        () -> processBusinessChain(business), executor);
                //记录处理中
                activeFutures.put(business, future);

                System.out.printf("[%s] 已启动业务 %s 的处理流程%n",
                        Thread.currentThread().getName(), business);
            }
        }

        /**
         * 处理整个业务任务链
         */
        private void processBusinessChain(String business) {
            BlockingQueue<SlaveRecord> queue = taskQueues.get(business);
            if (queue == null) return;

            try {
                while (!queue.isEmpty()) {
                    SlaveRecord slaveTask = queue.take();
                    executeTask(business, slaveTask);
                }

                db.updateMasterStatus(business, "已完成");
                System.out.printf("[%s] 业务 %s 所有任务完成%n",
                        Thread.currentThread().getName(), business);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("[%s] 业务 %s 处理被中断: %s%n",
                        Thread.currentThread().getName(), business, e.getMessage());
                db.updateMasterStatus(business, "已中断");
            }
        }

        /**
         * 执行单个任务
         */
        private void executeTask(String business, SlaveRecord slaveTask) {
            String currentThread = Thread.currentThread().getName();
            String slaveTaskName = slaveTask.subName;

            System.out.printf("[%s] 【任务启动】%s | 业务 %s%n",
                    currentThread, slaveTaskName, business);

            try {
                // 模拟任务执行 (随机耗时)
                int duration = ThreadLocalRandom.current().nextInt(1, 5);
                System.out.printf("[%s] 【任务执行】%s | 业务 %s | 预计耗时: %d秒%n",
                        currentThread, slaveTaskName, business, duration);

                TimeUnit.SECONDS.sleep(duration);

                // 更新任务状态
                db.updateSubTaskStatus(business, slaveTaskName, "已完成");
                System.out.printf("[%s] 【任务完成】%s | 业务 %s | 实际耗时: %d秒%n",
                        currentThread, slaveTaskName, business, duration);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("[%s] 任务 %s-%s 被中断%n",
                        currentThread, business, slaveTaskName);
                db.updateSubTaskStatus(business, slaveTaskName, "已中断");
                throw new RuntimeException("任务中断", e);
            } catch (Exception e) {
                System.out.printf("[%s] 任务 %s-%s 处理失败: %s%n",
                        currentThread, business, slaveTaskName, e.getMessage());
                db.updateSubTaskStatus(business, slaveTaskName, "已失败");
            }
        }

        /**
         * 等待所有业务完成
         */
        private void awaitCompletion() throws Exception {
            CompletableFuture<?>[] futures = activeFutures.values()
                    .toArray(new CompletableFuture[0]);

            CompletableFuture.allOf(futures).get(1, TimeUnit.MINUTES);
        }

        /**
         * 关闭线程池
         */
        private void shutdownExecutorGracefully() {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.out.printf("[%s] 线程池未及时关闭，执行强制关闭%n",
                            Thread.currentThread().getName());
                    executor.shutdownNow();
                }
                System.out.printf("[%s] 线程池已关闭%n", Thread.currentThread().getName());
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 程序入口
     */
    public static void main(String[] args) {
        new BusinessProcessor().execute();
    }
}