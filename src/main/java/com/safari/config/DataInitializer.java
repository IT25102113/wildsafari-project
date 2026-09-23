package com.safari.config;

import com.safari.common.ActivityLogService;
import com.safari.module.allocation_mgmt.GuideRepository;
import com.safari.module.allocation_mgmt.VehicleRepository;
import com.safari.module.allocation_mgmt.TripAllocationRepository;
import com.safari.module.allocation_mgmt.TripAllocation;
import com.safari.module.allocation_mgmt.Guide;
import com.safari.module.allocation_mgmt.Vehicle;
import com.safari.module.booking_mgmt.BookingRepository;
import com.safari.module.booking_mgmt.Booking;
import com.safari.module.conservation_mgmt.ParkPermitRepository;
import com.safari.module.conservation_mgmt.WildlifeSightingRepository;
import com.safari.module.conservation_mgmt.IncidentReportRepository;
import com.safari.module.conservation_mgmt.ParkPermit;
import com.safari.module.conservation_mgmt.WildlifeSighting;
import com.safari.module.conservation_mgmt.IncidentReport;
import com.safari.module.finance_mgmt.PaymentRepository;
import com.safari.module.finance_mgmt.InvoiceRepository;
import com.safari.module.finance_mgmt.Payment;
import com.safari.module.finance_mgmt.Invoice;
import com.safari.module.inventory_mgmt.EquipmentRepository;
import com.safari.module.inventory_mgmt.Equipment;
import com.safari.module.package_mgmt.SafariPackageRepository;
import com.safari.module.package_mgmt.SafariPackage;
import com.safari.module.user_mgmt.UserRepository;
import com.safari.module.user_mgmt.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SafariPackageRepository packageRepository;
    private final GuideRepository guideRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final TripAllocationRepository allocationRepository;
    private final ParkPermitRepository permitRepository;
    private final WildlifeSightingRepository sightingRepository;
    private final IncidentReportRepository incidentRepository;
    private final EquipmentRepository equipmentRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ActivityLogService activityLogService;

    public DataInitializer(UserRepository userRepository,
                           SafariPackageRepository packageRepository,
                           GuideRepository guideRepository,
                           VehicleRepository vehicleRepository,
                           BookingRepository bookingRepository,
                           TripAllocationRepository allocationRepository,
                           ParkPermitRepository permitRepository,
                           WildlifeSightingRepository sightingRepository,
                           IncidentReportRepository incidentRepository,
                           EquipmentRepository equipmentRepository,
                           PaymentRepository paymentRepository,
                           InvoiceRepository invoiceRepository,
                           ActivityLogService activityLogService) {
        this.userRepository = userRepository;
        this.packageRepository = packageRepository;
        this.guideRepository = guideRepository;
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
        this.allocationRepository = allocationRepository;
        this.permitRepository = permitRepository;
        this.sightingRepository = sightingRepository;
        this.incidentRepository = incidentRepository;
        this.equipmentRepository = equipmentRepository;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.activityLogService = activityLogService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Already initialized
        }

        System.out.println("Initializing Production-Ready Wildlife Safari Database for SE2030 Group 03...");

        // 1. Seed Core Role Users
        User admin = userRepository.save(new User("Ruwan Senanayake (Chief Admin)", "admin@safari.lk", "admin123", "0771234567", "ADMIN"));
        User operator = userRepository.save(new User("Dulanja Perera (Tour Operations Lead)", "operator@safari.lk", "operator123", "0714567890", "TOUR_OPERATOR"));
        User opsMgr = userRepository.save(new User("Chaminda Silva (Fleet Dispatcher)", "ops@safari.lk", "ops123", "0729876543", "OPERATIONS_MANAGER"));
        User ranger = userRepository.save(new User("Pradeep Bandara (DWC Park Officer)", "ranger@safari.lk", "ranger123", "0761122334", "CONSERVATION_OFFICER"));
        User logistics = userRepository.save(new User("Nuwan Jayalath (Logistics Supervisor)", "logistics@safari.lk", "logistics123", "0785566778", "LOGISTICS_STAFF"));
        User finance = userRepository.save(new User("Anjali Wickramasinghe (Finance Manager)", "finance@safari.lk", "finance123", "0759988776", "FINANCE_OFFICER"));
        User tourist = userRepository.save(new User("Kavinda Perera (Tourist)", "kavinda.perera@gmail.com", "pass123", "0777654321", "CUSTOMER"));

        // 2. Seed Safari Packages
        SafariPackage pkg1 = new SafariPackage();
        pkg1.setName("Yala Big 4 Predator Expedition");
        pkg1.setNationalPark("Yala National Park");
        pkg1.setDescription("Intensive dawn-to-dusk safari tracking the elusive Sri Lankan Leopard, Sloth Bear, Mugger Crocodile, and Asian Elephant across Block 1 and 2.");
        pkg1.setBasePrice(new BigDecimal("28500.00"));
        pkg1.setDurationDays(1);
        pkg1.setDifficultyLevel("MODERATE");
        pkg1.setMaxGroupSize(6);
        pkg1.setStatus("ACTIVE");
        pkg1.setCoverImage("/images/yala_leopard.jpg");
        pkg1.setPeakSeasonMultiplier(new BigDecimal("1.25"));
        pkg1.setItinerary("05:30 AM: Palatupana Park Gate Entry\n06:30 AM: Leopard tracking along Buthawa coastal dunes\n10:30 AM: Menik Ganga resting site\n02:00 PM: Deep forest tracking near Sithulpawwa\n06:00 PM: Sunset exit and debrief");
        pkg1 = packageRepository.save(pkg1);

        SafariPackage pkg2 = new SafariPackage();
        pkg2.setName("Wilpattu Wilderness & Ancient Villu Trail");
        pkg2.setNationalPark("Wilpattu National Park");
        pkg2.setDescription("Journey through Sri Lanka's largest national park featuring natural rainwater lakes (Villus), high leopard density, barking deer, and bird life.");
        pkg2.setBasePrice(new BigDecimal("34000.00"));
        pkg2.setDurationDays(2);
        pkg2.setDifficultyLevel("EASY");
        pkg2.setMaxGroupSize(6);
        pkg2.setStatus("ACTIVE");
        pkg2.setCoverImage("/images/wilpattu_lake.jpg");
        pkg2.setPeakSeasonMultiplier(new BigDecimal("1.20"));
        pkg2.setItinerary("Day 1: 06:00 AM Hunuwilagama gate entry, Kokmotte camp trail.\nDay 2: Morning birding expedition at Maradanmaduwa, lake-side picnic, departure by 05:00 PM.");
        pkg2 = packageRepository.save(pkg2);

        SafariPackage pkg3 = new SafariPackage();
        pkg3.setName("Udawalawe Elephant Sanctuary Safari");
        pkg3.setNationalPark("Udawalawe National Park");
        pkg3.setDescription("Guaranteed sightings of large elephant herds in open grassland savannas surrounding the reservoir. Ideal for families and wildlife photographers.");
        pkg3.setBasePrice(new BigDecimal("22000.00"));
        pkg3.setDurationDays(1);
        pkg3.setDifficultyLevel("EASY");
        pkg3.setMaxGroupSize(8);
        pkg3.setStatus("ACTIVE");
        pkg3.setCoverImage("/images/udawalawe_elephant.jpg");
        pkg3.setPeakSeasonMultiplier(new BigDecimal("1.15"));
        pkg3.setItinerary("06:00 AM: Entry via Udawalawe Main Barrier\n08:00 AM: Reservoir lakeside elephant gathering\n11:30 AM: Visit to Elephant Transit Home\n01:30 PM: Midday birding watch and wrap-up");
        pkg3 = packageRepository.save(pkg3);

        SafariPackage pkg4 = new SafariPackage();
        pkg4.setName("Minneriya Great Elephant Gathering Explorer");
        pkg4.setNationalPark("Minneriya National Park");
        pkg4.setDescription("Witness Asia's greatest natural wildlife spectacle where over 300 wild elephants congregate on the dried grass beds of Minneriya Tank.");
        pkg4.setBasePrice(new BigDecimal("24500.00"));
        pkg4.setDurationDays(1);
        pkg4.setDifficultyLevel("EASY");
        pkg4.setMaxGroupSize(6);
        pkg4.setStatus("ACTIVE");
        pkg4.setCoverImage("/images/minneriya_gathering.jpg");
        pkg4.setPeakSeasonMultiplier(new BigDecimal("1.30"));
        pkg4.setItinerary("02:30 PM: Afternoon briefing & 4x4 entry\n03:30 PM: Panoramic Minneriya tank shoreline watch\n05:45 PM: Sunset elephant herd migration\n06:30 PM: Return to base camp");
        pkg4 = packageRepository.save(pkg4);

        // 3. Seed Guides
        Guide g1 = new Guide();
        g1.setFullName("Sunil Bandara (Senior Naturalist)");
        g1.setLicenseNumber("DWC-LK-4091");
        g1.setContactNumber("0772345678");
        g1.setEmail("sunil.bandara@gmail.com");
        g1.setLanguages("English, Sinhala, German");
        g1.setExperienceYears(14);
        g1.setStatus("ACTIVE");
        g1.setDailyRate(new BigDecimal("5500.00"));
        g1 = guideRepository.save(g1);

        Guide g2 = new Guide();
        g2.setFullName("Chathura Dissanayake (Wildlife Tracker)");
        g2.setLicenseNumber("DWC-LK-4822");
        g2.setContactNumber("0713456789");
        g2.setEmail("chathura.d@gmail.com");
        g2.setLanguages("English, Sinhala, French");
        g2.setExperienceYears(9);
        g2.setStatus("ACTIVE");
        g2.setDailyRate(new BigDecimal("4800.00"));
        g2 = guideRepository.save(g2);

        // 4. Seed Vehicles
        Vehicle v1 = new Vehicle();
        v1.setRegistrationNumber("WP-CAB-4821");
        v1.setVehicleModel("Toyota Land Cruiser 79 Safari 4x4 (High Elevation)");
        v1.setCapacity(6);
        v1.setConditionStatus("EXCELLENT");
        v1.setStatus("AVAILABLE");
        v1.setLastServiceDate(LocalDate.now().minusDays(20));
        v1 = vehicleRepository.save(v1);

        Vehicle v2 = new Vehicle();
        v2.setRegistrationNumber("CP-NA-9024");
        v2.setVehicleModel("Land Rover Defender 110 Soft Top Edition");
        v2.setCapacity(6);
        v2.setConditionStatus("EXCELLENT");
        v2.setStatus("AVAILABLE");
        v2.setLastServiceDate(LocalDate.now().minusDays(35));
        v2 = vehicleRepository.save(v2);

        // 5. Seed Inventory
        Equipment eq1 = new Equipment();
        eq1.setItemCode("EQ-OPT-01");
        eq1.setItemName("Nikon Monarch 8x42 Waterproof Binoculars");
        eq1.setCategory("OPTICS");
        eq1.setTotalQuantity(10);
        eq1.setAvailableQuantity(8);
        eq1.setMinThreshold(3);
        eq1.setConditionStatus("GOOD");
        eq1.setLocation("Yala Base Depot");
        equipmentRepository.save(eq1);

        Equipment eq2 = new Equipment();
        eq2.setItemCode("EQ-NAV-01");
        eq2.setItemName("Garmin inReach Explorer+ GPS Communicator");
        eq2.setCategory("NAVIGATION");
        eq2.setTotalQuantity(6);
        eq2.setAvailableQuantity(5);
        eq2.setMinThreshold(2);
        eq2.setConditionStatus("GOOD");
        eq2.setLocation("Wilpattu Office");
        equipmentRepository.save(eq2);

        Equipment eq3 = new Equipment();
        eq3.setItemCode("EQ-SAF-01");
        eq3.setItemName("Tactical Field Trauma First-Aid Backpack");
        eq3.setCategory("SAFETY");
        eq3.setTotalQuantity(8);
        eq3.setAvailableQuantity(8);
        eq3.setMinThreshold(2);
        eq3.setConditionStatus("GOOD");
        eq3.setLocation("Medical Bay");
        equipmentRepository.save(eq3);

        Equipment eq4 = new Equipment();
        eq4.setItemCode("EQ-CMP-01");
        eq4.setItemName("Outback 4-Person Safari Canvas Tent");
        eq4.setCategory("CAMPING");
        eq4.setTotalQuantity(4);
        eq4.setAvailableQuantity(1); // Triggers Low Stock Alert!
        eq4.setMinThreshold(2);
        eq4.setConditionStatus("GOOD");
        eq4.setLocation("Camping Lockup");
        equipmentRepository.save(eq4);

        // 6. Seed Confirmed Booking
        Booking b1 = new Booking();
        b1.setBookingReference("WS-2026-10492");
        b1.setSafariPackage(pkg1);
        b1.setCustomerId(tourist.getId());
        b1.setCustomerName("Kavinda Perera");
        b1.setCustomerEmail("kavinda.perera@gmail.com");
        b1.setCustomerPhone("0777654321");
        b1.setTripDate(LocalDate.now().plusDays(4));
        b1.setParticipantCount(2);
        b1.setSpecialRequests("Nikon binoculars rental requested. High-resolution leopard photography focus.");
        b1.setTotalPrice(new BigDecimal("57000.00"));
        b1.setBookingStatus("CONFIRMED");
        b1.setPaymentStatus("PAID");
        b1 = bookingRepository.save(b1);

        // 7. Seed Trip Allocation
        TripAllocation alloc1 = new TripAllocation(b1, g1, v1, b1.getTripDate(), "VIP Guests. High-elevation safari vehicle assigned.");
        allocationRepository.save(alloc1);

        // 8. Seed Payment & Invoice
        Payment pay1 = new Payment();
        pay1.setPaymentReference("PAY-2026-9041");
        pay1.setBooking(b1);
        pay1.setAmount(b1.getTotalPrice());
        pay1.setPaymentMethod("CARD_SANDBOX");
        pay1.setPaymentStatus("PAID");
        pay1.setTransactionDate(LocalDateTime.now().minusHours(2));
        pay1.setRemarks("Authorized via Sandbox Gateway (MasterCard ending in 4242)");
        pay1 = paymentRepository.save(pay1);

        Invoice inv1 = new Invoice();
        inv1.setInvoiceNumber("INV-2026-8801");
        inv1.setPayment(pay1);
        inv1.setBooking(b1);
        inv1.setSubtotal(new BigDecimal("51818.18"));
        inv1.setTaxAmount(new BigDecimal("5181.82"));
        inv1.setTotalAmount(b1.getTotalPrice());
        inv1.setInvoiceDate(LocalDate.now());
        inv1.setStatus("ISSUED");
        inv1.setNotes("Includes DWC park entry levies and 4x4 off-road permit fees.");
        invoiceRepository.save(inv1);

        // 9. Seed Conservation Records
        ParkPermit perm1 = new ParkPermit();
        perm1.setPermitNumber("DWC-YAL-2026-891");
        perm1.setParkName("Yala National Park");
        perm1.setBookingId(b1.getId());
        perm1.setIssueDate(LocalDate.now());
        perm1.setValidDate(b1.getTripDate());
        perm1.setVisitorCount(2);
        perm1.setTotalFeeLkr(new BigDecimal("11500.00"));
        perm1.setStatus("ISSUED");
        perm1.setIssuingOfficer("Ranger Pradeep Bandara");
        permitRepository.save(perm1);

        WildlifeSighting sight1 = new WildlifeSighting();
        sight1.setParkName("Yala National Park");
        sight1.setSpeciesName("Sri Lankan Leopard (Panthera pardus kotiya)");
        sight1.setAreaSector("Buthawa Plains Sector 3");
        sight1.setSightingTimestamp(LocalDateTime.now().minusDays(1));
        sight1.setAnimalCount(1);
        sight1.setObservedBehavior("Young adult male resting on rock outcrop in morning sun, healthy condition.");
        sight1.setRecordedByGuideId(g1.getId());
        sight1.setStatus("SUBMITTED");
        sightingRepository.save(sight1);

        IncidentReport inc1 = new IncidentReport();
        inc1.setIncidentNumber("INC-2026-104");
        inc1.setParkName("Yala National Park");
        inc1.setBookingId(b1.getId());
        inc1.setIncidentType("OFF_TRACK_DRIVING");
        inc1.setSeverity("LOW");
        inc1.setDescription("External safari vehicle attempted to deviate 15 meters from designated track near Menik Ganga.");
        inc1.setActionTaken("Verbal warning issued and vehicle registration logged for park entry audit.");
        inc1.setReportedDate(LocalDate.now().minusDays(2));
        inc1.setStatus("SUBMITTED");
        incidentRepository.save(inc1);

        // 10. Initial Activity Logs
        activityLogService.saveLog("system", "SYSTEM", "Core", "SEED_DATA", "Default production dataset populated for SE2030 Group 03.");
        activityLogService.saveLog("admin@safari.lk", "ADMIN", "System Administration", "SYSTEM_READY", "Safari Management Platform successfully online.");

        System.out.println("Wildlife Safari Database initialization completed successfully!");
    }
}
