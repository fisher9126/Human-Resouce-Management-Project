package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "thongbao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ThongBao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "noi_dung", columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Column(name = "loai", length = 20)
    private String loai; // info / warning

    @Column(name = "da_doc")
    @Builder.Default
    private Boolean daDoc = false;

    @Column(name = "thoi_gian")
    @Builder.Default
    private LocalDateTime thoiGian = LocalDateTime.now();
}
