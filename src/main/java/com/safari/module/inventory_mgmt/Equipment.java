package com.safari.module.inventory_mgmt;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_inventory")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item code is required")
    @Column(name = "item_code", nullable = false, unique = true)
    private String itemCode; // e.g. EQ-OPT-01

    @NotBlank(message = "Item name is required")
    @Column(name = "item_name", nullable = false)
    private String itemName; // e.g. Nikon Monarch 8x42 Waterproof Binoculars

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category; // OPTICS, CAMPING, NAVIGATION, SAFETY, COMMUNICATION

    @Min(value = 0, message = "Total quantity cannot be negative")
    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity = 0;

    @Min(value = 0, message = "Available quantity cannot be negative")
    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity = 0;

    @Min(value = 1, message = "Minimum stock threshold must be at least 1")
    @Column(name = "min_threshold", nullable = false)
    private int minThreshold = 2;

    @Column(name = "condition_status", nullable = false)
    private String conditionStatus = "GOOD"; // GOOD, DAMAGED, UNDER_REPAIR

    @Column(nullable = false)
    private String location = "Main Safari Depot";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Equipment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
    public int getMinThreshold() { return minThreshold; }
    public void setMinThreshold(int minThreshold) { this.minThreshold = minThreshold; }
    public String getConditionStatus() { return conditionStatus; }
    public void setConditionStatus(String conditionStatus) { this.conditionStatus = conditionStatus; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isLowStock() {
        return this.availableQuantity <= this.minThreshold;
    }
}
