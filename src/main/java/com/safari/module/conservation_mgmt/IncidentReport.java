package com.safari.module.conservation_mgmt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident_reports")
public class IncidentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incident_number", nullable = false, unique = true)
    private String incidentNumber; // e.g. INC-2026-042

    @NotBlank(message = "National park is required")
    @Column(name = "park_name", nullable = false)
    private String parkName;

    @Column(name = "booking_id")
    private Long bookingId;

    @NotBlank(message = "Incident type is required")
    @Column(name = "incident_type", nullable = false)
    private String incidentType; // OFF_TRACK_DRIVING, ANIMAL_HARASSMENT, VEHICLE_BREAKDOWN, MEDICAL_EMERGENCY

    @NotBlank(message = "Severity level is required")
    @Column(nullable = false)
    private String severity = "LOW"; // LOW, MEDIUM, HIGH, CRITICAL

    @NotBlank(message = "Incident description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotBlank(message = "Action taken is required")
    @Column(name = "action_taken", columnDefinition = "TEXT", nullable = false)
    private String actionTaken;

    @NotNull(message = "Reported date is required")
    @Column(name = "reported_date", nullable = false)
    private LocalDate reportedDate = LocalDate.now();

    @Column(nullable = false)
    private String status = "SUBMITTED"; // DRAFT, SUBMITTED, INVESTIGATED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public IncidentReport() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIncidentNumber() { return incidentNumber; }
    public void setIncidentNumber(String incidentNumber) { this.incidentNumber = incidentNumber; }
    public String getParkName() { return parkName; }
    public void setParkName(String parkName) { this.parkName = parkName; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }
    public LocalDate getReportedDate() { return reportedDate; }
    public void setReportedDate(LocalDate reportedDate) { this.reportedDate = reportedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
