package org.example.common.queue;

import java.util.*;
import java.util.concurrent.*;

/**
 * 线程池业务任务
 */
public class QueueUtil {

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

    /**
     * 程序入口
     */
    public static void main(String[] args) {

        List<SlaveRecord> slaveRecordList = new ArrayList<>();
        SlaveRecord slaveRecordA1 = new SlaveRecord("A", "A1");
        SlaveRecord slaveRecordA2 = new SlaveRecord("A", "A2");
        SlaveRecord slaveRecordA3 = new SlaveRecord("A", "A3");
        slaveRecordList.add(slaveRecordA1);
        slaveRecordList.add(slaveRecordA2);
        slaveRecordList.add(slaveRecordA3);

        LinkedBlockingQueue<SlaveRecord> blockingQueue = new LinkedBlockingQueue<>(slaveRecordList);

        try {
            elementQueue(blockingQueue);

            takeQueue(blockingQueue);
            System.out.println(blockingQueue.size());

            slaveRecordA1.subName = "A1-new";
            slaveRecordA2.subName = "A2-new";
            blockingQueue.put(slaveRecordA1);
            blockingQueue.put(slaveRecordA2);
            System.out.println(blockingQueue.size());

            takeQueue(blockingQueue);
            System.out.println(blockingQueue.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void takeQueue(LinkedBlockingQueue<SlaveRecord> blockingQueue) {
        try {
            SlaveRecord take1 = blockingQueue.take();
            System.out.println(take1.subName);
            SlaveRecord take2 = blockingQueue.take();
            System.out.println(take2.subName);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void elementQueue(LinkedBlockingQueue<SlaveRecord> blockingQueue) {
        try {
            SlaveRecord element = blockingQueue.element();
            System.out.println(element.subName);
            element.subName = element.subName + "-element";
            System.out.println(element.subName);
        } catch (NoSuchElementException e) {
            throw new RuntimeException(e);
        }
    }
    /*
    * A1
    * A2
    * 1
    * 3
    * A3
    * */
}