package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "ungvien")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UngVien {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "sdt", length = 15)
    private String sdt;

    @Column(name = "vi_tri_ung_tuyen", length = 200)
    private String viTriUngTuyen;

    @Column(name = "ma_tin", length = 20)
    private String maTin;

    @Column(name = "trang_thai", length = 30)
    private String trangThai; // Moi / DangXetDuyet / DaPhongVan / DatYeuCau / TuChoi

    @Column(name = "diem_danh_gia")
    private Integer diemDanhGia; // 1-10

    @Column(name = "anh", length = 255)
    private String anh;

    @Column(name = "ngay_nop")
    private LocalDate ngayNop;
}
