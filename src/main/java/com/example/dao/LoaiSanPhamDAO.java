
package com.example.dao;

import com.example.model.LoaiSanPham;
import com.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiSanPhamDAO {

    // 1. Hiển thị danh sách loại sản phẩm
    public List<LoaiSanPham> getAll() throws SQLException {

        List<LoaiSanPham> list = new ArrayList<>();

        String sql = "SELECT * FROM LoaiSanPham";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                LoaiSanPham loai = new LoaiSanPham();

                loai.setMaLoai(rs.getString("MaLoai"));
                loai.setTenLoai(rs.getString("TenLoai"));
                loai.setMoTa(rs.getString("MoTa"));

                list.add(loai);
            }

        }

        return list;
    }

    // 2. Thêm loại sản phẩm
    public boolean insert(LoaiSanPham loai) {

        String sql = "INSERT INTO LoaiSanPham "
                + "(MaLoai, TenLoai, MoTa) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loai.getMaLoai());
            ps.setString(2, loai.getTenLoai());
            ps.setString(3, loai.getMoTa());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Sửa loại sản phẩm
    public boolean update(LoaiSanPham loai) {

        String sql = "UPDATE LoaiSanPham "
                + "SET TenLoai=?, MoTa=? WHERE MaLoai=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loai.getTenLoai());
            ps.setString(2, loai.getMoTa());
            ps.setString(3, loai.getMaLoai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Xóa loại sản phẩm
    public boolean delete(String maLoai) {

        String sql = "DELETE FROM LoaiSanPham WHERE MaLoai=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maLoai);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
