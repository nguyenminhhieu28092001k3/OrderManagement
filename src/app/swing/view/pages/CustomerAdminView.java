package app.swing.view.pages;

import app.swing.model.User;
import app.swing.util.SessionManager;
import app.swing.view.LoginView;
import app.swing.view.pages.ProductManagementView;
import app.swing.view.pages.InventoryMovementManagementView;
import app.swing.view.pages.OrderManagementView;
import app.swing.view.pages.CustomerManagementView;
import app.swing.view.pages.SupplierManagementView;
import app.swing.view.pages.CategoryManagementView;
import app.swing.service.SupplierService;
import app.swing.service.CategoryService;
import app.swing.service.ProductService;
import app.swing.service.CustomerService;
import app.swing.service.InventoryMovementService;
import app.swing.service.OrderService;
import app.swing.model.Product;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author hieu
 */
public class CustomerAdminView extends JFrame {

    private User currentUser;

    // Service instances
    private SupplierService supplierService;
    private CategoryService categoryService;
    private ProductService productService;
    private CustomerService customerService;
    private InventoryMovementService inventoryMovementService;
    private JPanel currentSelectedPanel = null;
    private String currentSelectedTitle = "Dashboards"; // Default selection
    private JPanel contentArea; // Main content area
    private CardLayout contentCardLayout;
    private final String DASHBOARD_VIEW = "dashboard";
    private final String SUPPLIER_MANAGEMENT_VIEW = "supplier_management";
    private final String CATEGORY_MANAGEMENT_VIEW = "category_management";
    private final String PRODUCT_MANAGEMENT_VIEW = "product_management";
    private final String INVENTORY_MANAGEMENT_VIEW = "inventory_management";
    private final String CUSTOMER_MANAGEMENT_VIEW = "customer_management";
    private final String ORDER_MANAGEMENT_VIEW = "order_management";

    public CustomerAdminView(User user) {
        this.currentUser = user;
        // Initialize services
        this.supplierService = new SupplierService();
        this.categoryService = new CategoryService();
        this.productService = new ProductService();
        this.customerService = new CustomerService();
        this.inventoryMovementService = new InventoryMovementService();
        initComponents();
    }

    private void initComponents() {
        setTitle("Hệ thống quản lý doanh nghiệp - Customer Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main panel with sidebar layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header panel
        JPanel headerPanel = createHeaderPanel();

        // Sidebar and content container
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Sidebar navigation
        JPanel sidebarPanel = createSidebarPanel();

        // Create content area with CardLayout for different views
        contentArea = new JPanel();
        contentCardLayout = new CardLayout();
        contentArea.setLayout(contentCardLayout);

        // Add different view panels to content area
        contentArea.add(createDashboardPanel(), DASHBOARD_VIEW);

        // Create embedded management panels (all except Users)
        SupplierManagementView supplierMgmtView = new SupplierManagementView(true);
        contentArea.add(supplierMgmtView.getMainPanel(), SUPPLIER_MANAGEMENT_VIEW);

        CategoryManagementView categoryMgmtView = new CategoryManagementView(true);
        contentArea.add(categoryMgmtView.getMainPanel(), CATEGORY_MANAGEMENT_VIEW);

        ProductManagementView productMgmtView = new ProductManagementView(true);
        contentArea.add(productMgmtView.getMainPanel(), PRODUCT_MANAGEMENT_VIEW);

        InventoryMovementManagementView inventoryMgmtView = new InventoryMovementManagementView(true);
        contentArea.add(inventoryMgmtView.getMainPanel(), INVENTORY_MANAGEMENT_VIEW);

        CustomerManagementView customerMgmtView = new CustomerManagementView(true);
        contentArea.add(customerMgmtView.getMainPanel(), CUSTOMER_MANAGEMENT_VIEW);

        OrderManagementView orderMgmtView = new OrderManagementView(true);
        contentArea.add(orderMgmtView.getMainPanel(), ORDER_MANAGEMENT_VIEW);

        // Show dashboard by default
        contentCardLayout.show(contentArea, DASHBOARD_VIEW);

        // Add sidebar and content to center panel
        centerPanel.add(sidebarPanel, BorderLayout.WEST);
        centerPanel.add(contentArea, BorderLayout.CENTER);

        // Footer panel
        JPanel footerPanel = createFooterPanel();

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 139, 34));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Title
        JLabel titleLabel = new JLabel("QUẢN LÝ DOANH NGHIỆP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(34, 139, 34));

        JLabel userLabel = new JLabel(currentUser.getFullName() + " (Customer Admin)");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Đăng xuất");
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

    private JPanel createSidebarPanel() {
        // Main sidebar panel with clean white background like in the image
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));

        // Add some padding at the top
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Menu items with icons - Customer Admin permissions (no Users)
        sidebarPanel.add(createNavItem("Dashboards", "📊", true));
        sidebarPanel.add(createNavItem("Nhà cung cấp", "🏭", true));
        sidebarPanel.add(createNavItem("Danh mục", "📁", true));
        sidebarPanel.add(createNavItem("Sản phẩm", "📦", true));
        sidebarPanel.add(createNavItem("Xuất nhập kho", "📋", true));
        sidebarPanel.add(createNavItem("Khách hàng", "👨‍💼", true));
        sidebarPanel.add(createNavItem("Đơn hàng", "🛒", true));

        // Add flexible space
        sidebarPanel.add(Box.createVerticalGlue());

        // User account info at bottom
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(Color.WHITE);
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Create a circular avatar
        JPanel avatarPanel = createAvatarPanel();

        // User info
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel nameLabel = new JLabel(currentUser.getFullName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setForeground(new Color(60, 60, 60));

        JLabel roleLabel = new JLabel("Customer Admin");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(120, 120, 120));

        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(roleLabel, BorderLayout.CENTER);

        userPanel.add(avatarPanel, BorderLayout.WEST);
        userPanel.add(infoPanel, BorderLayout.CENTER);

        // Add to main panel
        sidebarPanel.add(userPanel);

        return sidebarPanel;
    }

    private JPanel createNavItem(String title, String icon, boolean hasSubmenu) {
        // Create main panel
        JPanel navItem = new JPanel(new BorderLayout());
        navItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Check if this item is currently selected
        boolean isSelected = title.equals(currentSelectedTitle);

        // Set background based on selection
        Color bgColor = isSelected ? new Color(240, 245, 255) : Color.WHITE;
        navItem.setBackground(bgColor);

        // Create content panel for icon and text
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(bgColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // Create icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(24, 24));

        // Set icon color based on selection
        if (isSelected) {
            iconLabel.setForeground(new Color(65, 105, 225)); // Royal blue
        } else {
            iconLabel.setForeground(new Color(80, 80, 80));
        }

        // Create text label
        JLabel textLabel = new JLabel(title);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Set text color based on selection
        if (isSelected) {
            textLabel.setForeground(new Color(65, 105, 225)); // Royal blue
        } else {
            textLabel.setForeground(new Color(60, 60, 60));
        }

        // Create arrow indicator for submenu
        JLabel arrowLabel = new JLabel(">");
        arrowLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        arrowLabel.setForeground(new Color(150, 150, 150));
        arrowLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Add components to content panel
        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(textLabel, BorderLayout.CENTER);

        if (hasSubmenu) {
            contentPanel.add(arrowLabel, BorderLayout.EAST);
        }

        // Add content to main panel
        navItem.add(contentPanel, BorderLayout.CENTER);

        // Add left blue indicator bar if selected
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(3, 0));
        if (isSelected) {
            indicator.setBackground(new Color(65, 105, 225)); // Royal blue
            navItem.add(indicator, BorderLayout.WEST);
        } else {
            indicator.setBackground(Color.WHITE);
            navItem.add(indicator, BorderLayout.WEST);
        }

        // Store reference for selection tracking
        if (isSelected) {
            currentSelectedPanel = navItem;
        }

        // Add hover effect
        navItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isSelected) {
                    navItem.setBackground(new Color(248, 248, 250));
                    contentPanel.setBackground(new Color(248, 248, 250));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelected) {
                    navItem.setBackground(Color.WHITE);
                    contentPanel.setBackground(Color.WHITE);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Update selection
                setSelectedNavItem(title, navItem, contentPanel, iconLabel, textLabel, indicator);

                // Handle navigation - switch content views
                switch (title) {
                    case "Dashboards":
                        contentCardLayout.show(contentArea, DASHBOARD_VIEW);
                        break;
                    case "Nhà cung cấp":
                        contentCardLayout.show(contentArea, SUPPLIER_MANAGEMENT_VIEW);
                        break;
                    case "Danh mục":
                        contentCardLayout.show(contentArea, CATEGORY_MANAGEMENT_VIEW);
                        break;
                    case "Sản phẩm":
                        contentCardLayout.show(contentArea, PRODUCT_MANAGEMENT_VIEW);
                        break;
                    case "Xuất nhập kho":
                        contentCardLayout.show(contentArea, INVENTORY_MANAGEMENT_VIEW);
                        break;
                    case "Khách hàng":
                        contentCardLayout.show(contentArea, CUSTOMER_MANAGEMENT_VIEW);
                        break;
                    case "Đơn hàng":
                        contentCardLayout.show(contentArea, ORDER_MANAGEMENT_VIEW);
                        break;
                    default:
                JOptionPane.showMessageDialog(CustomerAdminView.this,
                    "Chức năng \"" + title + "\" đang được phát triển!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                        break;
                }
            }
        });

        return navItem;
    }

    private void setSelectedNavItem(String title, JPanel navItem, JPanel contentPanel,
                                   JLabel iconLabel, JLabel textLabel, JPanel indicator) {
        // Reset previous selection if exists
        if (currentSelectedPanel != null && !currentSelectedTitle.equals(title)) {
            resetNavItemStyle(currentSelectedPanel);
        }

        // Set new selection
        currentSelectedTitle = title;
        currentSelectedPanel = navItem;

        // Apply selected styles
        Color selectedBgColor = new Color(240, 245, 255);
        Color selectedTextColor = new Color(65, 105, 225);

        navItem.setBackground(selectedBgColor);
        contentPanel.setBackground(selectedBgColor);
        iconLabel.setForeground(selectedTextColor);
        textLabel.setForeground(selectedTextColor);
        indicator.setBackground(selectedTextColor);

        // Repaint to ensure visual update
        navItem.repaint();
    }

    private void resetNavItemStyle(JPanel navItem) {
        // Reset to default styles
        Component[] components = navItem.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getPreferredSize().width == 3) { // This is the indicator
                    panel.setBackground(Color.WHITE);
                } else { // This is the content panel
                    panel.setBackground(Color.WHITE);
                    // Reset content panel children
                    Component[] contentComponents = panel.getComponents();
                    for (Component contentComp : contentComponents) {
                        if (contentComp instanceof JLabel) {
                            JLabel label = (JLabel) contentComp;
                            if (label.getText().length() == 1 || label.getText().contains("📊") ||
                                label.getText().contains("🏭") || label.getText().contains("📁") ||
                                label.getText().contains("📦") || label.getText().contains("📋") ||
                                label.getText().contains("👨‍💼") || label.getText().contains("🛒")) {
                                // This is an icon
                                label.setForeground(new Color(80, 80, 80));
                            } else if (!label.getText().equals(">")) {
                                // This is text
                                label.setForeground(new Color(60, 60, 60));
                            }
                        }
                    }
                }
            }
        }
        navItem.setBackground(Color.WHITE);
        navItem.repaint();
    }

    private JPanel createAvatarPanel() {
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw circle
                g2d.setColor(new Color(65, 105, 225)); // Royal blue
                g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);

                // Draw text (initials)
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();

                // Get user initials
                String fullName = currentUser.getFullName();
                String initials = "";
                String[] parts = fullName.split("\\s+");
                if (parts.length >= 2) {
                    initials = String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[parts.length-1].charAt(0));
                } else if (parts.length == 1 && parts[0].length() > 0) {
                    initials = String.valueOf(parts[0].charAt(0));
                }

                int textWidth = fm.stringWidth(initials);
                int textHeight = fm.getHeight();

                g2d.drawString(initials, (getWidth() - textWidth) / 2,
                              (getHeight() - textHeight) / 2 + fm.getAscent());

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(32, 32);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };

        avatarPanel.setOpaque(false);
        return avatarPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Create scrollable content
        JPanel scrollableContent = new JPanel();
        scrollableContent.setLayout(new BoxLayout(scrollableContent, BoxLayout.Y_AXIS));
        scrollableContent.setBackground(new Color(245, 245, 245));

        // Combined header and stats panel
        JPanel combinedPanel = createCombinedHeaderStatsPanel();
        scrollableContent.add(combinedPanel);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createCombinedHeaderStatsPanel() {
        JPanel combinedPanel = new JPanel();
        combinedPanel.setLayout(new BoxLayout(combinedPanel, BoxLayout.Y_AXIS));
        combinedPanel.setBackground(new Color(245, 245, 245));
        combinedPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        // Main stats section - moved to top
        JPanel mainStatsSection = createMainStatsSection();
        combinedPanel.add(mainStatsSection);

        // Small gap
        combinedPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Secondary stats section
        JPanel secondaryStatsSection = createSecondaryStatsSection();
        combinedPanel.add(secondaryStatsSection);

        // Small gap
        combinedPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Header section - moved to bottom
        JPanel headerSection = createHeaderSection();
        combinedPanel.add(headerSection);

        return combinedPanel;
    }

    private JPanel createHeaderSection() {
        JPanel dashboardHeader = new JPanel(new BorderLayout());
        dashboardHeader.setBackground(new Color(245, 245, 245));

        // Center panel for welcome message
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(245, 245, 245));

        JLabel welcomeLabel = new JLabel("Chào mừng, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(50, 50, 50));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy",
            new java.util.Locale("vi", "VN"))));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(120, 120, 120));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(dateLabel);

        dashboardHeader.add(centerPanel, BorderLayout.CENTER);

        return dashboardHeader;
    }

    private JPanel createMainStatsSection() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(new Color(245, 245, 245));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Get real statistics from database (no user count for customer admin)
        int orderCount = getOrderCount();
        int productCount = getProductCount();
        int customerCount = getCustomerCount();
        int supplierCount = getSupplierCount();

        statsPanel.add(createStatsCard("Tổng Đơn Hàng", String.valueOf(orderCount), new Color(60, 179, 113)));
        statsPanel.add(createStatsCard("Tổng Sản Phẩm", String.valueOf(productCount), new Color(255, 165, 0)));
        statsPanel.add(createStatsCard("Tổng Khách Hàng", String.valueOf(customerCount), new Color(220, 20, 60)));
        statsPanel.add(createStatsCard("Nhà Cung Cấp", String.valueOf(supplierCount), new Color(70, 130, 180)));

        return statsPanel;
    }

    private JPanel createSecondaryStatsSection() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(new Color(245, 245, 245));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Get additional statistics
        int pendingOrders = getPendingOrderCount();
        int categoryCount = getCategoryCount();
        int lowStockProducts = getLowStockProductCount();

        statsPanel.add(createStatsCard("Đơn Chờ Xử Lý", String.valueOf(pendingOrders), new Color(255, 193, 7)));
        statsPanel.add(createStatsCard("Danh Mục", String.valueOf(categoryCount), new Color(108, 117, 125)));
        statsPanel.add(createStatsCard("Sản Phẩm Sắp Hết", String.valueOf(lowStockProducts), new Color(220, 53, 69)));

        return statsPanel;
    }

    // Statistics helper methods
    private int getOrderCount() {
        try {
            return new OrderService().getAllOrders().size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getProductCount() {
        try {
            return productService.getAllProducts().size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getCustomerCount() {
        try {
            return customerService.getAllCustomers().size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getSupplierCount() {
        try {
            return supplierService.getAllSuppliers().size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getPendingOrderCount() {
        try {
            return (int) new OrderService().getAllOrders().stream()
                .filter(order -> "pending".equals(order.getStatus()))
                .count();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getCategoryCount() {
        try {
            return categoryService.getAllCategories().size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getLowStockProductCount() {
        try {
            return (int) productService.getAllProducts().stream()
                .filter(Product::isLowStock)
                .count();
        } catch (Exception e) {
            return 0;
        }
    }

    private JPanel createStatsCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(200, 100));

        // Center panel for better balance
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // Title at top
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);

        // Small gap
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Value at bottom
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(valueLabel);

        card.add(centerPanel, BorderLayout.CENTER);

        return card;
    }


    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout());
        footerPanel.setBackground(new Color(34, 139, 34));
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel footerLabel = new JLabel("Hệ thống quản lý doanh nghiệp - Phiên bản 1.0");
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
