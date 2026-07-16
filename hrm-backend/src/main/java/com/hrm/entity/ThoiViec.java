package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "thoiviec")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ThoiViec {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nhan_vien_id", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "ngay_thoi_viec")
    private LocalDate ngayThoiViec;

    @Column(name = "ly_do", columnDefinition = "TEXT")
    private String lyDo;

    @Column(name = "trang_thai", length = 20)
    private String trangThai; // ChoDuyet / DaDuyet
}
