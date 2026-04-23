#  Backend Assignment – Redis-Powered Social Engine

## 📌 Overview

This project implements a backend system simulating a social media platform with **AI bot interactions**, focusing on:

* Real-time virality scoring
* Concurrency-safe atomic operations
* Rate limiting using Redis
* Notification batching system

The system is designed to handle **high concurrency scenarios** while maintaining **data integrity and statelessness**.

---

## 🏗️ Tech Stack

* **Backend:** Spring Boot
* **Database:** PostgreSQL (Docker)
* **Cache & Concurrency:** Redis (Docker)
* **Build Tool:** Maven
* **Testing:** Postman

---

## ⚙️ Setup Instructions

### 1️⃣ Clone Repository

```bash
git clone https://github.com/pranitlavangare0007/internship-assignment.git
cd assignment
```

---

### 2️⃣ Start Services (Docker)

```bash
docker-compose up -d
```

Services:

* PostgreSQL → `localhost:5432`
* Redis → `localhost:6379`

---

### 3️⃣ Run Application

Using IntelliJ ▶️ or:

```bash
mvn spring-boot:run
```

---

## 🧱 Database Schema

### Entities:

* **User** → id, username, isPremium
* **Bot** → id, name, personaDescription
* **Post** → id, authorId, authorType, content, createdAt
* **Comment** → id, postId, authorId, authorType, depthLevel

---

## 🔌 API Endpoints

### 📍 Posts

* `POST /api/posts` → Create post

### 💬 Comments

* `POST /api/posts/{postId}/comments` → Add comment

### ❤️ Likes

* `POST /api/posts/{postId}/like` → Like post

### 👤 Users

* `POST /api/users` → Create user

### 🤖 Bots

* `POST /api/bots` → Create bot

---

## ⚡ Phase 2: Redis Virality Engine

### 🎯 Virality Score

| Action        | Score |
| ------------- | ----- |
| Human Like    | +20   |
| Human Comment | +50   |
| Bot Reply     | +1    |

👉 Stored in Redis:

```
post:{id}:virality_score
```

---

## 🔐 Atomic Locks (Concurrency Control)

### 1️⃣ Horizontal Cap

* Max **100 bot replies per post**
* Redis key:

```
post:{id}:bot_count
```

* Implemented using **atomic INCR**

---

### 2️⃣ Vertical Cap

* Max depth level = **20**
* Prevents deeply nested threads

---

### 3️⃣ Cooldown Cap

* Bot cannot interact with same user within **10 minutes**
* Redis key:

```
cooldown:bot_{id}:human_{id}
```

* Uses **TTL (Time-To-Live)**

---

## 🔔 Phase 3: Notification Engine

### 🟢 Immediate Notification

If no recent notification:

```
Push Notification Sent to User
```

---

### 🟡 Batched Notifications

If within 15 minutes:

* Stored in Redis List:

```
user:{id}:pending_notifs
```

---

### 🔄 CRON Job (Every 5 mins)

* Aggregates notifications
* Logs summary:

```
Summarized Push Notification: Bot X and N others interacted...
```

---

## 🧠 Concurrency & Thread Safety

### ✔ Atomic Operations

* Redis `INCR` ensures thread-safe counting

### ✔ Race Condition Handling

* Limit checks happen **before DB writes**
* Rollback (`DECR`) ensures consistency

### ✔ Stateless Design

* No in-memory storage (no HashMap/static variables)
* All state managed in Redis

---

## 🧪 Testing Strategy

### 🔥 Spam Test

* 200 concurrent bot requests
* Result:

```
Exactly 100 comments stored ✔️
```

---

### ✔ Edge Cases Tested

* Depth > 20 ❌
* Bot limit > 100 ❌
* Cooldown violation ❌
* Notification batching ✔️

---

## 📦 Postman Collection

Included in repository for API testing.

---

## 📌 Key Highlights

* 🚀 Handles high concurrency safely
* ⚡ Redis used for real-time operations
* 🔐 Strong consistency guarantees
* 📊 Clean separation: DB (data) vs Redis (control)

---

## 👨‍💻 Author

**Pranit Lavangare**

---
