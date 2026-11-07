package com.example.view;

import com.example.dao.ChiTietHoaDonDAO;
import com.example.dao.HoaDonDAO;
import com.example.dao.KhachHangDAO;
import com.example.dao.SanPhamDAO;
import com.example.model.ChiTietHoaDon;
import com.example.model.HoaDon;
import com.example.model.KhachHang;
import com.example.model.SanPham;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ChiTietHoaDonPanel extends JPanel {

    private static final Color GREEN = new Color(20, 139, 85);
    private static final Color BLUE = new Color(48, 112, 190);
    private static final Color TEXT = new Color(45, 55, 72);
    private static final Color MUTED = new Color(117, 128, 145);
    private static final Color BORDER = new Color(229, 233, 239);
    private static final Color BACKGROUND = new Color(246, 248, 251);

    private final ChiTietHoaDonDAO chiTietDAO = new ChiTietHoaDonDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final JComboBox<InvoiceOption> cmbHoaDon = new JComboBox<InvoiceOption>();
    private final JTextField txtTimKiem = new JTextField();
    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JButton btnThem;
    private final JButton btnXoaDaChon;
    private final JLabel lblTongTien = new JLabel("Tổng tiền: 0 đ");
    private final List<SanPham> danhSachSanPham = new ArrayList<SanPham>();
    private boolean dangTaiHoaDon;

    public ChiTietHoaDonPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        JLabel title = new JLabel("Chi tiết hóa đơn");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Quản lý các sản phẩm và số lượng trong từng hóa đơn");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.add(title);
        titleText.add(Box.createVerticalStrut(5));
        titleText.add(subtitle);

        btnThem = taoNut("+  Thêm sản phẩm", GREEN, Color.WHITE);
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

        cmbHoaDon.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbHoaDon.setPreferredSize(new Dimension(330, 36));
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimKiem.setToolTipText("Tìm theo mã sản phẩm hoặc tên sản phẩm");
        txtTimKiem.setPreferredSize(new Dimension(265, 36));
        JButton btnTimKiem = new JButton(TableActionIcons.search());
        btnTimKiem.setToolTipText("Tìm kiếm sản phẩm trong hóa đơn");
        btnTimKiem.getAccessibleContext().setAccessibleName("Tìm kiếm sản phẩm trong hóa đơn");
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
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 9));
        toolbar.setBackground(Color.WHITE);
        toolbar.add(new JLabel("Hóa đơn:"));
        toolbar.add(cmbHoaDon);
        toolbar.add(searchBox);
        toolbar.add(btnXoaDaChon);

        model = new DefaultTableModel(
                new Object[] { "✓", "MÃ SẢN PHẨM", "SẢN PHẨM", "SỐ LƯỢNG",
                        "ĐƠN GIÁ", "THÀNH TIỀN", "THAO TÁC" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 6;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                if (columnIndex == 3) {
                    return Integer.class;
                }
                if (columnIndex == 4 || columnIndex == 5) {
                    return BigDecimal.class;
                }
                return String.class;
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
        table.getColumnModel().getColumn(1).setPreferredWidth(145);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(165);
        table.getColumnModel().getColumn(6).setPreferredWidth(86);
        table.getColumnModel().getColumn(6).setMinWidth(86);
        table.getColumnModel().getColumn(6).setMaxWidth(86);
        table.getColumnModel().getColumn(4).setCellRenderer(new CurrencyRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new CurrencyRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionCell());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionCellEditor(new RowActionHandler() {
            @Override
            public void edit(int modelRow) {
                moHopThoaiChiTiet(layChiTietTaiModelRow(modelRow));
            }

            @Override
            public void delete(int modelRow) {
                xoaChiTietTaiModelRow(modelRow);
            }
        }));
        table.getColumnModel().getColumn(0).setHeaderRenderer(new SelectAllHeaderRenderer());
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
        JLabel listTitle = new JLabel("Danh sách sản phẩm trong hóa đơn");
        listTitle.setFont(new Font("Arial", Font.BOLD, 15));
        listTitle.setForeground(TEXT);
        listTitle.setBorder(BorderFactory.createEmptyBorder(14, 14, 5, 14));
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(Color.WHITE);
        tableHeader.add(listTitle, BorderLayout.NORTH);
        tableHeader.add(toolbar, BorderLayout.CENTER);

        lblTongTien.setFont(new Font("Arial", Font.BOLD, 15));
        lblTongTien.setForeground(TEXT);
        lblTongTien.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createLineBorder(BORDER));
        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        tableCard.add(lblTongTien, BorderLayout.SOUTH);

        add(heading, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
        loadBackendData();

        btnThem.addActionListener(e -> moHopThoaiChiTiet(null));
        btnLamMoi.addActionListener(e -> loadBackendData());
        btnXoaDaChon.addActionListener(e -> xoaChiTietDaChon());
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
        cmbHoaDon.addActionListener(e -> {
            if (!dangTaiHoaDon) {
                loadChiTiet();
            }
        });
        model.addTableModelListener(e -> {
            capNhatTrangThaiXoa();
            capNhatTongTien();
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

    private void loadBackendData() {
        String selectedInvoiceId = selectedInvoiceId();
        try {
            List<HoaDon> invoices = hoaDonDAO.getAll();
            List<KhachHang> customers = khachHangDAO.getAll();
            List<SanPham> products = sanPhamDAO.getAll();
            danhSachSanPham.clear();
            danhSachSanPham.addAll(products);
            dangTaiHoaDon = true;
            cmbHoaDon.removeAllItems();
            for (HoaDon invoice : invoices) {
                cmbHoaDon.addItem(new InvoiceOption(invoice, findCustomerName(customers, invoice.getMaKH())));
            }
            if (selectedInvoiceId != null) {
                for (int i = 0; i < cmbHoaDon.getItemCount(); i++) {
                    if (cmbHoaDon.getItemAt(i).invoice.getMaHD().equals(selectedInvoiceId)) {
                        cmbHoaDon.setSelectedIndex(i);
                        break;
                    }
                }
            }
            dangTaiHoaDon = false;
            btnThem.setEnabled(cmbHoaDon.getSelectedItem() != null && !danhSachSanPham.isEmpty());
            loadChiTiet();
        } catch (SQLException ex) {
            dangTaiHoaDon = false;
            model.setRowCount(0);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Không thể tải hóa đơn, khách hàng hoặc sản phẩm từ cơ sở dữ liệu.\nChi tiết: "
                            + ex.getMessage(),
                    "Lỗi kết nối backend", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String selectedInvoiceId() {
        InvoiceOption selected = (InvoiceOption) cmbHoaDon.getSelectedItem();
        return selected == null ? null : selected.invoice.getMaHD();
    }

    private String findCustomerName(List<KhachHang> customers, String maKH) {
        for (KhachHang customer : customers) {
            if (customer.getMaKH().equals(maKH)) {
                return customer.getTenKH();
            }
        }
        return "Không tìm thấy khách hàng";
    }

    private void loadChiTiet() {
        InvoiceOption selectedInvoice = (InvoiceOption) cmbHoaDon.getSelectedItem();
        model.setRowCount(0);
        if (selectedInvoice == null) {
            btnThem.setEnabled(false);
            lblTongTien.setText("Tổng tiền: 0 đ");
            return;
        }
        btnThem.setEnabled(!danhSachSanPham.isEmpty());
        try {
            for (ChiTietHoaDon detail : chiTietDAO.getByMaHD(selectedInvoice.invoice.getMaHD())) {
                SanPham product = findProduct(detail.getMaSP());
                String productName = product == null ? "Không tìm thấy sản phẩm" : product.getTenSP();
                BigDecimal lineTotal = detail.getDonGia().multiply(BigDecimal.valueOf(detail.getSoLuong()));
                model.addRow(new Object[] {
                        Boolean.FALSE,
                        detail.getMaSP(),
                        productName,
                        Integer.valueOf(detail.getSoLuong()),
                        detail.getDonGia(),
                        lineTotal,
                        ""
                });
            }
            timKiem();
            capNhatTongTien();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Không thể tải chi tiết hóa đơn.\nChi tiết: " + ex.getMessage(),
                    "Lỗi đọc dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private SanPham findProduct(String maSP) {
        for (SanPham product : danhSachSanPham) {
            if (product.getMaSP().equals(maSP)) {
                return product;
            }
        }
        return null;
    }

    private ChiTietHoaDon layChiTietTaiModelRow(int row) {
        if (row < 0 || row >= model.getRowCount()) {
            return null;
        }
        InvoiceOption invoice = (InvoiceOption) cmbHoaDon.getSelectedItem();
        if (invoice == null) {
            return null;
        }
        return new ChiTietHoaDon(
                invoice.invoice.getMaHD(),
                valueAt(row, 1),
                Integer.parseInt(valueAt(row, 3)),
                (BigDecimal) model.getValueAt(row, 4));
    }

    private void moHopThoaiChiTiet(final ChiTietHoaDon existing) {
        final InvoiceOption invoice = (InvoiceOption) cmbHoaDon.getSelectedItem();
        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "Hãy tạo hóa đơn trước khi thêm chi tiết.",
                    "Chưa có hóa đơn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (danhSachSanPham.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có sản phẩm trong hệ thống.",
                    "Chưa có sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isNew = existing == null;
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                isNew ? "Thêm sản phẩm vào hóa đơn" : "Sửa chi tiết hóa đơn",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JComboBox<ProductOption> cmbSanPham = new JComboBox<ProductOption>();
        for (SanPham product : danhSachSanPham) {
            cmbSanPham.addItem(new ProductOption(product));
        }
        JTextField txtSoLuong = new JTextField(22);
        JTextField txtDonGia = new JTextField(22);
        if (isNew) {
            txtSoLuong.setText("1");
            ProductOption firstProduct = (ProductOption) cmbSanPham.getSelectedItem();
            if (firstProduct != null && firstProduct.product.getDonGia() != null) {
                txtDonGia.setText(firstProduct.product.getDonGia().toPlainString());
            }
        } else {
            cmbSanPham.setSelectedItem(findProductOption(cmbSanPham, existing.getMaSP()));
            cmbSanPham.setEnabled(false);
            txtSoLuong.setText(String.valueOf(existing.getSoLuong()));
            txtDonGia.setText(existing.getDonGia().toPlainString());
        }
        cmbSanPham.addActionListener(e -> {
            if (isNew) {
                ProductOption selected = (ProductOption) cmbSanPham.getSelectedItem();
                if (selected != null && selected.product.getDonGia() != null) {
                    txtDonGia.setText(selected.product.getDonGia().toPlainString());
                }
            }
        });

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(Color.WHITE);
        fields.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));
        themTruong(fields, "Sản phẩm", cmbSanPham, 0);
        themTruong(fields, "Số lượng", txtSoLuong, 1);
        themTruong(fields, "Đơn giá (VNĐ)", txtDonGia, 2);
        JButton btnLuu = taoNut(isNew ? "Thêm vào hóa đơn" : "Lưu thay đổi", GREEN, Color.WHITE);
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
            int quantity;
            try {
                quantity = Integer.parseInt(txtSoLuong.getText().trim());
                if (quantity <= 0) {
                    baoLoi(dialog, "Số lượng phải lớn hơn 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                baoLoi(dialog, "Số lượng phải là số nguyên hợp lệ.");
                return;
            }
            BigDecimal price;
            try {
                price = new BigDecimal(txtDonGia.getText().trim());
                if (price.signum() < 0) {
                    baoLoi(dialog, "Đơn giá không được là số âm.");
                    return;
                }
            } catch (NumberFormatException ex) {
                baoLoi(dialog, "Đơn giá phải là số hợp lệ.");
                return;
            }
            ProductOption selectedProduct = (ProductOption) cmbSanPham.getSelectedItem();
            if (selectedProduct == null) {
                baoLoi(dialog, "Vui lòng chọn sản phẩm.");
                return;
            }
            ChiTietHoaDon detail = new ChiTietHoaDon(
                    invoice.invoice.getMaHD(),
                    isNew ? selectedProduct.product.getMaSP() : existing.getMaSP(),
                    quantity,
                    price);
            try {
                boolean success = isNew ? chiTietDAO.insert(detail) : chiTietDAO.update(detail);
                if (success) {
                    dialog.dispose();
                    loadChiTiet();
                    JOptionPane.showMessageDialog(this,
                            isNew ? "Đã thêm sản phẩm vào hóa đơn." : "Đã cập nhật chi tiết hóa đơn.",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    baoLoi(dialog, "Không tìm thấy chi tiết hóa đơn cần cập nhật.");
                }
            } catch (SQLException ex) {
                baoLoi(dialog, thongBaoLuuLoi(ex, isNew));
            }
        });
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private ProductOption findProductOption(JComboBox<ProductOption> productCombo, String maSP) {
        for (int i = 0; i < productCombo.getItemCount(); i++) {
            ProductOption option = productCombo.getItemAt(i);
            if (option.product.getMaSP().equals(maSP)) {
                return option;
            }
        }
        return null;
    }

    private void xoaChiTietTaiModelRow(int row) {
        ChiTietHoaDon detail = layChiTietTaiModelRow(row);
        if (detail == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this, "Xóa sản phẩm \"" + valueAt(row, 2) + "\" khỏi hóa đơn?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (chiTietDAO.delete(detail.getMaHD(), detail.getMaSP())) {
                loadChiTiet();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy chi tiết hóa đơn để xóa.",
                        "Xóa thất bại", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, thongBaoTonKho(ex),
                    "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaChiTietDaChon() {
        InvoiceOption invoice = (InvoiceOption) cmbHoaDon.getSelectedItem();
        if (invoice == null) {
            return;
        }
        List<String> productIds = new ArrayList<String>();
        for (int row = 0; row < model.getRowCount(); row++) {
            if (Boolean.TRUE.equals(model.getValueAt(row, 0))) {
                productIds.add(valueAt(row, 1));
            }
        }
        if (productIds.isEmpty()) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this, "Xóa " + productIds.size() + " dòng chi tiết đã chọn khỏi hóa đơn?",
                "Xác nhận xóa nhiều", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        List<String> failed = new ArrayList<String>();
        for (String maSP : productIds) {
            try {
                if (!chiTietDAO.delete(invoice.invoice.getMaHD(), maSP)) {
                    failed.add(maSP + ": không tìm thấy dòng dữ liệu");
                }
            } catch (SQLException ex) {
                failed.add(maSP + ": " + thongBaoTonKho(ex));
            }
        }
        loadChiTiet();
        if (!failed.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không thể xóa một số dòng:\n" + join(failed),
                    "Xóa chưa hoàn tất", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String thongBaoLuuLoi(SQLException ex, boolean isNew) {
        for (SQLException current = ex; current != null; current = current.getNextException()) {
            if (current.getErrorCode() == 51000) {
                return "Không đủ tồn kho cho số lượng yêu cầu. Kiểm tra tồn kho rồi thử lại.";
            }
            if (current.getErrorCode() == 2627 || current.getErrorCode() == 2601) {
                return "Sản phẩm này đã có trong hóa đơn. Hãy sửa số lượng ở dòng hiện có.";
            }
            if (current.getErrorCode() == 547) {
                return "Hóa đơn hoặc sản phẩm không còn tồn tại trong cơ sở dữ liệu.";
            }
        }
        return "Không thể " + (isNew ? "thêm" : "cập nhật")
                + " chi tiết hóa đơn: " + ex.getMessage();
    }

    private String thongBaoTonKho(SQLException ex) {
        for (SQLException current = ex; current != null; current = current.getNextException()) {
            if (current.getErrorCode() == 51000) {
                return "Không đủ tồn kho cho số lượng yêu cầu.";
            }
        }
        return "Không thể cập nhật chi tiết hóa đơn hoặc tồn kho: " + ex.getMessage();
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        sorter.setRowFilter(keyword.isEmpty()
                ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 1, 2));
    }

    private void capNhatTongTien() {
        BigDecimal total = BigDecimal.ZERO;
        for (int row = 0; row < model.getRowCount(); row++) {
            Object value = model.getValueAt(row, 5);
            if (value instanceof BigDecimal) {
                total = total.add((BigDecimal) value);
            }
        }
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        lblTongTien.setText("Tổng tiền: " + formatter.format(total) + " đ");
    }

    private void capNhatTrangThaiXoa() {
        boolean selected = false;
        for (int row = 0; row < model.getRowCount(); row++) {
            if (Boolean.TRUE.equals(model.getValueAt(row, 0))) {
                selected = true;
                break;
            }
        }
        btnXoaDaChon.setEnabled(selected);
        table.getTableHeader().repaint();
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

    private String valueAt(int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private String join(List<String> values) {
        StringBuilder text = new StringBuilder();
        for (String value : values) {
            if (text.length() > 0) {
                text.append("\n");
            }
            text.append(value);
        }
        return text.toString();
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
        JOptionPane.showMessageDialog(parent, message, "Thông tin chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
    }

    private static class InvoiceOption {
        private final HoaDon invoice;
        private final String customerName;

        private InvoiceOption(HoaDon invoice, String customerName) {
            this.invoice = invoice;
            this.customerName = customerName;
        }

        @Override
        public String toString() {
            return invoice.getMaHD() + "  |  " + invoice.getNgayLap()
                    + "  |  " + customerName;
        }
    }

    private static class ProductOption {
        private final SanPham product;

        private ProductOption(SanPham product) {
            this.product = product;
        }

        @Override
        public String toString() {
            return product.getMaSP() + " - " + product.getTenSP();
        }
    }

    private class SelectAllHeaderRenderer extends JCheckBox implements TableCellRenderer {
        private SelectAllHeaderRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            setBackground(new Color(248, 249, 251));
            setToolTipText("Chọn hoặc bỏ chọn tất cả dòng đang hiển thị");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable sourceTable, Object value, boolean selected, boolean focus, int row, int column) {
            boolean allSelected = sourceTable.getRowCount() > 0;
            for (int viewRow = 0; viewRow < sourceTable.getRowCount(); viewRow++) {
                int modelRow = sourceTable.convertRowIndexToModel(viewRow);
                if (!Boolean.TRUE.equals(ChiTietHoaDonPanel.this.model.getValueAt(modelRow, 0))) {
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

    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private final NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        private CurrencyRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            setText(value instanceof BigDecimal ? formatter.format(value) + " đ" : "");
        }
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

        private ActionCellEditor(final RowActionHandler handler) {
            panel.setBackground(Color.WHITE);
            styleButton(edit, BLUE);
            styleButton(delete, new Color(205, 72, 72));
            edit.setToolTipText("Sửa chi tiết");
            edit.getAccessibleContext().setAccessibleName("Sửa chi tiết hóa đơn");
            delete.setToolTipText("Xóa chi tiết");
            delete.getAccessibleContext().setAccessibleName("Xóa chi tiết hóa đơn");
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
}
