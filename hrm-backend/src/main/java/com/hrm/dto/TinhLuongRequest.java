package com.hrm.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class TinhLuongRequest {
    private Integer thang;
    private Integer nam;
    private Integer ngayCongChuan = 26;         // mac dinh 26 ngay
    private Integer soNguoiPhuThuoc = 0;        // ap dung khi tinh thue
    private BigDecimal thuong = BigDecimal.ZERO; // thuong them (neu co)
}
