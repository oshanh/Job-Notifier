# API Documentation

This document lists all available API endpoints in the Job Notifier backend application, mapping out request payloads, response bodies, and specific authentication contexts. The base URL for all local development requests is `http://localhost:8080`.

---
## 1. Authentication & Identity (Auth API)
Endpoints for User authentication, JWT issuance, and OTP registration operations. (Publicly Accessible)

### Login
* **URL:** `POST /auth/login`
* **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "password": "strongPassword123"
  }
  ```
* **Success Response (200 OK):** `{"token": "<JWT_STRING>"}`

### Register User
* **URL:** `POST /auth/register`
* **Request Body:**
  ```json
  {
    "name": "John Doe",
    "email": "user@example.com",
    "password": "strongpassword"
  }
  ```
* **Success Response (200 OK):** `{"token": "OTP_SENT"}` - Dispatches OTP to email asynchronously.

### Verify Registration OTP
* **URL:** `POST /auth/verify-registration`
* **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "otp": "123456"
  }
  ```
* **Success Response (200 OK):** User is persisted. Returns authentication token: `{"token": "<JWT_STRING>"}` 

### Forgot Password
* **URL:** `POST /auth/forgot-password`
* **Request Body:** `{"email": "user@example.com"}`
* **Success Response (200 OK):** `{"message": "OTP dispatched to your email"}`

### Reset Password
* **URL:** `POST /auth/reset-password`
* **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "otp": "123456",
    "newPassword": "newSecretPassword!!"
  }
  ```
* **Success Response (200 OK):** `{"message": "Password resetting successfully completed"}`

---

## 2. Personal Profile (User API)
Endpoints handling authenticated user contexts.
*(Requires implicit JWT Login context on all endpoints)*

### Get Current Profile
* **URL:** `GET /user/me`
* **Success Response (200 OK):** Returns stripped Current Context `UserDTO`.

### Update Current Profile
* **URL:** `PUT /user/me`
* **Request Body:** `UserDTO`
* **Success Response (200 OK):** Updates basic properties like Name. Returns `UserDTO` with refreshed Token.

### Request Email Change 
* **URL:** `POST /user/request-email-change`
* **Request Body:** `{"newEmail": "new@domain.com"}` (OTP dispatches to updated inbox).

### Verify Email Change
* **URL:** `POST /user/verify-email-change`
* **Request Body:** `{"newEmail": "new@domain.com", "otp": "123456"}`

---

## 3. Administrators (Admin API)
Endpoints handling deep system overrides and raw database tracking. 
*(Requires explicitly assigned `ROLE_ADMIN` authentication token)*

### List All Users
* **URL:** `GET /admin/users/all`
* **Success Response:** `List<UserDTO>`

### Add/Update/Delete Users directly
* **URLs:** 
  * `POST /admin/users/add`
  * `PUT /admin/users/update`
  * `DELETE /admin/users/delete`
* **Request Body:** Requires context-bound `UserDTO`.

---

## 4. Notifications & FOSMIS Engine
Internal University Student tracking hooks.

### FOSMIS Public Registration (Public)
* **URL:** `POST /fosmis-notification`
* **Description:** Initiates subscript-validation. Returns `{"message": "OTP_SENT"}`.
* **URL:** `POST /fosmis-notification/verify`
* **Description:** Expects `{ "email", "otp", ...FosmisProperties}`. Sets Active status permanently.

### FOSMIS Administrative Portal
* **URL:** `GET /fosmis` *(Requires `ROLE_ADMIN`)*
* **URL:** `GET /fosmis/{username}`
* **URL:** `POST /fosmis` 
* **URL:** `PUT /fosmis/{username}` 
* **URL:** `DELETE /fosmis/{username}` 

---

## 5. System Definitions
Endpoints for mapping Scrapers to UI constraints.

### Websites APIs *(Requires `ROLE_ADMIN` for POST/PUT/DELETE)*
* **URL:** `GET /websites` - Retrieves website properties natively.
* **URL:** `POST /websites`
* **URL:** `POST /websites/urls`

### References/Configurations *(Requires Custom Ownership matching JWT Session OR `ROLE_ADMIN`)*
* **URL:** `GET /pref` `[?email=target@mail.com]`
* **URL:** `POST /pref`, `PUT /pref`, `DELETE /pref`

---

## 6. Test Executables
Local utility commands (Often Public / Dev Scoped)

* `GET /test/scrape-topjobs` 
* `GET /test/scrape-airport` 
* `GET /test/scrape-kaleniya-uni`
* `GET /test/chat?message=AnyPromptText`
* `POST /test/gmail` - Requires `TesTGmailDTO`

---

## Configuration Reference
Key properties used by the application (set via `.env` or environment variables):

| Property | Description |
|---|---|
| `notify.email` | Administrative email alert routing pool |
| `fosmis.username` | Active crawler internal auth username |
| `fosmis.pwd` | Active crawler internal auth password |
| `GMAIL_USERNAME` | SMTP sending outbound dispatcher |
| `GMAIL_APP_PASSWORD` | 16-character SMTP security hash |
