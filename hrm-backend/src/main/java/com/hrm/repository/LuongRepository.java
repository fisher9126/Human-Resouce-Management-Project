package com.hrm.repository;

import com.hrm.entity.Luong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LuongRepository extends JpaRepository<Luong, Integer> {

    Optional<Luong> findByNhanVienIdAndThangAndNam(Integer nhanVienId, Integer thang, Integer nam);

    List<Luong> findByThangAndNam(Integer thang, Integer nam);

    List<Luong> findByNhanVienId(Integer nhanVienId);

    @Query("SELECT COALESCE(SUM(l.thucNhan), 0) FROM Luong l WHERE l.thang = :thang AND l.nam = :nam")
    BigDecimal tongQuyLuong(@Param("thang") Integer thang, @Param("nam") Integer nam);

    // ===== Loc theo phong ban (cho Manager) =====
    @Query("SELECT l FROM Luong l WHERE l.thang = :thang AND l.nam = :nam AND l.nhanVien.phongBan.id = :phongBanId")
    List<Luong> findByThangAndNamAndPhongBan(@Param("thang") Integer thang, @Param("nam") Integer nam,
                                             @Param("phongBanId") Integer phongBanId);

    @Query("SELECT COALESCE(SUM(l.thucNhan), 0) FROM Luong l WHERE l.thang = :thang AND l.nam = :nam AND l.nhanVien.phongBan.id = :phongBanId")
    BigDecimal tongQuyLuongTheoPhongBan(@Param("thang") Integer thang, @Param("nam") Integer nam,
                                        @Param("phongBanId") Integer phongBanId);

    @Query("SELECT COUNT(l) FROM Luong l WHERE l.thang = :thang AND l.nam = :nam AND l.nhanVien.phongBan.id = :phongBanId")
    long demBangLuongTheoPhongBan(@Param("thang") Integer thang, @Param("nam") Integer nam,
                                  @Param("phongBanId") Integer phongBanId);
}
