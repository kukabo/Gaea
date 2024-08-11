package org.example.common.jdbc.statement;

import com.mysql.cj.jdbc.Driver;
import java.sql.*;

public class StatementQueryPart {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //1、注册驱动
        /*
        * 8+ 版本是com.mysql.cj.jdbc.Driver
        * 5+ 版本是com.mysql.jdbc.Driver
        *
        * 问题：DriverManager.registerDriver(new Driver()); 会注册 2 次驱动
        *   1、DriverManager.registerDriver会注册一次
        *   2、new Driver()静态代码块也会注册一次
        * 解决：只注册一次驱动，只触发静态代码块。
        * 触发静态代码块：
        *   类加载机制：类加载的时刻，会触发静态代码块
        *             加载【class文件 -> jvm虚拟机的 class 对象】
        *             连接【验证（检查文件内部结构） -> 准备（类变量分配地址及设置默认初始值） -> 解析（符号引用替换直接引用；触发静态代码块）】
        *             初始化【静态属性赋真实值】
        * */
        DriverManager.registerDriver(new Driver());
        Class.forName("com.mysql.cj.jdbc.Driver");//可以读配置，不改代码完成数据库切换
        //2、获取连接
        /*
        * Java程序和数据库mysql建立连接
        * IP 端口 用户名 密码root
        * */
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gaea", "root", "root@mysql");
        //3、创建statement，可以发送sql语句到数据库并且获取返回结果
        /*
        * statement 适合执行静态sql，缺点是容易出现 sql注入攻击
        * 建议用preparedStatement
        * */
        Statement statement = connection.createStatement();
        //一次性加载到内存的条数
        statement.setFetchSize(100);
        //设置ResultSet对象的大小的限制
        statement.setMaxRows(20);

        //4、便写sql语句，发送sql语句，获取结果
        String sql = "select * from t_user";
        ResultSet resultSet = statement.executeQuery(sql);
        //5、进行结果解析
        while (resultSet.next()) {//看下有没有下一行，有就取数据
            int id = resultSet.getInt("id");
            String account = resultSet.getString("account");
            String password = resultSet.getString("password");
            String nickname = resultSet.getString("nickname");
            System.out.println(id + "--" + account + "--" + password + "--" + nickname);
        }
        //6、关闭连接
        resultSet.close();
        statement.close();
        connection.close();
    }

}
