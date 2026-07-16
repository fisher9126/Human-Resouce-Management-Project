package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "calamviec")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CaLamViec {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_ca", nullable = false, length = 100)
    private String tenCa;

    @Column(name = "gio_bat_dau", length = 10)
    private String gioBatDau;

    @Column(name = "gio_ket_thuc", length = 10)
    private String gioKetThuc;

    @Column(name = "mo_ta", length = 255)
    private String moTa;
}
