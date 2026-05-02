package com.example.cinema.controller;

import com.example.cinema.dto.Dto.*;
import com.example.cinema.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {
    private final ShowtimeService showtimeService;

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.success(showtimeService.getShowtimesByMovie(movieId), "OK"));
    }

    @GetMapping("/cinema/{cinemaId}")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getByCinema(@PathVariable Long cinemaId) {
        return ResponseEntity.ok(ApiResponse.success(showtimeService.getShowtimesByCinema(cinemaId), "OK"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(showtimeService.getShowtimeById(id), "OK"));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getAvailableSeats(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(showtimeService.getAvailableSeats(id), "OK"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> create(@Valid @RequestBody ShowtimeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(showtimeService.createShowtime(req), "Tạo lịch chiếu thành công"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
        showtimeService.cancelShowtime(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Hủy lịch chiếu thành công"));
    }
}
