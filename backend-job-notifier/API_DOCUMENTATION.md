# API Documentation

This document lists all the available API endpoints in the Job Notifier backend application, along with example requests and responses. The base URL for all local development requests is `http://localhost:8080`.

---

## 1. Preferences API
Endpoints for managing user job-alert preferences.

### Get Preferences (By Email)
* **URL:** `http://localhost:8080/pref?email=user@example.com`
* **Method:** `GET`
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
  "email_enabled": false
}
```

### Create Preferences
* **URL:** `http://localhost:8080/pref`
* **Method:** `POST`
* **Sample JSON to send:**
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
* **Success Response (200 OK):** 
```json
{
  "uid": null,
  "email": "user@example.com",
  "keyword": ["software engineer", "developer"],
  "whatsapp_num": null,
  "telegram_id": null,
  "whatsapp_enabled": false,
  "telegram_enabled": false,
  "email_enabled": false
}
```

### Update Preferences
* **URL:** `http://localhost:8080/pref`
* **Method:** `PUT`
* **Sample JSON to send:**
```json
{
  "email": "user@example.com",
  "keyword": ["backend developer", "java"],
  "whatsapp_num": "+9876543210",
  "telegram_id": "user4321",
  "whatsapp_enabled": false,
  "telegram_enabled": true,
  "email_enabled": true
}
```
* **Success Response (200 OK):** Replaces existing keywords and updates flags.

### Delete Preferences
* **URL:** `http://localhost:8080/pref?email=user@example.com`
* **Method:** `DELETE`
* **Query Parameters:** `email` (string, required)
* **Success Response (200 OK):**
```text
Preference deleted successfully
```

---

## 2. User API
Endpoints for managing user accounts.

### Create User
* **URL:** `http://localhost:8080/user`
* **Method:** `POST`
* **Sample JSON to send:**
```json
{
  "email": "user@example.com",
  "password": "strongPassword123"
}
```
* **Success Response (200 OK):** *(Currently no response body)*

---

## 3. Website API
Endpoints for managing scrapable websites and their specific URLs.

### Get All Websites
* **URL:** `http://localhost:8080/websites`
* **Method:** `GET`
* **Success Response (200 OK):**
```json
[
  {
    "website": "TopJobs",
    "url": [
      "https://topjobs.lk/applicant/vacancybyfunctionalarea.jsp?FA=SDV",
      "https://topjobs.lk/applicant/vacancybyfunctionalarea.jsp?FA=ENG"
    ]
  },
  {
    "website": "LinkedIn",
    "url": [
      "https://www.linkedin.com/jobs/search/?keywords=software%20engineer"
    ]
  }
]
```

### Create Website
* **URL:** `http://localhost:8080/websites`
* **Method:** `POST`
* **Sample JSON to send:**
```json
{
  "website": "TopJobs",
  "url": [
    "https://topjobs.lk/some-section"
  ]
}
```
* **Success Response (200 OK):** *(Returns the created WebsiteDTO)*

### Add URLs to Existing Website
* **URL:** `http://localhost:8080/websites/urls`
* **Method:** `POST`
* **Sample JSON to send:**
```json
{
  "website": "TopJobs",
  "url": [
    "https://topjobs.lk/another-section"
  ]
}
```
* **Success Response (200 OK):** *(Returns the updated WebsiteDTO)*

---

## 4. Test API
Endpoints exclusively for manually triggering and testing integrations via HTTP.

### Test Scrape TopJobs
* **URL:** `http://localhost:8080/test/scrape-topjobs`
* **Method:** `GET`
* **Description:** Manually triggers the manual scraping of topjobs.
* **Success Response (200 OK):** Returns a list of scraped jobs.
```json
[
  {
    "id": 1,
    "position": "Software Engineer",
    "company": "Company XYZ",
    "link": "https://topjobs.lk/job1xyz"
  }
]
```

### Test Gmail Notification
* **URL:** `http://localhost:8080/test/test-gmail`
* **Method:** `GET`
* **Description:** Sends a hardcoded test email using the configured SMTP server.
* **Success Response (200 OK):** *(No response body - verify via recipient inbox)*
