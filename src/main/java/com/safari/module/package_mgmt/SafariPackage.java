package com.safari.module.package_mgmt;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "safari_packages")
public class SafariPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Package name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "National Park is required")
    @Column(name = "national_park", nullable = false)
    private String nationalPark;

    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "1000.00", message = "Base price must be at least LKR 1,000.00")
    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Min(value = 1, message = "Duration must be at least 1 day")
    @Column(name = "duration_days", nullable = false)
    private int durationDays = 1;

    @NotBlank(message = "Difficulty level is required")
    @Column(name = "difficulty_level", nullable = false)
    private String difficultyLevel = "EASY"; // EASY, MODERATE, CHALLENGING

    @Min(value = 1, message = "Max group size must be at least 1")
    @Column(name = "max_group_size", nullable = false)
    private int maxGroupSize = 6;

    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private String status = "ACTIVE"; // DRAFT, ACTIVE, INACTIVE, DISCONTINUED

    @Column(name = "cover_image")
    private String coverImage;

    @NotNull(message = "Seasonal multiplier is required")
    @DecimalMin(value = "1.00", message = "Seasonal multiplier must be at least 1.00")
    @Column(name = "peak_season_multiplier", nullable = false)
    private BigDecimal peakSeasonMultiplier = new BigDecimal("1.25");

    @NotBlank(message = "Itinerary details are required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String itinerary;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public SafariPackage() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNationalPark() { return nationalPark; }
    public void setNationalPark(String nationalPark) { this.nationalPark = nationalPark; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public int getMaxGroupSize() { return maxGroupSize; }
    public void setMaxGroupSize(int maxGroupSize) { this.maxGroupSize = maxGroupSize; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public BigDecimal getPeakSeasonMultiplier() { return peakSeasonMultiplier; }
    public void setPeakSeasonMultiplier(BigDecimal peakSeasonMultiplier) { this.peakSeasonMultiplier = peakSeasonMultiplier; }
    public String getItinerary() { return itinerary; }
    public void setItinerary(String itinerary) { this.itinerary = itinerary; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
