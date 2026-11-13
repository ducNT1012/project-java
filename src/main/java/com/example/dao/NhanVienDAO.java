package com.example.dao;

import com.example.model.NhanVien;
import com.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    private static final String SELECT_COLUMNS = "SELECT MaNV, HoTen, GioiTinh, NgaySinh, "
            + "SDT, DiaChi, Email, ChucVu, NgayVaoLam, Luong, TrangThai FROM NhanVien";

    public List<NhanVien> getAll() throws SQLException {
        List<NhanVien> employees = new ArrayList<NhanVien>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_COLUMNS);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                employees.add(map(resultSet));
            }
        }
        return employees;
    }

    public boolean insert(NhanVien employee) throws SQLException {
        String sql = "INSERT INTO NhanVien "
                + "(MaNV, HoTen, GioiTinh, NgaySinh, SDT, DiaChi, Email, ChucVu, NgayVaoLam, Luong, TrangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, employee);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean update(NhanVien employee) throws SQLException {
        String sql = "UPDATE NhanVien SET HoTen = ?, GioiTinh = ?, NgaySinh = ?, SDT = ?, "
                + "DiaChi = ?, Email = ?, ChucVu = ?, NgayVaoLam = ?, Luong = ?, TrangThai = ? "
                + "WHERE MaNV = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getHoTen());
            statement.setString(2, employee.getGioiTinh());
            setDate(statement, 3, employee.getNgaySinh());
            statement.setString(4, employee.getSdt());
            statement.setString(5, employee.getDiaChi());
            statement.setString(6, employee.getEmail());
            statement.setString(7, employee.getChucVu());
            setDate(statement, 8, employee.getNgayVaoLam());
            if (employee.getLuong() == null) {
                statement.setNull(9, Types.DECIMAL);
            } else {
                statement.setBigDecimal(9, employee.getLuong());
            }
            statement.setBoolean(10, employee.isTrangThai());
            statement.setString(11, employee.getMaNV());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(String maNV) throws SQLException {
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maNV);
            return statement.executeUpdate() > 0;
        }
    }

    private NhanVien map(ResultSet resultSet) throws SQLException {
        NhanVien employee = new NhanVien();
        employee.setMaNV(resultSet.getString("MaNV"));
        employee.setHoTen(resultSet.getString("HoTen"));
        employee.setGioiTinh(resultSet.getString("GioiTinh"));
        Date ngaySinh = resultSet.getDate("NgaySinh");
        employee.setNgaySinh(ngaySinh == null ? null : ngaySinh.toLocalDate());
        employee.setSdt(resultSet.getString("SDT"));
        employee.setDiaChi(resultSet.getString("DiaChi"));
        employee.setEmail(resultSet.getString("Email"));
        employee.setChucVu(resultSet.getString("ChucVu"));
        Date ngayVaoLam = resultSet.getDate("NgayVaoLam");
        employee.setNgayVaoLam(ngayVaoLam == null ? null : ngayVaoLam.toLocalDate());
        employee.setLuong(resultSet.getBigDecimal("Luong"));
        employee.setTrangThai(resultSet.getBoolean("TrangThai"));
        return employee;
    }

    private void bind(PreparedStatement statement, NhanVien employee) throws SQLException {
        statement.setString(1, employee.getMaNV());
        statement.setString(2, employee.getHoTen());
        statement.setString(3, employee.getGioiTinh());
        setDate(statement, 4, employee.getNgaySinh());
        statement.setString(5, employee.getSdt());
        statement.setString(6, employee.getDiaChi());
        statement.setString(7, employee.getEmail());
        statement.setString(8, employee.getChucVu());
        setDate(statement, 9, employee.getNgayVaoLam());
        if (employee.getLuong() == null) {
            statement.setNull(10, Types.DECIMAL);
        } else {
            statement.setBigDecimal(10, employee.getLuong());
        }
        statement.setBoolean(11, employee.isTrangThai());
    }

    private void setDate(PreparedStatement statement, int index, java.time.LocalDate value)
            throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.DATE);
        } else {
            statement.setDate(index, Date.valueOf(value));
        }
    }
}
