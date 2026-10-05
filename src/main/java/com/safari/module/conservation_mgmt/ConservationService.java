package com.safari.module.conservation_mgmt;

import com.safari.common.ActivityLogService;
import com.safari.common.NotificationService;
import com.safari.module.allocation_mgmt.GuideRepository;
import com.safari.module.user_mgmt.UserRepository;
import com.safari.patterns.factory.ReferenceFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class ConservationService {

    private final ParkPermitRepository permitRepository;
    private final WildlifeSightingRepository sightingRepository;
    private final IncidentReportRepository incidentRepository;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final GuideRepository guideRepository;
    private final UserRepository userRepository;

    public ConservationService(ParkPermitRepository permitRepository,
                               WildlifeSightingRepository sightingRepository,
                               IncidentReportRepository incidentRepository,
                               ActivityLogService activityLogService,
                               NotificationService notificationService,
                               GuideRepository guideRepository,
                               UserRepository userRepository) {
        this.permitRepository = permitRepository;
        this.sightingRepository = sightingRepository;
        this.incidentRepository = incidentRepository;
        this.activityLogService = activityLogService;
        this.notificationService = notificationService;
        this.guideRepository = guideRepository;
        this.userRepository = userRepository;
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

        // Send alert notification to admin and dispatcher
        notificationService.sendNotification(
                "admin@safari.lk",
                "Wildlife Incident Alert: " + saved.getIncidentNumber(),
                "A " + saved.getSeverity() + " severity incident was reported in " + saved.getParkName() + ": " + saved.getDescription(),
                "INCIDENT_ALERT"
        );
        notificationService.sendNotification(
                "ops@safari.lk",
                "Wildlife Incident Alert: " + saved.getIncidentNumber(),
                "A " + saved.getSeverity() + " severity incident was reported in " + saved.getParkName() + ": " + saved.getDescription(),
                "INCIDENT_ALERT"
        );

        return saved;
    }

    @Transactional
    public void updatePermitStatus(Long permitId, String newStatus, String actorEmail) {
        permitRepository.findById(permitId).ifPresent(p -> {
            p.setStatus(newStatus);
            permitRepository.save(p);
            activityLogService.publishActivity(
                    actorEmail,
                    "CONSERVATION_OFFICER",
                    "Conservation & Compliance",
                    "UPDATE_PERMIT_STATUS",
                    "Permit " + p.getPermitNumber() + " status updated to " + newStatus
            );

            // Send notification about permit update
            notificationService.sendNotification(
                    "operator@safari.lk",
                    "Park Permit Status Updated",
                    "Permit " + p.getPermitNumber() + " for " + p.getParkName() + " has been updated to: " + newStatus,
                    "PERMIT_UPDATE"
            );
        });
    }

    @Transactional
    public void deletePermit(Long permitId, String actorEmail) {
        permitRepository.findById(permitId).ifPresent(p -> {
            String pNum = p.getPermitNumber();
            permitRepository.delete(p);
            activityLogService.publishActivity(
                    actorEmail,
                    "CONSERVATION_OFFICER",
                    "Conservation & Compliance",
                    "DELETE_PERMIT",
                    "Park permit " + pNum + " permanently removed."
            );
        });
    }

    @Transactional
    public void deleteSighting(Long sightingId, String actorEmail) {
        sightingRepository.findById(sightingId).ifPresent(s -> {
            String species = s.getSpeciesName();
            sightingRepository.delete(s);
            activityLogService.publishActivity(
                    actorEmail,
                    "CONSERVATION_OFFICER",
                    "Conservation & Compliance",
                    "DELETE_SIGHTING",
                    "Wildlife observation of " + species + " deleted from records."
            );
        });
    }

    @Transactional
    public void updateIncidentStatus(Long incidentId, String status, String actionTaken, String actorEmail) {
        incidentRepository.findById(incidentId).ifPresent(inc -> {
            inc.setStatus(status);
            if (actionTaken != null && !actionTaken.isBlank()) {
                inc.setActionTaken(actionTaken);
            }
            incidentRepository.save(inc);
            activityLogService.publishActivity(
                    actorEmail,
                    "CONSERVATION_OFFICER",
                    "Conservation & Compliance",
                    "UPDATE_INCIDENT_STATUS",
                    "Incident " + inc.getIncidentNumber() + " status changed to " + status
            );
        });
    }

    @Transactional
    public void deleteIncident(Long incidentId, String actorEmail) {
        incidentRepository.findById(incidentId).ifPresent(inc -> {
            String num = inc.getIncidentNumber();
            incidentRepository.delete(inc);
            activityLogService.publishActivity(
                    actorEmail,
                    "CONSERVATION_OFFICER",
                    "Conservation & Compliance",
                    "DELETE_INCIDENT",
                    "Incident report " + num + " was removed."
            );
        });
    }

    // Compliance Summary Metrics
    public Map<String, Object> getComplianceSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPermits", permitRepository.count());
        summary.put("totalSightings", sightingRepository.count());
        summary.put("totalIncidents", incidentRepository.count());
        return summary;
    }

    /**
     * Broadcast an official DWC Wildlife Advisory / Park Alert
     * @param targetAudience GUIDES, ADMIN, or ALL
     * @param parkName National Park name
     * @param alertLevel INFO, WARNING, or CRITICAL
     * @param title Alert headline
     * @param message Directive details
     * @param officerEmail Sender officer's email
     * @return Number of recipients notified
     */
    @Transactional
    public int broadcastAlert(String targetAudience, String parkName, String alertLevel, String title, String message, String officerEmail) {
        Set<String> recipientEmails = new LinkedHashSet<>();

        if ("GUIDES".equalsIgnoreCase(targetAudience) || "ALL".equalsIgnoreCase(targetAudience)) {
            guideRepository.findByStatus("ACTIVE").forEach(g -> {
                if (g.getEmail() != null && !g.getEmail().isBlank()) {
                    recipientEmails.add(g.getEmail());
                }
            });
        }

        if ("ADMIN".equalsIgnoreCase(targetAudience) || "ALL".equalsIgnoreCase(targetAudience)) {
            userRepository.findByRole("ADMIN").forEach(u -> {
                if (u.getEmail() != null && !u.getEmail().isBlank()) {
                    recipientEmails.add(u.getEmail());
                }
            });
            userRepository.findByRole("OPERATIONS_MANAGER").forEach(u -> {
                if (u.getEmail() != null && !u.getEmail().isBlank()) {
                    recipientEmails.add(u.getEmail());
                }
            });
        }

        // Always notify the issuing officer so they have record in their own alerts feed
        recipientEmails.add(officerEmail);

        String advisoryTitle = "📢 DWC Advisory [" + parkName + "]: " + title;
        String advisoryMessage = "⚠️ Alert Level: " + alertLevel + "\nNational Park: " + parkName + "\nIssued by: DWC Lead Officer (" + officerEmail + ")\n\nDirective:\n" + message;

        for (String email : recipientEmails) {
            notificationService.sendNotification(
                    email,
                    advisoryTitle,
                    advisoryMessage,
                    alertLevel
            );
        }

        activityLogService.publishActivity(
                officerEmail,
                "CONSERVATION_OFFICER",
                "Conservation & Compliance",
                "BROADCAST_ADVISORY",
                "Broadcasted DWC alert '" + title + "' for " + parkName + " to " + recipientEmails.size() + " recipient(s) [" + targetAudience + "]"
        );

        return recipientEmails.size();
    }
}
