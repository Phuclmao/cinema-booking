package com.example.cinema.service;

import com.example.cinema.dto.Dto.*;
import com.example.cinema.entity.*;
import com.example.cinema.entity.Showtime.ShowtimeStatus;
import com.example.cinema.exception.Exceptions.*;
import com.example.cinema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieService movieService;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;

    public List<ShowtimeResponse> getShowtimesByMovie(Long movieId) {
        return showtimeRepository.findActiveByMovieId(movieId, LocalDateTime.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ShowtimeResponse> getShowtimesByCinema(Long cinemaId) {
        return showtimeRepository.findActiveByCinemaId(cinemaId, LocalDateTime.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ShowtimeResponse getShowtimeById(Long id) {
        return toResponse(findById(id));
    }

    public List<SeatResponse> getAvailableSeats(Long showtimeId) {
        Showtime showtime = findById(showtimeId);
        List<Seat> available = seatRepository.findAvailableSeats(
                showtime.getRoom().getId(), showtimeId);
        return available.stream().map(seat -> SeatResponse.builder()
                .id(seat.getId()).seatName(seat.getSeatName())
                .seatRow(seat.getSeatRow()).seatNumber(seat.getSeatNumber())
                .type(seat.getType()).available(true)
                .price(getPriceForSeat(seat, showtime)).build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public ShowtimeResponse createShowtime(ShowtimeRequest request) {
        Movie movie = movieService.findById(request.getMovieId());
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Phòng không tồn tại"));

        LocalDateTime endTime = request.getStartTime().plusMinutes(movie.getDuration() + 15);

        List<Showtime> conflicts = showtimeRepository.findConflicting(
                room.getId(), request.getStartTime(), endTime);
        if (!conflicts.isEmpty())
            throw new BadRequestException("Phòng đã có lịch chiếu trong khung giờ này");

        Showtime showtime = Showtime.builder()
                .movie(movie).room(room)
                .startTime(request.getStartTime())
                .endTime(endTime)
                .priceStandard(request.getPriceStandard())
                .priceVip(request.getPriceVip())
                .priceCouple(request.getPriceCouple())
                .status(ShowtimeStatus.ACTIVE).build();

        return toResponse(showtimeRepository.save(showtime));
    }

    public void cancelShowtime(Long id) {
        Showtime showtime = findById(id);
        showtime.setStatus(ShowtimeStatus.CANCELLED);
        showtimeRepository.save(showtime);
    }

    public Showtime findById(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suất chiếu không tồn tại: " + id));
    }

    public double getPriceForSeat(Seat seat, Showtime showtime) {
        return switch (seat.getType()) {
            case VIP -> showtime.getPriceVip();
            case COUPLE -> showtime.getPriceCouple();
            default -> showtime.getPriceStandard();
        };
    }

    private ShowtimeResponse toResponse(Showtime st) {
        List<Seat> available = seatRepository.findAvailableSeats(st.getRoom().getId(), st.getId());
        return ShowtimeResponse.builder()
                .id(st.getId())
                .movieId(st.getMovie().getId())
                .movieTitle(st.getMovie().getTitle())
                .moviePoster(st.getMovie().getPosterUrl())
                .roomId(st.getRoom().getId())
                .roomName(st.getRoom().getName())
                .cinemaName(st.getRoom().getCinema().getName())
                .cinemaAddress(st.getRoom().getCinema().getAddress())
                .startTime(st.getStartTime())
                .endTime(st.getEndTime())
                .priceStandard(st.getPriceStandard())
                .priceVip(st.getPriceVip())
                .priceCouple(st.getPriceCouple())
                .status(st.getStatus())
                .availableSeats(available.size())
                .build();
    }
}
