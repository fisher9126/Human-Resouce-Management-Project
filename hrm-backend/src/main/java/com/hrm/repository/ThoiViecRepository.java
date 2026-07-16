package com.hrm.repository;

import com.hrm.entity.ThoiViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ThoiViecRepository extends JpaRepository<ThoiViec, Integer> {
    List<ThoiViec> findByNhanVienId(Integer nhanVienId);
    List<ThoiViec> findByTrangThai(String trangThai);

    @Query("SELECT t FROM ThoiViec t WHERE t.nhanVien.phongBan.id = :phongBanId")
    List<ThoiViec> findByPhongBan(@Param("phongBanId") Integer phongBanId);
}
