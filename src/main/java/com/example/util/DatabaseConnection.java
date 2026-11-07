package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:sqlserver://DESKTOP-PVOS5R2:1433;"
            + "databaseName=QuanLyBanHang;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    private static final String USER = "sa";

    private static final String PASSWORD = "duc2007";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}