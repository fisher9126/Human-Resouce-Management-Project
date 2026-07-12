package com.hrm.repository;

import com.hrm.entity.HopDong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HopDongRepository extends JpaRepository<HopDong, Integer> {

    List<HopDong> findByNhanVienId(Integer nhanVienId);

    boolean existsBySoHopDong(String soHopDong);

    // Hop dong con hieu luc va het han truoc mot moc ngay (canh bao 30 ngay)
    List<HopDong> findByTrangThaiAndNgayHetHanLessThanEqual(
            HopDong.TrangThaiHopDong trangThai, LocalDate moc);
}
