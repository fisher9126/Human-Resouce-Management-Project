package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chucvu")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChucVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_chuc_vu", nullable = false, length = 100)
    private String tenChucVu;

    @Column(name = "luong_co_ban", nullable = false, precision = 15, scale = 2)
    private BigDecimal luongCoBan;

    @Column(name = "phu_cap_chuc_vu", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal phuCapChucVu = BigDecimal.ZERO;
}
