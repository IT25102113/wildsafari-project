package com.safari.module.conservation_mgmt;

import com.safari.common.ActivityLogService;
import com.safari.patterns.factory.ReferenceFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConservationService {

    private final ParkPermitRepository permitRepository;
    private final WildlifeSightingRepository sightingRepository;
    private final IncidentReportRepository incidentRepository;
    private final ActivityLogService activityLogService;

    public ConservationService(ParkPermitRepository permitRepository,
                               WildlifeSightingRepository sightingRepository,
                               IncidentReportRepository incidentRepository,
                               ActivityLogService activityLogService) {
        this.permitRepository = permitRepository;
        this.sightingRepository = sightingRepository;
        this.incidentRepository = incidentRepository;
        this.activityLogService = activityLogService;
    }

    // 1. Permits
    public List<ParkPermit> getAllPermits() {
        return permitRepository.findAllByOrderByIssueDateDesc();
    }

    public Optional<ParkPermit> findPermitById(Long id) {
        return permitRepository.findById(id);
    }

    @Transactional
    public ParkPermit issuePermit(ParkPermit permit, String actorEmail) {
        if (permit.getPermitNumber() == null || permit.getPermitNumber().isBlank()) {
            permit.setPermitNumber(ReferenceFactory.generatePermitNumber(permit.getParkName()));
        }
        ParkPermit saved = permitRepository.save(permit);
        activityLogService.publishActivity(
                actorEmail,
                "CONSERVATION_OFFICER",
                "Conservation & Compliance",
                "ISSUE_PERMIT",
                "Park permit issued: " + saved.getPermitNumber() + " for " + saved.getParkName()
        );
        return saved;
    }

    // 2. Wildlife Sightings
    public List<WildlifeSighting> getAllSightings() {
        return sightingRepository.findAllByOrderBySightingTimestampDesc();
    }

    public Optional<WildlifeSighting> findSightingById(Long id) {
        return sightingRepository.findById(id);
    }

    @Transactional
    public WildlifeSighting recordSighting(WildlifeSighting sighting, String actorEmail) {
        // Business Rule: If editing an already submitted sighting, reject modification
        if (sighting.getId() != null) {
            Optional<WildlifeSighting> existing = sightingRepository.findById(sighting.getId());
            if (existing.isPresent() && "SUBMITTED".equalsIgnoreCase(existing.get().getStatus())) {
                throw new IllegalStateException("Compliance Rule: Submitted sightings are permanently locked for scientific integrity and cannot be modified.");
            }
        }

        WildlifeSighting saved = sightingRepository.save(sighting);
        activityLogService.publishActivity(
                actorEmail,
                "CONSERVATION_OFFICER",
                "Conservation & Compliance",
                "RECORD_SIGHTING",
                "Recorded sighting: " + saved.getSpeciesName() + " (" + saved.getAnimalCount() + ") in " + saved.getParkName()
        );
        return saved;
    }

    // 3. Incident Reports
    public List<IncidentReport> getAllIncidents() {
        return incidentRepository.findAllByOrderByReportedDateDesc();
    }

    public Optional<IncidentReport> findIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

    @Transactional
    public IncidentReport reportIncident(IncidentReport report, String actorEmail) {
        if (report.getIncidentNumber() == null || report.getIncidentNumber().isBlank()) {
            report.setIncidentNumber("INC-2026-" + (int)(Math.random() * 9000 + 1000));
        }
        IncidentReport saved = incidentRepository.save(report);
        activityLogService.publishActivity(
                actorEmail,
                "CONSERVATION_OFFICER",
                "Conservation & Compliance",
                "REPORT_INCIDENT",
                "Incident registered: " + saved.getIncidentNumber() + " [" + saved.getSeverity() + "] in " + saved.getParkName()
        );
        return saved;
    }

    // Compliance Summary Metrics
    public Map<String, Object> getComplianceSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPermits", permitRepository.count());
        summary.put("totalSightings", sightingRepository.count());
        summary.put("totalIncidents", incidentRepository.count());
        return summary;
    }
}
