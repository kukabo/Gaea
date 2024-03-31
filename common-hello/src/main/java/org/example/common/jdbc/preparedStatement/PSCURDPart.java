package org.example.common.jdbc.preparedStatement;

import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PSCURDPart {

    @Test
    public void testInsert() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gaea", "root", "root");
        String sql = "insert into t_user (account, password, nickname) values (?, ?, ?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, "test");
        preparedStatement.setObject(2, "test");
        preparedStatement.setObject(3, "二狗子");
        int i = preparedStatement.executeUpdate();
        if (i > 0) {
            System.out.println("insert success.");
        } else {
            System.out.println("insert fail");
        }
        preparedStatement.close();
        connection.close();
    }

    @Test
    public void testQuery() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gaea", "root", "root");
        String sql = "select id, account, password, nickname from t_user;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        //resultSet 封装的是当前结果集行的信息
        ResultSet resultSet = preparedStatement.executeQuery();

        //metaData 封装的是当前结果集列的信息对象
        ResultSetMetaData metaData = preparedStatement.getMetaData();
        //列的个数，可以用来遍历列
        int columnCount = metaData.getColumnCount();

        List<Map<String, Object>> list = new ArrayList<>();

        while (resultSet.next()) {
            //把列封装到 map
            Map<String, Object> map = new HashMap<>();

            for (int i = 1; i <= columnCount; i++) {
                //指定列下角标的列的值
                Object value = resultSet.getObject(i);
                //指定列下角标的列的名称(columnLabel支持别名，select account as ac 。。。)
                String columnLabel = metaData.getColumnLabel(i);

                map.put(columnLabel, value);
            }

            list.add(map);
        }
        System.out.println("list = " + list);
        //关闭资源
        preparedStatement.close();
        connection.close();
    }

    /**
     * 批量插入
     * 1、rewriteBatchedStatements=true
     * 2、必须是values，且不能有分号
     * 3、addBatch
     * 4、executeBatch
     * @throws ClassNotFoundException
     * @throws SQLException
     */

    @Test
    public void testBatchInsert() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gaea?rewriteBatchedStatements=true", "root", "root");
        String sql = "insert into t_user (account, password, nickname) values (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {

            preparedStatement.setObject(1, "account"+i);
            preparedStatement.setObject(2, "password"+i);
            preparedStatement.setObject(3, "nickname"+i);

            preparedStatement.addBatch();//追加 values后面

        }
        long end = System.currentTimeMillis();

        System.out.println(end - start);

        preparedStatement.executeBatch();//一次性执行

        //关闭资源
        preparedStatement.close();
        connection.close();
    }

    //todo，主键回写和主键值获取

}
