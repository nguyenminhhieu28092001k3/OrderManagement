/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.swing.controller;

import app.swing.model.SanPham;

import java.io.Serializable;
import java.util.List;

/**
 * Controller cho SanPham - kế thừa cả CRUD và Search capabilities
 * @author khaim
 */
public class SanPhamController extends BaseController<SanPham> {
    
    // Search controller để xử lý các truy vấn phức tạp
    private final SanPhamSearchController searchController;

    public SanPhamController() {
        super(SanPham.class);
        this.searchController = new SanPhamSearchController();
    }

    // ===== CRUD OPERATIONS (từ BaseController) =====

    public Serializable addNewProduct(SanPham sanPham) {
        if (sanPham.getTen() == null || String.valueOf(sanPham.getTen()).trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
        }
        if (sanPham.getGia() <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0.");
        }
        if (sanPham.getIdDoanhNghiep() <= 0) {
            throw new IllegalArgumentException("ID doanh nghiệp không hợp lệ.");
        }
        return add(sanPham);
    }

    public boolean updateProduct(SanPham sanPham) {
        if (sanPham.getTen() == null || sanPham.getTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
        }
        if (sanPham.getGia() <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0.");
        }
        if (sanPham.getIdDoanhNghiep() <= 0) {
            throw new IllegalArgumentException("ID doanh nghiệp không hợp lệ.");
        }
        try {
            update(sanPham);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(SanPham sanPham) {
        if (sanPham.getId() <= 0) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ.");
        }
        if (sanPham.getNguoiXoa() == null || sanPham.getNguoiXoa().trim().isEmpty()) {
            throw new IllegalArgumentException("Người xóa không được để trống.");
        }
        if (sanPham.getNgayXoa() == null) {
            throw new IllegalArgumentException("Ngày xóa không được để trống.");
        }

        // Check if the product exists
        SanPham existingProduct = searchController.findById(sanPham.getId());
        if (existingProduct == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại.");
        }

        // Set the fields for deletion
        existingProduct.setNguoiXoa(sanPham.getNguoiXoa());
        existingProduct.setNgayXoa(sanPham.getNgayXoa());

        try {
            update(existingProduct);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ===== SEARCH & PAGINATION OPERATIONS =====

    /**
     * Lấy sản phẩm với phân trang
     */
    public List<SanPham> getSanPhamByPage(int page, int pageSize) {
        return searchController.findAllWithPagination(page, pageSize);
    }

    /**
     * Tìm kiếm sản phẩm theo tên với phân trang
     */
    public BaseSearchController.PageResult<SanPham> searchSanPhamByName(String name, int page, int pageSize) {
        String condition = "ten LIKE ?0";
        return searchController.getPageResultByCondition(condition, page, pageSize, "%" + name + "%");
    }

    /**
     * Lấy sản phẩm với sắp xếp theo giá
     */
    public List<SanPham> getSanPhamSortedByPrice(int page, int pageSize, boolean ascending) {
        return searchController.findAllWithPaginationAndSort(page, pageSize, "gia", ascending);
    }

    /**
     * Tìm sản phẩm theo khoảng giá
     */
    public BaseSearchController.PageResult<SanPham> searchSanPhamByPriceRange(double minPrice, double maxPrice, int page, int pageSize) {
        String condition = "gia BETWEEN ?0 AND ?1";
        return searchController.getPageResultByCondition(condition, page, pageSize, minPrice, maxPrice);
    }

    /**
     * Lấy tổng số trang
     */
    public int getTotalPages(int pageSize) {
        return searchController.getTotalPages(pageSize);
    }

    /**
     * Lấy PageResult đầy đủ thông tin
     */
    public BaseSearchController.PageResult<SanPham> getPageResult(int page, int pageSize) {
        return searchController.getPageResult(page, pageSize);
    }

    // Inner class cho search operations
    private static class SanPhamSearchController extends BaseSearchController<SanPham> {
        public SanPhamSearchController() {
            super(SanPham.class);
        }
    }
}
