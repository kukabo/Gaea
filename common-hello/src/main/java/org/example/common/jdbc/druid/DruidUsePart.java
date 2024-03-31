package org.example.common.jdbc.druid;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

/*
* 连接池：有多个连接，同一个连接只要空闲可以被使用，减少不必要的连接创建；
*       减少创建连接，提升性能
* */
public class DruidUsePart {

    @Test
    public void testDruid() throws Exception {

        //读取外部配置文件
        Properties properties = new Properties();
        //src下的资源文件可以用类加载器提供的方法
        InputStream resourceAsStream = DruidUsePart.class.getClassLoader().getResourceAsStream("dataSource.properties");

        properties. load(resourceAsStream);

        //创建连接池
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        //从连接池里获取连接
        Connection connection = dataSource.getConnection();

        //数据库操作
        PreparedStatement preparedStatement = connection.prepareStatement("sql");


        //关闭连接池
        preparedStatement.close();
        connection.close();


    }

}
