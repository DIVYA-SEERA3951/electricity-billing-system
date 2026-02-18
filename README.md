# ⚡ PowerBill Manager — Full Stack Electricity Billing System

A **production-ready full stack web application** with **role-based access control** built using:
- **Backend**: Spring Boot 3 · Java 17 · Spring Data JPA · HttpSession
- **Database**: MySQL (Railway)
- **Frontend**: HTML5 · CSS3 · Bootstrap 5 · Vanilla JS · Fetch API
- **Deployment**: Railway

---

## 👥 User Roles

| Feature | Admin 🛡️ | Customer 👤 |
|---------|-----------|------------|
| Add customers | ✅ | ❌ |
| View all customers | ✅ | ❌ |
| Delete customers | ✅ | ❌ |
| Generate bills | ✅ | ❌ |
| View all bills | ✅ | ❌ |
| View own profile | ❌ | ✅ |
| View own bills | ❌ | ✅ |

---

## 🖥️ Pages

| Page | URL | Access |
|------|-----|--------|
| Landing | `/index.html` | Public |
| Register | `/register.html` | Public |
| Login | `/login.html` | Public |
| Admin Dashboard | `/admin-dashboard.html` | ADMIN only |
| Customer Dashboard | `/customer-dashboard.html` | CUSTOMER only |
| Customers | `/customers.html` | ADMIN only |
| Bills | `/bills.html` | ADMIN only |

---

## 📁 Project Structure

```
electricity-billing-system/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/billing/
    │   ├── ElectricityBillingApplication.java
    │   ├── model/
    │   │   ├── Role.java            ← Enum: ADMIN, CUSTOMER
    │   │   ├── User.java
    │   │   ├── Customer.java
    │   │   └── Bill.java
    │   ├── repository/
    │   │   ├── UserRepository.java
    │   │   ├── CustomerRepository.java
    │   │   └── BillRepository.java
    │   ├── service/
    │   │   ├── SessionHelper.java   ← Session validation utility
    │   │   ├── AuthService.java
    │   │   ├── AdminService.java
    │   │   └── CustomerService.java
    │   ├── controller/
    │   │   ├── AuthController.java
    │   │   ├── AdminController.java
    │   │   └── CustomerController.java
    │   └── exception/
    │       ├── ResourceNotFoundException.java
    │       ├── UnauthorizedException.java
    │       ├── ForbiddenException.java
    │       └── GlobalExceptionHandler.java
    └── resources/
        ├── application.properties
        └── static/
            ├── index.html
            ├── register.html
            ├── login.html
            ├── admin-dashboard.html
            ├── customer-dashboard.html
            ├── customers.html
            ├── bills.html
            ├── css/style.css
            └── js/app.js
```

---

## 🔌 REST API Endpoints

### Auth (Public)

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| POST | `/api/register` | `{username, password, role, name?, email?, address?}` | Register |
| POST | `/api/login` | `{username, password}` | Login, sets HttpSession |
| POST | `/api/logout` | — | Invalidate session |
| GET | `/api/auth/check` | — | Check session status |

### Admin (Role: ADMIN required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/customers` | Add customer |
| GET | `/api/admin/customers` | Get all customers |
| DELETE | `/api/admin/customers/{id}` | Delete customer |
| POST | `/api/admin/bills` | Generate bill |
| GET | `/api/admin/bills` | Get all bills |

### Customer (Role: CUSTOMER required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/customer/profile` | Get own profile |
| GET | `/api/customer/bills` | Get own bills |

---

## 💡 Billing Rate Logic

| Units Consumed | Rate |
|---------------|------|
| 0 – 100 | ₹3.50 / unit |
| 101 – 300 | ₹5.00 / unit |
| 301+ | ₹7.00 / unit |

Example: 250 units = (100 × 3.50) + (150 × 5.00) = ₹1,100

---

## ☁️ Railway Deployment (Step-by-Step)

### Step 1: Push to GitHub

```bash
git init
git add .
git commit -m "Initial commit - Electricity Billing System"
git branch -M main
git remote add origin https://github.com/DIVYA-SEERA3951/electricity-billing-system.git
git push -u origin main
```

### Step 2: Create a Railway Account

Go to [railway.app](https://railway.app) and sign up with your GitHub account.

### Step 3: Create New Project

Click **"New Project"** → **"Deploy from GitHub repo"** → select `electricity-billing-system`.

### Step 4: Add MySQL Service

In your Railway project:
1. Click **"+ New"** → **"Database"** → **"Add MySQL"**
2. Railway creates a MySQL instance automatically.
3. Click on the MySQL service → go to **"Variables"** tab.
4. Copy these values:
   - `MYSQL_URL` — the full JDBC URL (starts with `jdbc:mysql://...`)
   - `MYSQLUSER` — the MySQL username
   - `MYSQLPASSWORD` — the MySQL password

> **Important:** The `MYSQL_URL` from Railway looks like:
> `mysql://user:pass@host:port/dbname`
> You must convert it to JDBC format:
> `jdbc:mysql://host:port/dbname?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`

### Step 5: Set Environment Variables

In your Spring Boot web service on Railway:
1. Click on your web service
2. Go to **"Variables"** tab
3. Add these variables:

| Variable | Value |
|----------|-------|
| `MYSQL_URL` | `jdbc:mysql://HOST:PORT/railway?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `MYSQLUSER` | your MySQL username from Railway |
| `MYSQLPASSWORD` | your MySQL password from Railway |

### Step 6: Deploy

Railway will automatically:
- Detect it's a Maven/Java project
- Run `mvn clean install`
- Start with `java -jar target/electricity-billing-system-0.0.1-SNAPSHOT.jar`

Wait 2–3 minutes for the build to finish. Your app will get a public URL like:
`https://electricity-billing-system-production.up.railway.app`

---

## 🛠️ Build & Start Commands

```bash
# Build
mvn clean install -DskipTests

# Start
java -jar target/electricity-billing-system-0.0.1-SNAPSHOT.jar
```

---

## ⚠️ Common Railway Deployment Errors & Fixes

| Error | Cause | Fix |
|-------|-------|-----|
| `Communications link failure` | Wrong DB host/port in MYSQL_URL | Copy the exact host:port from Railway MySQL Variables |
| `Access denied for user` | Wrong MYSQLUSER or MYSQLPASSWORD | Double-check Railway MySQL variables tab |
| `Unknown database 'railway'` | DB name wrong in URL | Railway default DB name is `railway` |
| `java.lang.UnsupportedClassVersionError` | Java version mismatch | Railway uses Java 17 by default; no extra config needed |
| Build timeout | First build is slow | Wait 5 minutes; Railway caches Maven dependencies |
| 404 on all pages | Context path issue | Ensure `server.port=${PORT:8080}` is in application.properties |
| Session not working | Cookie issue | Ensure you use `credentials: 'same-origin'` in fetch (already set in app.js) |
| `Table doesn't exist` | DDL not running | Verify `spring.jpa.hibernate.ddl-auto=update` in application.properties |

---

## 🔐 How Session Works (No localStorage)

```
User logs in → POST /api/login
                   ↓
Backend validates credentials
                   ↓
session.setAttribute("userId", user.getId())
session.setAttribute("username", user.getUsername())
session.setAttribute("role", user.getRole().name())
                   ↓
Browser stores JSESSIONID cookie automatically
                   ↓
Every protected API call sends JSESSIONID cookie
                   ↓
Backend reads session → validates role → returns data or 401/403
```

The frontend never touches the session. The server manages it entirely.

---

## 🎓 Interview Preparation

---

### 1. Strong 2-Minute Project Explanation

> "I built a Full Stack Electricity Billing System using Spring Boot 3 and Java 17 for the backend, MySQL for the database, and plain HTML with Bootstrap 5 for the frontend. What makes this project stand out is the implementation of role-based access control — there are two roles, Admin and Customer, each with different permissions. Admins can manage customers and generate bills. Customers can only view their own profile and bills. I used server-side HttpSession for authentication — the session is managed by Spring's embedded Tomcat and secured without Spring Security. The 3-layer architecture separates concerns cleanly: Controllers handle HTTP, Services handle business logic, and Repositories handle database access via Spring Data JPA. The app is deployed on Railway cloud with a cloud-hosted MySQL database. Tables are created automatically by Hibernate on startup using ddl-auto=update."

---

### 2. Core Concepts

#### 3-Layer Architecture

```
HTTP Request
     ↓
Controller Layer (AuthController, AdminController, CustomerController)
   - Handles HTTP methods, request parsing, response formatting
     ↓
Service Layer (AuthService, AdminService, CustomerService)
   - Business logic, session validation, bill calculation
     ↓
Repository Layer (UserRepository, CustomerRepository, BillRepository)
   - Database queries via Spring Data JPA
     ↓
MySQL Database (Railway)
```

Each layer has one responsibility. This makes testing and maintenance easy.

#### Role-Based Access Using HttpSession

When a user logs in:
1. Backend validates username/password
2. Session stores: userId, username, role
3. Browser stores JSESSIONID cookie automatically
4. On every protected request, the cookie is sent
5. Backend reads the session, checks the role
6. Returns 401 (not logged in) or 403 (wrong role) if invalid

This is done without Spring Security — pure manual session management via `HttpSession`.

#### REST Architecture Principles
- **Stateless communication** (each request is independent, but session is on server)
- **Resource-based URLs**: `/api/admin/customers`, `/api/customer/bills`
- **HTTP verbs**: GET (read), POST (create), DELETE (remove)
- **JSON**: standard input/output format
- **Status codes**: 200 OK, 201 Created, 401 Unauthorized, 403 Forbidden, 404 Not Found

#### JPA vs JDBC

| JDBC | JPA (Hibernate) |
|------|----------------|
| Manual SQL: `SELECT * FROM customers` | Auto: `customerRepository.findAll()` |
| Manual ResultSet parsing | Auto object mapping |
| High boilerplate | Minimal code |
| Error-prone | Type-safe |

JPA is an abstraction layer. Hibernate is the implementation. Spring Data JPA wraps Hibernate for zero-boilerplate repositories.

#### Dependency Injection
Spring creates and manages objects. `@Autowired` injects dependencies automatically.
```java
@Autowired
private CustomerRepository customerRepository; // Spring injects this
```
This avoids `new CustomerRepository()` — promotes loose coupling and testability.

#### Transaction Management
`@Transactional` ensures that if multiple DB operations run together, either ALL succeed or ALL rollback.
```java
@Transactional
public Customer addCustomer(...) {
    // If save() fails, nothing is committed
    return customerRepository.save(customer);
}
```
`@Transactional(readOnly = true)` optimizes read-only operations (no dirty checking by Hibernate).

#### Frontend-Backend Integration

```
Browser (HTML + JS)
    ↓ Fetch API call with credentials:'same-origin'
    ↓ JSESSIONID cookie sent automatically
Spring Boot Backend (REST Controller)
    ↓ Reads HttpSession
    ↓ Validates role
    ↓ Returns JSON
Browser renders the JSON as HTML table / card
```

No page reloads. The frontend uses the Fetch API to call REST endpoints and dynamically update the DOM.

---

### 3. 30 Technical Interview Questions & Answers

**Spring Boot**

**Q1. What is Spring Boot?**
A: A framework that simplifies Spring application setup with auto-configuration, embedded Tomcat server, and starter dependencies. No XML config required.

**Q2. What is `@SpringBootApplication`?**
A: It combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`. It bootstraps the entire application.

**Q3. What is the difference between `@Controller` and `@RestController`?**
A: `@RestController` = `@Controller` + `@ResponseBody`. It automatically converts return values to JSON. Used for REST APIs.

**Q4. What is `@Service`, `@Repository`, `@Component`?**
A: All are Spring beans. `@Service` marks business logic, `@Repository` marks data access (+ exception translation), `@Component` is generic.

**Q5. What is Embedded Tomcat?**
A: Spring Boot includes Tomcat inside the JAR. You run `java -jar app.jar` — no external server needed.

**Q6. What does `@Transactional` do?**
A: Wraps the method in a database transaction. If any operation fails, all DB changes rollback. Ensures data integrity.

**Q7. What is `@Transactional(readOnly = true)`?**
A: Hints Hibernate to skip dirty checking — better performance for read-only operations.

**Q8. What is `ResponseEntity`?**
A: A wrapper for HTTP responses allowing you to control the status code, headers, and body.

**JPA & Hibernate**

**Q9. What is JPA?**
A: Java Persistence API is a specification for ORM (Object Relational Mapping). Hibernate is the implementation.

**Q10. What is Spring Data JPA?**
A: A layer on top of JPA that gives you repositories for free — `findAll()`, `save()`, `deleteById()`, etc., without writing SQL.

**Q11. What is `@Entity`?**
A: Marks a Java class as a database table. Each instance is a row.

**Q12. What is `@Id` and `@GeneratedValue`?**
A: `@Id` is the primary key. `@GeneratedValue(IDENTITY)` auto-increments it in MySQL.

**Q13. What is `@OneToMany` and `@ManyToOne`?**
A: One Customer → Many Bills (`@OneToMany`). Each Bill → One Customer (`@ManyToOne`). Maps to a foreign key in DB.

**Q14. What is `@OneToOne`?**
A: One User → One Customer profile. Mapped with a foreign key (`user_id`) in the Customer table.

**Q15. What is `@Enumerated(EnumType.STRING)`?**
A: Stores enum values as strings (ADMIN, CUSTOMER) in the database instead of integers. More readable.

**Q16. What is `CascadeType.ALL`?**
A: If you delete a Customer, their Bills are also deleted. The operation cascades from parent to child.

**Q17. What is `FetchType.LAZY` vs `FetchType.EAGER`?**
A: LAZY — load related data only when accessed (better performance). EAGER — load everything immediately.

**Q18. What is `spring.jpa.hibernate.ddl-auto=update`?**
A: Hibernate automatically creates/updates tables on startup based on your `@Entity` classes.

**Session & Security**

**Q19. What is HttpSession?**
A: A server-side storage object tied to a browser via a cookie (JSESSIONID). Stores user data between requests.

**Q20. How does role-based access work without Spring Security?**
A: By manually checking `session.getAttribute("role")` in the service layer and throwing `UnauthorizedException` (401) or `ForbiddenException` (403).

**Q21. What happens when session expires?**
A: Session is invalidated after 30 minutes (configured in `application.properties`). Next API call returns 401, frontend redirects to login.

**Q22. What is JSESSIONID?**
A: A cookie the browser stores automatically after a successful login. It's sent with every subsequent request so the server can identify the session.

**REST & Controllers**

**Q23. What is `@RequestBody`?**
A: Converts incoming JSON to a Java object automatically (deserialization using Jackson).

**Q24. What is `@PathVariable`?**
A: Extracts a value from the URL path. Example: `/customers/{id}` — `@PathVariable Long id`.

**Q25. What HTTP status codes did you use?**
A: 200 OK, 201 Created, 400 Bad Request, 401 Unauthorized (no session), 403 Forbidden (wrong role), 404 Not Found, 500 Internal Server Error.

**Q26. What is `@RestControllerAdvice`?**
A: Global exception handler that intercepts exceptions from all controllers and returns formatted JSON error responses.

**Q27. What is `@ExceptionHandler`?**
A: Marks a method to handle a specific exception type, called when that exception is thrown anywhere.

**Frontend**

**Q28. What is the Fetch API?**
A: A modern browser API for making HTTP requests from JavaScript. Replaces XMLHttpRequest (AJAX).

**Q29. Why `credentials: 'same-origin'` in fetch?**
A: Ensures the browser sends the JSESSIONID cookie with every fetch request. Without this, the session won't work.

**Q30. How does the frontend check session on page load?**
A: It calls `GET /api/auth/check`. If 401 → redirect to login. If wrong role → redirect to correct dashboard.

---

### 4. 10 HR Interview Questions

**Q1. Tell me about yourself.**
> "I'm a fresher passionate about backend development. I built a full stack Electricity Billing System using Spring Boot, Java 17, MySQL, and Bootstrap. The project has role-based access for Admins and Customers and is deployed on Railway cloud. I enjoy building systems that solve real-world problems."

**Q2. Why Java and Spring Boot?**
> "Java is the enterprise standard — used by banks, telecom, and Fortune 500 companies. Spring Boot makes it production-ready fast with minimal configuration. I wanted to learn something that's directly usable in the industry."

**Q3. What challenges did you face?**
> "Implementing role-based access without Spring Security was a challenge. I had to design a clean session validation pattern using a utility class (SessionHelper) that all services call. Getting the Railway MySQL connection string in the correct JDBC format was also tricky."

**Q4. How is your project different from a basic CRUD app?**
> "Most beginners make basic CRUD with no security. My project has two distinct user roles with different permissions, server-side session management, a clean 3-layer architecture, and cloud deployment with a live MySQL database."

**Q5. Where do you see yourself in 5 years?**
> "As a senior Java backend developer or solution architect, designing scalable microservices and leading technical decisions in a product team."

**Q6. Why should we hire you?**
> "I have hands-on experience building, deploying, and understanding the full cycle of a Java web application — from architecture to cloud deployment. I learn fast and take ownership of what I build."

**Q7. Are you comfortable with new technologies?**
> "Yes. I taught myself Spring Boot, JPA, Railway deployment, and server-side session management for this project with no IDE — just a text editor and GitHub."

**Q8. How do you handle pressure or bugs?**
> "I isolate the problem, read the stack trace carefully, and test assumptions one at a time. When my Railway deployment failed due to a wrong MYSQL_URL format, I debugged systematically until I found the JDBC format difference."

**Q9. What is your greatest strength?**
> "Problem-solving with persistence. I don't stop until I understand why something works or doesn't work."

**Q10. Do you have questions for us?**
> "What does the typical first-month onboarding look like for a junior developer? What's the tech stack the team is currently working with?"

---

### 5. Cloud Deployment Architecture Explanation

> "My Spring Boot app is packaged as a fat JAR containing all dependencies, including the embedded Tomcat server. This JAR is deployed on Railway as a web service. Railway detects the Maven project, runs `mvn clean install` to build it, and starts it with `java -jar`. The static HTML/CSS/JS files are served directly from Spring Boot's classpath at `/resources/static/`. The database is a separate Railway MySQL service. The Spring Boot app connects to it using environment variables — `MYSQL_URL`, `MYSQLUSER`, `MYSQLPASSWORD` — so no credentials are hardcoded. Hibernate's `ddl-auto=update` creates the tables automatically on first startup."

**Architecture Diagram:**
```
Browser (Chrome / Firefox)
        │
        │ HTTPS
        ▼
Railway Web Service (Spring Boot JAR, Port 8080)
  ├─ Static files: index.html, dashboard.html, app.js (served by Tomcat)
  ├─ REST APIs: /api/login, /api/admin/*, /api/customer/*
  └─ HttpSession: stored in Tomcat memory, tracked via JSESSIONID cookie
        │
        │ JDBC (MySQL protocol)
        ▼
Railway MySQL Service (cloud-hosted database)
  ├─ Table: users
  ├─ Table: customers
  └─ Table: bills
```

---

### 6. Why HttpSession Instead of JWT?

| Feature | HttpSession | JWT |
|---------|-------------|-----|
| Storage | Server memory | Client (localStorage/cookie) |
| Complexity | Simple | Requires token generation, signing, validation |
| Stateless | No (stateful) | Yes |
| Fresher suitability | ✅ Easy to understand | Requires cryptography concepts |
| Scalability | Needs sticky sessions (for multiple servers) | Scales horizontally |
| Logout | Instant (invalidate session) | Token lives until expiry |

> "For a single-server deployment like Railway, HttpSession is simpler, secure, and easier to explain in an interview. JWT adds unnecessary complexity for this project scope. In production at scale, I would consider JWT or Spring Security with OAuth2."

---

### 7. Why Spring Security Was Skipped

> "Spring Security is powerful but adds significant complexity — filter chains, SecurityContext, UserDetailsService, password encoders, CSRF configuration. For a fresher-level project, implementing manual session-based auth helps you understand *what Spring Security does under the hood*. I intentionally chose HttpSession so I could explain every line of the security logic myself. In a production project, I would add Spring Security with BCrypt password hashing and CSRF protection."

---

## 📌 Quick Tech Stack Reference

| Technology | Role |
|------------|------|
| Java 17 | Programming language |
| Spring Boot 3.2 | Application framework |
| Spring Data JPA | Database abstraction |
| Hibernate | ORM implementation |
| MySQL | Relational database |
| Maven | Build tool |
| HttpSession | Session management |
| Railway | Cloud deployment |
| Bootstrap 5 | CSS framework |
| Fetch API | Frontend HTTP calls |
| Embedded Tomcat | Application server |
