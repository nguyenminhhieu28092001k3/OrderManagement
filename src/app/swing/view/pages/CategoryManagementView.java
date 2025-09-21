package app.swing.view.pages;

import app.swing.model.Category;
import app.swing.service.CategoryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Giao diện quản lý danh mục
 */
public class CategoryManagementView extends JFrame {

    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    private JTextField searchField;
    private CategoryService categoryService;
    private JTree categoryTree;
    private DefaultTreeModel treeModel;
    private Map<Long, DefaultMutableTreeNode> categoryNodeMap;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private final String TREE_VIEW = "tree_view";
    private final String TABLE_VIEW = "table_view";
    private JToggleButton treeViewButton;
    private JToggleButton tableViewButton;
    private boolean isEmbedded = false;

    public CategoryManagementView() {
        this.categoryService = new CategoryService();
        this.categoryNodeMap = new HashMap<>();
        this.isEmbedded = false;
        initComponents();
        loadCategoryData();
    }

    // Constructor for embedded mode
    public CategoryManagementView(boolean embedded) {
        this.categoryService = new CategoryService();
        this.categoryNodeMap = new HashMap<>();
        this.isEmbedded = embedded;
        if (!embedded) {
            initComponents();
            loadCategoryData();
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

        JLabel titleLabel = new JLabel("Quản Lý Danh Mục Sản Phẩm");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("Thêm, chỉnh sửa và quản lý cấu trúc danh mục sản phẩm");
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

        // Create card panel for tree and table views
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Create tree view panel
        JPanel treePanel = createTreePanel();
        cardPanel.add(treePanel, TREE_VIEW);

        // Create table view panel
        JPanel tablePanel = createTablePanel();
        cardPanel.add(tablePanel, TABLE_VIEW);

        // Default to tree view
        cardLayout.show(cardPanel, TREE_VIEW);

        // Initialize data
        loadCategoryData();

        // Add components
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(toolbarPanel, BorderLayout.PAGE_START);
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void initComponents() {
        setTitle("Quản lý danh mục sản phẩm");
        setSize(1024, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));

        // Create header with actions
        JPanel headerPanel = createHeaderPanel();

        // Create toolbar with actions and search
        JPanel toolbarPanel = createToolbarPanel();

        // Card panel for tree and table views
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Create tree view panel
        JPanel treePanel = createTreePanel();
        cardPanel.add(treePanel, TREE_VIEW);

        // Create table view panel
        JPanel tablePanel = createTablePanel();
        cardPanel.add(tablePanel, TABLE_VIEW);

        // Default to tree view
        cardLayout.show(cardPanel, TREE_VIEW);

        // Status bar
        JPanel statusPanel = createStatusPanel();

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(toolbarPanel, BorderLayout.PAGE_START);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
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
        JLabel titleLabel = new JLabel("Quản Lý Danh Mục Sản Phẩm");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Description
        JLabel descLabel = new JLabel("Thêm, chỉnh sửa và quản lý cấu trúc danh mục sản phẩm");
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

        addButton = createActionButton("Thêm danh mục", new Color(65, 105, 225));
        editButton = createActionButton("Sửa danh mục", new Color(60, 179, 113));
        deleteButton = createActionButton("Xóa danh mục", new Color(220, 20, 60));
        refreshButton = createActionButton("Làm mới", new Color(30, 144, 255));

        // View toggle buttons
        treeViewButton = new JToggleButton("Xem cây danh mục");
        treeViewButton.setSelected(true);
        tableViewButton = new JToggleButton("Xem bảng");

        // Add to toggle group
        ButtonGroup viewToggleGroup = new ButtonGroup();
        viewToggleGroup.add(treeViewButton);
        viewToggleGroup.add(tableViewButton);

        treeViewButton.addActionListener(e -> cardLayout.show(cardPanel, TREE_VIEW));
        tableViewButton.addActionListener(e -> cardLayout.show(cardPanel, TABLE_VIEW));

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(refreshButton);
        actionPanel.add(Box.createHorizontalStrut(20));
        actionPanel.add(treeViewButton);
        actionPanel.add(tableViewButton);

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

        if (text.equals("Thêm danh mục")) {
            button.addActionListener(e -> showAddCategoryDialog());
        } else if (text.equals("Sửa danh mục")) {
            button.addActionListener(e -> {
                if (tableViewButton.isSelected()) {
                    // Table view is active
                    int selectedRow = categoryTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = categoryTable.convertRowIndexToModel(selectedRow);
                        long categoryId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                        showEditCategoryDialog(categoryId);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Vui lòng chọn danh mục để sửa!",
                            "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    // Tree view is active
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)categoryTree.getLastSelectedPathComponent();
                    if (selectedNode != null && selectedNode.getUserObject() instanceof Category) {
                        Category category = (Category)selectedNode.getUserObject();
                        showEditCategoryDialog(category.getId());
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Vui lòng chọn danh mục để sửa!",
                            "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
        } else if (text.equals("Xóa danh mục")) {
            button.addActionListener(e -> {
                if (tableViewButton.isSelected()) {
                    // Table view is active
                    int selectedRow = categoryTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = categoryTable.convertRowIndexToModel(selectedRow);
                        long categoryId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                        String categoryName = tableModel.getValueAt(modelRow, 1).toString();
                        deleteCategory(categoryId, categoryName);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Vui lòng chọn danh mục để xóa!",
                            "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    // Tree view is active
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)categoryTree.getLastSelectedPathComponent();
                    if (selectedNode != null && selectedNode.getUserObject() instanceof Category) {
                        Category category = (Category)selectedNode.getUserObject();
                        deleteCategory(category.getId(), category.getName());
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Vui lòng chọn danh mục để xóa!",
                            "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
        } else if (text.equals("Làm mới")) {
            button.addActionListener(e -> loadCategoryData());
        }

        return button;
    }

    private JPanel createTreePanel() {
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        // Create root node for tree
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Tất cả danh mục");
        treeModel = new DefaultTreeModel(rootNode);

        // Create tree
        categoryTree = new JTree(treeModel);
        categoryTree.setShowsRootHandles(true);
        categoryTree.setRootVisible(true);

        // Double-click to edit
        categoryTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)categoryTree.getLastSelectedPathComponent();
                    if (selectedNode != null && selectedNode.getUserObject() instanceof Category) {
                        Category category = (Category)selectedNode.getUserObject();
                        showEditCategoryDialog(category.getId());
                    }
                }
            }
        });

        // Add tree to scroll pane
        JScrollPane treeScrollPane = new JScrollPane(categoryTree);
        treeScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        treePanel.add(treeScrollPane, BorderLayout.CENTER);

        return treePanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        // Create table model
        String[] columns = {"ID", "Tên danh mục", "Danh mục cha", "Mô tả"};
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
        categoryTable = new JTable(tableModel);
        categoryTable.setRowHeight(40);
        categoryTable.setShowVerticalLines(false);
        categoryTable.setGridColor(new Color(230, 230, 230));
        categoryTable.getTableHeader().setReorderingAllowed(false);
        categoryTable.getTableHeader().setBackground(new Color(240, 240, 240));
        categoryTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        categoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        categoryTable.setFont(new Font("Arial", Font.PLAIN, 13));
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.setSelectionBackground(new Color(220, 235, 250));
        categoryTable.setSelectionForeground(Color.BLACK);

        // Set column widths
        categoryTable.getColumnModel().getColumn(0).setMaxWidth(70); // ID column
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        categoryTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Parent

        // Add row selection listener for double click
        categoryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = categoryTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = categoryTable.convertRowIndexToModel(selectedRow);
                        long categoryId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                        showEditCategoryDialog(categoryId);
                    }
                }
            }
        });

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        categoryTable.setRowSorter(sorter);

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
        JScrollPane scrollPane = new JScrollPane(categoryTable);
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

    private void loadCategoryData() {
        // Clear table and tree
        tableModel.setRowCount(0);
        categoryNodeMap.clear();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();

        // Get all categories
        List<Category> categories = categoryService.getAllCategories();

        // First add all categories to map
        for (Category category : categories) {
            DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(category);
            categoryNodeMap.put(category.getId(), categoryNode);

            // Add to table
            Object[] row = {
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getName() : "-",
                category.getDescription()
            };
            tableModel.addRow(row);
        }

        // Then build tree structure
        for (Category category : categories) {
            DefaultMutableTreeNode node = categoryNodeMap.get(category.getId());
            if (category.hasParent() && categoryNodeMap.containsKey(category.getParentId())) {
                // Add as child to parent
                DefaultMutableTreeNode parentNode = categoryNodeMap.get(category.getParentId());
                parentNode.add(node);
            } else {
                // Add to root
                root.add(node);
            }
        }

        // Refresh tree
        treeModel.reload();
        for (int i = 0; i < categoryTree.getRowCount(); i++) {
            categoryTree.expandRow(i);
        }
    }

    private void showAddCategoryDialog() {
        // Create dialog
        JDialog dialog = new JDialog(this, "Thêm danh mục mới", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Name (required)
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Tên danh mục*:");
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Parent category (optional)
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel parentLabel = new JLabel("Danh mục cha:");
        formPanel.add(parentLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;

        // Create combobox with all categories
        List<Category> allCategories = categoryService.getAllCategories();
        DefaultComboBoxModel<Object> comboModel = new DefaultComboBoxModel<>();
        comboModel.addElement("-- Không có --");
        for (Category cat : allCategories) {
            comboModel.addElement(cat);
        }

        JComboBox<Object> parentComboBox = new JComboBox<>(comboModel);
        formPanel.add(parentComboBox, gbc);

        // Description (optional)
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel descriptionLabel = new JLabel("Mô tả:");
        formPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);

        // Required fields note
        gbc.gridx = 0;
        gbc.gridy = 3;
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
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập tên danh mục!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if name exists
            if (categoryService.nameExists(name, null)) {
                JOptionPane.showMessageDialog(dialog,
                    "Tên danh mục đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create category
            Category category = new Category();
            category.setName(name);

            // Set parent if selected
            if (parentComboBox.getSelectedIndex() > 0) {
                Category parentCategory = (Category) parentComboBox.getSelectedItem();
                category.setParentId(parentCategory.getId());
            }

            category.setDescription(descriptionArea.getText().trim());

            boolean success = categoryService.createCategory(category);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                    "Thêm danh mục thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadCategoryData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Không thể thêm danh mục. Vui lòng thử lại!",
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

    private void showEditCategoryDialog(long id) {
        // Get category by ID
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy thông tin danh mục!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog(this, "Sửa thông tin danh mục", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Name (required)
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Tên danh mục*:");
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(20);
        nameField.setText(category.getName());
        formPanel.add(nameField, gbc);

        // Parent category (optional)
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel parentLabel = new JLabel("Danh mục cha:");
        formPanel.add(parentLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;

        // Create combobox with all categories except this one and its children
        List<Category> allCategories = categoryService.getAllCategories();
        DefaultComboBoxModel<Object> comboModel = new DefaultComboBoxModel<>();
        comboModel.addElement("-- Không có --");

        for (Category cat : allCategories) {
            // Skip self and children to prevent circular references
            if (cat.getId() != category.getId()) {
                comboModel.addElement(cat);
            }
        }

        JComboBox<Object> parentComboBox = new JComboBox<>(comboModel);

        // Select current parent if exists
        if (category.hasParent()) {
            for (int i = 0; i < comboModel.getSize(); i++) {
                if (comboModel.getElementAt(i) instanceof Category) {
                    Category itemCat = (Category) comboModel.getElementAt(i);
                    if (itemCat.getId() == category.getParentId()) {
                        parentComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } else {
            parentComboBox.setSelectedIndex(0);
        }

        formPanel.add(parentComboBox, gbc);

        // Description (optional)
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel descriptionLabel = new JLabel("Mô tả:");
        formPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setText(category.getDescription());
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);

        // Required fields note
        gbc.gridx = 0;
        gbc.gridy = 3;
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
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập tên danh mục!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if name exists (for other categories)
            if (!name.equals(category.getName()) && categoryService.nameExists(name, category.getId())) {
                JOptionPane.showMessageDialog(dialog,
                    "Tên danh mục đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update category
            category.setName(name);

            // Set parent if selected
            if (parentComboBox.getSelectedIndex() > 0) {
                Category parentCategory = (Category) parentComboBox.getSelectedItem();
                category.setParentId(parentCategory.getId());
            } else {
                category.setParentId(null);
            }

            category.setDescription(descriptionArea.getText().trim());

            boolean success = categoryService.updateCategory(category);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                    "Cập nhật thông tin danh mục thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadCategoryData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Không thể cập nhật thông tin danh mục. Vui lòng thử lại!",
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

    private void deleteCategory(long id, String name) {
        // Confirm delete
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa danh mục '" + name + "'?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = categoryService.deleteCategory(id);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Xóa danh mục thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                loadCategoryData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể xóa danh mục. Danh mục này có thể đang được sử dụng hoặc có chứa danh mục con!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
