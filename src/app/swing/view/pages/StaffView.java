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
public class StaffView extends JFrame {
    
    private User currentUser;
    
    public StaffView(User user) {
        this.currentUser = user;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Hệ thống bán hàng - Staff");
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
        headerPanel.setBackground(new Color(255, 140, 0));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Title
        JLabel titleLabel = new JLabel("🛍️ HỆ THỐNG BÁN HÀNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(255, 140, 0));
        
        JLabel userLabel = new JLabel("👤 " + currentUser.getFullName() + " (Staff)");
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
        
        // Staff menu buttons
        JButton posButton = createMenuButton("💳 Bán hàng (POS)", 
            "Giao diện bán hàng tại quầy");
        
        JButton productSearchButton = createMenuButton("🔍 Tìm sản phẩm", 
            "Tìm kiếm thông tin sản phẩm");
        
        JButton inventoryCheckButton = createMenuButton("📦 Kiểm tra kho", 
            "Kiểm tra tồn kho sản phẩm");
        
        JButton customerInfoButton = createMenuButton("👥 Thông tin khách hàng", 
            "Xem thông tin khách hàng");
        
        JButton salesHistoryButton = createMenuButton("📊 Lịch sử bán hàng", 
            "Xem lịch sử bán hàng cá nhân");
        
        JButton profileButton = createMenuButton("⚙️ Thông tin cá nhân", 
            "Xem và cập nhật thông tin cá nhân");
        
        menuPanel.add(posButton);
        menuPanel.add(productSearchButton);
        menuPanel.add(inventoryCheckButton);
        menuPanel.add(customerInfoButton);
        menuPanel.add(salesHistoryButton);
        menuPanel.add(profileButton);
        
        contentPanel.add(menuPanel, BorderLayout.CENTER);
        
        // Welcome message
        JPanel welcomePanel = new JPanel(new FlowLayout());
        welcomePanel.setBackground(new Color(245, 245, 245));
        
        JLabel welcomeLabel = new JLabel("🎉 Chào mừng bạn đến với hệ thống bán hàng!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(255, 140, 0));
        
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
        titleLabel.setForeground(new Color(255, 140, 0));
        
        JLabel descLabel = new JLabel("<html><div style='text-align: center'>" + description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        button.add(titleLabel, BorderLayout.NORTH);
        button.add(descLabel, BorderLayout.CENTER);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(StaffView.this, 
                    "Chức năng \"" + title + "\" đang được phát triển!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        return button;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout());
        footerPanel.setBackground(new Color(255, 140, 0));
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel footerLabel = new JLabel("© 2024 - Hệ thống bán hàng - Phiên bản 1.0");
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