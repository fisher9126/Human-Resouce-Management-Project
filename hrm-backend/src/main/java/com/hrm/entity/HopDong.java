package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hopdong")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HopDong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nhan_vien_id", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "so_hop_dong", nullable = false, unique = true, length = 50)
    private String soHopDong;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_hop_dong", nullable = false, length = 30)
    private LoaiHopDong loaiHopDong;

    @Column(name = "ngay_ky", nullable = false)
    private LocalDate ngayKy;

    @Column(name = "ngay_hieu_luc", nullable = false)
    private LocalDate ngayHieuLuc;

    @Column(name = "ngay_het_han")
    private LocalDate ngayHetHan;

    @Column(name = "luong_thoa_thuan", nullable = false, precision = 15, scale = 2)
    private BigDecimal luongThoaThuan;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private TrangThaiHopDong trangThai = TrangThaiHopDong.ConHieuLuc;

    public enum LoaiHopDong {
        ThuViec, XacDinhThoiHan, KhongThoiHan
    }

    public enum TrangThaiHopDong {
        ConHieuLuc, HetHieuLuc, DaHuy
    }
}
