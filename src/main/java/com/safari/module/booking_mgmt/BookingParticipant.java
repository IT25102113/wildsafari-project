package com.safari.module.booking_mgmt;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_participants")
public class BookingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "id_or_passport", nullable = false)
    private String idOrPassport;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String nationality = "Sri Lankan";

    @Column(name = "emergency_contact", nullable = false)
    private String emergencyContact;

    public BookingParticipant() {}

    public BookingParticipant(Booking booking, String fullName, String idOrPassport, int age, String nationality, String emergencyContact) {
        this.booking = booking;
        this.fullName = fullName;
        this.idOrPassport = idOrPassport;
        this.age = age;
        this.nationality = nationality;
        this.emergencyContact = emergencyContact;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getIdOrPassport() { return idOrPassport; }
    public void setIdOrPassport(String idOrPassport) { this.idOrPassport = idOrPassport; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
}
