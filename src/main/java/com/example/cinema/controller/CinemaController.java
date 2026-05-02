package com.example.cinema.controller;

import com.example.cinema.dto.Dto.*;
import com.example.cinema.service.CinemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
@RequiredArgsConstructor
public class CinemaController {
    private final CinemaService cinemaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CinemaResponse>>> getAll(@RequestParam(required = false) String city) {
        return ResponseEntity.ok(ApiResponse.success(cinemaService.getAllCinemas(city), "OK"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CinemaResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(cinemaService.getCinemaById(id), "OK"));
    }

    @GetMapping("/{id}/rooms")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRooms(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(cinemaService.getRoomsByCinema(id), "OK"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CinemaResponse>> create(@Valid @RequestBody CinemaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(cinemaService.createCinema(req), "Tạo rạp thành công"));
    }

    @PostMapping("/{id}/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest req) {
        req.setCinemaId(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(cinemaService.createRoom(req), "Tạo phòng thành công"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CinemaResponse>> update(@PathVariable Long id, @Valid @RequestBody CinemaRequest req) {
        return ResponseEntity.ok(ApiResponse.success(cinemaService.updateCinema(id, req), "Cập nhật thành công"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        cinemaService.deleteCinema(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa rạp thành công"));
    }
}
