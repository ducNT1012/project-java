package com.example.view;

import com.example.dao.HoaDonDAO;
import com.example.dao.KhachHangDAO;
import com.example.model.HoaDon;
import com.example.model.KhachHang;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HoaDonPanel extends JPanel {

    private static final Color GREEN = new Color(20, 139, 85);
    private static final Color BLUE = new Color(48, 112, 190);
    private static final Color TEXT = new Color(45, 55, 72);
    private static final Color MUTED = new Color(117, 128, 145);
    private static final Color BORDER = new Color(229, 233, 239);
    private static final Color BACKGROUND = new Color(246, 248, 251);

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final JTextField txtTimKiem = new JTextField();
    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JButton btnXoaDaChon;
    private final List<KhachHang> danhSachKhachHang = new ArrayList<KhachHang>();

    public HoaDonPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        JLabel title = new JLabel("Hóa đơn");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Quản lý hóa đơn bán hàng và thông tin khách hàng");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.add(title);
        titleText.add(Box.createVerticalStrut(5));
        titleText.add(subtitle);

        JButton btnThem = taoNut("+  Tạo hóa đơn", GREEN, Color.WHITE);
        JButton btnLamMoi = taoNut("Làm mới", Color.WHITE, TEXT);
        btnLamMoi.setBorder(BorderFactory.createLineBorder(BORDER));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(btnThem);
        actions.add(btnLamMoi);

        JPanel heading = new JPanel(new BorderLayout(12, 0));
        heading.setOpaque(false);
        heading.add(titleText, BorderLayout.WEST);
        heading.add(actions, BorderLayout.EAST);

        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimKiem.setToolTipText("Tìm theo mã hóa đơn, ngày lập hoặc khách hàng");
        txtTimKiem.setPreferredSize(new Dimension(315, 36));
        JButton btnTimKiem = new JButton(TableActionIcons.search());
        btnTimKiem.setToolTipText("Tìm kiếm hóa đơn");
        btnTimKiem.getAccessibleContext().setAccessibleName("Tìm kiếm hóa đơn");
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

        JButton btnChiTiet = taoNut("Chi tiết", Color.WHITE, TEXT);
        btnXoaDaChon = taoNut("Xóa đã chọn", new Color(205, 72, 72), Color.WHITE);
        btnChiTiet.setBorder(BorderFactory.createLineBorder(BORDER));
        btnChiTiet.setEnabled(false);
        btnXoaDaChon.setEnabled(false);
        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 8));
        tableActions.setBackground(Color.WHITE);
        tableActions.add(btnChiTiet);
        tableActions.add(btnXoaDaChon);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 8));
        searchPanel.add(searchBox, BorderLayout.WEST);
        searchPanel.add(tableActions, BorderLayout.EAST);

        model = new DefaultTableModel(
                new Object[] { "✓", "MÃ HÓA ĐƠN", "NGÀY LẬP", "MÃ KHÁCH HÀNG", "KHÁCH HÀNG", "THAO TÁC" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 5;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
        };
        table = new JTable(model);
        sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);
        styleTable();
        sorter.setSortable(0, false);
        table.getColumnModel().getColumn(0).setPreferredWidth(42);
        table.getColumnModel().getColumn(0).setMinWidth(42);
        table.getColumnModel().getColumn(0).setMaxWidth(48);
        table.getColumnModel().getColumn(0).setHeaderRenderer(new SelectAllHeaderRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(170);
        table.getColumnModel().getColumn(4).setPreferredWidth(250);
        table.getColumnModel().getColumn(5).setPreferredWidth(86);
        table.getColumnModel().getColumn(5).setMinWidth(86);
        table.getColumnModel().getColumn(5).setMaxWidth(86);
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionCell());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionCellEditor(new RowActionHandler() {
            @Override
            public void edit(int modelRow) {
                moHopThoaiHoaDon(layHoaDonTaiModelRow(modelRow));
            }

            @Override
            public void delete(int modelRow) {
                xoaHoaDonTaiModelRow(modelRow);
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
        JLabel listTitle = new JLabel("Danh sách hóa đơn");
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

        btnThem.addActionListener(e -> moHopThoaiHoaDon(null));
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            loadData();
        });
        btnTimKiem.addActionListener(e -> timKiem());
        txtTimKiem.addActionListener(e -> timKiem());
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timKiem();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timKiem();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timKiem();
            }
        });
        btnChiTiet.addActionListener(e -> hienThiChiTiet(layHoaDonDangChon()));
        btnXoaDaChon.addActionListener(e -> xoaHoaDonDaChon());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnChiTiet.setEnabled(table.getSelectedRow() >= 0);
            }
        });
        model.addTableModelListener(e -> capNhatTrangThaiXoa());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    hienThiChiTiet(layHoaDonDangChon());
                }
            }
        });
    }

    private void styleTable() {
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

    private void loadData() {
        try {
            List<KhachHang> customers = khachHangDAO.getAll();
            List<HoaDon> invoices = hoaDonDAO.getAll();
            danhSachKhachHang.clear();
            danhSachKhachHang.addAll(customers);
            model.setRowCount(0);
            for (HoaDon invoice : invoices) {
                model.addRow(new Object[] {
                        Boolean.FALSE,
                        invoice.getMaHD(),
                        invoice.getNgayLap() == null ? "" : invoice.getNgayLap().toString(),
                        invoice.getMaKH(),
                        tenKhachHang(invoice.getMaKH()),
                        ""
                });
            }
            timKiem();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Không thể tải hóa đơn và khách hàng từ cơ sở dữ liệu.\nChi tiết: " + ex.getMessage(),
                    "Lỗi kết nối backend", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String tenKhachHang(String maKH) {
        for (KhachHang customer : danhSachKhachHang) {
            if (customer.getMaKH().equals(maKH)) {
                return customer.getTenKH();
            }
        }
        return "";
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        sorter.setRowFilter(keyword.isEmpty()
                ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 1, 2, 3, 4));
        table.getTableHeader().repaint();
    }

    private HoaDon layHoaDonTaiModelRow(int row) {
        if (row < 0 || row >= model.getRowCount()) {
            return null;
        }
        return new HoaDon(
                valueAt(row, 1),
                valueAt(row, 2).isEmpty() ? null : LocalDate.parse(valueAt(row, 2)),
                valueAt(row, 3));
    }

    private HoaDon layHoaDonDangChon() {
        int selected = table.getSelectedRow();
        return selected < 0 ? null : layHoaDonTaiModelRow(table.convertRowIndexToModel(selected));
    }

    private void moHopThoaiHoaDon(final HoaDon existing) {
        if (danhSachKhachHang.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Chưa có khách hàng trong hệ thống. Hãy thêm khách hàng trước khi tạo hóa đơn.",
                    "Không thể tạo hóa đơn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean isNew = existing == null;
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                isNew ? "Tạo hóa đơn" : "Cập nhật hóa đơn",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextField txtMaHD = new JTextField(22);
        JTextField txtNgayLap = new JTextField(22);
        JComboBox<CustomerOption> cmbKhachHang = new JComboBox<CustomerOption>();
        for (KhachHang customer : danhSachKhachHang) {
            cmbKhachHang.addItem(new CustomerOption(customer));
        }
        if (isNew) {
            txtNgayLap.setText(LocalDate.now().toString());
        } else {
            txtMaHD.setText(existing.getMaHD());
            txtMaHD.setEditable(false);
            txtNgayLap.setText(existing.getNgayLap() == null ? "" : existing.getNgayLap().toString());
            for (int i = 0; i < cmbKhachHang.getItemCount(); i++) {
                if (cmbKhachHang.getItemAt(i).customer.getMaKH().equals(existing.getMaKH())) {
                    cmbKhachHang.setSelectedIndex(i);
                    break;
                }
            }
        }

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(Color.WHITE);
        fields.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));
        themTruong(fields, "Mã hóa đơn", txtMaHD, 0);
        themTruong(fields, "Ngày lập (yyyy-MM-dd)", txtNgayLap, 1);
        themTruong(fields, "Khách hàng", cmbKhachHang, 2);

        JButton btnLuu = taoNut(isNew ? "Tạo hóa đơn" : "Lưu thay đổi", GREEN, Color.WHITE);
        JButton btnHuy = taoNut("Hủy", Color.WHITE, TEXT);
        btnHuy.setBorder(BorderFactory.createLineBorder(BORDER));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 12));
        buttons.setBackground(Color.WHITE);
        buttons.add(btnHuy);
        buttons.add(btnLuu);
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.add(fields, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(content);

        btnHuy.addActionListener(e -> dialog.dispose());
        btnLuu.addActionListener(e -> {
            String maHD = txtMaHD.getText().trim();
            if (maHD.isEmpty()) {
                baoLoi(dialog, "Vui lòng nhập mã hóa đơn.");
                return;
            }
            LocalDate ngayLap;
            try {
                ngayLap = LocalDate.parse(txtNgayLap.getText().trim());
            } catch (DateTimeParseException ex) {
                baoLoi(dialog, "Ngày lập không hợp lệ. Hãy nhập theo định dạng yyyy-MM-dd.");
                return;
            }
            CustomerOption selectedCustomer = (CustomerOption) cmbKhachHang.getSelectedItem();
            if (selectedCustomer == null) {
                baoLoi(dialog, "Vui lòng chọn khách hàng.");
                return;
            }
            HoaDon invoice = new HoaDon(maHD, ngayLap, selectedCustomer.customer.getMaKH());
            boolean success = isNew ? hoaDonDAO.insert(invoice) : hoaDonDAO.update(invoice);
            if (success) {
                dialog.dispose();
                loadData();
                JOptionPane.showMessageDialog(this,
                        isNew ? "Tạo hóa đơn thành công." : "Cập nhật hóa đơn thành công.",
                        "Hóa đơn", JOptionPane.INFORMATION_MESSAGE);
            } else {
                baoLoi(dialog, "Không thể lưu hóa đơn. Kiểm tra mã hóa đơn, khách hàng và kết nối cơ sở dữ liệu.");
            }
        });
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void hienThiChiTiet(HoaDon invoice) {
        if (invoice == null) {
            return;
        }
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(Color.WHITE);
        fields.setBorder(BorderFactory.createEmptyBorder(20, 22, 14, 22));
        themChiTiet(fields, "Mã hóa đơn", invoice.getMaHD(), 0);
        themChiTiet(fields, "Ngày lập", invoice.getNgayLap() == null ? "" : invoice.getNgayLap().toString(), 1);
        themChiTiet(fields, "Mã khách hàng", invoice.getMaKH(), 2);
        themChiTiet(fields, "Khách hàng", tenKhachHang(invoice.getMaKH()), 3);
        JButton btnDong = taoNut("Đóng", BLUE, Color.WHITE);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 12));
        buttons.setBackground(Color.WHITE);
        buttons.add(btnDong);
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this), "Chi tiết hóa đơn", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.add(fields, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(content);
        btnDong.addActionListener(e -> dialog.dispose());
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void xoaHoaDonTaiModelRow(int row) {
        HoaDon invoice = layHoaDonTaiModelRow(row);
        if (invoice == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this, "Bạn có chắc muốn xóa hóa đơn \"" + invoice.getMaHD() + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (hoaDonDAO.delete(invoice.getMaHD())) {
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công.");
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn để xóa.",
                        "Xóa thất bại", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, thongBaoLoiXoa(ex),
                    "Xóa hóa đơn thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaHoaDonDaChon() {
        List<String> invoiceIds = new ArrayList<String>();
        for (int row = 0; row < model.getRowCount(); row++) {
            if (Boolean.TRUE.equals(model.getValueAt(row, 0))) {
                invoiceIds.add(valueAt(row, 1));
            }
        }
        if (invoiceIds.isEmpty()) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this, "Xóa " + invoiceIds.size() + " hóa đơn đã chọn?",
                "Xác nhận xóa nhiều", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        List<String> failed = new ArrayList<String>();
        for (String maHD : invoiceIds) {
            try {
                if (!hoaDonDAO.delete(maHD)) {
                    failed.add(maHD + ": không tìm thấy hóa đơn");
                }
            } catch (SQLException ex) {
                failed.add(maHD + ": " + thongBaoLoiXoa(ex));
            }
        }
        loadData();
        if (failed.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đã xóa " + invoiceIds.size() + " hóa đơn.");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đã xóa " + (invoiceIds.size() - failed.size()) + " hóa đơn. Các hóa đơn chưa xóa được:\n"
                            + join(failed),
                    "Một số hóa đơn chưa xóa được", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String thongBaoLoiXoa(SQLException ex) {
        for (SQLException current = ex; current != null; current = current.getNextException()) {
            if (current.getErrorCode() == 547) {
                return "Hóa đơn đang có chi tiết hóa đơn nên không thể xóa.";
            }
        }
        return "Lỗi cơ sở dữ liệu: " + ex.getMessage();
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
            model.setValueAt(Boolean.valueOf(select), table.convertRowIndexToModel(viewRow), 0);
        }
        table.getTableHeader().repaint();
    }

    private void capNhatTrangThaiXoa() {
        boolean hasSelection = false;
        for (int row = 0; row < model.getRowCount(); row++) {
            if (Boolean.TRUE.equals(model.getValueAt(row, 0))) {
                hasSelection = true;
                break;
            }
        }
        btnXoaDaChon.setEnabled(hasSelection);
        table.getTableHeader().repaint();
    }

    private String valueAt(int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private String join(List<String> values) {
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(value);
        }
        return result.toString();
    }

    private void themTruong(JPanel panel, String label, JComponent input, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        if (input instanceof JTextField) {
            ((JTextField) input).setFont(new Font("Arial", Font.PLAIN, 13));
            ((JTextField) input).setPreferredSize(new Dimension(260, 34));
        } else {
            input.setPreferredSize(new Dimension(260, 34));
        }
        panel.add(input, gbc);
    }

    private void themChiTiet(JPanel panel, String label, String value, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 16);
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        JLabel name = new JLabel(label);
        name.setFont(new Font("Arial", Font.BOLD, 12));
        name.setForeground(MUTED);
        panel.add(name, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JLabel detail = new JLabel(value == null || value.trim().isEmpty() ? "—" : value);
        detail.setFont(new Font("Arial", Font.PLAIN, 14));
        detail.setForeground(TEXT);
        panel.add(detail, gbc);
    }

    private JButton taoNut(String text, Color background, Color foreground) {
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

    private class SelectAllHeaderRenderer extends JCheckBox implements TableCellRenderer {
        private SelectAllHeaderRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            setBackground(new Color(248, 249, 251));
            setToolTipText("Chọn hoặc bỏ chọn tất cả hóa đơn đang hiển thị");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable sourceTable, Object value, boolean selected, boolean focus, int row, int column) {
            boolean allSelected = sourceTable.getRowCount() > 0;
            for (int viewRow = 0; viewRow < sourceTable.getRowCount(); viewRow++) {
                int modelRow = sourceTable.convertRowIndexToModel(viewRow);
                if (!Boolean.TRUE.equals(HoaDonPanel.this.model.getValueAt(modelRow, 0))) {
                    allSelected = false;
                    break;
                }
            }
            setSelected(allSelected);
            return this;
        }
    }

    private interface RowActionHandler {
        void edit(int modelRow);

        void delete(int modelRow);
    }

    private static class ActionCell extends JPanel implements TableCellRenderer {
        private ActionCell() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 5));
            setBackground(Color.WHITE);
            JLabel edit = new JLabel(TableActionIcons.edit());
            edit.setToolTipText("Sửa");
            JLabel delete = new JLabel(TableActionIcons.delete());
            delete.setToolTipText("Xóa");
            add(edit);
            add(delete);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            setBackground(selected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    private static class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 4));
        private final JButton edit = new JButton(TableActionIcons.edit());
        private final JButton delete = new JButton(TableActionIcons.delete());
        private int modelRow;

        private ActionCellEditor(RowActionHandler handler) {
            panel.setBackground(Color.WHITE);
            styleButton(edit, BLUE);
            styleButton(delete, new Color(205, 72, 72));
            edit.setToolTipText("Sửa hóa đơn");
            edit.getAccessibleContext().setAccessibleName("Sửa hóa đơn");
            delete.setToolTipText("Xóa hóa đơn");
            delete.getAccessibleContext().setAccessibleName("Xóa hóa đơn");
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

        private static void styleButton(JButton button, Color color) {
            button.setForeground(color);
            button.setBackground(Color.WHITE);
            button.setFocusPainted(false);
            button.setMargin(new Insets(2, 4, 2, 4));
            button.setBorder(BorderFactory.createLineBorder(color));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    private static class CustomerOption {
        private final KhachHang customer;

        private CustomerOption(KhachHang customer) {
            this.customer = customer;
        }

        @Override
        public String toString() {
            return customer.getTenKH() + " (" + customer.getMaKH() + ")";
        }
    }
}
