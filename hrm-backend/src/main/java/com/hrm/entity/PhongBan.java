package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phongban")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhongBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_phong_ban", nullable = false, unique = true, length = 20)
    private String maPhongBan;

    @Column(name = "ten_phong_ban", nullable = false, length = 100)
    private String tenPhongBan;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "truong_phong_id")
    private Integer truongPhongId;
}
