package org.example.common.jdbc.transaction;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;

public class BankService {

    @Test
    public void test() throws Exception {
        transfer("zhangsan", "lisi", 50);
    }

    public void transfer(String addAccount, String subAccount, int money) throws Exception {

        //注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        //建立连接
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gaea", "root", "root");

        BankDao bankDao = new BankDao();

        try {
            //关闭事务的自动提交
            connection.setAutoCommit(false);
            //业务处理
            bankDao.add(addAccount, money, connection);
            System.out.println("--------------------------------");
            bankDao.sub(subAccount, money, connection);
            //提交事务
            connection.commit();
        } catch (Exception e) {
            //回滚事务
            connection.rollback();
            throw e;
        } finally {
            //关闭资源
            connection.close();
        }
    }
}
