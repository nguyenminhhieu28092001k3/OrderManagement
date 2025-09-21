package app.swing.view.pages;

import app.swing.model.Category;
import app.swing.model.Product;
import app.swing.model.Supplier;
import app.swing.service.CategoryService;
import app.swing.service.ProductService;
import app.swing.service.SupplierService;

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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Management view for products
 */
public class ProductManagementView extends JFrame {

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    private JTextField searchField;
    private ProductService productService;
    private CategoryService categoryService;
    private SupplierService supplierService;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private boolean isEmbedded = false;

    public ProductManagementView() {
        this.productService = new ProductService();
        this.categoryService = new CategoryService();
        this.supplierService = new SupplierService();
        this.isEmbedded = false;
        initComponents();
        loadProductData();
    }

    // Constructor for embedded mode
    public ProductManagementView(boolean embedded) {
        this.productService = new ProductService();
        this.categoryService = new CategoryService();
        this.supplierService = new SupplierService();
        this.isEmbedded = embedded;
        if (!embedded) {
            initComponents();
            loadProductData();
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

        JLabel titleLabel = new JLabel("Quản Lý Sản Phẩm");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("Thêm, chỉnh sửa và quản lý thông tin sản phẩm và kho hàng");
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
        loadProductData();

        // Add components
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(toolbarPanel, BorderLayout.PAGE_START);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void initComponents() {
        setTitle("Quản lý sản phẩm");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));

        // Create header with actions
        JPanel headerPanel = createHeaderPanel();

        // Create toolbar with actions and search
        JPanel toolbarPanel = createToolbarPanel();

        // Product table panel
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
        JLabel titleLabel = new JLabel("Quản Lý Sản Phẩm");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Description
        JLabel descLabel = new JLabel("Thêm, chỉnh sửa và quản lý thông tin sản phẩm và kho hàng");
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

        addButton = createActionButton("Thêm sản phẩm", new Color(65, 105, 225));
        editButton = createActionButton("Sửa sản phẩm", new Color(60, 179, 113));
        deleteButton = createActionButton("Xóa sản phẩm", new Color(220, 20, 60));
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

        if (text.equals("Thêm sản phẩm")) {
            button.addActionListener(e -> showAddProductDialog());
        } else if (text.equals("Sửa sản phẩm")) {
            button.addActionListener(e -> {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = productTable.convertRowIndexToModel(selectedRow);
                    long productId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                    showEditProductDialog(productId);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn sản phẩm để sửa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (text.equals("Xóa sản phẩm")) {
            button.addActionListener(e -> {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = productTable.convertRowIndexToModel(selectedRow);
                    long productId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                    String productName = tableModel.getValueAt(modelRow, 2).toString();
                    deleteProduct(productId, productName);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn sản phẩm để xóa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (text.equals("Làm mới")) {
            button.addActionListener(e -> loadProductData());
        }

        return button;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        // Create table model
        String[] columns = {"ID", "SKU", "Tên sản phẩm", "Danh mục", "Nhà cung cấp", "Giá bán", "Giá nhập", "Tồn kho", "Mức tái đặt", "Trạng thái"};
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
        productTable = new JTable(tableModel);
        productTable.setRowHeight(40);
        productTable.setShowVerticalLines(false);
        productTable.setGridColor(new Color(230, 230, 230));
        productTable.getTableHeader().setReorderingAllowed(false);
        productTable.getTableHeader().setBackground(new Color(240, 240, 240));
        productTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        productTable.setFont(new Font("Arial", Font.PLAIN, 13));
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setSelectionBackground(new Color(220, 235, 250));
        productTable.setSelectionForeground(Color.BLACK);

        // Set column widths
        productTable.getColumnModel().getColumn(0).setMaxWidth(70); // ID column
        productTable.getColumnModel().getColumn(1).setPreferredWidth(100); // SKU
        productTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Name
        productTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Category
        productTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Supplier
        productTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Price
        productTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Cost
        productTable.getColumnModel().getColumn(7).setMaxWidth(80); // Stock
        productTable.getColumnModel().getColumn(8).setMaxWidth(80); // Reorder
        productTable.getColumnModel().getColumn(9).setMaxWidth(80); // Status

        // Add row selection listener for double click
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = productTable.convertRowIndexToModel(selectedRow);
                        long productId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                        showEditProductDialog(productId);
                    }
                }
            }
        });

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        productTable.setRowSorter(sorter);

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
        JScrollPane scrollPane = new JScrollPane(productTable);
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

    private void loadProductData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all products
        List<Product> products = productService.getAllProducts();

        // Add to table
        for (Product product : products) {
            Object[] row = {
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getCategory() != null ? product.getCategory().getName() : "-",
                product.getSupplier() != null ? product.getSupplier().getName() : "-",
                formatCurrency(product.getPrice()),
                formatCurrency(product.getCost()),
                String.valueOf(product.getStockQuantity()),
                String.valueOf(product.getReorderLevel()),
                product.isActive() ? "Hoạt động" : "Đã khóa"
            };
            tableModel.addRow(row);
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0";
        return decimalFormat.format(amount) + " VND";
    }

    private void showAddProductDialog() {
        // Create dialog
        JDialog dialog = new JDialog(this, "Thêm sản phẩm mới", true);
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // SKU (required)
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel skuLabel = new JLabel("SKU*:");
        formPanel.add(skuLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField skuField = new JTextField(20);
        formPanel.add(skuField, gbc);

        // Name (required)
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Tên sản phẩm*:");
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel descLabel = new JLabel("Mô tả:");
        formPanel.add(descLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descArea);
        formPanel.add(descScrollPane, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        JLabel categoryLabel = new JLabel("Danh mục:");
        formPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        DefaultComboBoxModel<Object> categoryModel = new DefaultComboBoxModel<>();
        categoryModel.addElement("-- Chọn danh mục --");
        List<Category> categories = categoryService.getAllCategories();
        for (Category cat : categories) {
            categoryModel.addElement(cat);
        }
        JComboBox<Object> categoryComboBox = new JComboBox<>(categoryModel);
        formPanel.add(categoryComboBox, gbc);

        // Supplier
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel supplierLabel = new JLabel("Nhà cung cấp:");
        formPanel.add(supplierLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        DefaultComboBoxModel<Object> supplierModel = new DefaultComboBoxModel<>();
        supplierModel.addElement("-- Chọn nhà cung cấp --");
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        for (Supplier sup : suppliers) {
            supplierModel.addElement(sup);
        }
        JComboBox<Object> supplierComboBox = new JComboBox<>(supplierModel);
        formPanel.add(supplierComboBox, gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel priceLabel = new JLabel("Giá bán*:");
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField priceField = new JTextField(20);
        priceField.setText("0.00");
        formPanel.add(priceField, gbc);

        // Cost
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel costLabel = new JLabel("Giá nhập*:");
        formPanel.add(costLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField costField = new JTextField(20);
        costField.setText("0.00");
        formPanel.add(costField, gbc);

        // Stock Quantity
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel stockLabel = new JLabel("Số lượng tồn kho:");
        formPanel.add(stockLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        formPanel.add(stockSpinner, gbc);

        // Reorder Level
        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel reorderLabel = new JLabel("Mức tái đặt hàng:");
        formPanel.add(reorderLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        JSpinner reorderSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        formPanel.add(reorderSpinner, gbc);

        // Active status
        gbc.gridx = 0;
        gbc.gridy = 9;
        JLabel activeLabel = new JLabel("Trạng thái:");
        formPanel.add(activeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        JCheckBox activeCheckBox = new JCheckBox("Hoạt động");
        activeCheckBox.setSelected(true);
        formPanel.add(activeCheckBox, gbc);

        // Required fields note
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
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
            String sku = skuField.getText().trim();
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String costText = costField.getText().trim();

            if (sku.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập đầy đủ thông tin bắt buộc!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if SKU exists
            if (productService.skuExists(sku, null)) {
                JOptionPane.showMessageDialog(dialog,
                    "SKU đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate price and cost
            BigDecimal price, cost;
            try {
                price = new BigDecimal(priceText);
                cost = new BigDecimal(costText);
                if (price.compareTo(BigDecimal.ZERO) < 0 || cost.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Giá bán và giá nhập phải là số hợp lệ và không âm!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create product
            Product product = new Product();
            product.setSku(sku);
            product.setName(name);
            product.setDescription(descArea.getText().trim());

            // Set category if selected
            if (categoryComboBox.getSelectedIndex() > 0) {
                Category category = (Category) categoryComboBox.getSelectedItem();
                product.setCategoryId(category.getId());
            }

            // Set supplier if selected
            if (supplierComboBox.getSelectedIndex() > 0) {
                Supplier supplier = (Supplier) supplierComboBox.getSelectedItem();
                product.setSupplierId(supplier.getId());
            }

            product.setPrice(price);
            product.setCost(cost);
            product.setStockQuantity((Integer) stockSpinner.getValue());
            product.setReorderLevel((Integer) reorderSpinner.getValue());
            product.setActive(activeCheckBox.isSelected());

            boolean success = productService.createProduct(product);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                    "Thêm sản phẩm thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadProductData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Không thể thêm sản phẩm. Vui lòng thử lại!",
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

    private void showEditProductDialog(long id) {
        // Get product by ID
        Product product = productService.getProductById(id);
        if (product == null) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy thông tin sản phẩm!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog(this, "Sửa thông tin sản phẩm", true);
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // SKU (required)
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel skuLabel = new JLabel("SKU*:");
        formPanel.add(skuLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField skuField = new JTextField(20);
        skuField.setText(product.getSku());
        formPanel.add(skuField, gbc);

        // Name (required)
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Tên sản phẩm*:");
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField nameField = new JTextField(20);
        nameField.setText(product.getName());
        formPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel descLabel = new JLabel("Mô tả:");
        formPanel.add(descLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setText(product.getDescription());
        JScrollPane descScrollPane = new JScrollPane(descArea);
        formPanel.add(descScrollPane, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        JLabel categoryLabel = new JLabel("Danh mục:");
        formPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        DefaultComboBoxModel<Object> categoryModel = new DefaultComboBoxModel<>();
        categoryModel.addElement("-- Chọn danh mục --");
        List<Category> categories = categoryService.getAllCategories();
        for (Category cat : categories) {
            categoryModel.addElement(cat);
        }
        JComboBox<Object> categoryComboBox = new JComboBox<>(categoryModel);

        // Select current category
        if (product.getCategory() != null) {
            for (int i = 0; i < categoryModel.getSize(); i++) {
                if (categoryModel.getElementAt(i) instanceof Category) {
                    Category itemCat = (Category) categoryModel.getElementAt(i);
                    if (itemCat.getId() == product.getCategory().getId()) {
                        categoryComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
        formPanel.add(categoryComboBox, gbc);

        // Supplier
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel supplierLabel = new JLabel("Nhà cung cấp:");
        formPanel.add(supplierLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        DefaultComboBoxModel<Object> supplierModel = new DefaultComboBoxModel<>();
        supplierModel.addElement("-- Chọn nhà cung cấp --");
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        for (Supplier sup : suppliers) {
            supplierModel.addElement(sup);
        }
        JComboBox<Object> supplierComboBox = new JComboBox<>(supplierModel);

        // Select current supplier
        if (product.getSupplier() != null) {
            for (int i = 0; i < supplierModel.getSize(); i++) {
                if (supplierModel.getElementAt(i) instanceof Supplier) {
                    Supplier itemSup = (Supplier) supplierModel.getElementAt(i);
                    if (itemSup.getId() == product.getSupplier().getId()) {
                        supplierComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
        formPanel.add(supplierComboBox, gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel priceLabel = new JLabel("Giá bán*:");
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField priceField = new JTextField(20);
        priceField.setText(product.getPrice().toString());
        formPanel.add(priceField, gbc);

        // Cost
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel costLabel = new JLabel("Giá nhập*:");
        formPanel.add(costLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField costField = new JTextField(20);
        costField.setText(product.getCost().toString());
        formPanel.add(costField, gbc);

        // Stock Quantity
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel stockLabel = new JLabel("Số lượng tồn kho:");
        formPanel.add(stockLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(product.getStockQuantity(), 0, Integer.MAX_VALUE, 1));
        formPanel.add(stockSpinner, gbc);

        // Reorder Level
        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel reorderLabel = new JLabel("Mức tái đặt hàng:");
        formPanel.add(reorderLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        JSpinner reorderSpinner = new JSpinner(new SpinnerNumberModel(product.getReorderLevel(), 0, Integer.MAX_VALUE, 1));
        formPanel.add(reorderSpinner, gbc);

        // Active status
        gbc.gridx = 0;
        gbc.gridy = 9;
        JLabel activeLabel = new JLabel("Trạng thái:");
        formPanel.add(activeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        JCheckBox activeCheckBox = new JCheckBox("Hoạt động");
        activeCheckBox.setSelected(product.isActive());
        formPanel.add(activeCheckBox, gbc);

        // Required fields note
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
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
            String sku = skuField.getText().trim();
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String costText = costField.getText().trim();

            if (sku.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập đầy đủ thông tin bắt buộc!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if SKU exists (for other products)
            if (!sku.equals(product.getSku()) && productService.skuExists(sku, product.getId())) {
                JOptionPane.showMessageDialog(dialog,
                    "SKU đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate price and cost
            BigDecimal price, cost;
            try {
                price = new BigDecimal(priceText);
                cost = new BigDecimal(costText);
                if (price.compareTo(BigDecimal.ZERO) < 0 || cost.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Giá bán và giá nhập phải là số hợp lệ và không âm!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update product
            product.setSku(sku);
            product.setName(name);
            product.setDescription(descArea.getText().trim());

            // Set category if selected
            if (categoryComboBox.getSelectedIndex() > 0) {
                Category category = (Category) categoryComboBox.getSelectedItem();
                product.setCategoryId(category.getId());
            } else {
                product.setCategoryId(null);
            }

            // Set supplier if selected
            if (supplierComboBox.getSelectedIndex() > 0) {
                Supplier supplier = (Supplier) supplierComboBox.getSelectedItem();
                product.setSupplierId(supplier.getId());
            } else {
                product.setSupplierId(null);
            }

            product.setPrice(price);
            product.setCost(cost);
            product.setStockQuantity((Integer) stockSpinner.getValue());
            product.setReorderLevel((Integer) reorderSpinner.getValue());
            product.setActive(activeCheckBox.isSelected());

            boolean success = productService.updateProduct(product);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                    "Cập nhật thông tin sản phẩm thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadProductData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Không thể cập nhật thông tin sản phẩm. Vui lòng thử lại!",
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

    private void deleteProduct(long id, String name) {
        // Confirm delete
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa sản phẩm '" + name + "'?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = productService.deleteProduct(id);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Xóa sản phẩm thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                loadProductData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể xóa sản phẩm. Sản phẩm này có thể đã được sử dụng trong đơn hàng hoặc có lịch sử xuất nhập kho!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
