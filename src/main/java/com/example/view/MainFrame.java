
package com.example.view;

import javax.swing.*;

import com.example.dao.TaiKhoanDAO;

import java.awt.*;

public class MainFrame extends JFrame {

        private String hoTen;

        private JPanel menuPanel;
        private JPanel contentPanel;

        private boolean menuThuGon = false;

        private JButton btnToggle;
        private JButton[] cacNutMenu;
        private JLabel[] nhomMenu;
        private JButton nutMenuDangChon;

        private final Color xanhDam = new Color(23, 43, 77);
        private final Color xanhHover = new Color(40, 65, 110);
        private final Color xanh = new Color(40, 120, 210);
        private final Color nen = new Color(245, 247, 251);
        private final Color chu = new Color(40, 50, 70);

        public MainFrame(String tenDangNhap) {
                TaiKhoanDAO dao = new TaiKhoanDAO();
                this.hoTen = dao.getHoTen(tenDangNhap);

                setTitle("Sales Manager - Quản lý bán hàng");
                setSize(1200, 750);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
                setMinimumSize(new Dimension(1000, 650));

                JPanel mainPanel = new JPanel(new BorderLayout());

                // ================= MENU BÊN TRÁI =================

                menuPanel = new JPanel(new BorderLayout());
                menuPanel.setPreferredSize(new Dimension(235, 750));
                menuPanel.setBackground(xanhDam);

                // Logo và nút thu gọn
                JPanel logoPanel = new JPanel(new BorderLayout());
                logoPanel.setBackground(xanhDam);
                logoPanel.setBorder(
                                BorderFactory.createEmptyBorder(15, 15, 15, 10));

                JLabel lblLogo = new JLabel("SALES MANAGER");
                lblLogo.setForeground(Color.WHITE);
                lblLogo.setFont(new Font("Arial", Font.BOLD, 19));

                btnToggle = new JButton(new SidebarToggleIcon(false));
                btnToggle.setForeground(Color.WHITE);
                btnToggle.setBackground(xanhDam);
                btnToggle.setBorderPainted(false);
                btnToggle.setFocusPainted(false);
                btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnToggle.setToolTipText("Thu gọn thanh điều hướng");
                btnToggle.setPreferredSize(new Dimension(32, 32));

                logoPanel.add(lblLogo, BorderLayout.CENTER);
                logoPanel.add(btnToggle, BorderLayout.EAST);

                // Các nút chức năng
                JPanel danhSachMenu = new JPanel();
                danhSachMenu.setBackground(xanhDam);
                danhSachMenu.setLayout(
                                new BoxLayout(danhSachMenu, BoxLayout.Y_AXIS));

                JButton btnTongQuan = taoNut("Tổng quan", 0);
                JButton btnSanPham = taoNut("Sản phẩm", 1);
                JButton btnLoaiSP = taoNut("Loại sản phẩm", 2);
                JButton btnKhachHang = taoNut("Khách hàng", 3);
                JButton btnHoaDon = taoNut("Hóa đơn", 4);
                JButton btnChiTiet = taoNut("Chi tiết hóa đơn", 5);

                cacNutMenu = new JButton[] {
                                btnTongQuan,
                                btnSanPham,
                                btnLoaiSP,
                                btnKhachHang,
                                btnHoaDon,
                                btnChiTiet
                };

                JLabel lblTongQuan = taoNhomMenu("TỔNG QUAN");
                JLabel lblBanHang = taoNhomMenu("BÁN HÀNG");
                JLabel lblHangHoa = taoNhomMenu("HÀNG HÓA");
                JLabel lblKhachHang = taoNhomMenu("KHÁCH HÀNG");
                nhomMenu = new JLabel[] { lblTongQuan, lblBanHang, lblHangHoa, lblKhachHang };

                danhSachMenu.add(lblTongQuan);
                danhSachMenu.add(btnTongQuan);
                danhSachMenu.add(lblBanHang);
                danhSachMenu.add(btnHoaDon);
                danhSachMenu.add(btnChiTiet);
                danhSachMenu.add(lblHangHoa);
                danhSachMenu.add(btnSanPham);
                danhSachMenu.add(btnLoaiSP);
                danhSachMenu.add(lblKhachHang);
                danhSachMenu.add(btnKhachHang);
                setMenuActive(btnTongQuan);

                menuPanel.add(logoPanel, BorderLayout.NORTH);
                menuPanel.add(danhSachMenu, BorderLayout.CENTER);

                // ================= KHU VỰC BÊN PHẢI =================

                JPanel rightPanel = new JPanel(new BorderLayout());
                rightPanel.setBackground(nen);

                // Thanh trên cùng
                JPanel topPanel = new JPanel(new BorderLayout());
                topPanel.setBackground(Color.WHITE);
                topPanel.setPreferredSize(new Dimension(900, 70));
                topPanel.setBorder(
                                BorderFactory.createEmptyBorder(10, 25, 10, 25));

                JLabel lblPage = new JLabel("TRANG QUẢN TRỊ");
                lblPage.setFont(new Font("Arial", Font.BOLD, 17));
                lblPage.setForeground(chu);

                // Khu vực tài khoản
                JButton btnUser = new JButton(new UserIcon());
                btnUser.setPreferredSize(new Dimension(45, 45));
                btnUser.setBackground(Color.WHITE);
                btnUser.setFocusPainted(false);
                btnUser.setBorderPainted(false);
                btnUser.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnUser.setToolTipText("Tài khoản Admin");

                // Menu tài khoản
                JPopupMenu menuTaiKhoan = new JPopupMenu();

                JMenuItem itemThongTin = new JMenuItem("Thông tin tài khoản");
                JMenuItem itemDangXuat = new JMenuItem("Đăng xuất");

                menuTaiKhoan.add(itemThongTin);
                menuTaiKhoan.addSeparator();
                menuTaiKhoan.add(itemDangXuat);

                btnUser.addActionListener(e -> {
                        menuTaiKhoan.show(
                                        btnUser,
                                        0,
                                        btnUser.getHeight());
                });

                // Hiển thị thông tin Admin
                itemThongTin.addActionListener(e -> {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "THÔNG TIN TÀI KHOẢN\n\n"
                                                        + "Họ tên: " + hoTen + "\n"
                                                        + "Tên đăng nhập: " + tenDangNhap + "\n"
                                                        + "Vai trò: Quản trị viên\n"
                                                        + "Trạng thái: Đang hoạt động",
                                        "Thông tin tài khoản",
                                        JOptionPane.INFORMATION_MESSAGE);
                });

                // Đăng xuất
                itemDangXuat.addActionListener(e -> dangXuat());

                JPanel userPanel = new JPanel(new FlowLayout(
                                FlowLayout.RIGHT, 5, 0));

                userPanel.setBackground(Color.WHITE);

                JLabel lblAdmin = new JLabel(hoTen);
                lblAdmin.setFont(new Font("Arial", Font.BOLD, 13));
                lblAdmin.setForeground(chu);

                userPanel.add(lblAdmin);
                userPanel.add(btnUser);

                topPanel.add(lblPage, BorderLayout.WEST);
                topPanel.add(userPanel, BorderLayout.EAST);

                // Khu vực nội dung
                contentPanel = new JPanel(new BorderLayout());
                contentPanel.setBackground(nen);
                contentPanel.setBorder(
                                BorderFactory.createEmptyBorder(30, 30, 30, 30));

                hienThiTongQuan();

                rightPanel.add(topPanel, BorderLayout.NORTH);
                rightPanel.add(contentPanel, BorderLayout.CENTER);

                mainPanel.add(menuPanel, BorderLayout.WEST);
                mainPanel.add(rightPanel, BorderLayout.CENTER);

                add(mainPanel);

                // ================= SỰ KIỆN MENU =================

                btnToggle.addActionListener(e -> thuGonMenu());

                btnTongQuan.addActionListener(e -> {
                    setMenuActive(btnTongQuan);
                    hienThiTongQuan();
                });

                btnSanPham.addActionListener(e -> {
                    setMenuActive(btnSanPham);
                    contentPanel.removeAll();
                    contentPanel.add(new SanPhamPanel(), BorderLayout.CENTER);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                });

                btnLoaiSP.addActionListener(e -> {
                        setMenuActive(btnLoaiSP);

                        contentPanel.removeAll();

                        contentPanel.add(
                                        new LoaiSanPhamPanel(),
                                        BorderLayout.CENTER);

                        contentPanel.revalidate();
                        contentPanel.repaint();
                });

                btnKhachHang.addActionListener(e -> {
                    setMenuActive(btnKhachHang);
                    contentPanel.removeAll();
                    contentPanel.add(new KhachHangPanel(), BorderLayout.CENTER);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                });

                btnHoaDon.addActionListener(e -> {
                    setMenuActive(btnHoaDon);
                    contentPanel.removeAll();
                    contentPanel.add(new HoaDonPanel(), BorderLayout.CENTER);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                });

                btnChiTiet.addActionListener(e -> {
                    setMenuActive(btnChiTiet);
                    contentPanel.removeAll();
                    contentPanel.add(new ChiTietHoaDonPanel(), BorderLayout.CENTER);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                });
        }

        // ================= THU GỌN MENU =================

        private void thuGonMenu() {

                menuThuGon = !menuThuGon;

                if (menuThuGon) {

                        menuPanel.setPreferredSize(
                                        new Dimension(75, 750));

                        btnToggle.setIcon(new SidebarToggleIcon(true));
                        btnToggle.setToolTipText("Mở rộng thanh điều hướng");

                        for (int i = 0; i < cacNutMenu.length; i++) {
                                cacNutMenu[i].setText("");
                                cacNutMenu[i].setHorizontalAlignment(
                                                SwingConstants.CENTER);
                                cacNutMenu[i].setBorder(
                                                BorderFactory.createEmptyBorder(15, 0, 15, 0));
                                cacNutMenu[i].setToolTipText(
                                                new String[] { "Tổng quan", "Sản phẩm", "Loại sản phẩm",
                                                                "Khách hàng", "Hóa đơn", "Chi tiết hóa đơn" }[i]);
                        }
                        for (JLabel label : nhomMenu) {
                                label.setVisible(false);
                        }

                } else {

                        menuPanel.setPreferredSize(
                                        new Dimension(235, 750));

                        btnToggle.setIcon(new SidebarToggleIcon(false));
                        btnToggle.setToolTipText("Thu gọn thanh điều hướng");

                        String[] tenMenu = {
                                        "Tổng quan",
                                        "Sản phẩm",
                                        "Loại sản phẩm",
                                        "Khách hàng",
                                        "Hóa đơn",
                                        "Chi tiết hóa đơn"
                        };

                        for (int i = 0; i < cacNutMenu.length; i++) {
                                cacNutMenu[i].setText(tenMenu[i]);
                                cacNutMenu[i].setHorizontalAlignment(
                                                SwingConstants.LEFT);
                                cacNutMenu[i].setBorder(
                                                BorderFactory.createEmptyBorder(15, 20, 15, 10));
                                cacNutMenu[i].setToolTipText(null);
                        }
                        for (JLabel label : nhomMenu) {
                                label.setVisible(true);
                        }
                }

                for (JButton button : cacNutMenu) {
                        button.setBackground(button == nutMenuDangChon ? xanhHover : xanhDam);
                }
                menuPanel.revalidate();
                menuPanel.repaint();
                revalidate();
        }

        // ================= TẠO NÚT MENU =================

        private JButton taoNut(String ten, int iconType) {

                JButton btn = new JButton(ten);

                btn.setIcon(new SidebarIcon(iconType, btn));
                btn.setIconTextGap(14);
                btn.setFont(new Font("Arial", Font.BOLD, 14));
                btn.setForeground(Color.WHITE);
                btn.setBackground(xanhDam);
                btn.setHorizontalAlignment(SwingConstants.LEFT);
                btn.setBorder(
                                BorderFactory.createEmptyBorder(15, 20, 15, 10));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setMaximumSize(new Dimension(235, 55));
                btn.setAlignmentX(Component.LEFT_ALIGNMENT);

                btn.addMouseListener(new java.awt.event.MouseAdapter() {

                        public void mouseEntered(
                                        java.awt.event.MouseEvent e) {
                                if (btn != nutMenuDangChon) {
                                        btn.setBackground(xanhHover);
                                }
                        }

                        public void mouseExited(
                                        java.awt.event.MouseEvent e) {
                                btn.setBackground(btn == nutMenuDangChon ? xanhHover : xanhDam);
                        }
                });

                return btn;
        }

        private class SidebarIcon implements Icon {
                private final int type;
                private final JButton owner;

                private SidebarIcon(int type, JButton owner) {
                        this.type = type;
                        this.owner = owner;
                }

                @Override
                public int getIconWidth() {
                        return 20;
                }

                @Override
                public int getIconHeight() {
                        return 20;
                }

                @Override
                public void paintIcon(Component component, Graphics graphics, int x, int y) {
                        Graphics2D g2 = (Graphics2D) graphics.create();
                        g2.translate(x, y);
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(owner == nutMenuDangChon
                                        ? new Color(111, 213, 164)
                                        : new Color(221, 231, 245));
                        g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                        switch (type) {
                                case 0:
                                        g2.drawRoundRect(2, 2, 7, 7, 2, 2);
                                        g2.drawRoundRect(12, 2, 7, 7, 2, 2);
                                        g2.drawRoundRect(2, 12, 7, 7, 2, 2);
                                        g2.drawRoundRect(12, 12, 7, 7, 2, 2);
                                        break;
                                case 1:
                                        g2.drawRoundRect(2, 3, 16, 15, 3, 3);
                                        g2.drawLine(2, 8, 18, 8);
                                        g2.drawLine(7, 8, 7, 18);
                                        g2.drawLine(12, 12, 15, 12);
                                        break;
                                case 2:
                                        g2.drawRoundRect(3, 3, 14, 14, 3, 3);
                                        g2.drawLine(6, 7, 14, 7);
                                        g2.drawLine(6, 10, 14, 10);
                                        g2.drawLine(6, 13, 11, 13);
                                        break;
                                case 3:
                                        g2.drawOval(7, 2, 7, 7);
                                        g2.drawArc(3, 11, 15, 10, 0, 180);
                                        break;
                                case 4:
                                        g2.drawRoundRect(4, 2, 13, 17, 2, 2);
                                        g2.drawLine(7, 7, 14, 7);
                                        g2.drawLine(7, 10, 14, 10);
                                        g2.drawLine(7, 13, 12, 13);
                                        break;
                                default:
                                        g2.drawRoundRect(2, 3, 16, 14, 2, 2);
                                        g2.drawLine(6, 7, 14, 7);
                                        g2.drawLine(6, 10, 14, 10);
                                        g2.drawLine(6, 13, 12, 13);
                                        g2.fillOval(3, 6, 1, 1);
                                        g2.fillOval(3, 9, 1, 1);
                                        g2.fillOval(3, 12, 1, 1);
                                        break;
                        }
                        g2.dispose();
                }
        }

        private static class SidebarToggleIcon implements Icon {
                private final boolean collapsed;

                private SidebarToggleIcon(boolean collapsed) {
                        this.collapsed = collapsed;
                }

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
                        Graphics2D g2 = (Graphics2D) graphics.create();
                        g2.translate(x, y);
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(225, 235, 248));
                        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                        g2.drawRoundRect(1, 2, 16, 14, 3, 3);
                        g2.drawLine(6, 3, 6, 15);
                        if (collapsed) {
                                g2.drawLine(9, 7, 12, 9);
                                g2.drawLine(12, 9, 9, 11);
                        } else {
                                g2.drawLine(13, 7, 10, 9);
                                g2.drawLine(10, 9, 13, 11);
                        }
                        g2.dispose();
                }
        }

        private JLabel taoNhomMenu(String ten) {
                JLabel label = new JLabel(ten);
                label.setForeground(new Color(154, 171, 197));
                label.setFont(new Font("Arial", Font.BOLD, 10));
                label.setBorder(BorderFactory.createEmptyBorder(15, 20, 6, 8));
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                label.setMaximumSize(new Dimension(235, 34));
                return label;
        }

        private void setMenuActive(JButton button) {
                nutMenuDangChon = button;
                if (cacNutMenu == null) {
                        return;
                }
                for (JButton item : cacNutMenu) {
                        item.setBackground(item == button ? xanhHover : xanhDam);
                }
        }

        // ================= GIAO DIỆN TỔNG QUAN =================

        private void hienThiTongQuan() {

                contentPanel.removeAll();

                JPanel header = new JPanel();
                header.setBackground(nen);
                header.setLayout(
                                new BoxLayout(header, BoxLayout.Y_AXIS));

                JLabel lblTitle = new JLabel("Tổng quan hệ thống");
                lblTitle.setFont(new Font("Arial", Font.BOLD, 27));
                lblTitle.setForeground(chu);

                JLabel lblMoTa = new JLabel(
                                "Chào mừng Admin đến với hệ thống quản lý bán hàng.");

                lblMoTa.setFont(new Font("Arial", Font.PLAIN, 14));
                lblMoTa.setForeground(Color.GRAY);

                header.add(lblTitle);
                header.add(Box.createVerticalStrut(10));
                header.add(lblMoTa);

                JPanel cards = new JPanel(new GridLayout(2, 2, 20, 20));
                cards.setBackground(nen);

                cards.add(taoThe("SẢN PHẨM",
                                "Quản lý thông tin sản phẩm", "01"));

                cards.add(taoThe("LOẠI SẢN PHẨM",
                                "Phân loại hàng hóa", "02"));

                cards.add(taoThe("KHÁCH HÀNG",
                                "Quản lý thông tin khách hàng", "03"));

                cards.add(taoThe("HÓA ĐƠN",
                                "Theo dõi hoạt động bán hàng", "04"));

                contentPanel.add(header, BorderLayout.NORTH);
                contentPanel.add(cards, BorderLayout.CENTER);

                contentPanel.revalidate();
                contentPanel.repaint();
        }

        // ================= TẠO THẺ =================

        private JPanel taoThe(
                        String tieuDe, String moTa, String ma) {

                JPanel card = new JPanel(new BorderLayout());
                card.setBackground(Color.WHITE);

                card.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(
                                                new Color(230, 233, 240)),
                                BorderFactory.createEmptyBorder(25, 25, 25, 25)));

                JLabel lblMa = new JLabel(ma);
                lblMa.setFont(new Font("Arial", Font.BOLD, 28));
                lblMa.setForeground(xanh);

                JLabel lblTieuDe = new JLabel(tieuDe);
                lblTieuDe.setFont(new Font("Arial", Font.BOLD, 17));
                lblTieuDe.setForeground(chu);

                JLabel lblMoTa = new JLabel(moTa);
                lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
                lblMoTa.setForeground(Color.GRAY);

                JPanel textPanel = new JPanel();
                textPanel.setBackground(Color.WHITE);
                textPanel.setLayout(
                                new BoxLayout(textPanel, BoxLayout.Y_AXIS));

                textPanel.add(lblTieuDe);
                textPanel.add(Box.createVerticalStrut(12));
                textPanel.add(lblMoTa);

                card.add(lblMa, BorderLayout.WEST);
                card.add(textPanel, BorderLayout.CENTER);

                return card;
        }

        // ================= ĐĂNG XUẤT =================

        private void dangXuat() {

                int chon = JOptionPane.showConfirmDialog(
                                this,
                                "Bạn có chắc chắn muốn đăng xuất?",
                                "Xác nhận đăng xuất",
                                JOptionPane.YES_NO_OPTION);

                if (chon == JOptionPane.YES_OPTION) {
                        new LoginFrame().setVisible(true);
                        dispose();
                }
        }

        private void thongBao(String ten) {

                JOptionPane.showMessageDialog(
                                this,
                                ten + " sẽ được xây dựng ở bước tiếp theo!");
        }

        // ================= ICON NGƯỜI DÙNG =================

        private static class UserIcon implements Icon {

                public int getIconWidth() {
                        return 28;
                }

                public int getIconHeight() {
                        return 28;
                }

                public void paintIcon(
                                Component c, Graphics g, int x, int y) {

                        Graphics2D g2 = (Graphics2D) g.create();

                        g2.setRenderingHint(
                                        RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);

                        g2.setColor(new Color(40, 120, 210));

                        // Đầu người
                        g2.fillOval(x + 9, y + 2, 10, 10);

                        // Vai và thân người
                        g2.fillRoundRect(
                                        x + 4, y + 14, 20, 13, 10, 10);

                        g2.dispose();
                }
        }
}
