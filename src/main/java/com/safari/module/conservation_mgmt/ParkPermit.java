package com.safari.module.conservation_mgmt;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "park_permits")
public class ParkPermit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "permit_number", nullable = false, unique = true)
    private String permitNumber; // e.g. DWC-YAL-2026-891

    @NotBlank(message = "Park name is required")
    @Column(name = "park_name", nullable = false)
    private String parkName;

    @Column(name = "booking_id")
    private Long bookingId;

    @NotNull(message = "Issue date is required")
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate = LocalDate.now();

    @NotNull(message = "Valid date is required")
    @Column(name = "valid_date", nullable = false)
    private LocalDate validDate = LocalDate.now();

    @Min(value = 1, message = "Visitor count must be at least 1")
    @Column(name = "visitor_count", nullable = false)
    private int visitorCount = 1;

    @NotNull(message = "Total fee is required")
    @DecimalMin(value = "0.0")
    @Column(name = "total_fee_lkr", nullable = false)
    private BigDecimal totalFeeLkr = BigDecimal.ZERO;

    @Column(nullable = false)
    private String status = "ISSUED"; // ISSUED, PENDING, EXPIRED, CANCELLED

    @NotBlank(message = "Issuing officer name is required")
    @Column(name = "issuing_officer", nullable = false)
    private String issuingOfficer;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ParkPermit() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPermitNumber() { return permitNumber; }
    public void setPermitNumber(String permitNumber) { this.permitNumber = permitNumber; }
    public String getParkName() { return parkName; }
    public void setParkName(String parkName) { this.parkName = parkName; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getValidDate() { return validDate; }
    public void setValidDate(LocalDate validDate) { this.validDate = validDate; }
    public int getVisitorCount() { return visitorCount; }
    public void setVisitorCount(int visitorCount) { this.visitorCount = visitorCount; }
    public BigDecimal getTotalFeeLkr() { return totalFeeLkr; }
    public void setTotalFeeLkr(BigDecimal totalFeeLkr) { this.totalFeeLkr = totalFeeLkr; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIssuingOfficer() { return issuingOfficer; }
    public void setIssuingOfficer(String issuingOfficer) { this.issuingOfficer = issuingOfficer; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
