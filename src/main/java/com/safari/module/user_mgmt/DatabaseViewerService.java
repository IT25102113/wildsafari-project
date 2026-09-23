package com.safari.module.user_mgmt;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class DatabaseViewerService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public static final List<TableInfo> SUPPORTED_TABLES = List.of(
            new TableInfo("safari_packages", "Safari Packages", "Module 1: Package Management"),
            new TableInfo("bookings", "Safari Bookings", "Module 2: Booking Management"),
            new TableInfo("booking_participants", "Booking Participants", "Module 2: Booking Management"),
            new TableInfo("guides", "Tour Guides", "Module 3: Fleet & Allocation"),
            new TableInfo("vehicles", "Safari Vehicles", "Module 3: Fleet & Allocation"),
            new TableInfo("trip_allocations", "Trip Allocations", "Module 3: Fleet & Allocation"),
            new TableInfo("park_permits", "DWC Park Permits", "Module 4: Conservation & Compliance"),
            new TableInfo("wildlife_sightings", "Wildlife Sightings", "Module 4: Conservation & Compliance"),
            new TableInfo("incident_reports", "Park Incident Reports", "Module 4: Conservation & Compliance"),
            new TableInfo("equipment_inventory", "Equipment Inventory", "Module 5: Inventory & Gear"),
            new TableInfo("equipment_allocations", "Equipment Allocations", "Module 5: Inventory & Gear"),
            new TableInfo("payments", "Customer Payments", "Module 6: Payments & Invoices"),
            new TableInfo("invoices", "Tax Invoices", "Module 6: Payments & Invoices"),
            new TableInfo("users", "User Accounts & Staff", "Core: Role Authentication"),
            new TableInfo("activity_logs", "Activity Logs (Audit)", "Core: System Audit Trail")
    );

    public DatabaseViewerService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public List<TableSummary> getAllTableSummaries() {
        List<TableSummary> summaries = new ArrayList<>();
        for (TableInfo info : SUPPORTED_TABLES) {
            long count = 0;
            try {
                Long res = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + info.tableName(), Long.class);
                count = (res != null) ? res : 0;
            } catch (Exception ignored) {
                // Table might not exist yet if ddl-auto didn't create it
            }
            summaries.add(new TableSummary(info.tableName(), info.displayName(), info.module(), count));
        }
        return summaries;
    }

    public TableData getTableData(String tableName) {
        // Validate against whitelist
        TableInfo match = SUPPORTED_TABLES.stream()
                .filter(t -> t.tableName().equalsIgnoreCase(tableName))
                .findFirst()
                .orElse(SUPPORTED_TABLES.get(0));

        String validTable = match.tableName();
        List<String> columns = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();

        try {
            // Get column names in order
            jdbcTemplate.query("SELECT * FROM " + validTable + " LIMIT 1", rs -> {
                var meta = rs.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    columns.add(meta.getColumnLabel(i));
                }
                return null;
            });

            // Fetch top 100 rows
            rows = jdbcTemplate.queryForList("SELECT * FROM " + validTable + " LIMIT 100");
        } catch (Exception e) {
            // Fallback or empty
        }

        return new TableData(match.tableName(), match.displayName(), match.module(), columns, rows);
    }

    public Map<String, String> getDatabaseMetadata() {
        Map<String, String> meta = new LinkedHashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData dbMeta = conn.getMetaData();
            meta.put("Product Name", dbMeta.getDatabaseProductName());
            meta.put("Product Version", dbMeta.getDatabaseProductVersion());
            meta.put("JDBC URL", dbMeta.getURL());
            meta.put("Driver Name", dbMeta.getDriverName());
            meta.put("Database User", dbMeta.getUserName());
        } catch (SQLException e) {
            meta.put("Status", "Connected via Spring DataSource (" + e.getMessage() + ")");
        }
        return meta;
    }

    public record TableInfo(String tableName, String displayName, String module) {}
    public record TableSummary(String tableName, String displayName, String module, long rowCount) {}
    public record TableData(String tableName, String displayName, String module, List<String> columns, List<Map<String, Object>> rows) {}
}
