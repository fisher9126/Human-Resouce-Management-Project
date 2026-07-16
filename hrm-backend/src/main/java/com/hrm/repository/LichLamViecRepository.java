package com.hrm.repository;

import com.hrm.entity.LichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LichLamViecRepository extends JpaRepository<LichLamViec, Integer> {
    List<LichLamViec> findByNhanVienId(Integer nhanVienId);
}
