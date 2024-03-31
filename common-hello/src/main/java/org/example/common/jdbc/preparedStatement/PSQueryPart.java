package org.example.common.jdbc.preparedStatement;

import java.sql.*;

public class PSQueryPart {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gaea", "root", "root");
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
