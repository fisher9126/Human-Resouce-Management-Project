package com.hrm.repository;

import com.hrm.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {
    List<ThongBao> findByDaDocFalse();
    long countByDaDocFalse();
    List<ThongBao> findTop20ByOrderByThoiGianDesc();
}
