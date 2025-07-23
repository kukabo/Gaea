package org.example.common.thread;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 线程池业务任务
 */
public class EvtTaskThreadDemo {

    private static final String preStatus = "待处理";
    private static final String activeStatus = "处理中";
    private static final String successStatus = "处理成功";
    private static final String failStatus = "处理失败";


    // 业务主表记录
    static class MasterRecord {
        String name;        // 业务名称
        volatile String status;      // 业务状态：待处理，处理中，处理成功，处理失败
        List<SlaveRecord> slaveTasks = new ArrayList<>(); // 子业务列表

        MasterRecord(String name, String status) {
            this.name = name;
            this.status = status;
        }

        void addSubTask(String subName, String subStatus) {
            slaveTasks.add(new SlaveRecord(name, subName, subStatus));
        }
    }

    // 业务子表记录
    static class SlaveRecord {
        String masterName;  // 所属业务名称
        String subName;     // 子业务名称
        volatile String subStatus;  //子业务状态：待处理，处理中，处理成功，处理失败

        SlaveRecord(String masterName, String subName, String subStatus) {
            this.masterName = masterName;
            this.subName = subName;
            this.subStatus = subStatus;
        }
    }

    // oldTask实例表
    static class OldTaskInstance {
        String subName; // 子业务名称
        String instanceStatus; // 执行状态： 处理中， 处理成功， 处理失败

        public OldTaskInstance(String subName, String instanceStatus) {
            this.subName = subName;
            this.instanceStatus = instanceStatus;
        }
    }

    // 模拟数据库
    static class DatabaseSimulator {
        private final Map<String, MasterRecord> masterDataMap = new HashMap<>();
        private final Map<String, OldTaskInstance> oldTaskInstanceMap = new HashMap<>();

        // 待处理的业务
        public void insertMasterData() {
            createMasterRecord("A", preStatus, "A1异步", "A2", "A3");
            createMasterRecord("B", preStatus, "B1异步", "B2", "B3");
            createMasterRecord("C", preStatus, "C1异步", "C2", "C3");
        }

        private void createMasterRecord(String name, String status, String... tasks) {
            MasterRecord record = new MasterRecord(name, status);
            for (String task : tasks) {
                record.addSubTask(task, status);
            }
            masterDataMap.put(name, record);
        }

        public void insertTaskInstance(SlaveRecord slaveRecord) {
            OldTaskInstance oldTaskInstance = new OldTaskInstance(slaveRecord.subName, activeStatus);
            oldTaskInstanceMap.put(slaveRecord.subName, oldTaskInstance);
        }

        synchronized void updateTaskInstance(SlaveRecord slaveRecord, String instanceStatus) {
            OldTaskInstance oldTaskInstance = oldTaskInstanceMap.get(slaveRecord.subName);
            if (oldTaskInstance != null) {
                oldTaskInstance.instanceStatus = instanceStatus;
                System.out.printf("[%s] | 异步子业务 %s 状态更新: %s%n",
                        Thread.currentThread().getName(), slaveRecord.subName, instanceStatus);
            }
        }

        String searchInstanceStatus(SlaveRecord slaveRecord) {
            OldTaskInstance oldTaskInstance = oldTaskInstanceMap.get(slaveRecord.subName);
            return oldTaskInstance.instanceStatus;
        }

        public List<MasterRecord> searchPendingTasks() {
            return new ArrayList<>(masterDataMap.values());
        }

        public MasterRecord searchMaser(String name) {
            return masterDataMap.get(name);
        }

        synchronized void updateMasterStatus(String name, String status) {
            MasterRecord record = searchMaser(name);
            if (record != null) {
                record.status = status;
                System.out.printf("[%s] | 主业务 %s 数据库状态更新: %s%n",
                        Thread.currentThread().getName(), name, status);
            }
        }

        SlaveRecord searchSlave(String masterName, String subName) {
            MasterRecord masterRecord = searchMaser(masterName);
            for (SlaveRecord slaveTask : masterRecord.slaveTasks) {
                if (slaveTask.subName.equals(subName))
                    return slaveTask;
            }
            return null;
        }

        synchronized void updateSlaveStatus(String masterName, String subName, String status) {
            MasterRecord masterRecord = searchMaser(masterName);
            for (SlaveRecord slave : masterRecord.slaveTasks) {
                if (slave.subName.equals(subName))
                    slave.subStatus = status;
            }
            System.out.printf("[%s] | 主业务 %s | 子任务 %s 数据库状态更新: %s%n",
                    Thread.currentThread().getName(), masterName, subName, status);
        }
    }

    // 业务处理类
    static class BusinessProcessor {
        private final ExecutorService threadPoolExecutor;
        private final DatabaseSimulator mockDataBase = new DatabaseSimulator();

        // 全部业务，子业务按存放顺序执行，已执行的会移除队列并且激活下一个子业务处理中，全部处理完后应该是null
        private final Map<String, BlockingQueue<SlaveRecord>> allBizQueueMap = new ConcurrentHashMap<>();
        // 需要异步处理
        private final Map<String, BlockingQueue<SlaveRecord>> aSyncBizQueueMap = new ConcurrentHashMap<>();
        // 需要同步处理
        private final Map<String, BlockingQueue<SlaveRecord>> syncBizQueueMap = new ConcurrentHashMap<>();
        private final Map<String, CompletableFuture<Void>> activeFutureMap = new ConcurrentHashMap<>();

        public BusinessProcessor() {
            threadPoolExecutor = createConfiguredThreadPool();
            mockDataBase.insertMasterData();
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

        private void printLog(String threadName, String methodName, String masterName, String subName)  {
            if (masterName.equals("null")) {
                System.out.printf("[%s-%s]%n", threadName, methodName);
                return;
            }
            if (subName.equals("null")) {
                System.out.printf("[%s-%s] | 主业务 %s %n",
                        threadName, methodName, masterName);
                return;
            }
            System.out.printf("[%s-%s] | 主业务 %s ｜ 子任务 %s %n",
                    threadName, methodName, masterName, subName);
        }

        /**
         * 从数据库加载待处理业务
         * 深 copy，区分数据库和队列
         */
        private List<MasterRecord> searchMasterRecordsFromDB() {
            List<MasterRecord> taskListFromDB = mockDataBase.searchPendingTasks();

            List<MasterRecord> newMasterList = new ArrayList<>();
            for (MasterRecord task : taskListFromDB) {
                MasterRecord record = new MasterRecord(task.name, task.status);
                for (SlaveRecord slaveTask : task.slaveTasks) {
                    record.addSubTask(slaveTask.subName, slaveTask.subStatus);
                }
                newMasterList.add(record);
            }

            String taskNames = newMasterList.stream()
                    .map(m -> m.name)
                    .collect(Collectors.joining(", "));

            System.out.printf("[%s] | 加载到 %d 个待处理业务: %s%n",
                    Thread.currentThread().getName(), newMasterList.size(), taskNames);
            return newMasterList;
        }

        /**
         * 业务逻辑
         */
        public void execute() {
            try {
                printLog(Thread.currentThread().getName(), "execute", "null", "null");
                // 1.加载待处理业务
                List<MasterRecord> pendingTasks = searchMasterRecordsFromDB();
                if (pendingTasks.isEmpty()) {
                    System.out.printf("无主业务%n");
                    return;
                }
                initializeTaskQueues(pendingTasks);

                // 2.启动所有异步子业务处理
                startAsyncSalveProcessors();

                // 3.启动剩余所有同步子业务处理
                startSyncSalveProcessors();

                // 4.等待所有业务完成
                awaitCompletion();

                System.out.printf("[%s] | 所有业务处理完成%n", Thread.currentThread().getName());
            } catch (Exception e) {
                System.out.printf("[%s] | Error:业务处理异常！%n", Thread.currentThread().getName());
                e.printStackTrace();
            } finally {
                shutdownExecutor();
            }
        }

        /**
         * 初始化各业务的任务队列
         * 特别说明：1.三个队列子业务是同一个引用，互相影响
         * 2.数据库和队列是两个不同的引用，互不影响
         */
        private void initializeTaskQueues(List<MasterRecord> masterList) {
            for (MasterRecord master : masterList) {
                if (master.slaveTasks.isEmpty()) {
                    System.out.printf("[%s] | 主业务%s无子业务%n",
                            Thread.currentThread().getName(), master.name);
                    return;
                }
                initAsyncQueueMap(master);
                initSyncQueueMap(master);
                initAllQueueMap(master);
                activeMasterBefore(master);
            }
        }

        /**
         * 把所有的异步子业务放入队列，封装到 aSyncBizQueueMap
         * 异步子业务必须是第一个，且只能有一个
         */
        private void initAsyncQueueMap(MasterRecord master) {
            SlaveRecord aSyncSlaveRecord = master.slaveTasks.get(0);
            if (!aSyncSlaveRecord.subName.contains("异步")) {
                System.out.printf("无异步任务需要处理" + master.name);
                return;
            }

            List<SlaveRecord> aSyncSlaveList = new ArrayList<>();
            aSyncSlaveList.add(aSyncSlaveRecord);
            BlockingQueue<SlaveRecord> aSyncQueues = new LinkedBlockingQueue<>(aSyncSlaveList);//异步子业务队列
            aSyncBizQueueMap.put(master.name, aSyncQueues);
            System.out.printf("[%s] | 主业务 %s 异步子业务初始化 %d 个子业务%n",
                    Thread.currentThread().getName(), master.name, aSyncSlaveList.size());
        }

        /**
         * 把所有的同步子业务放入队列，封装到 syncBizQueueMap
         * 异步子业务必须是第一个，且只能有一个
         */
        private void initSyncQueueMap(MasterRecord master) {
            ArrayList<SlaveRecord> syncRecords = new ArrayList<>(master.slaveTasks);
            SlaveRecord slaveRecord = syncRecords.get(0);
            if (slaveRecord.subName.contains("异步")) {
                syncRecords.remove(0);
            }
            BlockingQueue<SlaveRecord> syncQueues = new LinkedBlockingQueue<>(syncRecords);//同步子业务队列
            syncBizQueueMap.put(master.name, syncQueues);
            System.out.printf("[%s] | 主业务 %s 同步子业务初始化 %d 个子业务%n",
                    Thread.currentThread().getName(), master.name, syncRecords.size());
        }

        /**
         * 把所有的子业务放入队列，封装到 allBizQueueMap
         */
        private void initAllQueueMap(MasterRecord master) {
            String masterName = master.name;
            BlockingQueue<SlaveRecord> allBizQueue = new LinkedBlockingQueue<>(master.slaveTasks);//全队列
            allBizQueueMap.put(masterName, allBizQueue);
            System.out.printf("[%s] | 主业务 %s 全队列初始化 %d 个子业务%n",
                    Thread.currentThread().getName(), masterName, master.slaveTasks.size());
        }

        /**
         * 首先激活【主业务和第一个子业务】，队列中的业务和数据库中的业务状态上保持一致
         * 1.先更新队列
         * 2.再更新数据库：主业务和子业务
         */
        private void activeMasterBefore(MasterRecord master) {
            //1
            String masterName = master.name;
            BlockingQueue<SlaveRecord> allBizQueue = allBizQueueMap.get(masterName);
            SlaveRecord element = allBizQueue.element();
            if (!element.subStatus.equals(preStatus)) {
                throw new RuntimeException("Error:全队列第一个子业务初始状态不是待处理！" +
                        masterName + "-" + element.subName);
            }
            element.subStatus = activeStatus;//影响异步子队列
            //2
            mockDataBase.updateMasterStatus(masterName, activeStatus);
            mockDataBase.updateSlaveStatus(masterName, master.slaveTasks.get(0).subName, activeStatus);
        }

        /**
         * 同一个子线程 执行全部的异步子业务
         */
        private void startAsyncSalveProcessors() {
            //所有的异步子业务从队列中捞出，封装list
            List<SlaveRecord> aSyncList = new LinkedList<>();
            for (String masterName : aSyncBizQueueMap.keySet()) {
                BlockingQueue<SlaveRecord> aSyncQueue = aSyncBizQueueMap.get(masterName);
                while (!aSyncQueue.isEmpty()) {
                    try {
                        SlaveRecord aSyncSlave = aSyncQueue.take();
                        aSyncList.add(aSyncSlave);
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Error:处理中断！" + masterName, e);
                    }
                }
            }
            //线程处理
            if (!aSyncList.isEmpty()) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(
                        () -> processAsyncBizTask(aSyncList), threadPoolExecutor);
                activeFutureMap.put("Async", future);
            }
        }

        /**
         * 异步子业务的处理逻辑
         * 1.处理前，校验
         * 2.业务逻辑：全部的异步子业务，插入etl实例
         * 3.处理后，状态回写
         */
        private void processAsyncBizTask(List<SlaveRecord> aSyncSlaveRecords) {
            printLog(Thread.currentThread().getName(),
                    "processAsyncBizTask", "null", "null");
            for (SlaveRecord slaveRecord : aSyncSlaveRecords) {
                String masterName = slaveRecord.masterName;
                String subName = slaveRecord.subName;
                String name = masterName + "-" + subName;
                //1
                checkAsyncStatusBeforeExecute(slaveRecord);
                //2
                mockDataBase.insertTaskInstance(slaveRecord);
                try {
                    System.out.printf("[%s]模拟业务逻辑处理睡眠9s%n", Thread.currentThread().getName());
                    Thread.sleep(1000*9);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Error:中断异常！" + name);
                }
                if (!subName.equals("B1异步")) {
                    mockDataBase.updateTaskInstance(slaveRecord, successStatus);
                } else {
                    mockDataBase.updateTaskInstance(slaveRecord, failStatus);
                }
                //3
                aSyncProcessStatusAfterExecute(slaveRecord);
            }
        }

        /**
         * 处理前的校验
         * 1.当前子业务必须是处理中
         * 2.数据库主业务、子业务必须是处理中
         * 3.全队列验证顺序，并且首元素（第一个子业务）必须是处理中 (1和3是同一个引用）
         */
        private void checkAsyncStatusBeforeExecute(SlaveRecord slaveRecord) {
            String masterName = slaveRecord.masterName;
            String slaveName = slaveRecord.subName;
            String slaveStatus = slaveRecord.subStatus;

            printLog(Thread.currentThread().getName(), "checkAsyncStatusBeforeExecute",
                    masterName, slaveName + slaveStatus);
            //1
            if (!slaveStatus.equals(activeStatus))
                throw new RuntimeException("Error:当前子业务状态不是处理中！" + slaveStatus);
            //2
            String masterStatus = mockDataBase.searchMaser(masterName).status;
            if (!masterStatus.equals(activeStatus))
                throw new RuntimeException("Error:数据库主业务状态不是处理中，无法执行！" + masterStatus);

            String dbSlaveStatus = mockDataBase.searchSlave(masterName, slaveName).subStatus;
            if (!dbSlaveStatus.equals(activeStatus))
                throw new RuntimeException("Error:数据库子业务状态不是处理中，无法执行！" + dbSlaveStatus);
            //3
            BlockingQueue<SlaveRecord> currentMasterAllSlaveQueue = allBizQueueMap.get(masterName);
            SlaveRecord activeSlave = currentMasterAllSlaveQueue.element();

            String activeSubName = activeSlave.subName;
            if (!activeSubName.equals(slaveName))
                throw new RuntimeException("Error:子业务执行时顺序有误！" + activeSubName);

            String activeSubStatus = activeSlave.subStatus;
            if (!activeSubStatus.equals(activeStatus))
                throw new RuntimeException("Error:没有完成子业务激活！" + activeSubStatus);
        }

        /**
         * 如果处理失败，更新主业务失败
         * 如果处理成功，需要激活下一个子业务处理中
         */
        private void aSyncProcessStatusAfterExecute(SlaveRecord currentSlave) {
            if (mockDataBase.searchInstanceStatus(currentSlave).equals(failStatus)) {
                failUpdateStatusFail(currentSlave, "-aSyncProcessStatusAfterExecute");
            } else {
                successTakeCurrentSlaveAndActiveNext(currentSlave, "-aSyncProcessStatusAfterExecute");
            }
        }

        /**
         * 处理失败，更新数据库
         * 1.更新当前子业务状态为 处理失败
         * 2.主业务状态更新为 处理失败
         * 注意：不需要把队列下一个子业务更新为失败，报错的是当前子业务；只需在当前子业务执行前校验是否激活。

         * todo 1.如果主业务 A 有 2 个异步子业务，第一个报错了，后续还能否正常运行？
         * 2.同一个主业务的多个异步子业务，是否能并行执行？
         */
        private void failUpdateStatusFail(SlaveRecord currentSlave, String methodName) {
            try {
                String masterName = currentSlave.masterName;
                String subName = currentSlave.subName;
                String name = masterName + "-" + subName;

                String currentSlaveSubStatus = currentSlave.subStatus;
                if (!currentSlaveSubStatus.equals(activeStatus))
                    throw new RuntimeException("Error:当前子业务状态不是处理中，无法更新子业务状态为【处理失败】-" +
                            currentSlaveSubStatus + "-" + name + methodName);
                mockDataBase.updateSlaveStatus(masterName, subName, failStatus);
                mockDataBase.updateMasterStatus(masterName, failStatus);
            } catch (Exception e) {
                throw new RuntimeException("Error:failUpdateStatusFail处理失败！", e);
            }
        }

        /**
         * 处理成功
         * 1.数据库更新当前子业务状态为处理成功
         * 2.从全队列中移除当前子业务（不需要更新全队列的子业务状态）
         * 3.如果全队列为空说明全部处理完了，需要数据库更新主业务处理成功
         * 4.否则，激活全队列下一个子业务为处理中；同时更新数据库子业务为处理中
         */
        private void successTakeCurrentSlaveAndActiveNext(SlaveRecord currentSlaveRecord, String methodName) {
            String masterName = currentSlaveRecord.masterName;
            String subName = currentSlaveRecord.subName;
            String name = masterName + "-" + subName;
            //1
            mockDataBase.updateSlaveStatus(masterName, subName, successStatus);
            //2
            BlockingQueue<SlaveRecord> allBizQueue = allBizQueueMap.get(masterName);
            try {
                SlaveRecord slaveRecord = allBizQueue.take();
                System.out.printf("[%s] | 主业务 %s ｜ 当前子业务%s从全队列移除成功%n",
                        Thread.currentThread().getName(), masterName, slaveRecord.subName);
            } catch (InterruptedException e) {
                throw new RuntimeException("Error:队列访问异常！" + name, e);
            }
            //3
            if (allBizQueue.size() == 0) {
                System.out.printf("[%s] | 主业务 %s ｜ 全队列为空%n", Thread.currentThread().getName(), masterName);
                mockDataBase.updateMasterStatus(masterName, successStatus);
                return;
            }
            //4
            SlaveRecord element = allBizQueue.element();
            if (!element.subStatus.equals(preStatus))
                throw new RuntimeException("Error:全队列下个子业务状态不是待处理，激活失败！" +
                        element.subName + "-" + element.subStatus + "-" + name + methodName);
            element.subStatus = activeStatus;
            System.out.printf("[%s] | 主业务 %s ｜ 子业务%s 队列更新状态为%s%n",
                    Thread.currentThread().getName(), masterName, element.subName, activeStatus);
            mockDataBase.updateSlaveStatus(masterName, element.subName, activeStatus);//数据库更新
        }

        /**
         * 为每个主业务开启一个线程，处理此主业务下的同步子业务
         * 1.for循环处理：
             * 1).记录异步出错的主业务个数failNum
             * 2).如果当前同步子业务首元素还未激活（处理中），说明异步子业务还未处理完，跳过
             * 3).如果activeFutureMap存在说明执行过，不能重复执行，跳过
             * 4).开启线程执行
         * 2.如果异步子任务还没有全部处理完，递归调用
         */
        private void startSyncSalveProcessors() {
            int failNum = 0;
            for (String masterName : syncBizQueueMap.keySet()) {

                String masterStatus = mockDataBase.searchMaser(masterName).status;
                if (masterStatus.equals(failStatus)) {
                    System.out.printf("[%s] ｜ 主业务 %s %s 不可处理！%n",
                            Thread.currentThread().getName(), masterName, masterStatus);
                    failNum ++;
                }

                BlockingQueue<SlaveRecord> syncQueue = syncBizQueueMap.get(masterName);
                SlaveRecord dbSlave = mockDataBase.searchSlave(masterName, syncQueue.element().subName);
                if (!dbSlave.subStatus.equals(activeStatus)) {
                    System.out.printf("[%s] | 主业务 %s | 子业务 %s 等待异步子业务结束！%n",
                            Thread.currentThread().getName(), masterName, dbSlave.subName);
                    continue;
                }

                if (activeFutureMap.get(masterName) != null) {
                    System.out.printf("[%s] | 主业务 %s | 子业务 %s 已经执行！%n",
                            Thread.currentThread().getName(), masterName, dbSlave.subName);
                    continue;
                }

                //一个主业务，开启一个线程处理
                CompletableFuture<Void> future = CompletableFuture.runAsync(
                        () -> processSyncBizTask(masterName), threadPoolExecutor);

                //记录处理中
                activeFutureMap.put(masterName, future);
                System.out.printf("[%s] ｜ 主业务 %s 已启动处理流程%n",
                        Thread.currentThread().getName(), masterName);
            }

            // 减1是因为异步也放到了activeFutureMap，需要排除
            if (activeFutureMap.size() - 1 + failNum != syncBizQueueMap.keySet().size()) {
                try {
                    System.out.printf("[%s] | 等待异步子业务结束，时长1分钟！%n", Thread.currentThread().getName());
                    Thread.sleep(1000*60);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                startSyncSalveProcessors();
            }
        }

        /**
         * 处理整个同步子业务
         */
        private void processSyncBizTask(String masterName) {

            printLog(Thread.currentThread().getName(), "processSyncBizTask", masterName, "null");

            BlockingQueue<SlaveRecord> syncQueue = syncBizQueueMap.get(masterName);
            while (!syncQueue.isEmpty()) {
                SlaveRecord syncSlaveRecord;
                try {
                    syncSlaveRecord = syncQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Error:处理被中断" + masterName, e);
                }
                executeSyncSlaveTask(syncSlaveRecord);
            }
            System.out.printf("[%s] | 主业务 %s 所有任务完成%n",
                    Thread.currentThread().getName(), masterName);
        }

        /**
         * 执行单个子业务
         */
        private void executeSyncSlaveTask(SlaveRecord slaveTask) {
            String masterName = slaveTask.masterName;
            String slaveName = slaveTask.subName;
            String name = masterName + "-" + slaveName;
            String currentThread = Thread.currentThread().getName();

            printLog(currentThread, "executeSyncSlaveTask", masterName, slaveName);

            try {
                //处理前校验
                checkAsyncStatusBeforeExecute(slaveTask);
                // 模拟任务执行 (随机耗时)
                int duration = ThreadLocalRandom.current().nextInt(1, 5);
                System.out.printf("[%s] | 主业务 %s | 【任务执行】%s | 预计耗时: %d秒%n",
                        currentThread, masterName, slaveName, duration);
                try {
                    TimeUnit.SECONDS.sleep(duration);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Error:任务中断" + name, e);
                }
                //处理后执行成功
                successTakeCurrentSlaveAndActiveNext(slaveTask, "-executeSyncSlaveTask");
            } catch (Exception e) {
                System.out.printf("[%s] | Error:主业务 %s | 子业务%s  %s%n",
                        currentThread, masterName, slaveName, e.getMessage());
                failUpdateStatusFail(slaveTask, "-executeSyncSlaveTask");
            }
        }

        /**
         * 等待所有业务完成
         *
         * 比较 Future 和 CompletableFuture：
         * 1.Future类型的实例代表一个未来能获取结果的对象
         * 2.Future获得异步执行结果时，要么调用阻塞方法get()，要么轮询看isDone()是否为true，这两种方法都不是很好，因为主线程也会被迫等待
         * 3.CompletableFuture，异步任务结束 或 异步任务出错时，会自动回调某个对象的方法；
         * 4.CompletableFuture，主线程设置好回调后，不再关心异步任务的执行
         */
        private void awaitCompletion() {
            CompletableFuture<?>[] futures = activeFutureMap.values().toArray(new CompletableFuture[0]);
            try {
                // 获取结果，但只等待30分钟
                CompletableFuture.allOf(futures).get(30, TimeUnit.MINUTES);
            } catch (Exception e) {
                throw new RuntimeException("Error:awaitCompletion", e);
            }
        }

        /**
         * 关闭线程池
         */
        private void shutdownExecutor() {
            threadPoolExecutor.shutdown();
            try {
                if (!threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.out.printf("[%s] | 线程池未及时关闭，执行强制关闭%n",
                            Thread.currentThread().getName());
                    threadPoolExecutor.shutdownNow();
                }
                System.out.printf("[%s] | 线程池已关闭%n", Thread.currentThread().getName());
            } catch (InterruptedException e) {
                System.out.printf("[%s] | Error:shutdownExecutor异常%n", Thread.currentThread().getName());
                threadPoolExecutor.shutdownNow();
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