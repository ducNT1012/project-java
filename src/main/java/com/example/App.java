package com.example;

import com.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) {

        System.out.println("==============================");
        System.out.println("      QUAN LY BAN HANG");
        System.out.println("==============================");

        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Ket noi SQL Server thanh cong!");
        } catch (SQLException e) {
            System.err.println("Ket noi SQL Server that bai: " + e.getMessage());
        }
    }
}