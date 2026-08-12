# API Documentation

This document lists all available API endpoints in the Job Notifier backend application, along with example requests and responses. The base URL for all local development requests is `http://localhost:8080`.

---

## 1. Preferences API
Endpoints for managing user job-alert preferences.

### Get Preferences (By Email)
* **URL:** `GET /pref?email=user@example.com`
* **Query Parameters:** `email` (string, required)
* **Success Response (200 OK):**
```json
{
  "uid": null,
  "email": "user@example.com",
  "keyword": ["java", "spring boot"],
  "whatsapp_num": null,
  "telegram_id": null,
  "whatsapp_enabled": false,
  "telegram_enabled": false,
  "email_enabled": true
}
```

### Create Preferences
* **URL:** `POST /pref`
* **Request Body:**
```json
{
  "email": "user@example.com",
  "keyword": ["software engineer", "developer"],
  "whatsapp_num": "+1234567890",
  "telegram_id": "user1234",
  "whatsapp_enabled": true,
  "telegram_enabled": true,
  "email_enabled": true
}
```
* **Success Response (200 OK):** Returns the saved `PreferenceDTO`.

### Update Preferences
* **URL:** `PUT /pref`
* **Request Body:** Same shape as Create. Clears and replaces existing keywords.
* **Success Response (200 OK):** Returns the updated `PreferenceDTO`.

### Delete Preferences
* **URL:** `DELETE /pref?email=user@example.com`
* **Query Parameters:** `email` (string, required)
* **Success Response (200 OK):**
```text
Preference deleted successfully
```

---

## 2. User API
Endpoints for managing user accounts.

### Get All Users
* **URL:** `GET /user/all`
* **Success Response (200 OK):**
```json
[
  {
    "name": "John Doe",
    "email": "john@example.com"
  }
]
```

### Create User
* **URL:** `POST /user/add`
* **Request Body:**
```json
{
  "name": "John Doe",
  "email": "user@example.com",
  "password": "strongPassword123"
}
```
* **Success Response (200 OK):** Returns the saved `UserDTO` (without password).

---

## 3. Website API
Endpoints for managing scrapable websites and their specific URLs.

### Get All Websites
* **URL:** `GET /websites`
* **Success Response (200 OK):**
```json
[
  {
    "website": "TopJobs",
    "url": [
      "https://topjobs.lk/applicant/vacancybyfunctionalarea.jsp?FA=SDV",
      "https://topjobs.lk/applicant/vacancybyfunctionalarea.jsp?FA=ENG"
    ]
  }
]
```

### Create Website
* **URL:** `POST /websites`
* **Request Body:**
```json
{
  "website": "TopJobs",
  "url": ["https://topjobs.lk/some-section"]
}
```
* **Success Response (200 OK):** Returns the created `WebsiteDTO`.

### Add URLs to Existing Website
* **URL:** `POST /websites/urls`
* **Request Body:**
```json
{
  "website": "TopJobs",
  "url": ["https://topjobs.lk/another-section"]
}
```
* **Success Response (200 OK):** Returns the updated `WebsiteDTO`.

---

## 4. Test API
Endpoints for manually triggering scrapes, sending test notifications, and testing integrations via HTTP.

### Scrape TopJobs (Manual Trigger)
* **URL:** `GET /test/scrape-topjobs`
* **Description:** Manually triggers the TopJobs scraper, saves new jobs to the DB, and fires preference-matched email notifications.
* **Success Response (200 OK):** Returns a list of **new** scraped jobs (empty list if no new jobs).
```json
[
  {
    "position": "Software Engineer",
    "companyName": "Company XYZ",
    "source": "https://www.topjobs.lk/...",
    "closingDate": "2026-09-01"
  }
]
```

### Scrape Airport Jobs (Manual Trigger)
* **URL:** `GET /test/scrape-airport`
* **Description:** Manually triggers the Airport & Aviation Services scraper and sends job notification email to `notify.email`.
* **Success Response (200 OK):** Returns the full list of currently listed airport jobs.
```json
[
  {
    "position": "Aircraft Technician",
    "companyName": "Airport",
    "source": "https://www.airport.lk/...",
    "closingDate": "2026-09-15"
  }
]
```

### Send Test Gmail
* **URL:** `POST /test/gmail`
* **Description:** Sends a custom test email via the configured SMTP server. Returns `true` on success.
* **Request Body:**
```json
{
  "email": "recipient@example.com",
  "subject": "Test Subject",
  "message": "Hello from Job Notifier!"
}
```
* **Success Response (200 OK):**
```json
true
```

### AI Chat / Email
* **URL:** `GET /test/chat?message=Tell me a joke about Java`
* **Description:** Sends a prompt to the AI service, which generates a response and emails it to `notify.email`.
* **Query Parameters:** `message` (string, optional — defaults to `"Tell me a joke about Java"`)
* **Success Response (200 OK):** Returns the AI-generated response as plain text.

---

## 5. Background Scheduled Tasks
These run automatically and are **not** triggered by HTTP calls.

| Task | Schedule | Description |
|---|---|---|
| **FOSMIS Notice Check** | Every 20 minutes | Logs into FOSMIS, scrapes the notice board (`form_53_a.php`), saves new notices to MongoDB, and emails each new notice to `notify.email`. Uses a cached Jsoup session — re-authenticates automatically on session expiry. |

---

## Configuration Reference
Key properties used by the application (set via `.env` or environment variables):

| Property | Description |
|---|---|
| `notify.email` | Email address that receives Airport & FOSMIS notifications |
| `fosmis.username` | FOSMIS portal login username |
| `fosmis.pwd` | FOSMIS portal login password |
| `GMAIL_USERNAME` | Gmail address used as the SMTP sender |
| `GMAIL_APP_PASSWORD` | 16-character Gmail App Password for SMTP authentication |
