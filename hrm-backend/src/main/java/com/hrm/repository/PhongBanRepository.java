package com.hrm.repository;

import com.hrm.entity.PhongBan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PhongBanRepository extends JpaRepository<PhongBan, Integer> {
    Optional<PhongBan> findByMaPhongBan(String maPhongBan);
    boolean existsByMaPhongBan(String maPhongBan);
}
