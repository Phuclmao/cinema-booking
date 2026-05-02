package com.example.cinema.controller;

import com.example.cinema.dto.Dto.*;
import com.example.cinema.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getMyBookings(auth.getName()), "OK"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingById(id, auth.getName()), "OK"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(bookingService.createBooking(req, auth.getName()), "Đặt vé thành công!"));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long id,
            @RequestBody(required = false) CancelRequest req,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(
                bookingService.cancelBooking(id, auth.getName(), req), "Hủy vé thành công"));
    }
}
