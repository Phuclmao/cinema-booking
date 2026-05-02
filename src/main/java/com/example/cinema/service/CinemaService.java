package com.example.cinema.service;

import com.example.cinema.dto.Dto.*;
import com.example.cinema.entity.*;
import com.example.cinema.entity.Room.RoomType;
import com.example.cinema.entity.Seat.SeatType;
import com.example.cinema.exception.Exceptions.*;
import com.example.cinema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;

    public List<CinemaResponse> getAllCinemas(String city) {
        List<Cinema> cinemas = city != null
                ? cinemaRepository.findByCityIgnoreCase(city)
                : cinemaRepository.findAll();
        return cinemas.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public CinemaResponse getCinemaById(Long id) {
        return toResponse(findById(id));
    }

    public CinemaResponse createCinema(CinemaRequest request) {
        Cinema cinema = Cinema.builder()
                .name(request.getName()).address(request.getAddress())
                .city(request.getCity()).phone(request.getPhone())
                .rooms(new ArrayList<>()).build();
        return toResponse(cinemaRepository.save(cinema));
    }

    public CinemaResponse updateCinema(Long id, CinemaRequest request) {
        Cinema cinema = findById(id);
        cinema.setName(request.getName());
        cinema.setAddress(request.getAddress());
        if (request.getCity() != null) cinema.setCity(request.getCity());
        if (request.getPhone() != null) cinema.setPhone(request.getPhone());
        return toResponse(cinemaRepository.save(cinema));
    }

    public void deleteCinema(Long id) {
        cinemaRepository.delete(findById(id));
    }

    public List<RoomResponse> getRoomsByCinema(Long cinemaId) {
        findById(cinemaId);
        return roomRepository.findByCinemaId(cinemaId).stream()
                .map(this::toRoomResponse).collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        Cinema cinema = findById(request.getCinemaId());
        Room room = Room.builder()
                .name(request.getName())
                .totalSeats(request.getTotalSeats())
                .type(request.getType() != null ? request.getType() : RoomType.STANDARD)
                .cinema(cinema)
                .seats(new ArrayList<>())
                .build();
        Room saved = roomRepository.save(room);
        generateSeatsForRoom(saved);
        return toRoomResponse(saved);
    }

    private void generateSeatsForRoom(Room room) {
        int total = room.getTotalSeats();
        int cols = 10;
        int rows = (int) Math.ceil((double) total / cols);
        String[] rowLabels = {"A","B","C","D","E","F","G","H","I","J","K","L"};
        List<Seat> seats = new ArrayList<>();

        for (int r = 0; r < rows && r < rowLabels.length; r++) {
            for (int c = 1; c <= cols; c++) {
                if ((r * cols + c) > total) break;
                SeatType type = SeatType.STANDARD;
                if (room.getType() == RoomType.VIP) type = SeatType.VIP;
                else if (r == rows - 1 && c % 2 == 1) type = SeatType.COUPLE;

                seats.add(Seat.builder()
                        .seatRow(rowLabels[r])
                        .seatNumber(c)
                        .type(type)
                        .room(room).build());
            }
        }
        seatRepository.saveAll(seats);
    }

    public Cinema findById(Long id) {
        return cinemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rạp không tồn tại: " + id));
    }

    private CinemaResponse toResponse(Cinema c) {
        int rooms = c.getRooms() != null ? c.getRooms().size() : 0;
        return CinemaResponse.builder().id(c.getId()).name(c.getName())
                .address(c.getAddress()).city(c.getCity()).phone(c.getPhone())
                .totalRooms(rooms).build();
    }

    private RoomResponse toRoomResponse(Room r) {
        return RoomResponse.builder().id(r.getId()).name(r.getName())
                .totalSeats(r.getTotalSeats()).type(r.getType())
                .cinemaName(r.getCinema().getName()).build();
    }
}
