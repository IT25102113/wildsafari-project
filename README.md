# Wildlife Safari Trip Management System (SE2030 Group 03)

An enterprise-grade, production-ready web application developed using **Java 17, Spring Boot 3, and MySQL**, engineered to satisfy all requirements of the **SE2030 Software Engineering Group Project (Group 03)**.

---

## 👥 University Member Roles & Modules
Each student's module is cleanly organized under `com.safari.module`:
1. **Gallage S.P (IT25101916)** &rarr; `com.safari.module.package_mgmt` (Module 1: Safari Package Management)
2. **Gamarachchi D.A (IT25102113)** &rarr; `com.safari.module.booking_mgmt` (Module 2: Booking & Reservation Management)
3. **Kalhara N.O (IT25100156)** &rarr; `com.safari.module.allocation_mgmt` (Module 3: Guide & Vehicle Fleet Allocation)
4. **Gunawardhana M.S (IT25101131)** &rarr; `com.safari.module.conservation_mgmt` (Module 4: Conservation & Compliance Management)
5. **Dimalsha K.G.T (IT25101218)** &rarr; `com.safari.module.inventory_mgmt` (Module 5: Inventory & Equipment Management)
6. **Patabendi M.K.K (IT25101973)** &rarr; `com.safari.module.finance_mgmt` (Module 6: Payment & Invoice Processing)
7. **Chief Admin & System Core** &rarr; `com.safari.module.user_mgmt` & `com.safari.common` (User creation, Audit Trail)

---

## 🎨 Design System & Palette
- **Palette:** `#8EA66B` (Safari Olive) | `#D8A2A2` (Dusty Rose) | `#FFDCDC` (Soft Blush) | `#FFF9D6` (Warm Sand)
- **Responsive Layout:** Desktop (1920px), Tablet (768px), and Mobile (390px).

---

## 🏛️ Academic Design Patterns Implemented
1. **Singleton Pattern:** `com.safari.util.DatabaseConnectionManager`
2. **Strategy Pattern:** `com.safari.patterns.pricing.PricingStrategy` (Standard vs Peak Season Dynamic Surge)
3. **Factory Pattern:** `com.safari.patterns.factory.ReferenceFactory` (Vouchers, Invoices, Permits)
4. **Observer Pattern:** `com.safari.patterns.observer.ActivityEventListener` (Decoupled event-driven audit logging)

---

## 🚀 How to Run in IntelliJ IDEA & MySQL (XAMPP)

### Step 1: MySQL Setup
- Start MySQL in **XAMPP Control Panel** (Port `3306`).
- Credentials in `application.properties`: User: `root`, Password: `root123`, DB: `safari_db`.
- Import `safari_db.sql` into **phpMyAdmin** or run the script in MySQL Workbench.

### Step 2: Open and Run in IntelliJ IDEA
- In IntelliJ: **File &rarr; Open &rarr; `/Users/dularaanjana/Desktop/se`**.
- Navigate to `src/main/java/com/safari/SafariApplication.java`.
- Click the green **Run ▶** button.
- Open: `http://localhost:8080` in your web browser.

---

## 🎓 Viva Demo Quick Role Switcher
Use the top switcher banner to immediately change perspectives without re-authenticating:
- **Admin:** `admin@safari.lk` / `admin123`
- **1. Packages (Gallage):** `operator@safari.lk` / `operator123`
- **2. Bookings (Gamarachchi):** `kavinda.perera@gmail.com` / `pass123`
- **3. Allocation (Kalhara):** `ops@safari.lk` / `ops123`
- **4. Conservation (Gunawardhana):** `ranger@safari.lk` / `ranger123`
- **5. Logistics (Dimalsha):** `logistics@safari.lk` / `logistics123`
- **6. Finance (Patabendi):** `finance@safari.lk` / `finance123`
