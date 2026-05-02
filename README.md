# Cinema Booking API 🎬

Hệ thống đặt vé xem phim trực tuyến, xây dựng bằng **Spring Boot 3 + Spring Security + JWT + SQL Server**.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT |
| Database | SQL Server + Spring Data JPA / Hibernate |
| Build Tool | Maven |
| Java | Java 17 |
| Test | JUnit 5 + Mockito |

## Kiến trúc hệ thống

```
src/main/java/com/example/cinema/
├── config/         # SecurityConfig, UserDetailsConfig
├── controller/     # AuthController, MovieController, CinemaController,
│                   # ShowtimeController, BookingController
├── dto/            # Request/Response DTOs
├── entity/         # User, Movie, Cinema, Room, Seat, Showtime, Booking, BookingSeat
├── exception/      # GlobalExceptionHandler, Custom Exceptions
├── repository/     # 7 JPA Repositories
├── security/       # JwtUtil, JwtAuthFilter
└── service/        # AuthService, MovieService, CinemaService,
                    # ShowtimeService, BookingService
```

## Cài đặt và chạy

### Yêu cầu
- Java 17+
- SQL Server
- Maven 3.8+

### Tạo database
```sql
CREATE DATABASE cinema_booking_db;
```

### Cấu hình `application.properties`
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=cinema_booking_db;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD
```

### Chạy project
```bash
mvn spring-boot:run
```

---

## API Endpoints

### Auth
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/auth/register` | Đăng ký |
| POST | `/api/auth/login` | Đăng nhập |

### Movies (public)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/movies` | Lấy tất cả phim (filter: status, genre, title) |
| GET | `/api/movies/{id}` | Chi tiết phim |
| POST | `/api/movies` | Thêm phim *(Admin)* |
| PUT | `/api/movies/{id}` | Sửa phim *(Admin)* |
| DELETE | `/api/movies/{id}` | Xóa phim *(Admin)* |

### Cinemas (public)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/cinemas` | Lấy tất cả rạp (filter: city) |
| GET | `/api/cinemas/{id}` | Chi tiết rạp |
| GET | `/api/cinemas/{id}/rooms` | Danh sách phòng chiếu |
| POST | `/api/cinemas` | Thêm rạp *(Admin)* |
| POST | `/api/cinemas/{id}/rooms` | Thêm phòng *(Admin)* |

### Showtimes (public)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/showtimes/movie/{movieId}` | Lịch chiếu theo phim |
| GET | `/api/showtimes/cinema/{cinemaId}` | Lịch chiếu theo rạp |
| GET | `/api/showtimes/{id}/seats` | Ghế còn trống |
| POST | `/api/showtimes` | Tạo lịch chiếu *(Admin)* |

### Bookings (cần JWT)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/bookings` | Đặt vé |
| GET | `/api/bookings/my` | Lịch sử đặt vé |
| GET | `/api/bookings/{id}` | Chi tiết booking |
| PATCH | `/api/bookings/{id}/cancel` | Hủy vé |

---

## Ví dụ sử dụng

### 1. Đăng ký & Đăng nhập
```bash
# Đăng ký
POST /api/auth/register
{
  "username": "phuc",
  "email": "phuc@example.com",
  "password": "123456",
  "fullName": "Cao Hữu Phúc"
}

# Đăng nhập → nhận token
POST /api/auth/login
{"username": "phuc", "password": "123456"}
```

### 2. Xem phim đang chiếu
```bash
GET /api/movies?status=NOW_SHOWING
```

### 3. Xem lịch chiếu & ghế trống
```bash
GET /api/showtimes/movie/1
GET /api/showtimes/1/seats
```

### 4. Đặt vé
```bash
POST /api/bookings
Authorization: Bearer <token>
{
  "showtimeId": 1,
  "seatIds": [1, 2, 3]
}
```

### 5. Hủy vé
```bash
PATCH /api/bookings/1/cancel
Authorization: Bearer <token>
{"reason": "Bận việc đột xuất"}
```

---

## Enum Values

**Movie Status:** `COMING_SOON` | `NOW_SHOWING` | `ENDED`

**Room Type:** `STANDARD` | `VIP` | `IMAX`

**Seat Type:** `STANDARD` | `VIP` | `COUPLE`

**Booking Status:** `CONFIRMED` | `CANCELLED` | `COMPLETED`

---

## Chạy Unit Test
```bash
mvn test
```
