
package com.example.dao;

import com.example.model.HoaDon;
import com.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HoaDonDAO {

    private static final Logger LOGGER = Logger.getLogger(HoaDonDAO.class.getName());
    private static final String SELECT_ALL_SQL = "SELECT MaHD, NgayLap, MaKH FROM HoaDon";
    private static final String INSERT_SQL = "INSERT INTO HoaDon (MaHD, NgayLap, MaKH) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE HoaDon SET NgayLap = ?, MaKH = ? WHERE MaHD = ?";
    private static final String DELETE_SQL = "DELETE FROM HoaDon WHERE MaHD = ?";

    public List<HoaDon> getAll() throws SQLException {
        List<HoaDon> invoices = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_ALL_SQL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Date invoiceDate = rs.getDate("NgayLap");
                HoaDon invoice = new HoaDon();
                invoice.setMaHD(rs.getString("MaHD"));
                invoice.setNgayLap(invoiceDate == null ? null : invoiceDate.toLocalDate());
                invoice.setMaKH(rs.getString("MaKH"));
                invoices.add(invoice);
            }
        }
        return invoices;
    }

    public boolean insert(HoaDon hd) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setString(1, hd.getMaHD());
            ps.setDate(2, Date.valueOf(hd.getNgayLap()));
            ps.setString(3, hd.getMaKH());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to insert invoice " + hd.getMaHD(), e);
            return false;
        }
    }

    public boolean update(HoaDon hd) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {
            ps.setDate(1, Date.valueOf(hd.getNgayLap()));
            ps.setString(2, hd.getMaKH());
            ps.setString(3, hd.getMaHD());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to update invoice " + hd.getMaHD(), e);
            return false;
        }
    }

    public boolean delete(String maHD) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {
            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;
        }
    }
}
