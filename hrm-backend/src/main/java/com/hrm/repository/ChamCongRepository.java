package com.hrm.repository;

import com.hrm.entity.ChamCong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChamCongRepository extends JpaRepository<ChamCong, Integer> {

    Optional<ChamCong> findByNhanVienIdAndNgay(Integer nhanVienId, LocalDate ngay);

    List<ChamCong> findByNhanVienIdAndNgayBetween(Integer nhanVienId, LocalDate tu, LocalDate den);

    List<ChamCong> findByNgayBetween(LocalDate tu, LocalDate den);
}
