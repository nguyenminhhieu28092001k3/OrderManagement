/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.swing.model;


import javax.persistence.*;
import java.time.LocalDateTime;
/**
 *
 * @author khaim
 */
@Entity
@Table(name = "san_pham")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_doanh_nghiep")
    private int idDoanhNghiep;

    private String ten;

    @Column(columnDefinition = "text")
    private String mota;

    private double gia;

    @Column(name = "key_search", columnDefinition = "tsvector", insertable = false, updatable = false)
    private String keySearch;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "nguoi_tao")
    private String nguoiTao;

    @Column(name = "nguoi_cap_nhat")
    private String nguoiCapNhat;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "nguoi_xoa")
    private String nguoiXoa;

    @Column(name = "ngay_xoa")
    private LocalDateTime ngayXoa;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdDoanhNghiep() { return idDoanhNghiep; }
    public void setIdDoanhNghiep(int idDoanhNghiep) { this.idDoanhNghiep = idDoanhNghiep; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getMota() { return mota; }
    public void setMota(String mota) { this.mota = mota; }

    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }

    public String getKeySearch() { return keySearch; }
    public void setKeySearch(String keySearch) { this.keySearch = keySearch; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public String getNguoiTao() { return nguoiTao; }
    public void setNguoiTao(String nguoiTao) { this.nguoiTao = nguoiTao; }

    public String getNguoiCapNhat() { return nguoiCapNhat; }
    public void setNguoiCapNhat(String nguoiCapNhat) { this.nguoiCapNhat = nguoiCapNhat; }

    public LocalDateTime getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(LocalDateTime ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    public String getNguoiXoa() { return nguoiXoa; }
    public void setNguoiXoa(String nguoiXoa) { this.nguoiXoa = nguoiXoa; }

    public LocalDateTime getNgayXoa() { return ngayXoa; }
    public void setNgayXoa(LocalDateTime ngayXoa) { this.ngayXoa = ngayXoa; }
}
