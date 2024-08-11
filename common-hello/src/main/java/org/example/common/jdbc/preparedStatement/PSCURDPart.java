package org.example.common.jdbc.preparedStatement;

import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PSCURDPart {

    @Test
    public void testQuery() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gaea", "root", "root@mysql");
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

    @Test
    public void selectTest() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gaea", "root", "root@mysql");
        //3、编写 sql
        String sql = "select * from t_user where account = ?;";
        //4、创建preparedStatement，会对 sql预编译，知道有多少个条件，防止 sql注入
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        //5、传入动态值，执行 sql，获取结果
        preparedStatement.setString(1, "root");
        ResultSet resultSet = preparedStatement.executeQuery();
        //6、进行结果解析
        while (resultSet.next()) {//看下有没有下一行，有就取数据
            int id = resultSet.getInt("id");
            String account = resultSet.getString("account");
            String password = resultSet.getString("password");
            String nickname = resultSet.getString("nickname");
            System.out.println(id + "--" + account + "--" + password + "--" + nickname);
        }
        //6、关闭连接
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

}
