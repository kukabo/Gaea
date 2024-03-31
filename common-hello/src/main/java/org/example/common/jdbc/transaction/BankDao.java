package org.example.common.jdbc.transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/*
 * 1、注册驱动（引入mysql-connector-java）
 * 2、建立连接connection
 * 3、创建发送sql语句的对象statement
 * 4、statement发送sql语句到数据库，获取结果（数据库拿到这个请求后优化/查询）
 * 5、创建解析结果的对象resultset
 * 6、销毁资源connection，statement，resultset
 * */
public class BankDao {

    public void add(String account, int money, Connection connection) throws Exception {
        String sql = "update t_bank set money = money + ? where account = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, money);
        preparedStatement.setString(2, account);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        System.out.println("加钱成功");
    }

    public void sub(String account, int money, Connection connection) throws Exception {
        String sql = "update t_bank set money = money - ? where account = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, money);
        preparedStatement.setString(2, account);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        System.out.println("减钱成功");
    }
}
