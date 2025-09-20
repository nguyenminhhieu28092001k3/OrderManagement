package app.swing.view.pages;

import app.swing.model.User;
import app.swing.util.SessionManager;
import app.swing.view.LoginView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author hieu
 */
public class AdminView extends JFrame {
    
    private User currentUser;
    
    public AdminView(User user) {
        this.currentUser = user;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Hệ thống quản trị - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Content panel
        JPanel contentPanel = createContentPanel();
        
        // Footer panel
        JPanel footerPanel = createFooterPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Title
        JLabel titleLabel = new JLabel("🔧 HỆ THỐNG QUẢN TRỊ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(70, 130, 180));
        
        JLabel userLabel = new JLabel("👤 " + currentUser.getFullName() + " (Admin)");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("🚪 Đăng xuất");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        
        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(20));
        userPanel.add(logoutButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Menu panel
        JPanel menuPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        menuPanel.setBackground(new Color(245, 245, 245));
        
        // Admin menu buttons
        JButton userManagementButton = createMenuButton("👥 Quản lý người dùng", 
            "Quản lý tài khoản admin, customer_admin, staff");
        
        JButton systemConfigButton = createMenuButton("⚙️ Cấu hình hệ thống", 
            "Cài đặt và cấu hình hệ thống");
        
        JButton companyManagementButton = createMenuButton("🏢 Quản lý doanh nghiệp", 
            "Quản lý thông tin các doanh nghiệp");
        
        JButton reportButton = createMenuButton("📊 Báo cáo tổng hợp", 
            "Xem báo cáo tổng hợp toàn hệ thống");
        
        JButton auditLogButton = createMenuButton("📋 Nhật ký hệ thống", 
            "Xem nhật ký hoạt động hệ thống");
        
        JButton backupButton = createMenuButton("💾 Sao lưu & Khôi phục", 
            "Sao lưu và khôi phục dữ liệu");
        
        menuPanel.add(userManagementButton);
        menuPanel.add(systemConfigButton);
        menuPanel.add(companyManagementButton);
        menuPanel.add(reportButton);
        menuPanel.add(auditLogButton);
        menuPanel.add(backupButton);
        
        contentPanel.add(menuPanel, BorderLayout.CENTER);
        
        // Welcome message
        JPanel welcomePanel = new JPanel(new FlowLayout());
        welcomePanel.setBackground(new Color(245, 245, 245));
        
        JLabel welcomeLabel = new JLabel("🎉 Chào mừng bạn đến với hệ thống quản trị!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(70, 130, 180));
        
        welcomePanel.add(welcomeLabel);
        contentPanel.add(welcomePanel, BorderLayout.NORTH);
        
        return contentPanel;
    }
    
    private JButton createMenuButton(String title, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(70, 130, 180));
        
        JLabel descLabel = new JLabel("<html><div style='text-align: center'>" + description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        button.add(titleLabel, BorderLayout.NORTH);
        button.add(descLabel, BorderLayout.CENTER);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminView.this, 
                    "Chức năng \"" + title + "\" đang được phát triển!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        return button;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout());
        footerPanel.setBackground(new Color(70, 130, 180));
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel footerLabel = new JLabel("© 2025 - Hệ thống quản lý - Phiên bản 1.0");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        footerLabel.setForeground(Color.WHITE);
        
        footerPanel.add(footerLabel);
        
        return footerPanel;
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            this.dispose();
            new LoginView().setVisible(true);
        }
    }
} 