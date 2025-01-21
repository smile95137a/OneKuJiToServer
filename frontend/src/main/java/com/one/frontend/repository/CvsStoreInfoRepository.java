package com.one.frontend.repository;

import com.one.frontend.model.CvsStoreInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CvsStoreInfoRepository extends JpaRepository<CvsStoreInfo, Long> {
    Optional<CvsStoreInfo> findByUuid(String uuid);
}
