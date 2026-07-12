package com.hrm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateProfileRequest {
    private String sdt;
    private String diaChi;
    private String anh;     // URL/base64 avatar
}
