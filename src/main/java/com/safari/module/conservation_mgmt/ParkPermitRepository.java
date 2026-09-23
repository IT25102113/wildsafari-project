package com.safari.module.conservation_mgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkPermitRepository extends JpaRepository<ParkPermit, Long> {
    Optional<ParkPermit> findByPermitNumber(String permitNumber);
    List<ParkPermit> findByParkName(String parkName);
    List<ParkPermit> findAllByOrderByIssueDateDesc();
}
