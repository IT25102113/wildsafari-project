package com.safari.module.conservation_mgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WildlifeSightingRepository extends JpaRepository<WildlifeSighting, Long> {
    List<WildlifeSighting> findByParkName(String parkName);
    List<WildlifeSighting> findAllByOrderBySightingTimestampDesc();
}
