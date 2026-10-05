package com.safari.module.allocation_mgmt;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "guide_availability",
       uniqueConstraints = @UniqueConstraint(columnNames = {"guide_id", "unavailable_date"}))
public class GuideAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guide_id", nullable = false)
    private Guide guide;

    @Column(name = "unavailable_date", nullable = false)
    private LocalDate unavailableDate;

    @Column
    private String reason; // PERSONAL_LEAVE, SICK, OTHER

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public GuideAvailability() {}

    public GuideAvailability(Guide guide, LocalDate unavailableDate, String reason) {
        this.guide = guide;
        this.unavailableDate = unavailableDate;
        this.reason = reason;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Guide getGuide() { return guide; }
    public void setGuide(Guide guide) { this.guide = guide; }
    public LocalDate getUnavailableDate() { return unavailableDate; }
    public void setUnavailableDate(LocalDate unavailableDate) { this.unavailableDate = unavailableDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
