package com.hrm.repository;

import com.hrm.entity.NghiPhep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // ===== Loc theo phong ban (cho Manager) =====
    @Query("SELECT np FROM NghiPhep np WHERE np.nhanVien.phongBan.id = :phongBanId")
    List<NghiPhep> findByPhongBan(@Param("phongBanId") Integer phongBanId);

    @Query("SELECT np FROM NghiPhep np WHERE np.trangThai = :trangThai AND np.nhanVien.phongBan.id = :phongBanId")
    List<NghiPhep> findByTrangThaiAndPhongBan(@Param("trangThai") NghiPhep.TrangThaiDon trangThai,
                                              @Param("phongBanId") Integer phongBanId);

    @Query("SELECT COUNT(np) FROM NghiPhep np WHERE np.trangThai = :trangThai AND np.nhanVien.phongBan.id = :phongBanId")
    long countByTrangThaiAndPhongBan(@Param("trangThai") NghiPhep.TrangThaiDon trangThai,
                                     @Param("phongBanId") Integer phongBanId);
}
