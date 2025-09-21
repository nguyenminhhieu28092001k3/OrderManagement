package app.swing.view.pages;

import app.swing.model.*;
import app.swing.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Management view for orders and order items
 */
public class OrderManagementView extends JFrame {

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    private JTextField searchField;
    private OrderService orderService;
    private CustomerService customerService;
    private ProductService productService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private boolean isEmbedded = false;

    public OrderManagementView() {
        this.orderService = new OrderService();
        this.customerService = new CustomerService();
        this.productService = new ProductService();
        this.isEmbedded = false;
        initComponents();
        loadOrderData();
    }

    // Constructor for embedded mode
    public OrderManagementView(boolean embedded) {
        this.orderService = new OrderService();
        this.customerService = new CustomerService();
        this.productService = new ProductService();
        this.isEmbedded = embedded;
        if (!embedded) {
            initComponents();
            loadOrderData();
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

        JLabel titleLabel = new JLabel("Quản Lý Đơn Hàng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("Thêm, chỉnh sửa và quản lý đơn hàng và chi tiết đơn hàng");
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
        loadOrderData();

        // Add components
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(toolbarPanel, BorderLayout.PAGE_START);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void initComponents() {
        setTitle("Quản lý đơn hàng");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));

        // Create header with actions
        JPanel headerPanel = createHeaderPanel();

        // Create toolbar with actions and search
        JPanel toolbarPanel = createToolbarPanel();

        // Order table panel
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
        JLabel titleLabel = new JLabel("Quản Lý Đơn Hàng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Description
        JLabel descLabel = new JLabel("Thêm, chỉnh sửa và quản lý đơn hàng và chi tiết đơn hàng");
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

        addButton = createActionButton("Thêm đơn hàng", new Color(65, 105, 225));
        editButton = createActionButton("Sửa đơn hàng", new Color(60, 179, 113));
        deleteButton = createActionButton("Xóa đơn hàng", new Color(220, 20, 60));
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

        if (text.equals("Thêm đơn hàng")) {
            button.addActionListener(e -> showAddOrderDialog());
        } else if (text.equals("Sửa đơn hàng")) {
            button.addActionListener(e -> {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = orderTable.convertRowIndexToModel(selectedRow);
                    long orderId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                    showEditOrderDialog(orderId);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn đơn hàng để sửa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (text.equals("Xóa đơn hàng")) {
            button.addActionListener(e -> {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = orderTable.convertRowIndexToModel(selectedRow);
                    long orderId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                    String orderNumber = tableModel.getValueAt(modelRow, 1).toString();
                    deleteOrder(orderId, orderNumber);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn đơn hàng để xóa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (text.equals("Làm mới")) {
            button.addActionListener(e -> loadOrderData());
        }

        return button;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        // Create table model
        String[] columns = {"ID", "Số đơn hàng", "Khách hàng", "Trạng thái", "Ngày đặt",
                           "Ngày giao", "Tổng tiền", "Ghi chú"};
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
        orderTable = new JTable(tableModel);
        orderTable.setRowHeight(40);
        orderTable.setShowVerticalLines(false);
        orderTable.setGridColor(new Color(230, 230, 230));
        orderTable.getTableHeader().setReorderingAllowed(false);
        orderTable.getTableHeader().setBackground(new Color(240, 240, 240));
        orderTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        orderTable.setFont(new Font("Arial", Font.PLAIN, 13));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setSelectionBackground(new Color(220, 235, 250));
        orderTable.setSelectionForeground(Color.BLACK);

        // Set column widths
        orderTable.getColumnModel().getColumn(0).setMaxWidth(70); // ID column
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Order number
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Customer
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Placed date
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Delivery date
        orderTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Total

        // Add row selection listener for double click
        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = orderTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
                        long orderId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                        showEditOrderDialog(orderId);
                    }
                }
            }
        });

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        orderTable.setRowSorter(sorter);

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
        JScrollPane scrollPane = new JScrollPane(orderTable);
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

    private void loadOrderData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all orders
        List<Order> orders = orderService.getAllOrders();

        // Add to table
        for (Order order : orders) {
            Object[] row = {
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer() != null ? order.getCustomer().getName() : "N/A",
                order.getStatusDisplay(),
                order.getPlacedAt() != null ? order.getPlacedAt().format(dateFormatter) : "",
                order.getDeliveryDate() != null ? order.getDeliveryDate().format(dateFormatter) : "",
                order.getTotal() != null ? order.getTotal().toString() : "0",
                order.getNotes()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddOrderDialog() {
        // Create dialog
        JDialog dialog = new JDialog(this, "Thêm đơn hàng mới", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Main content panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Order info tab
        JPanel orderInfoPanel = createOrderInfoPanel(null);
        tabbedPane.addTab("Thông tin đơn hàng", orderInfoPanel);

        // Order items tab
        JPanel orderItemsPanel = createOrderItemsPanel(new ArrayList<>());
        tabbedPane.addTab("Chi tiết đơn hàng", orderItemsPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton saveButton = new JButton("Lưu");
        saveButton.setBackground(new Color(65, 105, 225));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Hủy");

        // Save button action
        saveButton.addActionListener(e -> {
            if (saveOrder(dialog, orderInfoPanel, orderItemsPanel, null)) {
                dialog.dispose();
                loadOrderData();
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add components to dialog
        dialog.add(tabbedPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditOrderDialog(long orderId) {
        // Get order by ID
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy thông tin đơn hàng!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog(this, "Sửa thông tin đơn hàng", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Main content panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Order info tab
        JPanel orderInfoPanel = createOrderInfoPanel(order);
        tabbedPane.addTab("Thông tin đơn hàng", orderInfoPanel);

        // Order items tab
        JPanel orderItemsPanel = createOrderItemsPanel(order.getOrderItems());
        tabbedPane.addTab("Chi tiết đơn hàng", orderItemsPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton saveButton = new JButton("Lưu");
        saveButton.setBackground(new Color(65, 105, 225));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Hủy");

        // Save button action
        saveButton.addActionListener(e -> {
            if (saveOrder(dialog, orderInfoPanel, orderItemsPanel, order)) {
                dialog.dispose();
                loadOrderData();
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add components to dialog
        dialog.add(tabbedPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createOrderInfoPanel(Order order) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Order number
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel orderNumberLabel = new JLabel("Số đơn hàng:");
        panel.add(orderNumberLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField orderNumberField = new JTextField(20);
        orderNumberField.setName("orderNumber");
        if (order != null) {
            orderNumberField.setText(order.getOrderNumber());
            orderNumberField.setEditable(false); // Don't allow editing order number
        }
        panel.add(orderNumberField, gbc);

        // Customer
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel customerLabel = new JLabel("Khách hàng*:");
        panel.add(customerLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        List<Customer> customers = customerService.getAllCustomers();
        DefaultComboBoxModel<Customer> customerModel = new DefaultComboBoxModel<>();
        customerModel.addElement(null); // Allow no customer selection
        for (Customer customer : customers) {
            customerModel.addElement(customer);
        }
        JComboBox<Customer> customerComboBox = new JComboBox<>(customerModel);
        customerComboBox.setName("customer");
        if (order != null && order.getCustomer() != null) {
            customerComboBox.setSelectedItem(order.getCustomer());
        }
        panel.add(customerComboBox, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel statusLabel = new JLabel("Trạng thái:");
        panel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        String[] statuses = {"draft", "pending", "paid", "shipped", "completed", "cancelled", "refunded"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setName("status");
        if (order != null) {
            statusComboBox.setSelectedItem(order.getStatus());
        } else {
            statusComboBox.setSelectedItem("pending");
        }
        panel.add(statusComboBox, gbc);

        // Delivery date
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel deliveryDateLabel = new JLabel("Ngày giao hàng:");
        panel.add(deliveryDateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField deliveryDateField = new JTextField(20);
        deliveryDateField.setName("deliveryDate");
        deliveryDateField.setToolTipText("Định dạng: dd/MM/yyyy HH:mm");
        if (order != null && order.getDeliveryDate() != null) {
            deliveryDateField.setText(order.getDeliveryDate().format(dateFormatter));
        }
        panel.add(deliveryDateField, gbc);

        // Tax
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel taxLabel = new JLabel("Thuế:");
        panel.add(taxLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField taxField = new JTextField(20);
        taxField.setName("tax");
        if (order != null && order.getTax() != null) {
            taxField.setText(order.getTax().toString());
        } else {
            taxField.setText("0");
        }
        panel.add(taxField, gbc);

        // Discount
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel discountLabel = new JLabel("Giảm giá:");
        panel.add(discountLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField discountField = new JTextField(20);
        discountField.setName("discount");
        if (order != null && order.getDiscount() != null) {
            discountField.setText(order.getDiscount().toString());
        } else {
            discountField.setText("0");
        }
        panel.add(discountField, gbc);

        // Shipping fee
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel shippingFeeLabel = new JLabel("Phí vận chuyển:");
        panel.add(shippingFeeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField shippingFeeField = new JTextField(20);
        shippingFeeField.setName("shippingFee");
        if (order != null && order.getShippingFee() != null) {
            shippingFeeField.setText(order.getShippingFee().toString());
        } else {
            shippingFeeField.setText("0");
        }
        panel.add(shippingFeeField, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel notesLabel = new JLabel("Ghi chú:");
        panel.add(notesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setName("notes");
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        if (order != null) {
            notesArea.setText(order.getNotes());
        }
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        panel.add(notesScrollPane, gbc);

        // Required fields note
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        JLabel requiredNote = new JLabel("* Trường bắt buộc");
        requiredNote.setFont(new Font("Arial", Font.ITALIC, 11));
        requiredNote.setForeground(Color.RED);
        panel.add(requiredNote, gbc);

        return panel;
    }

    private JPanel createOrderItemsPanel(List<OrderItem> orderItems) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Toolbar for order items
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemButton = new JButton("Thêm sản phẩm");
        addItemButton.setBackground(new Color(65, 105, 225));
        addItemButton.setForeground(Color.WHITE);
        addItemButton.setFocusPainted(false);

        JButton removeItemButton = new JButton("Xóa sản phẩm");
        removeItemButton.setBackground(new Color(220, 20, 60));
        removeItemButton.setForeground(Color.WHITE);
        removeItemButton.setFocusPainted(false);

        toolbar.add(addItemButton);
        toolbar.add(removeItemButton);

        // Order items table
        String[] columns = {"Sản phẩm", "SKU", "Số lượng", "Đơn giá", "Giảm giá", "Thành tiền"};
        DefaultTableModel itemsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2 && column <= 4;
            }
        };

        JTable itemsTable = new JTable(itemsTableModel);
        itemsTable.setName("itemsTable");
        itemsTable.setRowHeight(30);
        itemsTable.getTableHeader().setBackground(new Color(240, 240, 240));

        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                Object[] row = {
                    item.getProductName(),
                    item.getSku(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getDiscount(),
                    item.getLineTotal()
                };
                itemsTableModel.addRow(row);
            }
        }

        addItemButton.addActionListener(e -> showAddOrderItemDialog(itemsTableModel));

        removeItemButton.addActionListener(e -> {
            int selectedRow = itemsTable.getSelectedRow();
            if (selectedRow >= 0) {
                itemsTableModel.removeRow(selectedRow);
                updateOrderTotal(panel);
            } else {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn sản phẩm để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        itemsTableModel.addTableModelListener(e -> updateOrderTotal(panel));

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Tổng tiền: 0");
        totalLabel.setName("totalLabel");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalPanel.add(totalLabel);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(totalPanel, BorderLayout.SOUTH);

        updateOrderTotal(panel);
        return panel;
    }

    private void showAddOrderItemDialog(DefaultTableModel itemsTableModel) {
        JDialog dialog = new JDialog(this, "Thêm sản phẩm", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Sản phẩm:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        List<Product> products = productService.getAllProducts();
        DefaultComboBoxModel<Product> productModel = new DefaultComboBoxModel<>();
        for (Product product : products) {
            productModel.addElement(product);
        }
        JComboBox<Product> productComboBox = new JComboBox<>(productModel);
        dialog.add(productComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Số lượng:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        dialog.add(quantitySpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Đơn giá:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JTextField unitPriceField = new JTextField();
        dialog.add(unitPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Giảm giá:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JTextField discountField = new JTextField("0");
        dialog.add(discountField, gbc);

        productComboBox.addActionListener(e -> {
            Product selected = (Product) productComboBox.getSelectedItem();
            if (selected != null && selected.getPrice() != null) {
                unitPriceField.setText(selected.getPrice().toString());
            }
        });

        if (products.size() > 0 && products.get(0).getPrice() != null) {
            unitPriceField.setText(products.get(0).getPrice().toString());
        }

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Thêm");
        addButton.setBackground(new Color(65, 105, 225));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Hủy");

        addButton.addActionListener(e -> {
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            if (selectedProduct != null) {
                try {
                    int quantity = (Integer) quantitySpinner.getValue();
                    BigDecimal unitPrice = new BigDecimal(unitPriceField.getText());
                    BigDecimal discount = new BigDecimal(discountField.getText());
                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity)).subtract(discount);

                    Object[] row = {selectedProduct.getName(), selectedProduct.getSku(), quantity, unitPrice, discount, lineTotal};
                    itemsTableModel.addRow(row);
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);
        dialog.setVisible(true);
    }

    private void updateOrderTotal(JPanel orderItemsPanel) {
        JLabel totalLabel = findComponentByName(orderItemsPanel, "totalLabel", JLabel.class);
        JTable itemsTable = findComponentByName(orderItemsPanel, "itemsTable", JTable.class);

        if (totalLabel != null && itemsTable != null) {
            BigDecimal total = BigDecimal.ZERO;
            DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();

            for (int i = 0; i < model.getRowCount(); i++) {
                Object lineTotalObj = model.getValueAt(i, 5);
                if (lineTotalObj != null) {
                    try {
                        BigDecimal lineTotal = new BigDecimal(lineTotalObj.toString());
                        total = total.add(lineTotal);
                    } catch (NumberFormatException e) {
                        // Ignore invalid numbers
                    }
                }
            }
            totalLabel.setText("Tổng tiền: " + total);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T findComponentByName(Container container, String name, Class<T> type) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName()) && type.isInstance(component)) {
                return (T) component;
            }
            if (component instanceof Container) {
                T found = findComponentByName((Container) component, name, type);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private boolean saveOrder(JDialog dialog, JPanel orderInfoPanel, JPanel orderItemsPanel, Order existingOrder) {
        try {
            @SuppressWarnings("unchecked")
            JComboBox<Customer> customerComboBox = findComponentByName(orderInfoPanel, "customer", JComboBox.class);
            @SuppressWarnings("unchecked")
            JComboBox<String> statusComboBox = findComponentByName(orderInfoPanel, "status", JComboBox.class);
            JTextField deliveryDateField = findComponentByName(orderInfoPanel, "deliveryDate", JTextField.class);
            JTextField taxField = findComponentByName(orderInfoPanel, "tax", JTextField.class);
            JTextField discountField = findComponentByName(orderInfoPanel, "discount", JTextField.class);
            JTextField shippingFeeField = findComponentByName(orderInfoPanel, "shippingFee", JTextField.class);
            JTextArea notesArea = findComponentByName(orderInfoPanel, "notes", JTextArea.class);

            if (customerComboBox == null || customerComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Order order = existingOrder != null ? existingOrder : new Order();

            if (existingOrder == null) {
                order.setOrderNumber("ORD-" + System.currentTimeMillis());
            }

            order.setCustomer((Customer) customerComboBox.getSelectedItem());
            order.setStatus((String) statusComboBox.getSelectedItem());

            String deliveryDateText = deliveryDateField.getText().trim();
            if (!deliveryDateText.isEmpty()) {
                try {
                    order.setDeliveryDate(LocalDateTime.parse(deliveryDateText, dateFormatter));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(dialog, "Định dạng ngày giao hàng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            order.setTax(new BigDecimal(taxField.getText()));
            order.setDiscount(new BigDecimal(discountField.getText()));
            order.setShippingFee(new BigDecimal(shippingFeeField.getText()));
            order.setNotes(notesArea.getText());

            JTable itemsTable = findComponentByName(orderItemsPanel, "itemsTable", JTable.class);
            DefaultTableModel itemsModel = (DefaultTableModel) itemsTable.getModel();
            List<OrderItem> orderItems = new ArrayList<>();

            for (int i = 0; i < itemsModel.getRowCount(); i++) {
                OrderItem item = new OrderItem();
                item.setProductName((String) itemsModel.getValueAt(i, 0));
                item.setSku((String) itemsModel.getValueAt(i, 1));
                item.setQuantity(Integer.parseInt(itemsModel.getValueAt(i, 2).toString()));
                item.setUnitPrice(new BigDecimal(itemsModel.getValueAt(i, 3).toString()));
                item.setDiscount(new BigDecimal(itemsModel.getValueAt(i, 4).toString()));
                item.setLineTotal(new BigDecimal(itemsModel.getValueAt(i, 5).toString()));
                orderItems.add(item);
            }

            order.setOrderItems(orderItems);
            order.calculateTotal();

            boolean success;
            if (existingOrder == null) {
                success = orderService.createOrder(order);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Thêm đơn hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                success = orderService.updateOrder(order);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật đơn hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            if (!success) {
                JOptionPane.showMessageDialog(dialog, "Không thể lưu đơn hàng. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

            return success;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Có lỗi xảy ra: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void deleteOrder(long id, String orderNumber) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa đơn hàng '" + orderNumber + "'?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = orderService.deleteOrder(id);

            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa đơn hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadOrderData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa đơn hàng. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
