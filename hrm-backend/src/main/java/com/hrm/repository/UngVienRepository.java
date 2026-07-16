package com.hrm.repository;

import com.hrm.entity.UngVien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UngVienRepository extends JpaRepository<UngVien, Integer> {
    List<UngVien> findByTrangThai(String trangThai);
}
