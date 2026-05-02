package com.example.cinema.dto;

import com.example.cinema.entity.Booking.BookingStatus;
import com.example.cinema.entity.Movie.MovieStatus;
import com.example.cinema.entity.Room.RoomType;
import com.example.cinema.entity.Seat.SeatType;
import com.example.cinema.entity.Showtime.ShowtimeStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Dto {

    // =================== AUTH ===================
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank @Size(min = 3, max = 50)
        private String username;
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6)
        private String password;
        private String fullName;
        private String phone;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String username;
        private String email;
        private String role;
        private String message;
    }

    // =================== MOVIE ===================
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class MovieRequest {
        @NotBlank private String title;
        private String description;
        @NotBlank private String genre;
        @NotNull @Min(1) private Integer duration;
        private LocalDate releaseDate;
        private String posterUrl;
        private String director;
        private String cast;
        private Double rating;
        private MovieStatus status;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MovieResponse {
        private Long id;
        private String title;
        private String description;
        private String genre;
        private Integer duration;
        private LocalDate releaseDate;
        private String posterUrl;
        private String director;
        private String cast;
        private Double rating;
        private MovieStatus status;
    }

    // =================== CINEMA ===================
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CinemaRequest {
        @NotBlank private String name;
        @NotBlank private String address;
        private String city;
        private String phone;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CinemaResponse {
        private Long id;
        private String name;
        private String address;
        private String city;
        private String phone;
        private int totalRooms;
    }

    // =================== ROOM ===================
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RoomRequest {
        @NotBlank private String name;
        @NotNull @Min(1) private Integer totalSeats;
        private RoomType type;
        @NotNull private Long cinemaId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RoomResponse {
        private Long id;
        private String name;
        private Integer totalSeats;
        private RoomType type;
        private String cinemaName;
    }

    // =================== SHOWTIME ===================
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ShowtimeRequest {
        @NotNull private Long movieId;
        @NotNull private Long roomId;
        @NotNull private LocalDateTime startTime;
        @NotNull @Positive private Double priceStandard;
        @NotNull @Positive private Double priceVip;
        @NotNull @Positive private Double priceCouple;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ShowtimeResponse {
        private Long id;
        private Long movieId;
        private String movieTitle;
        private String moviePoster;
        private Long roomId;
        private String roomName;
        private String cinemaName;
        private String cinemaAddress;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Double priceStandard;
        private Double priceVip;
        private Double priceCouple;
        private ShowtimeStatus status;
        private int availableSeats;
    }

    // =================== SEAT ===================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SeatResponse {
        private Long id;
        private String seatName;
        private String seatRow;
        private Integer seatNumber;
        private SeatType type;
        private boolean available;
        private Double price;
    }

    // =================== BOOKING ===================
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class BookingRequest {
        @NotNull private Long showtimeId;
        @NotEmpty private List<Long> seatIds;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BookingResponse {
        private Long id;
        private String bookingCode;
        private String movieTitle;
        private String cinemaName;
        private String roomName;
        private LocalDateTime showtime;
        private List<String> seats;
        private Double totalPrice;
        private BookingStatus status;
        private LocalDateTime createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CancelRequest {
        private String reason;
    }

    // =================== COMMON ===================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public static <T> ApiResponse<T> success(T data, String message) {
            return ApiResponse.<T>builder().success(true).message(message).data(data).build();
        }

        public static <T> ApiResponse<T> error(String message) {
            return ApiResponse.<T>builder().success(false).message(message).build();
        }
    }
}
