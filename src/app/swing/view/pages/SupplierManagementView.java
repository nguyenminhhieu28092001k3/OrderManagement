package app.swing.view.pages;

import app.swing.model.Supplier;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Management view for suppliers
 */
public class SupplierManagementView extends JFrame {

    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    private JTextField searchField;
    private SupplierService supplierService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private boolean isEmbedded = false;

    public SupplierManagementView() {
        this.supplierService = new SupplierService();
        this.isEmbedded = false;
        initComponents();
        loadSupplierData();
    }

    // Constructor for embedded mode
    public SupplierManagementView(boolean embedded) {
        this.supplierService = new SupplierService();
        this.isEmbedded = embedded;
        if (!embedded) {
            initComponents();
            loadSupplierData();
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

        JLabel titleLabel = new JLabel("Quản Lý Nhà Cung Cấp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("Thêm, chỉnh sửa và quản lý thông tin nhà cung cấp");
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
        loadSupplierData();

        // Add components
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(toolbarPanel, BorderLayout.PAGE_START);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void initComponents() {
        setTitle("Quản lý nhà cung cấp");
        setSize(1024, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));

        // Create header with actions
        JPanel headerPanel = createHeaderPanel();

        // Create toolbar with actions and search
        JPanel toolbarPanel = createToolbarPanel();

        // Supplier table panel
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
        JLabel titleLabel = new JLabel("Quản Lý Nhà Cung Cấp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Description
        JLabel descLabel = new JLabel("Thêm, chỉnh sửa và quản lý thông tin nhà cung cấp");
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

        addButton = createActionButton("Thêm nhà cung cấp", new Color(65, 105, 225));
        editButton = createActionButton("Sửa nhà cung cấp", new Color(60, 179, 113));
        deleteButton = createActionButton("Xóa nhà cung cấp", new Color(220, 20, 60));
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

        if (text.equals("Thêm nhà cung cấp")) {
            button.addActionListener(e -> showAddSupplierDialog());
        } else if (text.equals("Sửa nhà cung cấp")) {
            button.addActionListener(e -> {
                int selectedRow = supplierTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = supplierTable.convertRowIndexToModel(selectedRow);
                    long supplierId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                    showEditSupplierDialog(supplierId);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn nhà cung cấp để sửa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (text.equals("Xóa nhà cung cấp")) {
            button.addActionListener(e -> {
                int selectedRow = supplierTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = supplierTable.convertRowIndexToModel(selectedRow);
                    long supplierId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                    String supplierName = tableModel.getValueAt(modelRow, 1).toString();
                    deleteSupplier(supplierId, supplierName);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn nhà cung cấp để xóa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (text.equals("Làm mới")) {
            button.addActionListener(e -> loadSupplierData());
        }

        return button;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        // Create table model
        String[] columns = {"ID", "Tên nhà cung cấp", "Người liên hệ", "Email", "Điện thoại", "Địa chỉ", "Ghi chú"};
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
        supplierTable = new JTable(tableModel);
        supplierTable.setRowHeight(40);
        supplierTable.setShowVerticalLines(false);
        supplierTable.setGridColor(new Color(230, 230, 230));
        supplierTable.getTableHeader().setReorderingAllowed(false);
        supplierTable.getTableHeader().setBackground(new Color(240, 240, 240));
        supplierTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        supplierTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        supplierTable.setFont(new Font("Arial", Font.PLAIN, 13));
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.setSelectionBackground(new Color(220, 235, 250));
        supplierTable.setSelectionForeground(Color.BLACK);

        // Set column widths
        supplierTable.getColumnModel().getColumn(0).setMaxWidth(70); // ID column
        supplierTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        supplierTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Contact person
        supplierTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Email
        supplierTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Phone
        supplierTable.getColumnModel().getColumn(5).setPreferredWidth(200); // Address
        supplierTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Notes

        // Add row selection listener for double click
        supplierTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = supplierTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = supplierTable.convertRowIndexToModel(selectedRow);
                        long supplierId = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
                        showEditSupplierDialog(supplierId);
                    }
                }
            }
        });

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        supplierTable.setRowSorter(sorter);

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
        JScrollPane scrollPane = new JScrollPane(supplierTable);
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

    private void loadSupplierData() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all suppliers
        List<Supplier> suppliers = supplierService.getAllSuppliers();

        // Add to table
        for (Supplier supplier : suppliers) {
            Object[] row = {
                supplier.getId(),
                supplier.getName(),
                supplier.getContactName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.getNote()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddSupplierDialog() {
        // Create dialog
        JDialog dialog = new JDialog(this, "Thêm nhà cung cấp mới", true);
        dialog.setSize(500, 450);
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
        JLabel nameLabel = new JLabel("Tên nhà cung cấp*:");
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Contact name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel contactNameLabel = new JLabel("Người liên hệ:");
        formPanel.add(contactNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField contactNameField = new JTextField(20);
        formPanel.add(contactNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Điện thoại:");
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel addressLabel = new JLabel("Địa chỉ:");
        formPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField addressField = new JTextField(20);
        formPanel.add(addressField, gbc);

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
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập tên nhà cung cấp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if name exists
            if (supplierService.nameExists(name, null)) {
                JOptionPane.showMessageDialog(dialog,
                    "Tên nhà cung cấp đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create supplier
            Supplier supplier = new Supplier();
            supplier.setName(name);
            supplier.setContactName(contactNameField.getText().trim());
            supplier.setEmail(emailField.getText().trim());
            supplier.setPhone(phoneField.getText().trim());
            supplier.setAddress(addressField.getText().trim());
            supplier.setNote(notesArea.getText().trim());

            boolean success = supplierService.createSupplier(supplier);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                    "Thêm nhà cung cấp thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadSupplierData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Không thể thêm nhà cung cấp. Vui lòng thử lại!",
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

    private void showEditSupplierDialog(long id) {
        // Get supplier by ID
        Supplier supplier = supplierService.getSupplierById(id);
        if (supplier == null) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy thông tin nhà cung cấp!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog(this, "Sửa thông tin nhà cung cấp", true);
        dialog.setSize(500, 450);
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
        JLabel nameLabel = new JLabel("Tên nhà cung cấp*:");
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(20);
        nameField.setText(supplier.getName());
        formPanel.add(nameField, gbc);

        // Contact name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel contactNameLabel = new JLabel("Người liên hệ:");
        formPanel.add(contactNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField contactNameField = new JTextField(20);
        contactNameField.setText(supplier.getContactName());
        formPanel.add(contactNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField emailField = new JTextField(20);
        emailField.setText(supplier.getEmail());
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Điện thoại:");
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField phoneField = new JTextField(20);
        phoneField.setText(supplier.getPhone());
        formPanel.add(phoneField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel addressLabel = new JLabel("Địa chỉ:");
        formPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField addressField = new JTextField(20);
        addressField.setText(supplier.getAddress());
        formPanel.add(addressField, gbc);

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
        notesArea.setText(supplier.getNote());
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
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập tên nhà cung cấp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if name exists (for other suppliers)
            if (!name.equals(supplier.getName()) && supplierService.nameExists(name, supplier.getId())) {
                JOptionPane.showMessageDialog(dialog,
                    "Tên nhà cung cấp đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update supplier
            supplier.setName(name);
            supplier.setContactName(contactNameField.getText().trim());
            supplier.setEmail(emailField.getText().trim());
            supplier.setPhone(phoneField.getText().trim());
            supplier.setAddress(addressField.getText().trim());
            supplier.setNote(notesArea.getText().trim());

            boolean success = supplierService.updateSupplier(supplier);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                    "Cập nhật thông tin nhà cung cấp thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadSupplierData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Không thể cập nhật thông tin nhà cung cấp. Vui lòng thử lại!",
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

    private void deleteSupplier(long id, String name) {
        // Confirm delete
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa nhà cung cấp '" + name + "'?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = supplierService.deleteSupplier(id);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Xóa nhà cung cấp thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                loadSupplierData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể xóa nhà cung cấp. Nhà cung cấp này đang được sử dụng trong sản phẩm!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
