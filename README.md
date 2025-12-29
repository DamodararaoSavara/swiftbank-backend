# 🏦 SwiftBank Backend – Secure Digital Banking API

SwiftBank Backend is a secure, scalable digital banking REST API built using Spring Boot.
It implements JWT-based authentication, OTP verification, Redis caching, and role-based authorization to simulate real-world banking operations.

## 🚀 Features
## 🔐 Authentication & Security

    -> JWT Authentication & Authorization

    -> OTP verification for login & password reset

    -> Role-based access control (ADMIN / USER)

    -> Account lock mechanism for failed attempts

    -> Password encryption using BCrypt

## 💳 Banking Operations

    -> Account creation & management

    -> Fund transfer between accounts

    -> Transaction history

    -> Admin account control (activate / deactivate users)
 
 ## ⚡ Performance & Reliability

    -> Redis caching for faster reads

    -> Idempotency handling for safe transactions

    -> Distributed locking using Redis

    -> Global exception handling

## 📩 Communication

    -> SMS OTP using Twilio

    -> Email service for notifications

## 🛠 Tech Stack
Layer	Technology
Backend	Java 17
Framework	Spring Boot
Security	Spring Security, JWT
Database	MySQL
Cache	Redis
Messaging	Twilio SMS, Java Mail
Build Tool	Maven
API Style	RESTful

## 🧱 Project Architecture
com.vipro.banking
├── config          # Security & cache configuration
├── controller      # REST APIs
├── dto             # Request & Response DTOs
├── entity          # JPA Entities
├── exception       # Global exception handling
├── jwt             # JWT filters & providers
├── mapper          # Entity–DTO mappers
├── redis           # Redis services & locks
├── repository      # JPA repositories
├── security        # Custom UserDetailsService
├── service         # Business logic interfaces
├── service.impl    # Business logic implementations
├── utility         # OTP & password utilities

## 🔁 Authentication Flow (JWT + OTP)
User Login
   ↓
Validate Credentials
   ↓
Generate OTP (SMS)
   ↓
Verify OTP
   ↓
Generate JWT Token
   ↓
Access Secured APIs

## 📡 API Highlights
### 🔑 Authentication

POST /api/auth/login

POST /api/auth/verify-otp

POST /api/auth/register

### 💰 Banking

POST /api/account/transfer

GET /api/account/transactions

GET /api/account/profile

### 🛠 Admin

PUT /api/admin/account/lock

PUT /api/admin/account/unlock

## ⚙️ Configuration

Create application.properties :

spring.datasource.url=jdbc:mysql://localhost:3306/swiftbank
spring.datasource.username=db_user
spring.datasource.password=db_password

jwt.secret=your_jwt_secret

twilio.account.sid=your_twilio_sid
twilio.auth.token=your_twilio_token
twilio.phone.number=your_twilio_number

spring.redis.host=localhost
spring.redis.port=6379


## 🔐 Important: Use environment variables in production.

▶️ Run the Application
mvn clean install
mvn spring-boot:run

URL
http://localhost:8080

🧪 Testing
mvn test

## 🧠 Key Learnings

. Secure authentication using JWT + OTP

. Redis caching & distributed locks

. Real-world Spring Security configuration

. Exception handling & clean architecture

. GitHub push-protection & secret management

## 👨‍💻 Author

## *Damodararao Savara*
## Java Backend Developer
🔗 GitHub: https://github.com/DamodararaoSavara

## 📌 Future Enhancements

– Dockerization

– Swagger / OpenAPI documentation

– Rate limiting

– Monitoring with Actuator & Prometheus

– Kafka for async transaction processing

⭐ If you like this project, give it a star!
