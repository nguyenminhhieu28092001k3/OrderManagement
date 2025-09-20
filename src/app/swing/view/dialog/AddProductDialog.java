package app.swing.view.dialog;

import app.swing.model.SanPham;
import app.swing.view.component.GenericComboBox;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;

public class AddProductDialog extends JDialog {
    private boolean succeeded = false;
    private JTextField txtIdDoanhNghiep, txtTen;
    private JFormattedTextField txtGia;
    private JTextArea txtMota;
    private SanPham sanPham;

    // Sample Doanh Nghiep ID for testing
    private static class DoanhNghiepItem {
        private final int id;
        private final String name;
        public DoanhNghiepItem(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        public String toString() { return name; }
    }

    // TODO: Sample Doanh Nghiep IDs for testing
    private static final DoanhNghiepItem[] doanhNghiepItems = {
        new DoanhNghiepItem(1, "COFFEE SHOP A"),
        new DoanhNghiepItem(2, "COFFEE SHOP B"),
        new DoanhNghiepItem(3, "COFFEE SHOP C")
    };

    public AddProductDialog() {
        try {
            if (!UIManager.getLookAndFeel().getName().contains("FlatLaf")) {
                UIManager.setLookAndFeel(new FlatLightLaf());
                SwingUtilities.updateComponentTreeUI(this);
            }
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf for dialog");
        }

        setTitle("Thêm sản phẩm mới");
        setModal(true);
        setSize(400, 325);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        // ID Doanh nghiệp
        add(new JLabel("Doanh nghiệp:"), gbc);
        gbc.gridx = 1;
        GenericComboBox<DoanhNghiepItem> cboDoanhNghiep = new GenericComboBox<>(java.util.Arrays.asList(doanhNghiepItems), DoanhNghiepItem::toString);
        cboDoanhNghiep.setSelectedIndex(0); // Select the first item by default
        cboDoanhNghiep.addActionListener(e -> {
            DoanhNghiepItem selectedItem = (DoanhNghiepItem) cboDoanhNghiep.getSelectedItem();
            if (selectedItem != null) {
                txtIdDoanhNghiep.setText(String.valueOf(selectedItem.getId()));
            }
        });
        add(cboDoanhNghiep, gbc);
        gbc.gridwidth = 1; // Reset to single column for next inputs
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5); // Reset insets for next inputs
        gbc.gridx = 1;
        txtIdDoanhNghiep = new JTextField();
        txtIdDoanhNghiep.setEditable(false); // Disable editing, set by combo box selection

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Tên sản phẩm:"), gbc);
        gbc.gridx = 1;
        txtTen = new JTextField();
        add(txtTen, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        txtMota = new JTextArea(4, 20);
        txtMota.setLineWrap(true);
        txtMota.setWrapStyleWord(true);
        JScrollPane scrollMota = new JScrollPane(txtMota);
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollMota, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Giá:"), gbc);
        gbc.gridx = 1;
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(true);
        NumberFormatter numberFormatter = new NumberFormatter(numberFormat);
        numberFormatter.setValueClass(Double.class);
        numberFormatter.setAllowsInvalid(false); // Prevents non-numeric input
        numberFormatter.setMinimum(0.0); // Optional: only allow positive numbers
        txtGia = new JFormattedTextField(numberFormatter);
        txtGia.setColumns(10);
        add(txtGia, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JButton btnOK = new JButton("OK");
        add(btnOK, gbc);
        gbc.gridx = 1;
        JButton btnCancel = new JButton("Hủy");
        add(btnCancel, gbc);

        btnOK.addActionListener(e -> {
            try {
                int idDoanhNghiep = Integer.parseInt(txtIdDoanhNghiep.getText().trim());
                String ten = txtTen.getText().trim();
                String mota = txtMota.getText().trim();
                txtGia.commitEdit();
                Number giaNumber = (Number) txtGia.getValue();
                double gia = giaNumber != null ? giaNumber.doubleValue() : 0;

                LocalDateTime ngayTao = LocalDateTime.now();
                // TODO: Temporarily hardcoded
                String nguoiTao = "khangho";

                if (ten.isEmpty()) throw new IllegalArgumentException("Tên không được để trống");
                if (gia <= 0) throw new IllegalArgumentException("Giá phải lớn hơn 0");
                if (idDoanhNghiep <= 0) throw new IllegalArgumentException("ID doanh nghiệp không hợp lệ");

                sanPham = new SanPham();
                sanPham.setIdDoanhNghiep(idDoanhNghiep);
                sanPham.setTen(ten);
                sanPham.setMota(mota);
                sanPham.setGia(gia);
                sanPham.setNguoiTao(nguoiTao);
                sanPham.setNguoiCapNhat(nguoiTao);
                sanPham.setNgayTao(ngayTao);
                sanPham.setNgayCapNhat(ngayTao);

                succeeded = true;
                dispose();
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Giá trị nhập vào không hợp lệ.",
                        "Lỗi nhập liệu",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi nhập liệu",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> {
            succeeded = false;
            dispose();
        });
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public SanPham getSanPham() {
        return sanPham;
    }
}