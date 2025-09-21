package app.swing.view.pages;

import app.swing.model.User;
import app.swing.service.UserService;
import app.swing.util.SessionManager;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.List;
import java.util.Vector;

/**
 * Giao diện quản lý người dùng
 * @author hieu
 */
public class UserManagementView extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    private JTextField searchField;
    private UserService userService;
    private final String[] ROLES = {"admin", "customer_admin", "staff"};
    private boolean isEmbedded = false;

    public UserManagementView() {
        this.userService = new UserService();
        this.isEmbedded = false;
        initComponents();
        loadUserData();
    }

    // Constructor for embedded mode (used in AdminView)
    public UserManagementView(boolean embedded) {
        this.userService = new UserService();
        this.isEmbedded = embedded;
        if (!embedded) {
            initComponents();
            loadUserData();
        }
    }

    // Method to get the main panel for embedding
    public JPanel getMainPanel() {
        if (isEmbedded) {
            return createEmbeddedPanel();
        }
        return null;
    }

    private JPanel createEmbeddedPanel() {
        // Create main panel without frame-specific components
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));

        // Create header (without back button)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(65, 105, 225));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("Quản Lý Người Dùng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("Thêm, chỉnh sửa và quản lý tài khoản người dùng hệ thống");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setForeground(new Color(220, 220, 220));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(descLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Create toolbar
        JPanel toolbarPanel = createToolbarPanel();

        // Create table panel
        JPanel tablePanel = createTablePanel();

        // Initialize data
        loadUserData();

        // Add components
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(toolbarPanel, BorderLayout.PAGE_START);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void initComponents() {
        setTitle("Quản lý người dùng");
        setSize(1024, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with card layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));

        // Create header with actions
        JPanel headerPanel = createHeaderPanel();

        // Create toolbar with actions and search
        JPanel toolbarPanel = createToolbarPanel();

        // User table panel
        JPanel tablePanel = createTablePanel();

        // Status bar
        JPanel statusPanel = createStatusPanel();

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(toolbarPanel, BorderLayout.PAGE_START);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        // Set main panel as content pane
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        // Create header panel with gradient background
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 110, 170));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Title
        JLabel titleLabel = new JLabel("Quản Lý Người Dùng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Description
        JLabel descLabel = new JLabel("Thêm, chỉnh sửa và quản lý tài khoản người dùng hệ thống");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setForeground(new Color(220, 220, 220));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(descLabel);

        // Back button on the right
        backButton = new JButton("Quay lại");
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setBackground(new Color(40, 80, 140));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createToolbarPanel() {
        // Create toolbar with buttons and search
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.setBackground(new Color(240, 240, 240));
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Left side - action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        addButton = createActionButton("Thêm người dùng", new Color(70, 130, 180));
        editButton = createActionButton("Sửa người dùng", new Color(60, 179, 113));
        deleteButton = createActionButton("Xóa người dùng", new Color(220, 20, 60));
        refreshButton = createActionButton("Làm mới", new Color(30, 144, 255));

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(refreshButton);

        // Right side - search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        searchField = new JTextField(18);
        searchField.setPreferredSize(new Dimension(searchField.getPreferredSize().width, 30));

        // Add icon to search field
        JPanel searchFieldPanel = new JPanel(new BorderLayout());
        searchFieldPanel.setOpaque(false);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);

        searchPanel.add(searchLabel);
        searchPanel.add(searchFieldPanel);

        toolbarPanel.add(actionPanel, BorderLayout.WEST);
        toolbarPanel.add(searchPanel, BorderLayout.EAST);

        return toolbarPanel;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 34));

        if (text.equals("Thêm người dùng")) {
            button.addActionListener(e -> showAddUserDialog());
        } else if (text.equals("Sửa người dùng")) {
            button.addActionListener(e -> {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = userTable.convertRowIndexToModel(selectedRow);
                    int userId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
                    showEditUserDialog(userId);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn người dùng để sửa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (text.equals("Xóa người dùng")) {
            button.addActionListener(e -> {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = userTable.convertRowIndexToModel(selectedRow);
                    int userId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
                    String username = tableModel.getValueAt(modelRow, 1).toString();
                    deleteUser(userId, username);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn người dùng để xóa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (text.equals("Làm mới")) {
            button.addActionListener(e -> loadUserData());
        }

        return button;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        // Create table model
        String[] columns = {"ID", "Tên đăng nhập", "Vai trò", "Trạng thái", "Họ tên", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Create and configure table
        userTable = new JTable(tableModel);
        userTable.setRowHeight(40);
        userTable.setShowVerticalLines(false);
        userTable.setGridColor(new Color(230, 230, 230));
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.getTableHeader().setBackground(new Color(240, 240, 240));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.setFont(new Font("Arial", Font.PLAIN, 13));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setSelectionBackground(new Color(220, 235, 250));
        userTable.setSelectionForeground(Color.BLACK);

        // Set column widths
        userTable.getColumnModel().getColumn(0).setMaxWidth(70); // ID column
        userTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Role
        userTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Status

        // Add row selection listener for double click
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = userTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = userTable.convertRowIndexToModel(selectedRow);
                        int userId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
                        showEditUserDialog(userId);
                    }
                }
            }
        });

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(sorter);

        // Add search filter
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim().toLowerCase();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 20, 5, 20)
        ));

        JLabel statusLabel = new JLabel("Sẵn sàng");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));

        statusPanel.add(statusLabel);

        return statusPanel;
    }

    private void loadUserData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all users
        List<User> users = userService.getAllUsers();

        // Add to table
        for (User user : users) {
            Vector<Object> row = new Vector<>();
            row.add(user.getId());
            row.add(user.getUsername());
            row.add(user.getRole());
            row.add(user.isStatus() ? "Hoạt động" : "Đã khóa");
            row.add(user.getFullName());
            row.add(user.getEmail());
            tableModel.addRow(row);
        }
    }

    private void showAddUserDialog() {
        // Create dialog
        JDialog dialog = new JDialog(this, "Thêm người dùng mới", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JPasswordField passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Vai trò:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JComboBox<String> roleComboBox = new JComboBox<>(ROLES);
        formPanel.add(roleComboBox, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Trạng thái:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JCheckBox statusCheckBox = new JCheckBox("Hoạt động");
        statusCheckBox.setSelected(true);
        formPanel.add(statusCheckBox, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Họ và tên:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField fullNameField = new JTextField(15);
        formPanel.add(fullNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField emailField = new JTextField(15);
        formPanel.add(emailField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> {
            // Validate input
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();
            boolean status = statusCheckBox.isSelected();
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập đầy đủ thông tin bắt buộc!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if username exists
            if (userService.usernameExists(username)) {
                JOptionPane.showMessageDialog(dialog,
                    "Tên đăng nhập đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole(role);
            newUser.setStatus(status);
            newUser.setFullName(fullName);
            newUser.setEmail(email);

            boolean success = userService.createUser(newUser);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                    "Thêm người dùng thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Không thể thêm người dùng. Vui lòng thử lại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditUserDialog(int userId) {
        // Get user
        User user = userService.getUserById(userId);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy thông tin người dùng!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog(this, "Sửa thông tin người dùng", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField usernameField = new JTextField(15);
        usernameField.setText(user.getUsername());
        formPanel.add(usernameField, gbc);

        // Password (optional for edit)
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Mật khẩu mới (để trống nếu không đổi):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JPasswordField passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Vai trò:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JComboBox<String> roleComboBox = new JComboBox<>(ROLES);
        roleComboBox.setSelectedItem(user.getRole());
        formPanel.add(roleComboBox, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Trạng thái:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JCheckBox statusCheckBox = new JCheckBox("Hoạt động");
        statusCheckBox.setSelected(user.isStatus());
        formPanel.add(statusCheckBox, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Họ và tên:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField fullNameField = new JTextField(15);
        fullNameField.setText(user.getFullName());
        formPanel.add(fullNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField emailField = new JTextField(15);
        emailField.setText(user.getEmail());
        formPanel.add(emailField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> {
            // Validate input
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();
            boolean status = statusCheckBox.isSelected();
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();

            if (username.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập đầy đủ thông tin bắt buộc!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if username exists for other users
            if (!username.equals(user.getUsername()) && userService.usernameExistsForOtherUser(username, userId)) {
                JOptionPane.showMessageDialog(dialog,
                    "Tên đăng nhập đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update user
            user.setUsername(username);
            if (!password.isEmpty()) {
                user.setPassword(password);
            }
            user.setRole(role);
            user.setStatus(status);
            user.setFullName(fullName);
            user.setEmail(email);

            boolean success = userService.updateUser(user);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                    "Cập nhật thông tin người dùng thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Không thể cập nhật thông tin người dùng. Vui lòng thử lại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteUser(int userId, String username) {
        // Prevent deleting current user
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser.getId() == userId) {
            JOptionPane.showMessageDialog(this,
                "Không thể xóa tài khoản đang đăng nhập!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm delete
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa người dùng '" + username + "'?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userService.deleteUser(userId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Xóa người dùng thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể xóa người dùng. Vui lòng thử lại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
