package com.hrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class NhanVienRequest {

    @NotBlank(message = "Ho ten khong duoc de trong")
    private String hoTen;

    private LocalDate ngaySinh;
    private String gioiTinh;

    @NotBlank(message = "CCCD khong duoc de trong")
    private String cccd;

    @NotBlank(message = "SDT khong duoc de trong")
    private String sdt;

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong hop le")
    private String email;

    private String diaChi;
    private String anh;
    private LocalDate ngayVaoLam;
    private String trangThai;   // DangLamViec / NghiThaiSan / DaNghiViec
    private Integer phongBanId;
    private Integer chucVuId;
}
