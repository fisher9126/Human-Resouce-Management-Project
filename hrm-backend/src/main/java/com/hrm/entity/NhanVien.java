package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "nhanvien",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "cccd"),
           @UniqueConstraint(columnNames = "sdt"),
           @UniqueConstraint(columnNames = "email")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh;

    @Column(name = "cccd", nullable = false, length = 20)
    private String cccd;

    @Column(name = "sdt", nullable = false, length = 15)
    private String sdt;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "dia_chi", length = 255)
    private String diaChi;

    @Column(name = "anh", length = 255)
    private String anh;

    @Column(name = "ngay_vao_lam")
    private LocalDate ngayVaoLam;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20, nullable = false)
    @Builder.Default
    private TrangThaiNhanVien trangThai = TrangThaiNhanVien.DangLamViec;

    @Column(name = "so_ngay_phep_con_lai")
    @Builder.Default
    private Double soNgayPhepConLai = 12.0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phong_ban_id")
    private PhongBan phongBan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chuc_vu_id")
    private ChucVu chucVu;

    public enum TrangThaiNhanVien {
        DangLamViec, NghiThaiSan, DaNghiViec
    }
}
