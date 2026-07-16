package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "lichlamviec")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LichLamViec {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nhan_vien_id", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "ngay_lam_viec")
    private LocalDate ngayLamViec;

    @Column(name = "ca_lam_viec", length = 100)
    private String caLamViec;

    @Column(name = "kieu_ngay", length = 50)
    private String kieuNgay; // Ngay thuong / Ngay le
}
