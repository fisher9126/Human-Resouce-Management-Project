package com.hrm.repository;

import com.hrm.entity.KhenThuong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface KhenThuongRepository extends JpaRepository<KhenThuong, Integer> {
    List<KhenThuong> findByNhanVienId(Integer nhanVienId);

    @Query("SELECT k FROM KhenThuong k WHERE k.nhanVien.phongBan.id = :phongBanId")
    List<KhenThuong> findByPhongBan(@Param("phongBanId") Integer phongBanId);
}
