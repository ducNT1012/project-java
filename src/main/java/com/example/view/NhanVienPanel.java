package com.example.view;

import com.example.dao.NhanVienDAO;
import com.example.model.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class NhanVienPanel extends JPanel {

    private static final Color GREEN = new Color(20, 139, 85);
    private static final Color BLUE = new Color(48, 112, 190);
    private static final Color TEXT = new Color(45, 55, 72);
    private static final Color MUTED = new Color(117, 128, 145);
    private static final Color BORDER = new Color(229, 233, 239);
    private static final Color BACKGROUND = new Color(246, 248, 251);
    private static final int ACTION_COLUMN = 12;

    private final JTextField txtTimKiem = new JTextField();
    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final NhanVienDAO dao = new NhanVienDAO();
    private final JButton btnXoaDaChon;

    public NhanVienPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        JLabel title = new JLabel("Quản lý nhân viên");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Quản lý hồ sơ, chức vụ, lương và trạng thái làm việc");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);
        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.add(title);
        titleText.add(Box.createVerticalStrut(5));
        titleText.add(subtitle);

        JButton btnThem = taoNut("+  Thêm nhân viên", GREEN, Color.WHITE);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(btnThem);
        JPanel heading = new JPanel(new BorderLayout(12, 0));
        heading.setOpaque(false);
        heading.add(titleText, BorderLayout.WEST);
        heading.add(actions, BorderLayout.EAST);

        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimKiem.setToolTipText("Tìm theo mã, họ tên, số điện thoại, email hoặc chức vụ");
        txtTimKiem.setPreferredSize(new Dimension(330, 36));
        JButton btnTimKiem = new JButton(TableActionIcons.search());
        btnTimKiem.setToolTipText("Tìm kiếm nhân viên");
        btnTimKiem.getAccessibleContext().setAccessibleName("Tìm kiếm nhân viên");
        btnTimKiem.setPreferredSize(new Dimension(32, 30));
        btnTimKiem.setBackground(Color.WHITE);
        btnTimKiem.setFocusPainted(false);
        btnTimKiem.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel searchBox = new JPanel(new BorderLayout(7, 0));
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(0, 10, 0, 8)));
        searchBox.add(btnTimKiem, BorderLayout.WEST);
        searchBox.add(txtTimKiem, BorderLayout.CENTER);

        btnXoaDaChon = taoNut("Xóa đã chọn", new Color(205, 72, 72), Color.WHITE);
        btnXoaDaChon.setEnabled(false);
        JButton btnLamMoi = taoNut("Làm mới", Color.WHITE, TEXT);
        btnLamMoi.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 8));
        tableActions.setBackground(Color.WHITE);
        tableActions.add(btnXoaDaChon);
        tableActions.add(btnLamMoi);
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 8));
        searchPanel.add(searchBox, BorderLayout.WEST);
        searchPanel.add(tableActions, BorderLayout.EAST);

        model = new DefaultTableModel(new Object[] {
                "✓", "MÃ NV", "HỌ VÀ TÊN", "GIỚI TÍNH", "NGÀY SINH", "SỐ ĐIỆN THOẠI",
                "ĐỊA CHỈ", "EMAIL", "CHỨC VỤ", "NGÀY VÀO LÀM", "LƯƠNG", "TRẠNG THÁI", "THAO TÁC"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == ACTION_COLUMN;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
        };
        table = new JTable(model);
        sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);
        styleTable(table);
        sorter.setSortable(0, false);
        int[] widths = { 42, 75, 160, 80, 95, 120, 180, 160, 110, 110, 110, 105, 86 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        table.getColumnModel().getColumn(0).setMinWidth(42);
        table.getColumnModel().getColumn(0).setMaxWidth(48);
        table.getColumnModel().getColumn(0).setHeaderRenderer(new SelectAllHeaderRenderer());
        table.getColumnModel().getColumn(ACTION_COLUMN).setMinWidth(86);
        table.getColumnModel().getColumn(ACTION_COLUMN).setMaxWidth(86);
        table.getColumnModel().getColumn(ACTION_COLUMN).setCellRenderer(new ActionCell());
        table.getColumnModel().getColumn(ACTION_COLUMN).setCellEditor(new ActionCellEditor(
                new RowActionHandler() {
                    @Override
                    public void edit(int row) {
                        moHopThoaiNhanVien(layNhanVienTaiModelRow(row));
                    }

                    @Override
                    public void delete(int row) {
                        xoaNhanVienTaiModelRow(row);
                    }
                }));
        table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (table.getColumnModel().getColumnIndexAtX(e.getX()) == 0) {
                    chonTatCaHienThi();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        JLabel listTitle = new JLabel("Danh sách nhân viên");
        listTitle.setFont(new Font("Arial", Font.BOLD, 15));
        listTitle.setForeground(TEXT);
        listTitle.setBorder(BorderFactory.createEmptyBorder(14, 14, 5, 14));
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(Color.WHITE);
        tableHeader.add(listTitle, BorderLayout.NORTH);
        tableHeader.add(searchPanel, BorderLayout.CENTER);
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createLineBorder(BORDER));
        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(heading, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
        loadData();
        model.addTableModelListener(e -> capNhatTrangThaiXoa());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    hienThiThongTin(layNhanVienDangChon());
                }
            }
        });
        btnThem.addActionListener(e -> moHopThoaiNhanVien(null));
        btnXoaDaChon.addActionListener(e -> xoaNhanVienDaChon());
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            loadData();
            table.clearSelection();
        });
        txtTimKiem.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                timKiem();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                timKiem();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                timKiem();
            }
        });
        btnTimKiem.addActionListener(e -> timKiem());
        txtTimKiem.addActionListener(e -> timKiem());
    }

    private static void styleTable(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(43);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(228, 240, 250));
        table.setSelectionForeground(TEXT);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(239, 242, 246));
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.getTableHeader().setForeground(new Color(94, 107, 124));
        table.getTableHeader().setBackground(new Color(248, 249, 251));
        table.getTableHeader().setPreferredSize(new Dimension(0, 39));
        table.getTableHeader().setReorderingAllowed(false);
    }

    private void moHopThoaiNhanVien(NhanVien existing) {
        final boolean isNew = existing == null;
        final JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                isNew ? "Thêm nhân viên" : "Sửa nhân viên",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextField ma = taoInput();
        JTextField hoTen = taoInput();
        JComboBox<String> gioiTinh = new JComboBox<String>(new String[] { "", "Nam", "Nữ", "Khác" });
        styleCombo(gioiTinh);
        DateInput ngaySinh = new DateInput();
        JTextField sdt = taoInput();
        JTextField diaChi = taoInput();
        JTextField email = taoInput();
        JTextField chucVu = taoInput();
        DateInput ngayVaoLam = new DateInput();
        JTextField luong = taoInput();
        JCheckBox trangThai = new JCheckBox("Đang làm việc", true);
        trangThai.setOpaque(false);
        trangThai.setFont(new Font("Arial", Font.PLAIN, 13));
        if (!isNew) {
            ma.setText(existing.getMaNV());
            ma.setEditable(false);
            hoTen.setText(existing.getHoTen());
            gioiTinh.setSelectedItem(existing.getGioiTinh() == null ? "" : existing.getGioiTinh());
            ngaySinh.setText(dateText(existing.getNgaySinh()));
            sdt.setText(existing.getSdt());
            diaChi.setText(existing.getDiaChi());
            email.setText(existing.getEmail());
            chucVu.setText(existing.getChucVu());
            ngayVaoLam.setText(dateText(existing.getNgayVaoLam()));
            luong.setText(existing.getLuong() == null ? "" : existing.getLuong().toPlainString());
            trangThai.setSelected(existing.isTrangThai());
        }
        ngaySinh.setToolTipText("Định dạng yyyy-MM-dd, có thể để trống");
        ngayVaoLam.setToolTipText("Định dạng yyyy-MM-dd, có thể để trống");

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(Color.WHITE);
        fields.setBorder(BorderFactory.createEmptyBorder(18, 20, 12, 20));
        themTruong(fields, "Mã nhân viên *", ma, 0);
        themTruong(fields, "Họ và tên *", hoTen, 1);
        themTruong(fields, "Giới tính", gioiTinh, 2);
        themTruong(fields, "Ngày sinh", ngaySinh, 3);
        themTruong(fields, "Số điện thoại", sdt, 4);
        themTruong(fields, "Địa chỉ", diaChi, 5);
        themTruong(fields, "Email", email, 6);
        themTruong(fields, "Chức vụ", chucVu, 7);
        themTruong(fields, "Ngày vào làm", ngayVaoLam, 8);
        themTruong(fields, "Lương", luong, 9);
        themTruong(fields, "Trạng thái", trangThai, 10);

        JButton save = taoNut(isNew ? "Thêm nhân viên" : "Lưu thay đổi", GREEN, Color.WHITE);
        JButton cancel = taoNut("Hủy", Color.WHITE, TEXT);
        cancel.setBorder(BorderFactory.createLineBorder(BORDER));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        buttons.setBackground(new Color(248, 249, 251));
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        buttons.add(cancel);
        buttons.add(save);
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.add(fields, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(content);
        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            try {
                NhanVien employee = docNhanVien(
                        ma, hoTen, gioiTinh, ngaySinh, sdt, diaChi, email,
                        chucVu, ngayVaoLam, luong, trangThai);
                boolean success = isNew ? dao.insert(employee) : dao.update(employee);
                if (success) {
                    dialog.dispose();
                    loadData();
                    JOptionPane.showMessageDialog(this,
                            isNew ? "Thêm nhân viên thành công." : "Cập nhật nhân viên thành công.",
                            "Nhân viên", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    baoLoi(dialog, "Không tìm thấy nhân viên cần cập nhật.");
                }
            } catch (IllegalArgumentException ex) {
                baoLoi(dialog, ex.getMessage());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, thongBaoLoiLuu(ex),
                        "Không thể lưu nhân viên", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.pack();
        dialog.setMinimumSize(new Dimension(530, 560));
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private NhanVien docNhanVien(JTextField ma, JTextField hoTen, JComboBox<String> gioiTinh,
            DateInput ngaySinh, JTextField sdt, JTextField diaChi, JTextField email,
            JTextField chucVu, DateInput ngayVaoLam, JTextField luong, JCheckBox trangThai) {
        String id = ma.getText().trim();
        String name = hoTen.getText().trim();
        String phone = sdt.getText().trim();
        String address = diaChi.getText().trim();
        String emailValue = email.getText().trim();
        String position = chucVu.getText().trim();
        if (id.isEmpty() || name.isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên và họ tên là bắt buộc.");
        }
        checkLength("Mã nhân viên", id, 10);
        checkLength("Họ và tên", name, 100);
        checkLength("Giới tính", (String) gioiTinh.getSelectedItem(), 10);
        checkLength("Số điện thoại", phone, 15);
        checkLength("Địa chỉ", address, 200);
        checkLength("Email", emailValue, 100);
        checkLength("Chức vụ", position, 50);
        if (!phone.isEmpty() && !phone.matches("[0-9+().\\- ]{7,15}")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ.");
        }
        if (!emailValue.isEmpty() && !emailValue.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }

        NhanVien employee = new NhanVien();
        employee.setMaNV(id);
        employee.setHoTen(name);
        employee.setGioiTinh(emptyToNull((String) gioiTinh.getSelectedItem()));
        employee.setNgaySinh(parseDate(ngaySinh.getText(), "Ngày sinh"));
        employee.setSdt(emptyToNull(phone));
        employee.setDiaChi(emptyToNull(address));
        employee.setEmail(emptyToNull(emailValue));
        employee.setChucVu(emptyToNull(position));
        employee.setNgayVaoLam(parseDate(ngayVaoLam.getText(), "Ngày vào làm"));
        employee.setLuong(parseSalary(luong.getText()));
        employee.setTrangThai(trangThai.isSelected());
        return employee;
    }

    private LocalDate parseDate(String value, String label) {
        String text = value.trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(label + " phải theo định dạng yyyy-MM-dd.");
        }
    }

    private BigDecimal parseSalary(String value) {
        String text = value.trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            BigDecimal salary = new BigDecimal(text);
            if (salary.signum() < 0 || salary.stripTrailingZeros().scale() > 0
                    || salary.precision() > 18) {
                throw new IllegalArgumentException("Lương phải là số nguyên không âm, tối đa 18 chữ số.");
            }
            return salary;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Lương phải là số hợp lệ.");
        }
    }

    private void checkLength(String label, String value, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(label + " không được vượt quá " + maxLength + " ký tự.");
        }
    }

    private void hienThiThongTin(NhanVien employee) {
        if (employee == null) {
            return;
        }
        String details = "Mã nhân viên: " + employee.getMaNV()
                + "\nHọ và tên: " + employee.getHoTen()
                + "\nGiới tính: " + display(employee.getGioiTinh())
                + "\nNgày sinh: " + display(dateText(employee.getNgaySinh()))
                + "\nSố điện thoại: " + display(employee.getSdt())
                + "\nĐịa chỉ: " + display(employee.getDiaChi())
                + "\nEmail: " + display(employee.getEmail())
                + "\nChức vụ: " + display(employee.getChucVu())
                + "\nNgày vào làm: " + display(dateText(employee.getNgayVaoLam()))
                + "\nLương: " + display(employee.getLuong() == null ? null : employee.getLuong().toPlainString())
                + "\nTrạng thái: " + (employee.isTrangThai() ? "Đang làm việc" : "Đã nghỉ");
        JOptionPane.showMessageDialog(this, details, "Thông tin nhân viên", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xoaNhanVienTaiModelRow(int row) {
        NhanVien employee = layNhanVienTaiModelRow(row);
        if (employee == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa nhân viên \"" + employee.getHoTen() + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (dao.delete(employee.getMaNV())) {
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên để xóa.",
                            "Xóa thất bại", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                showDatabaseError(ex);
            }
        }
    }

    private void xoaNhanVienDaChon() {
        List<NhanVien> employees = new ArrayList<NhanVien>();
        for (int row = 0; row < model.getRowCount(); row++) {
            if (Boolean.TRUE.equals(model.getValueAt(row, 0))) {
                employees.add(layNhanVienTaiModelRow(row));
            }
        }
        if (employees.isEmpty()) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa " + employees.size() + " nhân viên đã chọn?",
                "Xác nhận xóa nhiều", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        List<String> failed = new ArrayList<String>();
        for (NhanVien employee : employees) {
            try {
                if (!dao.delete(employee.getMaNV())) {
                    failed.add(employee.getMaNV() + ": không tìm thấy dữ liệu");
                }
            } catch (SQLException ex) {
                failed.add(employee.getMaNV() + ": " + ex.getMessage());
            }
        }
        loadData();
        if (!failed.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không thể xóa một số nhân viên:\n" + join(failed),
                    "Xóa chưa hoàn tất", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            for (NhanVien employee : dao.getAll()) {
                model.addRow(new Object[] {
                        Boolean.FALSE, employee.getMaNV(), employee.getHoTen(),
                        emptyToEmpty(employee.getGioiTinh()), dateText(employee.getNgaySinh()),
                        emptyToEmpty(employee.getSdt()), emptyToEmpty(employee.getDiaChi()),
                        emptyToEmpty(employee.getEmail()), emptyToEmpty(employee.getChucVu()),
                        dateText(employee.getNgayVaoLam()),
                        employee.getLuong() == null ? "" : employee.getLuong().toPlainString(),
                        employee.isTrangThai() ? "Đang làm việc" : "Đã nghỉ", ""
                });
            }
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        sorter.setRowFilter(keyword.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(keyword)));
    }

    private void chonTatCaHienThi() {
        boolean allSelected = table.getRowCount() > 0;
        for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            if (!Boolean.TRUE.equals(model.getValueAt(modelRow, 0))) {
                allSelected = false;
                break;
            }
        }
        boolean select = !allSelected;
        for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            model.setValueAt(Boolean.valueOf(select), modelRow, 0);
        }
    }

    private void capNhatTrangThaiXoa() {
        for (int row = 0; row < model.getRowCount(); row++) {
            if (Boolean.TRUE.equals(model.getValueAt(row, 0))) {
                btnXoaDaChon.setEnabled(true);
                return;
            }
        }
        btnXoaDaChon.setEnabled(false);
    }

    private NhanVien layNhanVienTaiModelRow(int row) {
        if (row < 0 || row >= model.getRowCount()) {
            return null;
        }
        NhanVien employee = new NhanVien();
        employee.setMaNV(valueAt(row, 1));
        employee.setHoTen(valueAt(row, 2));
        employee.setGioiTinh(emptyToNull(valueAt(row, 3)));
        employee.setNgaySinh(parseStoredDate(valueAt(row, 4)));
        employee.setSdt(emptyToNull(valueAt(row, 5)));
        employee.setDiaChi(emptyToNull(valueAt(row, 6)));
        employee.setEmail(emptyToNull(valueAt(row, 7)));
        employee.setChucVu(emptyToNull(valueAt(row, 8)));
        employee.setNgayVaoLam(parseStoredDate(valueAt(row, 9)));
        employee.setLuong(valueAt(row, 10).isEmpty() ? null : new BigDecimal(valueAt(row, 10)));
        employee.setTrangThai("Đang làm việc".equals(valueAt(row, 11)));
        return employee;
    }

    private NhanVien layNhanVienDangChon() {
        int selected = table.getSelectedRow();
        return selected < 0 ? null : layNhanVienTaiModelRow(table.convertRowIndexToModel(selected));
    }

    private LocalDate parseStoredDate(String value) {
        return value.isEmpty() ? null : LocalDate.parse(value);
    }

    private static String dateText(LocalDate date) {
        return date == null ? "" : date.toString();
    }

    private static String emptyToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private static String emptyToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String display(String value) {
        return value == null || value.trim().isEmpty() ? "—" : value;
    }

    private static String join(List<String> values) {
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            if (result.length() > 0) {
                result.append('\n');
            }
            result.append(value);
        }
        return result.toString();
    }

    private String valueAt(int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private void showDatabaseError(SQLException exception) {
        JOptionPane.showMessageDialog(this,
                "Không thể kết nối hoặc đọc dữ liệu nhân viên.\nChi tiết: " + exception.getMessage(),
                "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
    }

    private String thongBaoLoiLuu(SQLException exception) {
        for (SQLException current = exception; current != null; current = current.getNextException()) {
            if (current.getErrorCode() == 2627 || current.getErrorCode() == 2601) {
                return "Mã nhân viên đã tồn tại. Vui lòng nhập mã khác.";
            }
        }
        return "Không thể lưu nhân viên.\nChi tiết: " + exception.getMessage();
    }

    private static JTextField taoInput() {
        JTextField field = new JTextField(22);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private static void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    private static void themTruong(JPanel panel, String text, Component input, int row) {
        GridBagConstraints label = new GridBagConstraints();
        label.gridx = 0;
        label.gridy = row;
        label.anchor = GridBagConstraints.WEST;
        label.insets = new Insets(5, 0, 5, 14);
        JLabel caption = new JLabel(text);
        caption.setFont(new Font("Arial", Font.BOLD, 12));
        caption.setForeground(MUTED);
        panel.add(caption, label);

        GridBagConstraints field = new GridBagConstraints();
        field.gridx = 1;
        field.gridy = row;
        field.weightx = 1;
        field.fill = GridBagConstraints.HORIZONTAL;
        field.insets = new Insets(5, 0, 5, 0);
        panel.add(input, field);
    }

    private static JButton taoNut(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        return button;
    }

    private void baoLoi(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thông tin chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
    }

    private void moLich(DateInput input) {
        LocalDate initialDate;
        try {
            initialDate = input.getText().trim().isEmpty()
                    ? LocalDate.now()
                    : LocalDate.parse(input.getText().trim());
        } catch (DateTimeParseException ex) {
            initialDate = LocalDate.now();
        }

        JDialog calendar = new JDialog(
                SwingUtilities.getWindowAncestor(this), "Chọn ngày", Dialog.ModalityType.APPLICATION_MODAL);
        calendar.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel monthNavigation = new JPanel(new BorderLayout(8, 0));
        monthNavigation.setOpaque(false);
        JButton previous = taoNut("‹", Color.WHITE, TEXT);
        JButton next = taoNut("›", Color.WHITE, TEXT);
        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monthLabel.setForeground(TEXT);
        monthNavigation.add(previous, BorderLayout.WEST);
        monthNavigation.add(monthLabel, BorderLayout.CENTER);
        monthNavigation.add(next, BorderLayout.EAST);

        JPanel weekdays = new JPanel(new GridLayout(1, 7, 3, 3));
        weekdays.setOpaque(false);
        for (DayOfWeek day : DayOfWeek.values()) {
            JLabel label = new JLabel(day.getDisplayName(TextStyle.SHORT, new Locale("vi")), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 11));
            label.setForeground(MUTED);
            weekdays.add(label);
        }
        JPanel days = new JPanel(new GridLayout(0, 7, 3, 3));
        days.setOpaque(false);

        YearMonth[] displayedMonth = { YearMonth.from(initialDate) };
        Runnable renderCalendar = () -> {
            monthLabel.setText(displayedMonth[0].getMonth()
                    .getDisplayName(TextStyle.FULL, new Locale("vi")) + " " + displayedMonth[0].getYear());
            days.removeAll();
            int leadingBlanks = displayedMonth[0].atDay(1).getDayOfWeek().getValue() - 1;
            for (int i = 0; i < leadingBlanks; i++) {
                days.add(new JLabel());
            }
            for (int day = 1; day <= displayedMonth[0].lengthOfMonth(); day++) {
                LocalDate date = displayedMonth[0].atDay(day);
                JButton dayButton = taoNut(Integer.toString(day), Color.WHITE, TEXT);
                dayButton.setBorder(BorderFactory.createLineBorder(BORDER));
                if (date.equals(LocalDate.now())) {
                    dayButton.setForeground(BLUE);
                }
                dayButton.addActionListener(e -> {
                    input.setText(date.toString());
                    calendar.dispose();
                });
                days.add(dayButton);
            }
            days.revalidate();
            days.repaint();
        };
        previous.addActionListener(e -> {
            displayedMonth[0] = displayedMonth[0].minusMonths(1);
            renderCalendar.run();
        });
        next.addActionListener(e -> {
            displayedMonth[0] = displayedMonth[0].plusMonths(1);
            renderCalendar.run();
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        JButton clear = taoNut("Xóa ngày", Color.WHITE, TEXT);
        clear.setBorder(BorderFactory.createLineBorder(BORDER));
        JButton today = taoNut("Hôm nay", BLUE, Color.WHITE);
        footer.add(clear);
        footer.add(today);
        clear.addActionListener(e -> {
            input.setText("");
            calendar.dispose();
        });
        today.addActionListener(e -> {
            input.setText(LocalDate.now().toString());
            calendar.dispose();
        });

        JPanel calendarBody = new JPanel(new BorderLayout(0, 8));
        calendarBody.setOpaque(false);
        calendarBody.add(weekdays, BorderLayout.NORTH);
        calendarBody.add(days, BorderLayout.CENTER);
        content.add(monthNavigation, BorderLayout.NORTH);
        content.add(calendarBody, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);
        calendar.setContentPane(content);
        renderCalendar.run();
        calendar.pack();
        calendar.setResizable(false);
        calendar.setLocationRelativeTo(input);
        calendar.setVisible(true);
    }

    private final class DateInput extends JPanel {
        private final JTextField textField = taoInput();

        private DateInput() {
            super(new BorderLayout());
            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(BORDER));

            textField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 4));
            JButton calendarButton = new JButton(new CalendarIcon());
            calendarButton.setPreferredSize(new Dimension(38, 34));
            calendarButton.setBackground(Color.WHITE);
            calendarButton.setFocusPainted(false);
            calendarButton.setBorderPainted(false);
            calendarButton.setContentAreaFilled(false);
            calendarButton.setToolTipText("Chọn ngày trên lịch");
            calendarButton.getAccessibleContext().setAccessibleName("Mở lịch chọn ngày");
            add(textField, BorderLayout.CENTER);
            add(calendarButton, BorderLayout.EAST);
            calendarButton.addActionListener(e -> moLich(this));
        }

        private String getText() {
            return textField.getText();
        }

        private void setText(String value) {
            textField.setText(value);
        }
    }

    private static final class CalendarIcon implements Icon {
        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.translate(x, y);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(BLUE);
            g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawRoundRect(2, 3, 14, 13, 2, 2);
            g.drawLine(2, 7, 16, 7);
            g.drawLine(6, 1, 6, 5);
            g.drawLine(12, 1, 12, 5);
            g.drawLine(5, 10, 7, 10);
            g.drawLine(10, 10, 12, 10);
            g.drawLine(5, 13, 7, 13);
            g.drawLine(10, 13, 12, 13);
            g.dispose();
        }
    }

    private interface RowActionHandler {
        void edit(int modelRow);

        void delete(int modelRow);
    }

    private class SelectAllHeaderRenderer extends JCheckBox implements TableCellRenderer {
        private SelectAllHeaderRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            setBackground(new Color(248, 249, 251));
            setToolTipText("Chọn hoặc bỏ chọn tất cả nhân viên đang hiển thị");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            boolean allSelected = table.getRowCount() > 0;
            for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                if (!Boolean.TRUE.equals(NhanVienPanel.this.model.getValueAt(modelRow, 0))) {
                    allSelected = false;
                    break;
                }
            }
            setSelected(allSelected);
            return this;
        }
    }

    private static class ActionCell extends JPanel implements TableCellRenderer {
        private ActionCell() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 5));
            setBackground(Color.WHITE);
            add(actionLabel(TableActionIcons.edit(), "Sửa"));
            add(actionLabel(TableActionIcons.delete(), "Xóa"));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            setBackground(selected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }

        private static JLabel actionLabel(Icon icon, String tooltip) {
            JLabel label = new JLabel(icon);
            label.setToolTipText(tooltip);
            return label;
        }
    }

    private static class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 4));
        private final JButton edit = new JButton(TableActionIcons.edit());
        private final JButton delete = new JButton(TableActionIcons.delete());
        private int modelRow;

        private ActionCellEditor(final RowActionHandler handler) {
            panel.setBackground(Color.WHITE);
            styleActionButton(edit, BLUE);
            styleActionButton(delete, new Color(205, 72, 72));
            edit.setToolTipText("Sửa nhân viên");
            edit.getAccessibleContext().setAccessibleName("Sửa nhân viên");
            delete.setToolTipText("Xóa nhân viên");
            delete.getAccessibleContext().setAccessibleName("Xóa nhân viên");
            panel.add(edit);
            panel.add(delete);
            edit.addActionListener(e -> {
                fireEditingStopped();
                handler.edit(modelRow);
            });
            delete.addActionListener(e -> {
                fireEditingStopped();
                handler.delete(modelRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean selected, int row, int column) {
            modelRow = table.convertRowIndexToModel(row);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        private static void styleActionButton(JButton button, Color color) {
            button.setForeground(color);
            button.setBackground(Color.WHITE);
            button.setFocusPainted(false);
            button.setMargin(new Insets(3, 5, 3, 5));
            button.setBorder(BorderFactory.createLineBorder(color));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }
}
