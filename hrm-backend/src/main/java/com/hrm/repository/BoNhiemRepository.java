package com.hrm.repository;

import com.hrm.entity.BoNhiem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BoNhiemRepository extends JpaRepository<BoNhiem, Integer> {
    List<BoNhiem> findByNhanVienId(Integer nhanVienId);

    @Query("SELECT b FROM BoNhiem b WHERE b.nhanVien.phongBan.id = :phongBanId")
    List<BoNhiem> findByPhongBan(@Param("phongBanId") Integer phongBanId);
}
