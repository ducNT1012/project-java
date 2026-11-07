
package com.example.dao;

import com.example.model.ChiTietHoaDon;
import com.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {

    private static final int INSUFFICIENT_STOCK_ERROR_CODE = 51000;

    public List<ChiTietHoaDon> getByMaHD(String maHD) throws SQLException {
        List<ChiTietHoaDon> details = new ArrayList<>();
        String sql = "SELECT MaHD, MaSP, SoLuong, DonGia FROM ChiTietHoaDon WHERE MaHD = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon detail = new ChiTietHoaDon();
                    detail.setMaHD(rs.getString("MaHD"));
                    detail.setMaSP(rs.getString("MaSP"));
                    detail.setSoLuong(rs.getInt("SoLuong"));
                    detail.setDonGia(rs.getBigDecimal("DonGia"));
                    details.add(detail);
                }
            }
        }
        return details;
    }

    public boolean insert(ChiTietHoaDon ct) throws SQLException {
        validateDetail(ct);
        String sql = "INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                giamTonKho(connection, ct.getMaSP(), ct.getSoLuong());
                ps.setString(1, ct.getMaHD());
                ps.setString(2, ct.getMaSP());
                ps.setInt(3, ct.getSoLuong());
                ps.setBigDecimal(4, ct.getDonGia());
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }
                connection.commit();
                return true;
            } catch (SQLException ex) {
                rollback(connection, ex);
                throw ex;
            }
        }
    }

    public boolean update(ChiTietHoaDon ct) throws SQLException {
        validateDetail(ct);
        String sql = "UPDATE ChiTietHoaDon SET SoLuong = ?, DonGia = ? WHERE MaHD = ? AND MaSP = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement lockProduct = connection.prepareStatement(
                        "SELECT SoLuongTon FROM SanPham WITH (UPDLOCK, HOLDLOCK) WHERE MaSP = ?");
                PreparedStatement select = connection.prepareStatement(
                        "SELECT SoLuong FROM ChiTietHoaDon WITH (UPDLOCK, HOLDLOCK) WHERE MaHD = ? AND MaSP = ?");
                PreparedStatement update = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                lockProduct.setString(1, ct.getMaSP());
                try (ResultSet product = lockProduct.executeQuery()) {
                    if (!product.next()) {
                        throw new SQLException("Không tìm thấy sản phẩm trong kho.");
                    }
                }

                select.setString(1, ct.getMaHD());
                select.setString(2, ct.getMaSP());
                int oldQuantity;
                try (ResultSet rs = select.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }
                    oldQuantity = rs.getInt("SoLuong");
                }

                int quantityDelta = ct.getSoLuong() - oldQuantity;
                if (quantityDelta > 0) {
                    giamTonKho(connection, ct.getMaSP(), quantityDelta);
                } else if (quantityDelta < 0) {
                    tangTonKho(connection, ct.getMaSP(), -quantityDelta);
                }

                update.setInt(1, ct.getSoLuong());
                update.setBigDecimal(2, ct.getDonGia());
                update.setString(3, ct.getMaHD());
                update.setString(4, ct.getMaSP());
                int affectedRows = update.executeUpdate();
                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }
                connection.commit();
                return true;
            } catch (SQLException ex) {
                rollback(connection, ex);
                throw ex;
            }
        }
    }

    public boolean delete(String maHD, String maSP) throws SQLException {
        String sql = "DELETE FROM ChiTietHoaDon WHERE MaHD = ? AND MaSP = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement lockProduct = connection.prepareStatement(
                        "SELECT SoLuongTon FROM SanPham WITH (UPDLOCK, HOLDLOCK) WHERE MaSP = ?");
                PreparedStatement select = connection.prepareStatement(
                        "SELECT SoLuong FROM ChiTietHoaDon WITH (UPDLOCK, HOLDLOCK) WHERE MaHD = ? AND MaSP = ?");
                PreparedStatement delete = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                lockProduct.setString(1, maSP);
                try (ResultSet product = lockProduct.executeQuery()) {
                    if (!product.next()) {
                        throw new SQLException("Không tìm thấy sản phẩm để hoàn lại tồn kho.");
                    }
                }

                select.setString(1, maHD);
                select.setString(2, maSP);
                int quantity;
                try (ResultSet rs = select.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }
                    quantity = rs.getInt("SoLuong");
                }

                delete.setString(1, maHD);
                delete.setString(2, maSP);
                int affectedRows = delete.executeUpdate();
                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }
                tangTonKho(connection, maSP, quantity);
                connection.commit();
                return true;
            } catch (SQLException ex) {
                rollback(connection, ex);
                throw ex;
            }
        }
    }

    private void giamTonKho(Connection connection, String maSP, int quantity) throws SQLException {
        String sql = "UPDATE SanPham SET SoLuongTon = SoLuongTon - ? "
                + "WHERE MaSP = ? AND SoLuongTon >= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setString(2, maSP);
            ps.setInt(3, quantity);
            if (ps.executeUpdate() == 0) {
                throw new SQLException(
                        "Sản phẩm không tồn tại hoặc số lượng tồn kho không đủ.", "45000",
                        INSUFFICIENT_STOCK_ERROR_CODE);
            }
        }
    }

    private void tangTonKho(Connection connection, String maSP, int quantity) throws SQLException {
        String sql = "UPDATE SanPham SET SoLuongTon = SoLuongTon + ? WHERE MaSP = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setString(2, maSP);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Không tìm thấy sản phẩm để hoàn lại tồn kho.");
            }
        }
    }

    private void validateDetail(ChiTietHoaDon detail) throws SQLException {
        if (detail == null || detail.getMaHD() == null || detail.getMaSP() == null
                || detail.getSoLuong() <= 0 || detail.getDonGia() == null
                || detail.getDonGia().signum() < 0) {
            throw new SQLException("Thông tin chi tiết hóa đơn không hợp lệ.");
        }
    }

    private void rollback(Connection connection, SQLException cause) {
        try {
            connection.rollback();
        } catch (SQLException rollbackError) {
            cause.addSuppressed(rollbackError);
        }
    }
}
