package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "khenthuong")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KhenThuong {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nhan_vien_id", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "ly_do", columnDefinition = "TEXT")
    private String lyDo;

    @Column(name = "hinh_thuc", length = 50)
    private String hinhThuc; // Tien mat / Giay khen

    @Column(name = "so_tien", precision = 15, scale = 2)
    private BigDecimal soTien;

    @Column(name = "ngay_khen_thuong")
    private LocalDate ngayKhenThuong;
}
