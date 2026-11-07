
package com.example.dao;

import com.example.model.SanPham;
import com.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SanPhamDAO {

    private static final Logger LOGGER = Logger.getLogger(SanPhamDAO.class.getName());
    private static final String SELECT_BY_CATEGORY_SQL =
            "SELECT TOP 1 1 FROM SanPham WHERE MaLoai = ?";
    private static final String SELECT_ALL_SQL = "SELECT MaSP, TenSP, DonGia, SoLuongTon, MaLoai FROM SanPham";
    private static final String INSERT_SQL =
            "INSERT INTO SanPham (MaSP, TenSP, DonGia, SoLuongTon, MaLoai) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE SanPham SET TenSP = ?, DonGia = ?, SoLuongTon = ?, MaLoai = ? WHERE MaSP = ?";
    private static final String DELETE_SQL = "DELETE FROM SanPham WHERE MaSP = ?";

    public boolean coSanPhamTheoMaLoai(String maLoai) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_BY_CATEGORY_SQL)) {
            ps.setString(1, maLoai);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<SanPham> getAll() throws SQLException {
        List<SanPham> products = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_ALL_SQL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SanPham product = new SanPham();
                product.setMaSP(rs.getString("MaSP"));
                product.setTenSP(rs.getString("TenSP"));
                product.setDonGia(rs.getBigDecimal("DonGia"));
                product.setSoLuongTon(rs.getInt("SoLuongTon"));
                product.setMaLoai(rs.getString("MaLoai"));
                products.add(product);
            }
        }
        return products;
    }

    public boolean insert(SanPham sp) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setBigDecimal(3, sp.getDonGia());
            ps.setInt(4, sp.getSoLuongTon());
            ps.setString(5, sp.getMaLoai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to insert product " + sp.getMaSP(), e);
            return false;
        }
    }

    public boolean update(SanPham sp) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, sp.getTenSP());
            ps.setBigDecimal(2, sp.getDonGia());
            ps.setInt(3, sp.getSoLuongTon());
            ps.setString(4, sp.getMaLoai());
            ps.setString(5, sp.getMaSP());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to update product " + sp.getMaSP(), e);
            return false;
        }
    }

    public boolean delete(String maSP) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {
            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;
        }
    }
}
