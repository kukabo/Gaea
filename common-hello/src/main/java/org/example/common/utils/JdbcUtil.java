package org.example.common.utils;


import com.alibaba.druid.pool.DruidDataSourceFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/*
* 工具类的方法，推荐写成静态
*
* 实现
*   属性 连接池对象，只实例化一次
*       单例模式
*       静态代码块 static {
*                }
*   方法
*       对外提供连接的方法
*       回收外部传入连接的方法
*
* ThreadLocal
*   把连接放在线程本地变量里，通过getConnection()可以获取到同一个连接，保证 事务都是在同一个连接里的
* */
public class JdbcUtil {

    private static final DataSource dataSource;

    private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    static {
        Properties properties = new Properties();

        InputStream resourceAsStream = JdbcUtil.class.getClassLoader().getResourceAsStream("dataSource.properties");

        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return Connection 从连接池里随机获取一个空闲的连接
     * @throws SQLException 数据库异常
     */
    public static Connection getConnection() throws SQLException {
        //先从 线程的本地变量里获取连接，目的是为了 使用同一个连接
        Connection connection = threadLocal.get();
        if (connection == null) {
            connection = dataSource.getConnection();
            threadLocal.set(connection);
        }
        return connection;
    }

    /**
     * @throws SQLException 数据库异常
     */
    public static void releaseConnection() throws SQLException {
        Connection connection = threadLocal.get();
        if (connection != null) {
            //使用完要清空线程本地变量
            threadLocal.remove();
            //连接设置为自动提交，即默认的事务状态
            connection.setAutoCommit(true);
            connection.close();
        }
    }

}
