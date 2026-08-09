# VIT Records — Student Management System

A full-stack rebuild of your project: Spring Boot + MySQL REST API with JWT auth and
role-based access control (Admin / Student), plus a plain HTML/CSS/JS frontend with
a dashboard and charts.

## What's included

**Backend** (`src/main/java/com/vit/sms`)
- JWT authentication — register & login, tokens signed with HMAC, 24h expiry (configurable)
- Role-based access control — `ADMIN` (full CRUD everywhere) vs `STUDENT` (read-only, can view own attendance)
- Student CRUD with server-side **search, department filter, and pagination**
- Course/subject management (CRUD)
- Attendance tracking (record/list/delete, plus attendance % per student)
- Admin dashboard stats endpoint (student count, course count, avg GPA, department breakdown, attendance breakdown) for charts
- Centralized exception handling (`GlobalExceptionHandler`) — consistent JSON error shape, validation errors included
- Bean Validation (`@NotBlank`, `@Email`, `@DecimalMin/Max`, etc.) on every DTO
- Swagger / OpenAPI docs at `/swagger-ui.html`
- A `DataSeeder` that creates a default admin account on first boot: **admin / Admin@123**

**Frontend** (`frontend/`) — plain HTML/CSS/JS, no build step needed
- `index.html` — login
- `register.html` — create an Admin or Student account
- `dashboard.html` — admin-only stats + Chart.js bar/doughnut charts
- `students.html` — registry with search, department filter, pagination, and (admin) create/edit/delete modal
- `courses.html` — course catalog CRUD (admin) / read-only list (student)
- `attendance.html` — look up attendance + percentage; admin can record/delete entries

## Setup

### 1. Database
Create nothing manually — `createDatabaseIfNotExist=true` in `application.properties` will create
the `vit_records` schema automatically the first time you run the app. Just make sure MySQL is running
and update the credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

Also **change `jwt.secret`** to your own long random string before you deploy this anywhere real.

### 2. Run the backend
This project doesn't include the Maven wrapper — copy your existing `mvnw`, `mvnw.cmd`, and
`.mvn/` folder into this project root (or open it in IntelliJ/VS Code with Maven support and
just run `SmsApplication`), then:
```bash
mvn spring-boot:run
```
It starts on `http://localhost:8081`. On first run you'll see in the console:
```
>>> Default admin created -> username: admin | password: Admin@123
```

Swagger UI: `http://localhost:8081/swagger-ui.html`

### 3. Run the frontend
The frontend is static — just open `frontend/index.html` in a browser, or serve it with any
static server, e.g.:
```bash
cd frontend
python3 -m http.server 5500
```
Then visit `http://localhost:5500`. It calls the API at `http://localhost:8081/api`
(configured in `frontend/js/api.js` via `API_BASE` — change this if you deploy the backend elsewhere).

## How auth/roles work

- Register as `STUDENT` and you also create a linked `Student` record (first/last name, department, GPA) in one step.
- Register as `ADMIN` for a plain admin account.
- Every protected endpoint expects `Authorization: Bearer <token>`. The frontend stores the token
  in `localStorage` and attaches it automatically (see `frontend/js/api.js`).
- `@PreAuthorize("hasRole('ADMIN')")` guards all create/update/delete endpoints; `STUDENT`s
  can view student/course lists and their own attendance, but can't modify data.

## Extending this further

Some natural next steps if you want to keep building:
- Add a "my profile" page for students to view/update their own info
- Add course enrollment (currently `Student` has a `courses` many-to-many field, but no
  enroll/unenroll endpoint yet — quick to add: `POST /api/students/{id}/courses/{courseId}`)
- Add refresh tokens instead of a single 24h JWT
- Add pagination to the course list once your catalog grows
- Write integration tests with Testcontainers + MySQL

## Project structure
```
student-management-system/
├── pom.xml
├── src/main/java/com/vit/sms/
│   ├── config/          # Security, OpenAPI, DataSeeder
│   ├── security/        # JWT util, filter, UserDetailsService
│   ├── entity/           # User, Student, Course, Attendance, Role
│   ├── repository/      # Spring Data JPA repos
│   ├── dto/              # Request/response DTOs with validation
│   ├── service/          # Business logic
│   ├── controller/      # REST controllers
│   └── exception/        # Global exception handling
├── src/main/resources/application.properties
└── frontend/
    ├── index.html / register.html / dashboard.html / students.html / courses.html / attendance.html
    ├── css/style.css
    └── js/ (api.js, nav.js)
```
