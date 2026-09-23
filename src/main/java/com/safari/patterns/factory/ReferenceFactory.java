package com.safari.patterns.factory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Factory Pattern for producing standardized business references (Invoices, Bookings, Permits).
 */
public class ReferenceFactory {

    public static String generateBookingReference() {
        int randomNum = ThreadLocalRandom.current().nextInt(10000, 99999);
        return "WS-" + LocalDate.now().getYear() + "-" + randomNum;
    }

    public static String generateInvoiceNumber() {
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "INV-" + LocalDate.now().getYear() + "-" + randomNum;
    }

    public static String generatePermitNumber(String parkPrefix) {
        String prefix = (parkPrefix != null && !parkPrefix.isBlank()) ? parkPrefix.toUpperCase().substring(0, 3) : "DWC";
        int randomNum = ThreadLocalRandom.current().nextInt(100, 999);
        return "DWC-" + prefix + "-" + LocalDate.now().getYear() + "-" + randomNum;
    }
}
