package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "bonhiem")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoNhiem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nhan_vien_id", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "tu_chuc_vu", length = 100)
    private String tuChucVu;
    @Column(name = "toi_chuc_vu", length = 100)
    private String toiChucVu;
    @Column(name = "tu_phong_ban", length = 100)
    private String tuPhongBan;
    @Column(name = "toi_phong_ban", length = 100)
    private String toiPhongBan;

    @Column(name = "ngay_bo_nhiem")
    private LocalDate ngayBoNhiem;

    @Column(name = "ly_do", columnDefinition = "TEXT")
    private String lyDo;
}
