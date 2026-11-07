package com.example.view;

import com.example.dao.LoaiSanPhamDAO;
import com.example.dao.SanPhamDAO;
import com.example.model.LoaiSanPham;
import com.example.model.SanPham;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.Icon;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SanPhamPanel extends JPanel {

    private static final Color GREEN = new Color(20, 139, 85);
    private static final Color TEXT = new Color(45, 55, 72);
    private static final Color MUTED = new Color(117, 128, 145);
    private static final Color BORDER = new Color(229, 233, 239);

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final LoaiSanPhamDAO loaiSanPhamDAO = new LoaiSanPhamDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField txtTimKiem;
    private final JComboBox<Object> cmbDanhMuc;
    private final JButton btnXoaDaChon;
    private List<LoaiSanPham> danhSachLoai = new ArrayList<LoaiSanPham>();

    public SanPhamPanel() {
        setLayout(new BorderLayout(0, 18));
        setBackground(new Color(246, 248, 251));
        setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        JLabel lblTieuDe = new JLabel("Sản phẩm");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 25));
        lblTieuDe.setForeground(TEXT);

        JLabel lblMoTa = new JLabel("Quản lý danh mục hàng hóa và tồn kho");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(MUTED);

        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.add(lblTieuDe);
        titleText.add(Box.createVerticalStrut(5));
        titleText.add(lblMoTa);

        JButton btnTao = taoNut("+  Tạo sản phẩm", GREEN, Color.WHITE);
        JButton btnLamMoi = taoNut("Làm mới", Color.WHITE, TEXT);
        JButton btnXuat = taoNut("↓  Xuất file", Color.WHITE, TEXT);
        btnLamMoi.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        btnXuat.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        btnXoaDaChon = taoNut("Xóa đã chọn", new Color(205, 72, 72), Color.WHITE);
        btnXoaDaChon.setEnabled(false);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(btnTao);
        actionPanel.add(btnLamMoi);
        actionPanel.add(btnXuat);

        JPanel heading = new JPanel(new BorderLayout(12, 0));
        heading.setOpaque(false);
        heading.add(titleText, BorderLayout.WEST);
        heading.add(actionPanel, BorderLayout.EAST);

        txtTimKiem = new JTextField();
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimKiem.setPreferredSize(new Dimension(275, 36));
        txtTimKiem.setToolTipText("Tìm theo mã SKU hoặc tên sản phẩm");

        cmbDanhMuc = new JComboBox<Object>();
        cmbDanhMuc.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbDanhMuc.setPreferredSize(new Dimension(165, 36));

        JButton btnTimKiem = taoNut("", Color.WHITE, MUTED);
        btnTimKiem.setIcon(TableActionIcons.search());
        btnTimKiem.setToolTipText("Tìm kiếm sản phẩm");
        btnTimKiem.getAccessibleContext().setAccessibleName("Tìm kiếm sản phẩm");
        btnTimKiem.setPreferredSize(new Dimension(32, 30));
        btnTimKiem.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        JPanel searchBox = new JPanel(new BorderLayout(7, 0));
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(0, 10, 0, 8)));
        searchBox.add(btnTimKiem, BorderLayout.WEST);
        searchBox.add(txtTimKiem, BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 10));
        toolbar.setBackground(Color.WHITE);
        toolbar.add(searchBox);
        toolbar.add(cmbDanhMuc);
        toolbar.add(btnXoaDaChon);

        tableModel = new DefaultTableModel(
                new Object[] { "✓", "MÃ SKU", "SẢN PHẨM", "DANH MỤC", "GIÁ BÁN",
                        "CÓ THỂ BÁN", "MÃ LOẠI", "THAO TÁC" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 7;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                if (columnIndex == 4 || columnIndex == 5) {
                    return columnIndex == 4 ? BigDecimal.class : Integer.class;
                }
                return String.class;
            }
        };

        table = new JTable(tableModel);
        sorter = new TableRowSorter<DefaultTableModel>(tableModel);
        table.setRowSorter(sorter);
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
        sorter.setSortable(0, false);
        table.getColumnModel().getColumn(0).setPreferredWidth(42);
        table.getColumnModel().getColumn(0).setMinWidth(42);
        table.getColumnModel().getColumn(0).setMaxWidth(48);
        table.getColumnModel().getColumn(0).setHeaderRenderer(new SelectAllHeaderRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(105);
        table.getColumnModel().getColumn(2).setPreferredWidth(210);
        table.getColumnModel().getColumn(3).setPreferredWidth(145);
        table.getColumnModel().getColumn(4).setPreferredWidth(135);
        table.getColumnModel().getColumn(5).setPreferredWidth(115);
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);
        table.getColumnModel().getColumn(7).setPreferredWidth(86);
        table.getColumnModel().getColumn(7).setMinWidth(86);
        table.getColumnModel().getColumn(7).setMaxWidth(86);
        table.getColumnModel().getColumn(4).setCellRenderer(new GiaRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new SoLuongRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new ActionCell());
        table.getColumnModel().getColumn(7).setCellEditor(new ActionCellEditor(
                new RowActionHandler() {
                    @Override
                    public void edit(int row) {
                        suaSanPhamTaiModelRow(row);
                    }

                    @Override
                    public void delete(int row) {
                        xoaSanPhamTaiModelRow(row);
                    }
                }));
        table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewColumn = table.getColumnModel().getColumnIndexAtX(e.getX());
                if (viewColumn == 0) {
                    chonTatCaHienThi();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JLabel lblDanhSach = new JLabel("Danh sách sản phẩm");
        lblDanhSach.setFont(new Font("Arial", Font.BOLD, 15));
        lblDanhSach.setForeground(TEXT);
        lblDanhSach.setBorder(BorderFactory.createEmptyBorder(14, 14, 7, 14));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(Color.WHITE);
        tableHeader.add(lblDanhSach, BorderLayout.NORTH);
        tableHeader.add(toolbar, BorderLayout.CENTER);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createLineBorder(BORDER));
        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setOpaque(false);
        main.add(heading, BorderLayout.NORTH);
        main.add(tableCard, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        loadBackendData();

        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterProducts();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterProducts();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterProducts();
            }
        });
        btnTimKiem.addActionListener(e -> filterProducts());
        txtTimKiem.addActionListener(e -> filterProducts());
        cmbDanhMuc.addActionListener(e -> filterProducts());
        tableModel.addTableModelListener(e -> capNhatTrangThaiXoa());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    suaSanPham();
                }
            }
        });

        btnTao.addActionListener(e -> moHopThoaiSanPham(null));
        btnLamMoi.addActionListener(e -> loadBackendData());
        btnXoaDaChon.addActionListener(e -> xoaSanPhamDaChon());
        btnXuat.addActionListener(e -> xuatDanhSach());
    }

    private void loadBackendData() {
        try {
            List<LoaiSanPham> categories = loaiSanPhamDAO.getAll();
            List<SanPham> products = sanPhamDAO.getAll();
            danhSachLoai = categories;
            tableModel.setRowCount(0);
            cmbDanhMuc.removeAllItems();
            cmbDanhMuc.addItem("Tất cả danh mục");
            for (LoaiSanPham loai : danhSachLoai) {
                cmbDanhMuc.addItem(new DanhMucOption(loai.getMaLoai(), loai.getTenLoai()));
            }
            for (SanPham sanPham : products) {
                tableModel.addRow(new Object[] {
                        Boolean.FALSE,
                        sanPham.getMaSP(),
                        sanPham.getTenSP(),
                        tenDanhMuc(sanPham.getMaLoai()),
                        sanPham.getDonGia(),
                        Integer.valueOf(sanPham.getSoLuongTon()),
                        sanPham.getMaLoai(),
                        ""
                });
            }
            filterProducts();
        } catch (SQLException ex) {
            danhSachLoai = new ArrayList<LoaiSanPham>();
            cmbDanhMuc.removeAllItems();
            cmbDanhMuc.addItem("Tất cả danh mục");
            tableModel.setRowCount(0);
            filterProducts();
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Không thể tải sản phẩm và danh mục từ cơ sở dữ liệu.\n"
                            + "Kiểm tra SQL Server và cấu hình kết nối.\nChi tiết: " + ex.getMessage(),
                    "Lỗi kết nối backend", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String tenDanhMuc(String maLoai) {
        for (LoaiSanPham loai : danhSachLoai) {
            if (loai.getMaLoai().equals(maLoai)) {
                return loai.getTenLoai();
            }
        }
        return maLoai == null ? "" : maLoai;
    }

    private void filterProducts() {
        final String keyword = txtTimKiem.getText().trim().toLowerCase(Locale.ROOT);
        final Object selectedCategory = cmbDanhMuc.getSelectedItem();
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                boolean matchesKeyword = keyword.length() == 0;
                for (int column = 1; !matchesKeyword && column <= 2; column++) {
                    Object value = entry.getValue(column);
                    if (value != null && value.toString().toLowerCase(Locale.ROOT).contains(keyword)) {
                        matchesKeyword = true;
                    }
                }
                if (!matchesKeyword) {
                    return false;
                }
                if (selectedCategory instanceof DanhMucOption) {
                    DanhMucOption option = (DanhMucOption) selectedCategory;
                    return option.maLoai.equals(String.valueOf(entry.getValue(6)));
                }
                return true;
            }
        });
        capNhatHeaderChonTatCa();
    }

    private void suaSanPham() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        suaSanPhamTaiModelRow(table.convertRowIndexToModel(selectedRow));
    }

    private void suaSanPhamTaiModelRow(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) {
            return;
        }
        SanPham sanPham = new SanPham(
                String.valueOf(tableModel.getValueAt(row, 1)),
                String.valueOf(tableModel.getValueAt(row, 2)),
                (BigDecimal) tableModel.getValueAt(row, 4),
                ((Number) tableModel.getValueAt(row, 5)).intValue(),
                String.valueOf(tableModel.getValueAt(row, 6)));
        moHopThoaiSanPham(sanPham);
    }

    private void moHopThoaiSanPham(final SanPham sanPham) {
        if (danhSachLoai.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Chưa có danh mục sản phẩm. Vui lòng tạo danh mục trước.",
                    "Không thể tạo sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final boolean isNew = sanPham == null;
        final JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                isNew ? "Tạo sản phẩm" : "Cập nhật sản phẩm",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        final JTextField txtMaSP = new JTextField(22);
        final JTextField txtTenSP = new JTextField(22);
        final JTextField txtDonGia = new JTextField(22);
        final JTextField txtSoLuong = new JTextField(22);
        final JComboBox<DanhMucOption> cmbLoai = new JComboBox<DanhMucOption>();
        for (LoaiSanPham loai : danhSachLoai) {
            cmbLoai.addItem(new DanhMucOption(loai.getMaLoai(), loai.getTenLoai()));
        }

        if (!isNew) {
            txtMaSP.setText(sanPham.getMaSP());
            txtMaSP.setEditable(false);
            txtTenSP.setText(sanPham.getTenSP());
            txtDonGia.setText(sanPham.getDonGia().toPlainString());
            txtSoLuong.setText(String.valueOf(sanPham.getSoLuongTon()));
            for (int i = 0; i < cmbLoai.getItemCount(); i++) {
                if (cmbLoai.getItemAt(i).maLoai.equals(sanPham.getMaLoai())) {
                    cmbLoai.setSelectedIndex(i);
                    break;
                }
            }
        }

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(Color.WHITE);
        fields.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));
        themTruong(fields, "Mã SKU", txtMaSP, 0);
        themTruong(fields, "Tên sản phẩm", txtTenSP, 1);
        themTruong(fields, "Danh mục", cmbLoai, 2);
        themTruong(fields, "Giá bán (VNĐ)", txtDonGia, 3);
        themTruong(fields, "Số lượng tồn", txtSoLuong, 4);

        JButton btnLuu = taoNut(isNew ? "Tạo sản phẩm" : "Lưu thay đổi", GREEN, Color.WHITE);
        JButton btnHuy = taoNut("Hủy", Color.WHITE, TEXT);
        btnHuy.setBorder(BorderFactory.createLineBorder(BORDER));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 12));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));
        buttons.add(btnHuy);
        buttons.add(btnLuu);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.add(fields, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(content);

        btnHuy.addActionListener(e -> dialog.dispose());
        btnLuu.addActionListener(e -> {
            String maSP = txtMaSP.getText().trim();
            String tenSP = txtTenSP.getText().trim();
            if (maSP.length() == 0 || tenSP.length() == 0) {
                baoLoi(dialog, "Vui lòng nhập mã SKU và tên sản phẩm.");
                return;
            }

            BigDecimal donGia;
            int soLuong;
            try {
                donGia = new BigDecimal(txtDonGia.getText().trim());
                if (donGia.signum() < 0) {
                    baoLoi(dialog, "Giá bán không được là số âm.");
                    return;
                }
            } catch (NumberFormatException ex) {
                baoLoi(dialog, "Giá bán phải là một số hợp lệ.");
                return;
            }
            try {
                soLuong = Integer.parseInt(txtSoLuong.getText().trim());
                if (soLuong < 0) {
                    baoLoi(dialog, "Số lượng tồn không được là số âm.");
                    return;
                }
            } catch (NumberFormatException ex) {
                baoLoi(dialog, "Số lượng tồn phải là số nguyên hợp lệ.");
                return;
            }

            DanhMucOption loai = (DanhMucOption) cmbLoai.getSelectedItem();
            SanPham value = new SanPham(maSP, tenSP, donGia, soLuong, loai.maLoai);
            boolean success = isNew ? sanPhamDAO.insert(value) : sanPhamDAO.update(value);
            if (success) {
                dialog.dispose();
                loadBackendData();
                JOptionPane.showMessageDialog(this,
                        isNew ? "Tạo sản phẩm thành công." : "Cập nhật sản phẩm thành công.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                baoLoi(dialog, "Không thể lưu sản phẩm. Hãy kiểm tra mã SKU, danh mục và kết nối cơ sở dữ liệu.");
            }
        });

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void xoaSanPhamTaiModelRow(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) {
            return;
        }
        String maSP = String.valueOf(tableModel.getValueAt(row, 1));
        int confirm = JOptionPane.showConfirmDialog(
                this, "Bạn có chắc muốn xóa sản phẩm \"" + tableModel.getValueAt(row, 2) + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (sanPhamDAO.delete(maSP)) {
                    loadBackendData();
                    JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công.");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không tìm thấy sản phẩm để xóa.",
                            "Xóa thất bại", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        thongBaoLoiXoa(ex),
                        "Xóa thất bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xoaSanPhamDaChon() {
        List<String> productIds = new ArrayList<String>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (Boolean.TRUE.equals(tableModel.getValueAt(row, 0))) {
                productIds.add(String.valueOf(tableModel.getValueAt(row, 1)));
            }
        }
        if (productIds.isEmpty()) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this, "Xóa " + productIds.size() + " sản phẩm đã chọn?",
                "Xác nhận xóa nhiều", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        List<String> failed = new ArrayList<String>();
        for (String maSP : productIds) {
            try {
                if (!sanPhamDAO.delete(maSP)) {
                    failed.add(maSP + ": không tìm thấy sản phẩm");
                }
            } catch (SQLException ex) {
                failed.add(maSP + ": " + thongBaoLoiXoa(ex));
            }
        }
        loadBackendData();
        if (failed.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đã xóa " + productIds.size() + " sản phẩm.");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đã xóa " + (productIds.size() - failed.size()) + " sản phẩm. "
                            + "Các sản phẩm chưa xóa được:\n" + join(failed),
                    "Một số mục chưa xóa được", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String thongBaoLoiXoa(SQLException ex) {
        for (SQLException current = ex; current != null; current = current.getNextException()) {
            if (current.getErrorCode() == 547) {
                return "Sản phẩm đang được dùng trong chi tiết hóa đơn nên không thể xóa.";
            }
        }
        return "Không thể xóa do lỗi cơ sở dữ liệu: " + ex.getMessage();
    }

    private void capNhatTrangThaiXoa() {
        capNhatHeaderChonTatCa();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (Boolean.TRUE.equals(tableModel.getValueAt(row, 0))) {
                btnXoaDaChon.setEnabled(true);
                return;
            }
        }
        btnXoaDaChon.setEnabled(false);
    }

    private void chonTatCaHienThi() {
        boolean allSelected = table.getRowCount() > 0;
        for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            if (!Boolean.TRUE.equals(tableModel.getValueAt(modelRow, 0))) {
                allSelected = false;
                break;
            }
        }
        boolean select = !allSelected;
        for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            tableModel.setValueAt(Boolean.valueOf(select), modelRow, 0);
        }
        capNhatHeaderChonTatCa();
    }

    private void capNhatHeaderChonTatCa() {
        if (table.getColumnModel().getColumnCount() == 0) {
            return;
        }
        table.getTableHeader().repaint();
    }

    private String join(List<String> values) {
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(value);
        }
        return result.toString();
    }

    private void xuatDanhSach() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Xuất danh sách sản phẩm");
        chooser.setSelectedFile(new java.io.File("danh-sach-san-pham.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                chooser.getSelectedFile().toPath(), Charset.forName("UTF-8"))) {
            writer.write('\uFEFF');
            writer.write("Mã SKU;Sản phẩm;Danh mục;Giá bán;Có thể bán");
            writer.newLine();
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int column = 1; column < 6; column++) {
                    if (column > 1) {
                        writer.write(';');
                    }
                    Object value = tableModel.getValueAt(row, column);
                    writer.write(csvValue(value == null ? "" : value.toString()));
                }
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Đã xuất danh sách sản phẩm thành công.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể xuất file: " + ex.getMessage(),
                    "Xuất file thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csvValue(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
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
        JOptionPane.showMessageDialog(parent, message, "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
    }

    private static class DanhMucOption {
        private final String maLoai;
        private final String tenLoai;

        private DanhMucOption(String maLoai, String tenLoai) {
            this.maLoai = maLoai;
            this.tenLoai = tenLoai;
        }

        @Override
        public String toString() {
            return tenLoai;
        }
    }

    private static class GiaRenderer extends DefaultTableCellRenderer {
        private final NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        private GiaRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            setText(value instanceof BigDecimal ? formatter.format(value) + " đ" : "");
        }
    }

    private static class SoLuongRenderer extends DefaultTableCellRenderer {
        private SoLuongRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
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
            setToolTipText("Chọn hoặc bỏ chọn tất cả sản phẩm đang hiển thị");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            boolean allSelected = table.getRowCount() > 0;
            for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                if (!Boolean.TRUE.equals(tableModel.getValueAt(modelRow, 0))) {
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

        private ActionCellEditor(RowActionHandler handler) {
            panel.setBackground(Color.WHITE);
            styleActionButton(edit, new Color(48, 112, 190));
            styleActionButton(delete, new Color(205, 72, 72));
            edit.setToolTipText("Sửa sản phẩm");
            edit.getAccessibleContext().setAccessibleName("Sửa sản phẩm");
            delete.setToolTipText("Xóa sản phẩm");
            delete.getAccessibleContext().setAccessibleName("Xóa sản phẩm");
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
            button.setMargin(new Insets(2, 4, 2, 4));
            button.setBorder(BorderFactory.createLineBorder(color));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }
}
