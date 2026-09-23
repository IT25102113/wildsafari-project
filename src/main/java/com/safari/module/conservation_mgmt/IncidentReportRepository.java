package com.safari.module.conservation_mgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentReportRepository extends JpaRepository<IncidentReport, Long> {
    Optional<IncidentReport> findByIncidentNumber(String incidentNumber);
    List<IncidentReport> findByParkName(String parkName);
    List<IncidentReport> findAllByOrderByReportedDateDesc();
}
