package com.hrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class NghiPhepRequest {

    @NotBlank(message = "Loai nghi khong duoc de trong")
    private String loaiNghi; // PhepNam / NghiOm / ThaiSan / KhongLuong

    @NotNull(message = "Ngay bat dau khong duoc de trong")
    private LocalDate ngayBatDau;

    @NotNull(message = "Ngay ket thuc khong duoc de trong")
    private LocalDate ngayKetThuc;

    @NotNull(message = "So ngay nghi khong duoc de trong")
    private Double soNgayNghi;

    @NotBlank(message = "Ly do khong duoc de trong")
    private String lyDo;
}
