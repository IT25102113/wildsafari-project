package com.safari.module.inventory_mgmt;

import com.safari.module.booking_mgmt.Booking;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "equipment_allocations")
public class EquipmentAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Min(value = 1, message = "Allocated quantity must be at least 1")
    @Column(name = "allocated_quantity", nullable = false)
    private int allocatedQuantity = 1;

    @NotNull
    @Column(name = "issued_date", nullable = false)
    private LocalDate issuedDate = LocalDate.now();

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(nullable = false)
    private String status = "ISSUED"; // ISSUED, RETURNED, DAMAGED

    @Column(columnDefinition = "TEXT")
    private String remarks;

    public EquipmentAllocation() {}

    public EquipmentAllocation(Equipment equipment, Booking booking, int allocatedQuantity, LocalDate issuedDate, String remarks) {
        this.equipment = equipment;
        this.booking = booking;
        this.allocatedQuantity = allocatedQuantity;
        this.issuedDate = issuedDate;
        this.remarks = remarks;
        this.status = "ISSUED";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public int getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(int allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }
    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
