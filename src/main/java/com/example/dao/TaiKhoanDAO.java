
package com.example.dao;

import com.example.util.DatabaseConnection;
import com.example.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaiKhoanDAO {

    // Kiểm tra tài khoản đăng nhập
    public boolean dangNhap(String tenDangNhap, String matKhau) {

        String sql = "SELECT MatKhauHash FROM TaiKhoan "
                + "WHERE TenDangNhap = ? AND TrangThai = 1";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String matKhauHash = rs.getString("MatKhauHash");

                return PasswordUtil.checkPassword(
                        matKhau, matKhauHash);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Lấy họ tên Admin từ SQL Server
    public String getHoTen(String tenDangNhap) {

        String sql = "SELECT HoTen FROM TaiKhoan "
                + "WHERE TenDangNhap = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("HoTen");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tenDangNhap;
    }

}
