
package com.example.dao;

import com.example.model.KhachHang;
import com.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    // 1. Hiển thị danh sách khách hàng
    public List<KhachHang> getAll() throws SQLException {

        List<KhachHang> list = new ArrayList<>();

        String sql = "SELECT * FROM KhachHang";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                KhachHang kh = new KhachHang();

                kh.setMaKH(rs.getString("MaKH"));
                kh.setTenKH(rs.getString("TenKH"));
                kh.setSdt(rs.getString("SDT"));
                kh.setDiaChi(rs.getString("DiaChi"));

                list.add(kh);
            }

        }

        return list;
    }

    // 2. Thêm khách hàng
    public boolean insert(KhachHang kh) {

        String sql = "INSERT INTO KhachHang "
                + "(MaKH, TenKH, SDT, DiaChi) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getTenKH());
            ps.setString(3, kh.getSdt());
            ps.setString(4, kh.getDiaChi());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Sửa khách hàng
    public boolean update(KhachHang kh) {

        String sql = "UPDATE KhachHang "
                + "SET TenKH=?, SDT=?, DiaChi=? "
                + "WHERE MaKH=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kh.getTenKH());
            ps.setString(2, kh.getSdt());
            ps.setString(3, kh.getDiaChi());
            ps.setString(4, kh.getMaKH());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Xóa khách hàng
    public boolean delete(String maKH) {

        String sql = "DELETE FROM KhachHang WHERE MaKH=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKH);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
