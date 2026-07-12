package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "chamcong",
       uniqueConstraints = @UniqueConstraint(columnNames = {"nhan_vien_id", "ngay"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChamCong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nhan_vien_id", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "ngay", nullable = false)
    private LocalDate ngay;

    @Column(name = "gio_vao")
    private LocalTime gioVao;

    @Column(name = "gio_ra")
    private LocalTime gioRa;

    @Column(name = "so_gio_lam")
    @Builder.Default
    private Float soGioLam = 0.0f;

    @Column(name = "di_tre")
    @Builder.Default
    private Boolean diTre = false;

    @Column(name = "ve_som")
    @Builder.Default
    private Boolean veSom = false;

    @Column(name = "trang_thai", length = 20)
    @Builder.Default
    private String trangThai = "HopLe";
}
