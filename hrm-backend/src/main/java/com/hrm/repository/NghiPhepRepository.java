package com.hrm.repository;

import com.hrm.entity.NghiPhep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface NghiPhepRepository extends JpaRepository<NghiPhep, Integer> {

    List<NghiPhep> findByNhanVienId(Integer nhanVienId);

    List<NghiPhep> findByTrangThai(NghiPhep.TrangThaiDon trangThai);

    List<NghiPhep> findByNhanVienIdAndTrangThai(Integer nhanVienId, NghiPhep.TrangThaiDon trangThai);

    long countByTrangThai(NghiPhep.TrangThaiDon trangThai);

    // Kiem tra trung lich nghi da duyet
    List<NghiPhep> findByNhanVienIdAndTrangThaiAndNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(
            Integer nhanVienId, NghiPhep.TrangThaiDon trangThai, LocalDate ngayKetThuc, LocalDate ngayBatDau);
}
