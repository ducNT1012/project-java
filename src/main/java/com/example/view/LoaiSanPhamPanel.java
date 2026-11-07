package com.example.view;

import com.example.dao.LoaiSanPhamDAO;
import com.example.dao.SanPhamDAO;
import com.example.model.LoaiSanPham;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LoaiSanPhamPanel extends JPanel {

    private static final Color GREEN = new Color(20, 139, 85);
    private static final Color BLUE = new Color(48, 112, 190);
    private static final Color TEXT = new Color(45, 55, 72);
    private static final Color MUTED = new Color(117, 128, 145);
    private static final Color BORDER = new Color(229, 233, 239);
    private static final Color BACKGROUND = new Color(246, 248, 251);

    private final JTextField txtTimKiem = new JTextField();
    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final LoaiSanPhamDAO dao = new LoaiSanPhamDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final JButton btnChiTiet;
    private final JButton btnXoaDaChon;

    public LoaiSanPhamPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        JLabel title = new JLabel("Loại sản phẩm");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Đánh dấu nhiều loại để xóa; Sửa và Xóa nằm ở cột thao tác");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.add(title);
        titleText.add(Box.createVerticalStrut(5));
        titleText.add(subtitle);

        JButton btnThem = taoNut("+  Thêm loại", GREEN, Color.WHITE);
        btnChiTiet = taoNut("Chi tiết", Color.WHITE, TEXT);
        btnXoaDaChon = taoNut("Xóa đã chọn", new Color(205, 72, 72), Color.WHITE);
        JButton btnLamMoi = taoNut("Làm mới", Color.WHITE, TEXT);
        btnChiTiet.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        btnLamMoi.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        btnChiTiet.setEnabled(false);
        btnXoaDaChon.setEnabled(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(btnThem);

        JPanel heading = new JPanel(new BorderLayout(12, 0));
        heading.setOpaque(false);
        heading.add(titleText, BorderLayout.WEST);
        heading.add(actions, BorderLayout.EAST);

        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimKiem.setToolTipText("Tìm theo mã, tên loại hoặc mô tả");
        txtTimKiem.setPreferredSize(new Dimension(275, 36));
        JButton btnTimKiem = new JButton(TableActionIcons.search());
        btnTimKiem.setToolTipText("Tìm kiếm loại sản phẩm");
        btnTimKiem.getAccessibleContext().setAccessibleName("Tìm kiếm loại sản phẩm");
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

        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 8));
        tableActions.setBackground(Color.WHITE);
        tableActions.add(btnChiTiet);
        tableActions.add(btnXoaDaChon);
        tableActions.add(btnLamMoi);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 8));
        searchPanel.add(searchBox, BorderLayout.WEST);
        searchPanel.add(tableActions, BorderLayout.EAST);

        model = new DefaultTableModel(
                new Object[] { "✓", "MÃ LOẠI", "TÊN LOẠI", "MÔ TẢ", "THAO TÁC" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 4;
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
        table.getColumnModel().getColumn(0).setPreferredWidth(42);
        table.getColumnModel().getColumn(0).setMinWidth(42);
        table.getColumnModel().getColumn(0).setMaxWidth(48);
        table.getColumnModel().getColumn(0).setHeaderRenderer(new SelectAllHeaderRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(230);
        table.getColumnModel().getColumn(3).setPreferredWidth(420);
        table.getColumnModel().getColumn(4).setPreferredWidth(86);
        table.getColumnModel().getColumn(4).setMinWidth(86);
        table.getColumnModel().getColumn(4).setMaxWidth(86);
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionCell());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionCellEditor(
                new RowActionHandler() {
                    @Override
                    public void edit(int row) {
                        moHopThoaiLoai(layLoaiTaiModelRow(row));
                    }

                    @Override
                    public void delete(int row) {
                        xoaLoaiTaiModelRow(row);
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

        JLabel listTitle = new JLabel("Danh sách loại sản phẩm");
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

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selected = table.getSelectedRow() >= 0;
                btnChiTiet.setEnabled(selected);
            }
        });
        model.addTableModelListener(e -> capNhatTrangThaiXoa());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    hienThiChiTiet(layLoaiDangChon());
                }
            }
        });

        btnThem.addActionListener(e -> moHopThoaiLoai(null));
        btnChiTiet.addActionListener(e -> hienThiChiTiet(layLoaiDangChon()));
        btnXoaDaChon.addActionListener(e -> xoaLoaiDaChon());
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            loadData();
            table.clearSelection();
        });

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
        btnTimKiem.addActionListener(e -> timKiem());
        txtTimKiem.addActionListener(e -> timKiem());
    }

    private static void styleTable(JTable table) {
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

    private void moHopThoaiLoai(final LoaiSanPham existing) {
        final boolean isNew = existing == null;
        final JDialog dialog = taoDialog(isNew ? "Thêm loại sản phẩm" : "Sửa loại sản phẩm");
        final JTextField ma = taoInput();
        final JTextField ten = taoInput();
        final JTextField moTa = taoInput();
        if (!isNew) {
            ma.setText(existing.getMaLoai());
            ma.setEditable(false);
            ten.setText(existing.getTenLoai());
            moTa.setText(existing.getMoTa());
        }

        JPanel fields = taoFormFields();
        themTruong(fields, "Mã loại", ma, 0);
        themTruong(fields, "Tên loại", ten, 1);
        themTruong(fields, "Mô tả", moTa, 2);

        JButton save = taoNut(isNew ? "Thêm loại" : "Lưu thay đổi", GREEN, Color.WHITE);
        JButton cancel = taoNut("Hủy", Color.WHITE, TEXT);
        cancel.setBorder(BorderFactory.createLineBorder(BORDER));
        JDialog finalDialog = dialog;
        dialog.setContentPane(taoDialogContent(fields, taoNutPanel(cancel, save)));

        cancel.addActionListener(e -> finalDialog.dispose());
        save.addActionListener(e -> {
            String maValue = ma.getText().trim();
            String tenValue = ten.getText().trim();
            if (maValue.isEmpty() || tenValue.isEmpty()) {
                baoLoi(finalDialog, "Mã loại và tên loại là bắt buộc.");
                return;
            }
            LoaiSanPham category = new LoaiSanPham(maValue, tenValue, moTa.getText().trim());
            boolean success = isNew ? dao.insert(category) : dao.update(category);
            if (success) {
                finalDialog.dispose();
                loadData();
                JOptionPane.showMessageDialog(this,
                        isNew ? "Thêm loại sản phẩm thành công." : "Cập nhật loại sản phẩm thành công.",
                        "Loại sản phẩm", JOptionPane.INFORMATION_MESSAGE);
            } else {
                baoLoi(finalDialog, "Không thể lưu loại sản phẩm. Hãy kiểm tra mã loại và kết nối cơ sở dữ liệu.");
            }
        });
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void hienThiChiTiet(LoaiSanPham category) {
        if (category == null) {
            return;
        }
        JPanel details = taoFormFields();
        themChiTiet(details, "Mã loại", category.getMaLoai(), 0);
        themChiTiet(details, "Tên loại", category.getTenLoai(), 1);
        themChiTiet(details, "Mô tả", category.getMoTa(), 2);

        JButton close = taoNut("Đóng", BLUE, Color.WHITE);
        JDialog dialog = taoDialog("Chi tiết loại sản phẩm");
        dialog.setContentPane(taoDialogContent(details, taoNutPanel(close)));
        close.addActionListener(e -> dialog.dispose());
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void xoaLoaiTaiModelRow(int row) {
        LoaiSanPham category = layLoaiTaiModelRow(row);
        if (category == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this, "Bạn có chắc muốn xóa loại sản phẩm \"" + category.getTenLoai() + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String error = xoaMotLoai(category);
        if (error == null) {
            loadData();
            table.clearSelection();
            JOptionPane.showMessageDialog(this, "Xóa loại sản phẩm thành công.");
        } else {
            JOptionPane.showMessageDialog(this, error, "Xóa thất bại",
                    error.startsWith("Không thể kiểm tra") ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE);
        }
    }

    private void xoaLoaiDaChon() {
        List<LoaiSanPham> categories = new ArrayList<LoaiSanPham>();
        for (int row = 0; row < model.getRowCount(); row++) {
            if (Boolean.TRUE.equals(model.getValueAt(row, 0))) {
                categories.add(layLoaiTaiModelRow(row));
            }
        }
        if (categories.isEmpty()) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this, "Xóa " + categories.size() + " loại sản phẩm đã chọn?",
                "Xác nhận xóa nhiều", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        List<String> failed = new ArrayList<String>();
        for (LoaiSanPham category : categories) {
            String error = xoaMotLoai(category);
            if (error != null) {
                failed.add(category.getMaLoai() + " (" + error + ")");
            }
        }
        loadData();
        if (failed.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đã xóa " + categories.size() + " loại sản phẩm.");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đã xóa " + (categories.size() - failed.size()) + " loại. Các mục sau chưa xóa được:\n"
                            + join(failed),
                    "Một số mục chưa xóa được", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String xoaMotLoai(LoaiSanPham category) {
        try {
            if (sanPhamDAO.coSanPhamTheoMaLoai(category.getMaLoai())) {
                return "vẫn còn sản phẩm đang sử dụng loại này";
            }
        } catch (SQLException ex) {
            return "Không thể kiểm tra ràng buộc sản phẩm: " + ex.getMessage();
        }
        if (!dao.delete(category.getMaLoai())) {
            return "Không thể xóa do ràng buộc dữ liệu hoặc lỗi cơ sở dữ liệu";
        }
        return null;
    }

    private LoaiSanPham layLoaiDangChon() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            return null;
        }
        return layLoaiTaiModelRow(table.convertRowIndexToModel(selected));
    }

    private LoaiSanPham layLoaiTaiModelRow(int row) {
        if (row < 0 || row >= model.getRowCount()) {
            return null;
        }
        return new LoaiSanPham(valueAt(row, 1), valueAt(row, 2), valueAt(row, 3));
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            for (LoaiSanPham category : dao.getAll()) {
                model.addRow(new Object[] {
                        Boolean.FALSE, category.getMaLoai(), category.getTenLoai(), category.getMoTa(), ""
                });
            }
        } catch (SQLException ex) {
            model.setRowCount(0);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Không thể tải danh sách loại sản phẩm từ cơ sở dữ liệu.\n"
                            + "Kiểm tra SQL Server và cấu hình kết nối.\nChi tiết: " + ex.getMessage(),
                    "Lỗi kết nối backend", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatTrangThaiXoa() {
        capNhatHeaderChonTatCa();
        for (int row = 0; row < model.getRowCount(); row++) {
            if (Boolean.TRUE.equals(model.getValueAt(row, 0))) {
                btnXoaDaChon.setEnabled(true);
                return;
            }
        }
        btnXoaDaChon.setEnabled(false);
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        sorter.setRowFilter(keyword.isEmpty()
                ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 1, 2, 3));
        capNhatHeaderChonTatCa();
    }

    private void chonTatCaHienThi() {
        boolean allSelected = table.getRowCount() > 0;
        for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            if (!Boolean.TRUE.equals(LoaiSanPhamPanel.this.model.getValueAt(modelRow, 0))) {
                allSelected = false;
                break;
            }
        }
        boolean select = !allSelected;
        for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            model.setValueAt(Boolean.valueOf(select), modelRow, 0);
        }
        capNhatHeaderChonTatCa();
    }

    private void capNhatHeaderChonTatCa() {
        table.getTableHeader().repaint();
    }

    private JDialog taoDialog(String title) {
        return new JDialog(SwingUtilities.getWindowAncestor(this), title, Dialog.ModalityType.APPLICATION_MODAL);
    }

    private static JPanel taoFormFields() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 22, 14, 22));
        return panel;
    }

    private static JPanel taoDialogContent(JPanel fields, JPanel buttons) {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.add(fields, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        return content;
    }

    private static JPanel taoNutPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 12, 6, 12));
        for (JButton button : buttons) {
            panel.add(button);
        }
        return panel;
    }

    private static JTextField taoInput() {
        JTextField input = new JTextField();
        input.setFont(new Font("Arial", Font.PLAIN, 13));
        input.setPreferredSize(new Dimension(270, 36));
        return input;
    }

    private static void themTruong(JPanel panel, String label, JTextField input, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 10);
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        panel.add(taoLabel(label), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(input, gbc);
    }

    private static void themChiTiet(JPanel panel, String label, String value, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 16);
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        panel.add(taoLabel(label), gbc);
        JLabel detail = new JLabel(value == null || value.trim().isEmpty() ? "—" : value);
        detail.setFont(new Font("Arial", Font.PLAIN, 14));
        detail.setForeground(TEXT);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(detail, gbc);
    }

    private static JLabel taoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(MUTED);
        return label;
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

    private String valueAt(int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private void baoLoi(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thông tin chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
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

    private interface RowActionHandler {
        void edit(int modelRow);

        void delete(int modelRow);
    }

    private class SelectAllHeaderRenderer extends JCheckBox implements TableCellRenderer {
        private SelectAllHeaderRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            setBackground(new Color(248, 249, 251));
            setToolTipText("Chọn hoặc bỏ chọn tất cả loại đang hiển thị");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            boolean allSelected = table.getRowCount() > 0;
            for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                if (!Boolean.TRUE.equals(LoaiSanPhamPanel.this.model.getValueAt(modelRow, 0))) {
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
            add(actionLabel(TableActionIcons.edit(), "Sửa", BLUE));
            add(actionLabel(TableActionIcons.delete(), "Xóa", new Color(205, 72, 72)));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            setBackground(selected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }

        private static JLabel actionLabel(Icon icon, String tooltip, Color color) {
            JLabel label = new JLabel(icon);
            label.setToolTipText(tooltip);
            label.setForeground(color);
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
            edit.setToolTipText("Sửa loại sản phẩm");
            edit.getAccessibleContext().setAccessibleName("Sửa loại sản phẩm");
            delete.setToolTipText("Xóa loại sản phẩm");
            delete.getAccessibleContext().setAccessibleName("Xóa loại sản phẩm");
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
