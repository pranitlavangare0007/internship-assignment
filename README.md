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

## 🧪 Horizontal Cap Test (Postman Runner)

To verify that the system correctly enforces the **maximum 100 bot replies per post**, follow these steps:

---

### 📌 Prerequisites

* Ensure your application is running on `http://localhost:8080`
* Make sure a post with `postId = 1` exists
* Ensure at least one bot exists (e.g., `authorId = 2`)

---

### 📝 Step 1: Prepare Request

Use the following API:

```http
POST /api/posts/1/comments
```

#### Request Body

```json
{
  "authorId": 2,
  "authorType": "BOT",
  "content": "Bot reply load test",
  "depthLevel": 0,
  "parentAuthorId": 3,
  "parentAuthorType": "BOT"
}
```

> ⚠️ Note:
> Using `parentAuthorType = BOT` ensures the **cooldown rule is not triggered**, allowing proper horizontal cap testing.

---

### ▶️ Step 2: Open Postman Runner

1. Open Postman
2. Save the above request in a collection
3. Click on **"Run"** (Runner icon ▶️)
4. Select the saved request

---

### ⚙️ Step 3: Configure Runner

Set the following:

```text
Iterations: 200
Delay: 0 ms
```

---

### 🚀 Step 4: Run Test

Click:

```text
Run <Collection Name>
```

---

### ✅ Expected Results

| Request Range | Result                  |
| ------------- | ----------------------- |
| 1 – 100       | ✅ Success               |
| 101 – 200     | ❌ 429 Too Many Requests |

---

### 🔍 Step 5: Verify Results

#### Check PostgreSQL

```sql
SELECT COUNT(*) FROM comment;
```

Expected:

```text
100
```

---

#### Check Redis

```bash
GET post:1:bot_count
```

Expected:

```text
100
```

---

### 🧠 Conclusion

* The system correctly enforces the **horizontal cap limit (100 bot replies)**
* Redis atomic operations ensure **thread-safe concurrency control**
* Database integrity is maintained under high load conditions

---


## 🧪 Database & Redis Verification Commands

### 🐘 PostgreSQL (Docker)

#### 🔌 Connect to Database

```bash
docker exec -it postgres_db psql -U postgres -d assignment_db
```

#### 📋 Show Tables

```sql
\dt
```

#### 📄 View Data

```sql
SELECT * FROM posts;
SELECT * FROM comment;
SELECT * FROM users;
SELECT * FROM bot;
```

#### 🔢 Count Comments (Horizontal Cap Test)

```sql
SELECT COUNT(*) FROM comment;
```

#### ❌ Exit

```sql
\q
```

---

### 🔴 Redis (Docker)

#### 🔌 Connect to Redis

```bash
docker exec -it redis_db redis-cli
```

#### 🔑 List All Keys

```bash
KEYS *
```

#### 📊 Check Virality Score

```bash
GET post:1:virality_score
```

#### 🤖 Check Bot Reply Count (Horizontal Cap)

```bash
GET post:1:bot_count
```

#### ⏱️ Check Cooldown Keys

```bash
KEYS cooldown:*
```

#### 🔔 Check Pending Notifications

```bash
LRANGE user:1:pending_notifs 0 -1
```

#### 🗑️ Delete Keys (for testing reset)

```bash
DEL post:1:bot_count
DEL cooldown:bot_2:human_1
```

#### ❌ Exit Redis

```bash
exit
```

---

### 🧠 Notes

* **PostgreSQL** → Stores actual data (posts, comments, users, bots)
* **Redis** → Stores counters, cooldowns, and notification queues
* Horizontal cap test is successful when:

```text
SELECT COUNT(*) FROM comment; = 100
```


## 📌 Key Highlights

* 🚀 Handles high concurrency safely
* ⚡ Redis used for real-time operations
* 🔐 Strong consistency guarantees
* 📊 Clean separation: DB (data) vs Redis (control)

---

## 👨‍💻 Author

**Pranit Lavangare**

---
