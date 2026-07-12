package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "taikhoan",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "nhan_vien_id")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaiKhoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false, length = 30)
    private VaiTro vaiTro;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nhan_vien_id")
    private NhanVien nhanVien;

    public enum VaiTro {
        ROLE_ADMIN, ROLE_MANAGER, ROLE_EMPLOYEE
    }
}
