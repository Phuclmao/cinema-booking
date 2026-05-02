# 🎬 Cinema Booking API

Hệ thống đặt vé xem phim trực tuyến được xây dựng bằng **Spring Boot 3**, tích hợp xác thực **JWT**, phân quyền theo role, và các nghiệp vụ thực tế như kiểm tra ghế trống, xử lý đặt vé đồng thời, và hủy vé.

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|------------|
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | SQL Server + Spring Data JPA / Hibernate |
| API Docs | Swagger / OpenAPI 3.0 |
| Build Tool | Maven |
| Java | Java 17 |
| Test | JUnit 5 + Mockito |

---

## ✨ Tính năng nổi bật

- 🔐 **JWT Authentication** — Đăng ký, đăng nhập, xác thực token
- 👥 **Phân quyền Admin/User** — Admin quản lý phim, rạp, lịch chiếu; User đặt/hủy vé
- 🎭 **Quản lý phim** — CRUD phim, tìm kiếm theo tên/thể loại/trạng thái
- 🏢 **Quản lý rạp & phòng** — Tạo rạp, phòng chiếu, tự động sinh ghế ngồi
- 🕐 **Lịch chiếu** — Tạo lịch chiếu, kiểm tra trùng lịch phòng
- 💺 **Đặt vé** — Xem ghế trống, đặt nhiều ghế cùng lúc, xử lý concurrent booking
- ❌ **Hủy vé** — Chỉ cho phép hủy trước suất chiếu 1 tiếng
- 📊 **API Documentation** — Swagger UI đầy đủ, có thể test trực tiếp

---

## 🗄 Kiến trúc Database

```
users (1) ──────────── (n) bookings
                              │
showtimes (1) ──────── (n) bookings
    │                         │
movies (1) ──── (n) showtimes │
                              │
rooms (1) ────── (n) seats    │
    │                 │       │
cinemas (1) ─── (n) rooms    │
                      │       │
                      └── (n) booking_seats
```

**8 bảng:** `users`, `movies`, `cinemas`, `rooms`, `seats`, `showtimes`, `bookings`, `booking_seats`

---

## 🚀 Cài đặt và chạy

### Yêu cầu
- Java 17+
- SQL Server
- Maven 3.8+

### Bước 1 — Tạo database
```sql
CREATE DATABASE cinema_booking_db;
```

### Bước 2 — Cấu hình `application.properties`
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=cinema_booking_db;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD
```

### Bước 3 — Chạy project
```bash
mvn spring-boot:run
```

### Bước 4 — Truy cập Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

---

## 📡 API Endpoints

### 🔐 Auth
| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| POST | `/api/auth/register` | Đăng ký tài khoản | ❌ |
| POST | `/api/auth/login` | Đăng nhập, nhận JWT | ❌ |

### 🎭 Movies
| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| GET | `/api/movies` | Danh sách phim (filter: status, genre, title) | ❌ |
| GET | `/api/movies/{id}` | Chi tiết phim | ❌ |
| POST | `/api/movies` | Thêm phim | 👑 Admin |
| PUT | `/api/movies/{id}` | Sửa phim | 👑 Admin |
| DELETE | `/api/movies/{id}` | Xóa phim | 👑 Admin |

### 🏢 Cinemas
| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| GET | `/api/cinemas` | Danh sách rạp (filter: city) | ❌ |
| GET | `/api/cinemas/{id}` | Chi tiết rạp | ❌ |
| GET | `/api/cinemas/{id}/rooms` | Phòng chiếu của rạp | ❌ |
| POST | `/api/cinemas` | Thêm rạp | 👑 Admin |
| POST | `/api/cinemas/{id}/rooms` | Thêm phòng chiếu | 👑 Admin |
| PUT | `/api/cinemas/{id}` | Sửa rạp | 👑 Admin |
| DELETE | `/api/cinemas/{id}` | Xóa rạp | 👑 Admin |

### 🕐 Showtimes
| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| GET | `/api/showtimes/movie/{movieId}` | Lịch chiếu theo phim | ❌ |
| GET | `/api/showtimes/cinema/{cinemaId}` | Lịch chiếu theo rạp | ❌ |
| GET | `/api/showtimes/{id}` | Chi tiết suất chiếu | ❌ |
| GET | `/api/showtimes/{id}/seats` | Ghế còn trống | ❌ |
| POST | `/api/showtimes` | Tạo lịch chiếu | 👑 Admin |
| DELETE | `/api/showtimes/{id}` | Hủy lịch chiếu | 👑 Admin |

### 🎫 Bookings
| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| POST | `/api/bookings` | Đặt vé | 🔐 User |
| GET | `/api/bookings/my` | Lịch sử đặt vé | 🔐 User |
| GET | `/api/bookings/{id}` | Chi tiết booking | 🔐 User |
| PATCH | `/api/bookings/{id}/cancel` | Hủy vé | 🔐 User |

---

## 💡 Ví dụ sử dụng

### 1. Đăng ký & Đăng nhập
```json
POST /api/auth/register
{
  "username": "khoa",
  "email": "khoa@example.com",
  "password": "123456",
  "fullName": "Đoàn Đăng Khoa"
}
```

### 2. Xem phim đang chiếu
```
GET /api/movies?status=NOW_SHOWING
GET /api/movies?genre=Hành động
GET /api/movies?title=avengers
```

### 3. Xem ghế trống của suất chiếu
```
GET /api/showtimes/1/seats
```

### 4. Đặt vé
```json
POST /api/bookings
Authorization: Bearer <token>
{
  "showtimeId": 1,
  "seatIds": [1, 2, 3]
}
```

### 5. Hủy vé
```json
PATCH /api/bookings/1/cancel
Authorization: Bearer <token>
{
  "reason": "Bận việc đột xuất"
}
```

---

## 🧪 Chạy Unit Test

```bash
mvn test
```

**7 test cases** bao gồm:
- Tạo phim thành công
- Lấy phim không tồn tại → throw exception
- Đặt vé thành công
- Đặt vé ghế đã được đặt → throw exception
- Hủy vé đã hủy → throw exception
- Lấy lịch sử đặt vé
- Cập nhật trạng thái phim

---

## 📋 Enum Values

| Enum | Values |
|------|--------|
| Movie Status | `COMING_SOON` / `NOW_SHOWING` / `ENDED` |
| Room Type | `STANDARD` / `VIP` / `IMAX` |
| Seat Type | `STANDARD` / `VIP` / `COUPLE` |
| Booking Status | `CONFIRMED` / `CANCELLED` / `COMPLETED` |

---
