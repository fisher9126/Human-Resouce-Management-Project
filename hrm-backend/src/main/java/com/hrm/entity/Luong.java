package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "luong",
       uniqueConstraints = @UniqueConstraint(columnNames = {"nhan_vien_id", "thang", "nam"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Luong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nhan_vien_id", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "thang", nullable = false)
    private Integer thang;

    @Column(name = "nam", nullable = false)
    private Integer nam;

    @Column(name = "luong_co_ban", nullable = false, precision = 15, scale = 2)
    private BigDecimal luongCoBan;

    @Column(name = "phu_cap", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal phuCap = BigDecimal.ZERO;

    @Column(name = "thuong", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal thuong = BigDecimal.ZERO;

    @Column(name = "khau_tru_bao_hiem", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal khauTruBaoHiem = BigDecimal.ZERO;

    @Column(name = "khau_tru_thue", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal khauTruThue = BigDecimal.ZERO;

    @Column(name = "thuc_nhan", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal thucNhan = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private TrangThaiLuong trangThai = TrangThaiLuong.Draft;

    public enum TrangThaiLuong {
        Draft, DaDuyet, DaThanhToan
    }
}
