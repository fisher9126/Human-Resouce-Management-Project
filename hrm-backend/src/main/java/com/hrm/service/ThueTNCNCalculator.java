package com.hrm.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Tinh thue TNCN luy tien tung phan theo bieu thue 7 bac (Luat Viet Nam).
 * Va tinh bao hiem bat buoc 10.5% (BHXH 8% + BHYT 1.5% + BHTN 1%).
 */
public final class ThueTNCNCalculator {

    private ThueTNCNCalculator() {}

    public static final BigDecimal TY_LE_BAO_HIEM = new BigDecimal("0.105"); // 10.5%
    public static final BigDecimal GIAM_TRU_BAN_THAN = new BigDecimal("11000000");
    public static final BigDecimal GIAM_TRU_PHU_THUOC = new BigDecimal("4400000");

    // Bao hiem bat buoc tren luong dong bao hiem
    public static BigDecimal tinhBaoHiem(BigDecimal luongDongBaoHiem) {
        return luongDongBaoHiem.multiply(TY_LE_BAO_HIEM).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Tinh thue TNCN luy tien tung phan.
     * @param thuNhapTinhThue = thu nhap chiu thue - bao hiem - giam tru gia canh
     */
    public static BigDecimal tinhThue(BigDecimal thuNhapTinhThue) {
        if (thuNhapTinhThue.signum() <= 0) return BigDecimal.ZERO;

        double x = thuNhapTinhThue.doubleValue();
        double thue;

        if (x <= 5_000_000)        thue = x * 0.05;
        else if (x <= 10_000_000)  thue = x * 0.10 - 250_000;
        else if (x <= 18_000_000)  thue = x * 0.15 - 750_000;
        else if (x <= 32_000_000)  thue = x * 0.20 - 1_650_000;
        else if (x <= 52_000_000)  thue = x * 0.25 - 3_250_000;
        else if (x <= 80_000_000)  thue = x * 0.30 - 5_850_000;
        else                       thue = x * 0.35 - 9_850_000;

        return BigDecimal.valueOf(Math.max(0, thue)).setScale(0, RoundingMode.HALF_UP);
    }

    public static BigDecimal giamTruGiaCanh(int soNguoiPhuThuoc) {
        return GIAM_TRU_BAN_THAN.add(
                GIAM_TRU_PHU_THUOC.multiply(BigDecimal.valueOf(Math.max(0, soNguoiPhuThuoc))));
    }
}
