package app.swing.view.pages;

import app.swing.model.InventoryMovement;
import app.swing.model.Product;
import app.swing.model.User;
import app.swing.service.InventoryMovementService;
import app.swing.service.ProductService;
import app.swing.service.UserService;
import app.swing.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Management view for inventory movements
 */
public class InventoryMovementManagementView extends JFrame {

    private JTable movementTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    private JTextField searchField;
    private InventoryMovementService movementService;
    private ProductService productService;
    private UserService userService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private boolean isEmbedded = false;

    public InventoryMovementManagementView() {
        this.movementService = new InventoryMovementService();
        this.productService = new ProductService();
        this.userService = new UserService();
        this.isEmbedded = false;
        initComponents();
        loadMovementData();
    }

    // Constructor for embedded mode
    public InventoryMovementManagementView(boolean embedded) {
        this.movementService = new InventoryMovementService();
        this.productService = new ProductService();
        this.userService = new UserService();
        this.isEmbedded = embedded;
        if (!embedded) {
            initComponents();
            loadMovementData();
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

        JLabel titleLabel = new JLabel("Quản Lý Xuất Nhập Kho");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("Theo dõi và quản lý các giao dịch xuất nhập kho");
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
        loadMovementData();

        // Add components
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(toolbarPanel, BorderLayout.PAGE_START);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void initComponents() {
        setTitle("Quản lý xuất nhập kho");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));

        // Create header with actions
        JPanel headerPanel = createHeaderPanel();

        // Create toolbar with actions and search
        JPanel toolbarPanel = createToolbarPanel();

        // Movement table panel
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
        headerPanel.setBackground(new Color(65, 105, 225));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Title
        JLabel titleLabel = new JLabel("Quản Lý Xuất Nhập Kho");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Description
        JLabel descLabel = new JLabel("Theo dõi và quản lý các giao dịch xuất nhập kho");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setForeground(new Color(220, 220, 220));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(descLabel);

        // Back button on the right (only for standalone mode)
        if (!isEmbedded) {
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

            headerPanel.add(buttonPanel, BorderLayout.EAST);
        }

        headerPanel.add(titlePanel, BorderLayout.WEST);

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

        addButton = createActionButton("Thêm giao dịch", new Color(65, 105, 225));
        editButton = createActionButton("Sửa giao dịch", new Color(60, 179, 113));
        deleteButton = createActionButton("Xóa giao dịch", new Color(220, 20, 60));
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

        if (text.equals("Thêm giao dịch")) {
            button.addActionListener(e -> showAddMovementDialog());
        } else if (text.equals("Sửa giao dịch")) {
            button.addActionListener(e -> {
                int selectedRow = movementTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = movementTable.convertRowIndexToModel(selectedRow);
                    long movementId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                    showEditMovementDialog(movementId);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn giao dịch để sửa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (text.equals("Xóa giao dịch")) {
            button.addActionListener(e -> {
                int selectedRow = movementTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = movementTable.convertRowIndexToModel(selectedRow);
                    long movementId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                    String productName = tableModel.getValueAt(modelRow, 2).toString();
                    deleteMovement(movementId, productName);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn giao dịch để xóa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (text.equals("Làm mới")) {
            button.addActionListener(e -> loadMovementData());
        }

        return button;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        // Create table model
        String[] columns = {"ID", "Sản phẩm", "SKU", "Loại giao dịch", "Số lượng", "Người thực hiện", "Ghi chú", "Thời gian"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        // Create and configure table
        movementTable = new JTable(tableModel);
        movementTable.setRowHeight(40);
        movementTable.setShowVerticalLines(false);
        movementTable.setGridColor(new Color(230, 230, 230));
        movementTable.getTableHeader().setReorderingAllowed(false);
        movementTable.getTableHeader().setBackground(new Color(240, 240, 240));
        movementTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        movementTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        movementTable.setFont(new Font("Arial", Font.PLAIN, 13));
        movementTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movementTable.setSelectionBackground(new Color(220, 235, 250));
        movementTable.setSelectionForeground(Color.BLACK);

        // Set column widths
        movementTable.getColumnModel().getColumn(0).setMaxWidth(70); // ID column
        movementTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Product name
        movementTable.getColumnModel().getColumn(2).setPreferredWidth(100); // SKU
        movementTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Kind
        movementTable.getColumnModel().getColumn(4).setMaxWidth(100); // Quantity
        movementTable.getColumnModel().getColumn(5).setPreferredWidth(150); // User
        movementTable.getColumnModel().getColumn(7).setPreferredWidth(140); // Time

        // Add row selection listener for double click
        movementTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = movementTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = movementTable.convertRowIndexToModel(selectedRow);
                        long movementId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                        showEditMovementDialog(movementId);
                    }
                }
            }
        });

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        movementTable.setRowSorter(sorter);

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
        JScrollPane scrollPane = new JScrollPane(movementTable);
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

    private void loadMovementData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all movements
        List<InventoryMovement> movements = movementService.getAllMovements();

        // Add to table
        for (InventoryMovement movement : movements) {
            Object[] row = {
                movement.getId(),
                movement.getProduct() != null ? movement.getProduct().getName() : "N/A",
                movement.getProduct() != null ? movement.getProduct().getSku() : "N/A",
                movement.getMovementTypeDisplay(),
                movement.getQuantityDisplay(),
                movement.getUser() != null ? movement.getUser().getFullName() : "Hệ thống",
                movement.getNote() != null ? movement.getNote() : "",
                movement.getCreatedAt().format(dateFormatter)
            };
            tableModel.addRow(row);
        }
    }

    private void showAddMovementDialog() {
        // Create dialog
        JDialog dialog = new JDialog(this, "Thêm giao dịch xuất nhập kho", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Product selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel productLabel = new JLabel("Sản phẩm*:");
        formPanel.add(productLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        DefaultComboBoxModel<Object> productModel = new DefaultComboBoxModel<>();
        productModel.addElement("-- Chọn sản phẩm --");
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            productModel.addElement(product);
        }
        JComboBox<Object> productComboBox = new JComboBox<>(productModel);
        formPanel.add(productComboBox, gbc);

        // Movement kind
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel kindLabel = new JLabel("Loại giao dịch*:");
        formPanel.add(kindLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        DefaultComboBoxModel<InventoryMovement.MovementKind> kindModel = new DefaultComboBoxModel<>();
        for (InventoryMovement.MovementKind kind : InventoryMovement.MovementKind.values()) {
            kindModel.addElement(kind);
        }
        JComboBox<InventoryMovement.MovementKind> kindComboBox = new JComboBox<>(kindModel);
        formPanel.add(kindComboBox, gbc);

        // Quantity change
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel qtyLabel = new JLabel("Số lượng thay đổi*:");
        formPanel.add(qtyLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        formPanel.add(qtySpinner, gbc);

        // Reference type
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel refTypeLabel = new JLabel("Loại tham chiếu:");
        formPanel.add(refTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField refTypeField = new JTextField(20);
        formPanel.add(refTypeField, gbc);

        // Reference ID
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel refIdLabel = new JLabel("ID tham chiếu:");
        formPanel.add(refIdLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JSpinner refIdSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Long.MAX_VALUE, 1));
        formPanel.add(refIdSpinner, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel notesLabel = new JLabel("Ghi chú:");
        formPanel.add(notesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        formPanel.add(notesScrollPane, gbc);

        // Required fields note
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        JLabel requiredNote = new JLabel("* Trường bắt buộc");
        requiredNote.setFont(new Font("Arial", Font.ITALIC, 11));
        requiredNote.setForeground(Color.RED);
        formPanel.add(requiredNote, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        JButton saveButton = new JButton("Lưu");
        saveButton.setBackground(new Color(65, 105, 225));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Hủy");

        // Save button action
        saveButton.addActionListener(e -> {
            // Validate input
            if (productComboBox.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng chọn sản phẩm!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int changeQty = (Integer) qtySpinner.getValue();
            if (changeQty == 0) {
                JOptionPane.showMessageDialog(dialog,
                    "Số lượng thay đổi không thể bằng 0!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create movement
            InventoryMovement movement = new InventoryMovement();
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            movement.setProductId(selectedProduct.getId());
            movement.setChangeQty(changeQty);

            InventoryMovement.MovementKind selectedKind = (InventoryMovement.MovementKind) kindComboBox.getSelectedItem();
            movement.setKind(selectedKind.getValue());

            movement.setReferenceType(refTypeField.getText().trim());

            Long refId = ((Number) refIdSpinner.getValue()).longValue();
            if (refId > 0) {
                movement.setReferenceId(refId);
            }

            movement.setNote(notesArea.getText().trim());

            // Set current user
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser != null) {
                movement.setUserId((long) currentUser.getId());
                movement.setCreatedBy((long) currentUser.getId());
            }

            boolean success = movementService.createMovement(movement);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                    "Thêm giao dịch thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadMovementData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Không thể thêm giao dịch. Vui lòng thử lại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditMovementDialog(long id) {
        // Get movement by ID
        InventoryMovement movement = movementService.getMovementById(id);
        if (movement == null) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy thông tin giao dịch!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog(this, "Sửa thông tin giao dịch", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Product selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel productLabel = new JLabel("Sản phẩm*:");
        formPanel.add(productLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        DefaultComboBoxModel<Object> productModel = new DefaultComboBoxModel<>();
        productModel.addElement("-- Chọn sản phẩm --");
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            productModel.addElement(product);
        }
        JComboBox<Object> productComboBox = new JComboBox<>(productModel);

        // Select current product
        if (movement.getProduct() != null) {
            for (int i = 0; i < productModel.getSize(); i++) {
                if (productModel.getElementAt(i) instanceof Product) {
                    Product itemProduct = (Product) productModel.getElementAt(i);
                    if (itemProduct.getId() == movement.getProduct().getId()) {
                        productComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
        formPanel.add(productComboBox, gbc);

        // Movement kind
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel kindLabel = new JLabel("Loại giao dịch*:");
        formPanel.add(kindLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        DefaultComboBoxModel<InventoryMovement.MovementKind> kindModel = new DefaultComboBoxModel<>();
        for (InventoryMovement.MovementKind kind : InventoryMovement.MovementKind.values()) {
            kindModel.addElement(kind);
        }
        JComboBox<InventoryMovement.MovementKind> kindComboBox = new JComboBox<>(kindModel);
        kindComboBox.setSelectedItem(movement.getMovementKind());
        formPanel.add(kindComboBox, gbc);

        // Quantity change
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel qtyLabel = new JLabel("Số lượng thay đổi*:");
        formPanel.add(qtyLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(movement.getChangeQty(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        formPanel.add(qtySpinner, gbc);

        // Reference type
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel refTypeLabel = new JLabel("Loại tham chiếu:");
        formPanel.add(refTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField refTypeField = new JTextField(20);
        refTypeField.setText(movement.getReferenceType());
        formPanel.add(refTypeField, gbc);

        // Reference ID
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel refIdLabel = new JLabel("ID tham chiếu:");
        formPanel.add(refIdLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        long refIdValue = movement.getReferenceId() != null ? movement.getReferenceId() : 0;
        JSpinner refIdSpinner = new JSpinner(new SpinnerNumberModel(refIdValue, 0, Long.MAX_VALUE, 1));
        formPanel.add(refIdSpinner, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel notesLabel = new JLabel("Ghi chú:");
        formPanel.add(notesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setText(movement.getNote());
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        formPanel.add(notesScrollPane, gbc);

        // Required fields note
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        JLabel requiredNote = new JLabel("* Trường bắt buộc");
        requiredNote.setFont(new Font("Arial", Font.ITALIC, 11));
        requiredNote.setForeground(Color.RED);
        formPanel.add(requiredNote, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        JButton saveButton = new JButton("Lưu");
        saveButton.setBackground(new Color(65, 105, 225));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Hủy");

        // Save button action
        saveButton.addActionListener(e -> {
            // Validate input
            if (productComboBox.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng chọn sản phẩm!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int changeQty = (Integer) qtySpinner.getValue();
            if (changeQty == 0) {
                JOptionPane.showMessageDialog(dialog,
                    "Số lượng thay đổi không thể bằng 0!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update movement
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            movement.setProductId(selectedProduct.getId());
            movement.setChangeQty(changeQty);

            InventoryMovement.MovementKind selectedKind = (InventoryMovement.MovementKind) kindComboBox.getSelectedItem();
            movement.setKind(selectedKind.getValue());

            movement.setReferenceType(refTypeField.getText().trim());

            Long refId = ((Number) refIdSpinner.getValue()).longValue();
            if (refId > 0) {
                movement.setReferenceId(refId);
            } else {
                movement.setReferenceId(null);
            }

            movement.setNote(notesArea.getText().trim());

            boolean success = movementService.updateMovement(movement);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                    "Cập nhật giao dịch thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadMovementData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Không thể cập nhật giao dịch. Vui lòng thử lại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteMovement(long id, String productName) {
        // Confirm delete
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa giao dịch cho sản phẩm '" + productName + "'?\n" +
            "Thao tác này sẽ đảo ngược thay đổi số lượng tồn kho.",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = movementService.deleteMovement(id);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Xóa giao dịch thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                loadMovementData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể xóa giao dịch. Vui lòng thử lại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
