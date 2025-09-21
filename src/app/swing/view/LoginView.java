package app.swing.view;

import app.swing.model.User;
import app.swing.service.UserService;
import app.swing.util.SessionManager;
import app.swing.view.pages.AdminView;
import app.swing.view.pages.CustomerAdminView;
import app.swing.view.pages.StaffView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author hieu
 */
public class LoginView extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;
    private UserService userService;

    public LoginView() {
        this.userService = new UserService();
        initComponents();
        setupEventListeners();
    }

    private void initComponents() {
        setTitle("Đăng nhập hệ thống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout());
        headerPanel.setBackground(new Color(240, 248, 255));

        JLabel titleLabel = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 25, 112));
        headerPanel.add(titleLabel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(30, 144, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(200, 35));
        loginButton.setFocusPainted(false);
        formPanel.add(loginButton, gbc);

        // Message label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(messageLabel, gbc);

        // Footer panel
        JPanel footerPanel = new JPanel(new FlowLayout());
        footerPanel.setBackground(new Color(240, 248, 255));

        JLabel footerLabel = new JLabel("© 2024 - Hệ thống quản lý");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);

        // Add to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupEventListeners() {
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Enter key listener for password field
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyTyped(KeyEvent e) {}
        });

        // Enter key listener for username field
        usernameField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyTyped(KeyEvent e) {}
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (username.isEmpty()) {
            showMessage("Vui lòng nhập tên đăng nhập!", Color.RED);
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showMessage("Vui lòng nhập mật khẩu!", Color.RED);
            passwordField.requestFocus();
            return;
        }

        // Show loading
        loginButton.setEnabled(false);
        loginButton.setText("Đang đăng nhập...");
        showMessage("Đang xác thực...", Color.BLUE);

        // Perform authentication in background thread
        SwingUtilities.invokeLater(() -> {
            UserService.AuthResult result = userService.authenticate(username, password);

            switch (result.getResult()) {
                case SUCCESS:
                    User user = result.getUser();
                    SessionManager.setCurrentUser(user);
                    showMessage("Đăng nhập thành công!", Color.GREEN);

                    // Redirect based on role
                    SwingUtilities.invokeLater(() -> {
                        redirectBasedOnRole(user);
                    });
                    break;

                case INVALID_CREDENTIALS:
                    showMessage("Tên đăng nhập hoặc mật khẩu không đúng.", Color.RED);
                    passwordField.setText("");
                    passwordField.requestFocus();
                    break;

                case ACCOUNT_DISABLED:
                    showMessage("Tài khoản của bạn đã bị khóa.", Color.RED);
                    clearFields();
                    break;

                case DATABASE_ERROR:
                    showMessage("Lỗi kết nối cơ sở dữ liệu.", Color.RED);
                    break;
            }

            // Reset button
            loginButton.setEnabled(true);
            loginButton.setText("Đăng nhập");
        });
    }

    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
    }

    private void redirectBasedOnRole(User user) {
        this.dispose();

        switch (user.getRole().toLowerCase()) {
            case "admin":
                new AdminView(user).setVisible(true);
                break;
            case "customer_admin":
                new CustomerAdminView(user).setVisible(true);
                break;
            case "staff":
                new StaffView(user).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Vai trò không xác định: " + user.getRole());
                System.exit(0);
        }
    }

    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}
