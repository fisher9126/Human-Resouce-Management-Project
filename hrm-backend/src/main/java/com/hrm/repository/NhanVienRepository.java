package com.hrm.repository;

import com.hrm.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    Optional<NhanVien> findByCccd(String cccd);
    Optional<NhanVien> findByEmail(String email);
    Optional<NhanVien> findBySdt(String sdt);

    boolean existsByCccd(String cccd);
    boolean existsByEmail(String email);
    boolean existsBySdt(String sdt);

    List<NhanVien> findByPhongBanId(Integer phongBanId);
    long countByPhongBanId(Integer phongBanId);

    @Query("SELECT nv FROM NhanVien nv WHERE " +
           "(:keyword IS NULL OR LOWER(nv.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR nv.sdt LIKE CONCAT('%', :keyword, '%') " +
           "   OR CAST(nv.id AS string) LIKE CONCAT('%', :keyword, '%')) " +
           "AND (:phongBanId IS NULL OR nv.phongBan.id = :phongBanId) " +
           "AND (:chucVuId IS NULL OR nv.chucVu.id = :chucVuId) " +
           "AND (:vaiTro IS NULL OR EXISTS " +
           "   (SELECT 1 FROM TaiKhoan tk WHERE tk.nhanVien = nv AND tk.vaiTro = :vaiTro))")
    Page<NhanVien> search(@Param("keyword") String keyword,
                          @Param("phongBanId") Integer phongBanId,
                          @Param("chucVuId") Integer chucVuId,
                          @Param("vaiTro") com.hrm.entity.TaiKhoan.VaiTro vaiTro,
                          Pageable pageable);
}
