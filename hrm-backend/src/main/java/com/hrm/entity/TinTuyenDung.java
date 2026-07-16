package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tintuyendung")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TinTuyenDung {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_tin", unique = true, length = 20)
    private String maTin;

    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "dia_diem", length = 100)
    private String diaDiem;

    @Column(name = "ngay_dang")
    private LocalDate ngayDang;

    @Column(name = "han_nop")
    private LocalDate hanNop;

    @Column(name = "hieu_luc")
    private Boolean hieuLuc;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;
}
