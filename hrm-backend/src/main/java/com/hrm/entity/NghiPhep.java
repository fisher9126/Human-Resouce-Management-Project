package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "nghiphep")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NghiPhep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nhan_vien_id", nullable = false)
    private NhanVien nhanVien;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_nghi", nullable = false, length = 30)
    private LoaiNghi loaiNghi;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "so_ngay_nghi", nullable = false)
    private Double soNgayNghi;

    @Column(name = "ly_do", columnDefinition = "TEXT", nullable = false)
    private String lyDo;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private TrangThaiDon trangThai = TrangThaiDon.ChoDuyet;

    @Column(name = "nguoi_duyet_id")
    private Integer nguoiDuyetId;

    @Column(name = "phan_hoi_nguoi_duyet", columnDefinition = "TEXT")
    private String phanHoiNguoiDuyet;

    public enum LoaiNghi {
        PhepNam, NghiOm, ThaiSan, KhongLuong
    }

    public enum TrangThaiDon {
        ChoDuyet, Duyet, TuChoi
    }
}
