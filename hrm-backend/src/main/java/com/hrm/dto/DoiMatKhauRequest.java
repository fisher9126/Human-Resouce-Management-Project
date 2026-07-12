package com.hrm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DoiMatKhauRequest {

    @NotBlank(message = "Mat khau cu khong duoc de trong")
    private String matKhauCu;

    @NotBlank(message = "Mat khau moi khong duoc de trong")
    private String matKhauMoi;

    @NotBlank(message = "Xac nhan mat khau khong duoc de trong")
    private String xacNhanMatKhau;
}
