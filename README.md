# Passport Management System

A Spring Boot REST API for managing passport applications with role-based access control, document validation, officer verification, and appointment scheduling.

## Project Overview

Passport Management System is a backend application designed to model a real passport processing workflow. The system supports:

- User registration and login
- Passport application creation and tracking
- Document upload and completeness validation
- Officer-led verification
- Appointment scheduling for approved applications
- Role-based access control for citizens, officers, and admins

The project is structured as a RESTful backend and is suitable for API testing through Postman or similar tools.

## Features

### Core Functionality

#### User Authentication and Authorization

- User registration with role assignment
- Login endpoint for credential validation
- Spring Security Basic Authentication for protected APIs
- BCrypt password hashing
- Role-based access control using `CITIZEN`, `OFFICER`, and `ADMIN`

#### Passport Application Management

- Create passport applications
- View applications by role
- Update application details
- Delete applications
- Track application status through the workflow

#### Document Management

- Upload documents for an application
- Support for multiple document types
- Retrieve documents by application
- Delete uploaded documents
- Check whether all required documents are available

#### Verification Workflow

- Officers can verify submitted applications
- Verification remarks can be recorded
- Verification status can be updated
- Citizens can view only their own verification records

#### Appointment Scheduling

- Officers can schedule appointments after verification
- Appointment status management
- Citizens can view their own appointment records
- Admins can manage appointment workflows

#### Role-Based Security

- Citizens can manage only their own applications and documents
- Officers can review and process assigned verifications
- Admins have the widest access across modules
- Endpoint-level restrictions are enforced through Spring Security

## Tech Stack

| Technology | Version | Purpose |
| --- | --- | --- |
| Java | 17+ | Core programming language |
| Spring Boot | 3.2.5 | Application framework |
| Spring Data JPA | - | ORM and persistence |
| Spring Security | - | Authentication and authorization |
| MySQL | 8.0+ | Relational database |
| Lombok | - | Boilerplate reduction |
| Maven | 3.6+ | Build and dependency management |
| Postman | - | API testing |

## Project Structure

```text
com.passport.system
|-- controller
|   |-- ApplicationController
|   |-- AppointmentController
|   |-- AuthController
|   |-- DocumentController
|   `-- VerificationController
|
|-- service
|   |-- ApplicationService
|   |-- AppointmentService
|   |-- DocumentService
|   |-- UserService
|   `-- VerificationService
|
|-- repository
|   |-- ApplicationRepository
|   |-- AppointmentRepository
|   |-- DocumentRepository
|   |-- UserRepository
|   `-- VerificationRepository
|
|-- entity
|   |-- User
|   |-- Role
|   |-- PassportApplication
|   |-- Document
|   |-- DocumentType
|   |-- Verification
|   |-- VerificationStatus
|   |-- Appointment
|   `-- AppointmentStatus
|
|-- dto
|   |-- ApplicationRequestDTO
|   |-- LoginDTO
|   `-- ResponseDTO
|
|-- security
|   |-- CurrentUserService
|   |-- DatabaseUserDetailsService
|   `-- SecurityConfig
|
`-- SystemApplication
```

## Entity Relationship Overview

The application is built around the following relationships:

### Core Relationships

#### User -> Passport Applications (One-to-Many)

- One user can create multiple passport applications
- Each passport application belongs to one user

#### Passport Application -> Documents (One-to-Many)

- One application can have multiple uploaded documents
- Each document belongs to one application

#### Passport Application -> Verification (One-to-One)

- One application can have one verification record
- Each verification belongs to one application

#### User -> Verifications (One-to-Many)

- One officer can verify multiple applications
- Each verification is linked to one officer

#### Passport Application -> Appointment (One-to-One)

- One application can have one appointment
- Each appointment belongs to one application

## Authentication

### Basic Authentication

All secured APIs use HTTP Basic Authentication.

- Authentication type: `Basic Auth`
- Basic Auth username: account `username`
- Basic Auth password: account `password`

Important distinction:

- `/auth/login` expects `email` and `password` in the request body
- Protected endpoints use Basic Auth with `username` and `password`

### Sample Accounts

Use these as testing credentials if the same users exist in your database:

| Role | Username | Password |
| --- | --- | --- |
| Citizen | `viral` | `user1234` |
| Officer | `officer1` | `user1234` |
| Admin | `admin` | `user1234` |

Important:

- These users are not automatically seeded by the current codebase.
- You must register them manually or insert them into MySQL before using them for Basic Auth.

### Example Authorization Header

```text
Authorization: Basic base64(username:password)
```

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| POST | `/auth/register` | Register a new user | No |
| POST | `/auth/login` | Validate login credentials | No |

### Application Management

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| POST | `/applications` | Create a new application | Yes |
| GET | `/applications` | Get all applications | Yes |
| GET | `/applications/{id}` | Get application by ID | Yes |
| PUT | `/applications/{id}` | Update an application | Yes |
| DELETE | `/applications/{id}` | Delete an application | Yes |

### Document Management

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| POST | `/documents?appId={id}` | Add document to an application | Yes |
| GET | `/documents/application/{id}` | Get documents by application | Yes |
| GET | `/documents/check-complete/{userId}` | Check required documents completeness | Yes |
| DELETE | `/documents/{id}` | Delete a document | Yes |

### Verification Management

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| POST | `/verifications?appId={id}&officerId={id}` | Create verification | Yes |
| GET | `/verifications` | Get all verifications | Yes |
| GET | `/verifications/{id}` | Get verification by ID | Yes |
| PUT | `/verifications/{id}` | Update verification | Yes |

### Appointment Management

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| POST | `/appointments?appId={id}` | Create appointment | Yes |
| GET | `/appointments` | Get all appointments | Yes |
| PUT | `/appointments/{id}` | Update appointment | Yes |

## Role Access Summary

### Citizen

- Register and log in
- Create and manage own applications
- Upload and delete own documents
- View own applications, documents, verifications, and appointments

### Officer

- View all applications
- Verify applications
- Schedule and update appointments
- View verifications and appointments

### Admin

- Full access to most modules
- Can create applications
- Can manage verifications and appointments
- Can access records across users

## Request Examples

### 1. Register User

`POST /auth/register`

```json
{
  "username": "viral",
  "email": "viral@example.com",
  "password": "user1234",
  "role": "CITIZEN"
}
```

### 2. Login

`POST /auth/login`

```json
{
  "email": "viral@example.com",
  "password": "user1234"
}
```

### 3. Create Application

`POST /applications`

```json
{
  "fullName": "Viral Dafda",
  "dob": "2003-05-10",
  "address": "Rajkot, Gujarat"
}
```

### 4. Add Document

`POST /documents?appId=1`

```json
{
  "documentType": "AADHAR",
  "filePath": "/uploads/aadhar.pdf"
}
```

Supported document types:

- `AADHAR`
- `PAN`
- `PHOTO`
- `ADDRESS_PROOF`

### 5. Create Verification

`POST /verifications?appId=1&officerId=2`

```json
{
  "status": "VERIFIED",
  "remarks": "Documents are valid"
}
```

Supported verification statuses:

- `VERIFIED`
- `REJECTED`

### 6. Create Appointment

`POST /appointments?appId=1`

```json
{
  "appointmentDate": "2026-04-10",
  "timeSlot": "10:00 AM - 11:00 AM",
  "status": "SCHEDULED"
}
```

Supported appointment statuses:

- `SCHEDULED`
- `COMPLETED`
- `CANCELLED`

## Complete Workflow

Here is a typical end-to-end flow of the system:

### Step 1: Register Users

- Citizen, officer, and admin users are created through `/auth/register`
- Each user is assigned a role

### Step 2: Authenticate

- Users validate credentials using `/auth/login`
- Protected endpoints are then accessed using Basic Auth

### Step 3: Create Passport Application

- Citizen creates a passport application
- Application status starts as `PENDING`

### Step 4: Upload Documents

- Citizen uploads required documents for the application
- Required documents include Aadhaar, PAN, photo, and address proof

### Step 5: Check Document Completeness

- System checks whether all required document types are present

### Step 6: Officer Verification

- Officer reviews the submitted application and documents
- Officer marks verification as `VERIFIED` or `REJECTED`

### Step 7: Schedule Appointment

- If the application proceeds, officer schedules an appointment
- Appointment details include date, timeslot, and status

### Step 8: Complete Appointment

- Officer updates appointment status after the visit
- Workflow moves toward completion

## How To Run The Project

### Prerequisites

- JDK 17 or higher
- Maven 3.6+ or use Maven Wrapper
- MySQL 8.0+

### Step 1: Create Database

Run this in MySQL:

```sql
CREATE DATABASE jt_project;
```

### Step 2: Update Database Configuration

Edit [application.properties](/d:/SEM4/JAVA%20TECHNOLOGY/digital-passport-system/system/src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jt_project
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Step 3: Open Project

Open the repository in IntelliJ IDEA, VS Code, or your preferred Java IDE.

### Step 4: Run the Application

From the project root:

```powershell
cd system
.\mvnw.cmd spring-boot:run
```

Or:

```powershell
cd system
mvn spring-boot:run
```

You can also run [SystemApplication.java](/d:/SEM4/JAVA%20TECHNOLOGY/digital-passport-system/system/src/main/java/com/passport/system/SystemApplication.java) directly from your IDE.

### Step 5: Verify the Application

Base URL:

```text
http://localhost:8080
```

You can verify startup by calling one of the available endpoints such as:

```text
POST /auth/register
```

## How To Test APIs

### Using Postman

Postman files are available in:

- [PassportApp.postman_collection.json](/d:/SEM4/JAVA%20TECHNOLOGY/digital-passport-system/system/postman/PassportApp.postman_collection.json)
- [PassportApp.local.postman_environment.json](/d:/SEM4/JAVA%20TECHNOLOGY/digital-passport-system/system/postman/PassportApp.local.postman_environment.json)

### Basic Auth Setup

For secured endpoints in Postman:

- Go to the `Authorization` tab
- Select `Basic Auth`
- Enter `username`
- Enter `password`

Example:

- Username: `viral`
- Password: `user1234`

### Example API Testing Flow

#### 1. Register a Citizen

`POST http://localhost:8080/auth/register`

```json
{
  "username": "viral",
  "email": "viral@example.com",
  "password": "user1234",
  "role": "CITIZEN"
}
```

#### 2. Create an Application

`POST http://localhost:8080/applications`

Use Basic Auth with:

- Username: `viral`
- Password: `user1234`

```json
{
  "fullName": "Viral Dafda",
  "dob": "2003-05-10",
  "address": "Rajkot, Gujarat"
}
```

#### 3. Upload a Document

`POST http://localhost:8080/documents?appId=1`

```json
{
  "documentType": "AADHAR",
  "filePath": "/uploads/aadhar.pdf"
}
```

#### 4. Check Document Completeness

`GET http://localhost:8080/documents/check-complete/1`

#### 5. Verify Application as Officer

`POST http://localhost:8080/verifications?appId=1&officerId=2`

Use officer credentials in Basic Auth.

```json
{
  "status": "VERIFIED",
  "remarks": "Documents are valid"
}
```

#### 6. Schedule Appointment

`POST http://localhost:8080/appointments?appId=1`

```json
{
  "appointmentDate": "2026-04-10",
  "timeSlot": "10:00 AM - 11:00 AM",
  "status": "SCHEDULED"
}
```

## Business Rules and Notes

- New applications default to status `PENDING`
- Required document types are `AADHAR`, `PAN`, `PHOTO`, and `ADDRESS_PROOF`
- Passwords are stored using BCrypt encoding
- CSRF is disabled for this REST API
- JPA schema generation is configured with `update`
- Citizens are restricted to their own data in most flows
- Officers and admins have broader access depending on endpoint rules

## Troubleshooting

### Issue: Application fails to start

Solution:

- Confirm MySQL is running
- Check database name and credentials in `application.properties`
- Make sure Java 17+ is installed

### Issue: 401 Unauthorized

Solution:

- Check Basic Auth username and password
- Make sure you are using `username` for Basic Auth, not `email`
- Verify the user exists in the database

### Issue: Login works but secured APIs fail

Solution:

- `/auth/login` uses `email`
- Secured endpoints use Basic Auth with `username`
- Both values must belong to the same stored user

### Issue: 403 Forbidden

Solution:

- The authenticated user may not have permission for that resource
- Citizens can only access their own applications and documents

### Issue: Database connection error

Solution:

- Ensure the `jt_project` database exists
- Verify MySQL username and password

### Issue: Port 8080 already in use

Solution:

Add this to [application.properties](/d:/SEM4/JAVA%20TECHNOLOGY/digital-passport-system/system/src/main/resources/application.properties):

```properties
server.port=8081
```

## Future Enhancements

Possible next improvements for the project:

- JWT-based authentication
- File upload support instead of storing only file paths
- Email or SMS notifications
- Application approval dashboards
- Audit logs for verification and appointment updates
- Swagger / OpenAPI documentation
- Admin reporting and analytics

## Conclusion

Passport Management System demonstrates a practical Spring Boot backend with:

- Clean layered architecture
- Role-based API security
- Real-world workflow modeling
- Relational entity design using JPA
- Modular services and controllers

The project is a solid base for extending into a larger e-governance or citizen services platform.
