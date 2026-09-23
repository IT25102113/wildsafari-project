package com.safari.module.conservation_mgmt;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "wildlife_sightings")
public class WildlifeSighting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "National park is required")
    @Column(name = "park_name", nullable = false)
    private String parkName;

    @NotBlank(message = "Species name is required")
    @Column(name = "species_name", nullable = false)
    private String speciesName; // e.g. Sri Lankan Leopard (Panthera pardus kotiya)

    @NotBlank(message = "Track sector / Area is required")
    @Column(name = "area_sector", nullable = false)
    private String areaSector;

    @NotNull(message = "Sighting timestamp is required")
    @Column(name = "sighting_timestamp", nullable = false)
    private LocalDateTime sightingTimestamp = LocalDateTime.now();

    @Min(value = 1, message = "Animal count must be at least 1")
    @Column(name = "animal_count", nullable = false)
    private int animalCount = 1;

    @NotBlank(message = "Observed behavior details are required")
    @Column(name = "observed_behavior", columnDefinition = "TEXT", nullable = false)
    private String observedBehavior;

    @Column(name = "recorded_by_guide_id")
    private Long recordedByGuideId;

    @Column(nullable = false)
    private String status = "SUBMITTED"; // DRAFT, SUBMITTED, ARCHIVED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public WildlifeSighting() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getParkName() { return parkName; }
    public void setParkName(String parkName) { this.parkName = parkName; }
    public String getSpeciesName() { return speciesName; }
    public void setSpeciesName(String speciesName) { this.speciesName = speciesName; }
    public String getAreaSector() { return areaSector; }
    public void setAreaSector(String areaSector) { this.areaSector = areaSector; }
    public LocalDateTime getSightingTimestamp() { return sightingTimestamp; }
    public void setSightingTimestamp(LocalDateTime sightingTimestamp) { this.sightingTimestamp = sightingTimestamp; }
    public int getAnimalCount() { return animalCount; }
    public void setAnimalCount(int animalCount) { this.animalCount = animalCount; }
    public String getObservedBehavior() { return observedBehavior; }
    public void setObservedBehavior(String observedBehavior) { this.observedBehavior = observedBehavior; }
    public Long getRecordedByGuideId() { return recordedByGuideId; }
    public void setRecordedByGuideId(Long recordedByGuideId) { this.recordedByGuideId = recordedByGuideId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
