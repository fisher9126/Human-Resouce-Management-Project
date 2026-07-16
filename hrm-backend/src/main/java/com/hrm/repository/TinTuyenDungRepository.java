package com.hrm.repository;

import com.hrm.entity.TinTuyenDung;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TinTuyenDungRepository extends JpaRepository<TinTuyenDung, Integer> {
    boolean existsByMaTin(String maTin);
}
