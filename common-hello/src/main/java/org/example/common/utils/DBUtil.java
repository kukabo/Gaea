package org.example.common.utils;

import org.example.common.manager.impl.dataSource.DataSourceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBUtil {

    public static Connection getConnection() throws SQLException {
        return DataSourceManager.getConnection();
    }

    public static PreparedStatement getPreparedStatement(String sql, Boolean flag) throws SQLException {
        Connection connection = getConnection();
        connection.setAutoCommit(flag);
        return connection.prepareStatement(sql);
    }

    public static void commit() throws SQLException {
        getConnection().commit();
    }

    public static void rollback() throws SQLException {
        getConnection().rollback();
    }

    public static void closeConnection() throws SQLException {
        DataSourceManager.releaseConnection();
    }
}
