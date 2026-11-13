package com.example.view;

import com.example.dao.TaiKhoanDAO;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

public class LoginFrame extends JFrame {

    private static final Color BLUE = new Color(43, 119, 205);
    private static final Color TEXT = new Color(39, 51, 70);
    private static final Color MUTED = new Color(119, 132, 150);
    private static final Color BORDER = new Color(220, 226, 234);
    private static final Color ERROR = new Color(198, 55, 55);

    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;
    private JPanel taiKhoanInput;
    private JPanel matKhauInput;
    private JLabel lblLoiTaiKhoan;
    private JLabel lblLoiMatKhau;
    private JLabel lblThongBao;
    private JButton btnDangNhap;
    private boolean dangChuyenTrang;

    public LoginFrame() {
        setTitle("Đăng nhập - Xcenter");
        setSize(900, 590);
        setMinimumSize(new Dimension(820, 540));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel brandPanel = taoBrandPanel();
        brandPanel.setPreferredSize(new Dimension(355, 590));
        root.add(brandPanel, BorderLayout.WEST);
        root.add(taoFormPanel(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel taoBrandPanel() {
        JPanel brandPanel = new GradientPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(BorderFactory.createEmptyBorder(42, 38, 34, 34));

        JLabel lblLogo = new JLabel("▣  XCENTER");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badge.setOpaque(false);
        JLabel lblBadge = new JLabel("  CỬA HÀNG ĐIỆN THOẠI  ");
        lblBadge.setFont(new Font("Arial", Font.BOLD, 10));
        lblBadge.setForeground(new Color(220, 255, 238));
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(35, 155, 102));
        lblBadge.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
        badge.add(lblBadge);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHeadline = new JLabel(
                "<html>Quản lý cửa hàng<br><font color='#69b5ff'>điện thoại Xcenter.</font></html>");
        lblHeadline.setFont(new Font("Arial", Font.BOLD, 30));
        lblHeadline.setForeground(Color.WHITE);
        lblHeadline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDescription = new JLabel(
                "<html>Theo dõi sản phẩm, khách hàng, nhân viên<br>"
                        + "và hóa đơn trên một hệ thống tiện lợi.</html>");
        lblDescription.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDescription.setForeground(new Color(211, 221, 237));
        lblDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFooter = new JLabel("QUẢN LÝ CỬA HÀNG ĐIỆN THOẠI");
        lblFooter.setFont(new Font("Arial", Font.BOLD, 10));
        lblFooter.setForeground(new Color(166, 184, 209));
        lblFooter.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandPanel.add(lblLogo);
        brandPanel.add(Box.createVerticalStrut(62));
        brandPanel.add(badge);
        brandPanel.add(Box.createVerticalStrut(22));
        brandPanel.add(lblHeadline);
        brandPanel.add(Box.createVerticalStrut(17));
        brandPanel.add(lblDescription);
        brandPanel.add(Box.createVerticalGlue());
        brandPanel.add(lblFooter);
        return brandPanel;
    }

    private JPanel taoFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridBagLayout());

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(350, 390));

        JLabel lblLogin = new JLabel("Chào mừng trở lại");
        lblLogin.setFont(new Font("Arial", Font.BOLD, 26));
        lblLogin.setForeground(TEXT);
        lblLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Đăng nhập để tiếp tục vào hệ thống");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSub.setForeground(MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = taoLabel("Tên đăng nhập");
        txtTaiKhoan = new JTextField();
        styleTextField(txtTaiKhoan);
        taiKhoanInput = taoInputContainer(txtTaiKhoan);

        lblLoiTaiKhoan = taoErrorLabel("Vui lòng nhập tên đăng nhập.");

        JLabel lblPass = taoLabel("Mật khẩu");
        txtMatKhau = new JPasswordField();
        styleTextField(txtMatKhau);
        matKhauInput = taoInputContainer(txtMatKhau);

        final char echoChar = txtMatKhau.getEchoChar();
        final boolean[] passwordVisible = new boolean[] { false };
        JButton btnTogglePassword = new JButton(new PasswordEyeIcon(false));
        btnTogglePassword.setPreferredSize(new Dimension(44, 42));
        btnTogglePassword.setMinimumSize(new Dimension(44, 42));
        btnTogglePassword.setMaximumSize(new Dimension(44, 42));
        btnTogglePassword.setBorderPainted(false);
        btnTogglePassword.setFocusPainted(false);
        btnTogglePassword.setContentAreaFilled(false);
        btnTogglePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTogglePassword.setToolTipText("Hiện mật khẩu");
        btnTogglePassword.getAccessibleContext().setAccessibleName("Hiện hoặc ẩn mật khẩu");
        btnTogglePassword.addActionListener(e -> {
            passwordVisible[0] = !passwordVisible[0];
            txtMatKhau.setEchoChar(passwordVisible[0] ? (char) 0 : echoChar);
            btnTogglePassword.setIcon(new PasswordEyeIcon(passwordVisible[0]));
            btnTogglePassword.setToolTipText(passwordVisible[0] ? "Ẩn mật khẩu" : "Hiện mật khẩu");
        });
        matKhauInput.add(btnTogglePassword, BorderLayout.EAST);

        lblLoiMatKhau = taoErrorLabel("Vui lòng nhập mật khẩu.");

        btnDangNhap = new JButton("ĐĂNG NHẬP");
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 13));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBackground(BLUE);
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setBorderPainted(false);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.setPreferredSize(new Dimension(350, 46));
        btnDangNhap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnDangNhap.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDangNhap.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        btnDangNhap.setRolloverEnabled(true);
        btnDangNhap.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btnDangNhap.isEnabled()) {
                    btnDangNhap.setBackground(new Color(34, 101, 182));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnDangNhap.setBackground(btnDangNhap.isEnabled()
                        ? BLUE : new Color(145, 169, 197));
            }
        });

        lblThongBao = new JLabel(" ");
        lblThongBao.setFont(new Font("Arial", Font.PLAIN, 12));
        lblThongBao.setForeground(new Color(28, 132, 82));
        lblThongBao.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblThongBao.setPreferredSize(new Dimension(350, 20));
        lblThongBao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        form.add(lblLogin);
        form.add(Box.createVerticalStrut(8));
        form.add(lblSub);
        form.add(Box.createVerticalStrut(32));
        form.add(lblUser);
        form.add(Box.createVerticalStrut(9));
        form.add(taiKhoanInput);
        form.add(lblLoiTaiKhoan);
        form.add(Box.createVerticalStrut(13));
        form.add(lblPass);
        form.add(Box.createVerticalStrut(9));
        form.add(matKhauInput);
        form.add(lblLoiMatKhau);
        form.add(Box.createVerticalStrut(20));
        form.add(lblThongBao);
        form.add(Box.createVerticalStrut(5));
        form.add(btnDangNhap);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 45, 30, 45);
        formPanel.add(form, gbc);

        btnDangNhap.addActionListener(e -> dangNhap());
        getRootPane().setDefaultButton(btnDangNhap);
        txtTaiKhoan.addActionListener(e -> txtMatKhau.requestFocusInWindow());
        txtMatKhau.addActionListener(e -> dangNhap());

        return formPanel;
    }

    private JLabel taoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel taoErrorLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setForeground(ERROR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setVisible(false);
        label.setPreferredSize(new Dimension(350, 18));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setCaretColor(BLUE);
        field.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 8));
        field.setOpaque(false);
    }

    private JPanel taoInputContainer(JTextField field) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(1, 2, 1, 2)));
        container.setPreferredSize(new Dimension(350, 46));
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(field, BorderLayout.CENTER);
        return container;
    }

    private void dangNhap() {
        if (dangChuyenTrang) {
            return;
        }

        String tenDangNhap = txtTaiKhoan.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        boolean hopLe = true;
        if (tenDangNhap.isEmpty()) {
            hienThiLoi(lblLoiTaiKhoan, taiKhoanInput, true);
            hopLe = false;
        } else {
            hienThiLoi(lblLoiTaiKhoan, taiKhoanInput, false);
        }

        if (matKhau.isEmpty()) {
            hienThiLoi(lblLoiMatKhau, matKhauInput, true);
            hopLe = false;
        } else {
            hienThiLoi(lblLoiMatKhau, matKhauInput, false);
        }

        if (!hopLe) {
            return;
        }

        TaiKhoanDAO dao = new TaiKhoanDAO();
        if (dao.dangNhap(tenDangNhap, matKhau)) {
            dangChuyenTrang = true;
            lblThongBao.setText("Đăng nhập thành công! Đang vào hệ thống...");
            lblLoiTaiKhoan.setVisible(false);
            lblLoiMatKhau.setVisible(false);
            btnDangNhap.setEnabled(false);
            btnDangNhap.setText("ĐANG VÀO HỆ THỐNG...");
            btnDangNhap.setBackground(new Color(145, 169, 197));

            Timer timer = new Timer(800, e -> {
                new MainFrame(tenDangNhap).setVisible(true);
                dispose();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            JOptionPane.showMessageDialog(
                    this, "Sai tên đăng nhập hoặc mật khẩu!",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            txtMatKhau.selectAll();
            txtMatKhau.requestFocusInWindow();
        }
    }

    private void hienThiLoi(JLabel label, JPanel input, boolean hasError) {
        label.setVisible(hasError);
        Border line = BorderFactory.createLineBorder(hasError ? ERROR : BORDER);
        input.setBorder(BorderFactory.createCompoundBorder(
                line, BorderFactory.createEmptyBorder(1, 2, 1, 2)));
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 42, 77),
                    getWidth(), getHeight(), new Color(29, 66, 111));
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static class PasswordEyeIcon implements Icon {
        private final boolean visible;

        private PasswordEyeIcon(boolean visible) {
            this.visible = visible;
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(MUTED);
            g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Path2D eye = new Path2D.Double();
            eye.moveTo(2, 10);
            eye.curveTo(5, 5, 8, 3.5, 10, 3.5);
            eye.curveTo(13, 3.5, 16, 5, 18, 10);
            eye.curveTo(15, 14.5, 12, 16.5, 10, 16.5);
            eye.curveTo(7, 16.5, 4, 14.5, 2, 10);
            g2.draw(eye);
            g2.draw(new Ellipse2D.Double(7.5, 7.5, 5, 5));

            if (!visible) {
                g2.draw(new Line2D.Double(3, 17, 17, 3));
            }
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
