package com.safari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SafariApplication {

    private final Environment environment;

    public SafariApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(SafariApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String port = environment.getProperty("local.server.port", environment.getProperty("server.port", "8080"));
        String host = "http://localhost:" + port;

        System.out.println("\n");
        System.out.println("  ============================================================================");
        System.out.println("  🌿  WILDLIFE SAFARI TRIP MANAGEMENT SYSTEM — READY & RUNNING!");
        System.out.println("  ============================================================================");
        System.out.println("  👉  Open Web App in Browser:        " + host);
        System.out.println("  👉  Live Database Tables Explorer:  " + host + "/admin/database");
        System.out.println("  👉  Quick Admin Dashboard:          " + host + "/demo/switch-role?role=ADMIN");
        System.out.println("  ============================================================================");
        System.out.println("  ===============================DEV-by-DULA==================================");

        System.out.println("\n");
    }
}
