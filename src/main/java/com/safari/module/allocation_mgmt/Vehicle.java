package com.safari.module.allocation_mgmt;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Registration number is required")
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber; // e.g. WP-CAB-4821

    @NotBlank(message = "Vehicle model is required")
    @Column(name = "vehicle_model", nullable = false)
    private String vehicleModel; // e.g. Toyota Land Cruiser 79 Safari 4x4

    @Min(value = 2, message = "Capacity must be at least 2 passengers")
    @Column(nullable = false)
    private int capacity = 6;

    @Column(name = "condition_status", nullable = false)
    private String conditionStatus = "EXCELLENT"; // EXCELLENT, GOOD, UNDER_MAINTENANCE

    @Column(nullable = false)
    private String status = "AVAILABLE"; // AVAILABLE, ASSIGNED, MAINTENANCE

    @NotNull(message = "Last service date is required")
    @Column(name = "last_service_date", nullable = false)
    private LocalDate lastServiceDate = LocalDate.now();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Vehicle() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getConditionStatus() { return conditionStatus; }
    public void setConditionStatus(String conditionStatus) { this.conditionStatus = conditionStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(LocalDate lastServiceDate) { this.lastServiceDate = lastServiceDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
