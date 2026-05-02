package com.example.cinema.service;

import com.example.cinema.dto.Dto.*;
import com.example.cinema.entity.*;
import com.example.cinema.entity.Booking.BookingStatus;
import com.example.cinema.entity.Showtime.ShowtimeStatus;
import com.example.cinema.exception.Exceptions.*;
import com.example.cinema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final ShowtimeService showtimeService;

    // Lấy lịch sử đặt vé của user
    public List<BookingResponse> getMyBookings(String username) {
        User user = getUser(username);
        return bookingRepository.findByUserId(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Lấy chi tiết một booking
    public BookingResponse getBookingById(Long id, String username) {
        User user = getUser(username);
        Booking booking = bookingRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking không tồn tại"));
        return toResponse(booking);
    }

    // Đặt vé — đây là logic quan trọng nhất
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String username) {
        User user = getUser(username);
        Showtime showtime = showtimeService.findById(request.getShowtimeId());

        // Kiểm tra suất chiếu còn hoạt động
        if (showtime.getStatus() != ShowtimeStatus.ACTIVE)
            throw new BadRequestException("Suất chiếu này đã bị hủy hoặc kết thúc");

        // Kiểm tra suất chiếu chưa bắt đầu
        if (showtime.getStartTime().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Suất chiếu này đã bắt đầu, không thể đặt vé");

        // Kiểm tra và lock ghế (xử lý concurrent booking)
        List<Seat> seats = new ArrayList<>();
        double totalPrice = 0;

        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ghế không tồn tại: " + seatId));

            // Kiểm tra ghế thuộc phòng chiếu
            if (!seat.getRoom().getId().equals(showtime.getRoom().getId()))
                throw new BadRequestException("Ghế " + seat.getSeatName() + " không thuộc phòng chiếu này");

            // Kiểm tra ghế đã được đặt chưa (synchronized để tránh race condition)
            synchronized (this) {
                boolean isBooked = bookingSeatRepository
                        .existsBySeatIdAndShowtimeIdAndBookingStatus(
                                seatId, showtime.getId(), BookingStatus.CONFIRMED);
                if (isBooked)
                    throw new BadRequestException("Ghế " + seat.getSeatName() + " đã được đặt");
            }

            seats.add(seat);
            totalPrice += showtimeService.getPriceForSeat(seat, showtime);
        }

        // Tạo booking
        String bookingCode = generateBookingCode();
        Booking booking = Booking.builder()
                .bookingCode(bookingCode)
                .user(user)
                .showtime(showtime)
                .totalPrice(totalPrice)
                .status(BookingStatus.CONFIRMED)
                .build();
        Booking savedBooking = bookingRepository.save(booking);

        // Tạo booking seats
        List<BookingSeat> bookingSeats = seats.stream().map(seat ->
                BookingSeat.builder()
                        .booking(savedBooking)
                        .seat(seat)
                        .showtime(showtime)
                        .price(showtimeService.getPriceForSeat(seat, showtime))
                        .build()
        ).collect(Collectors.toList());

        bookingSeatRepository.saveAll(bookingSeats);
        savedBooking.setBookingSeats(bookingSeats);

        return toResponse(savedBooking);
    }

    // Hủy vé
    @Transactional
    public BookingResponse cancelBooking(Long id, String username, CancelRequest request) {
        User user = getUser(username);
        Booking booking = bookingRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking không tồn tại"));

        if (booking.getStatus() == BookingStatus.CANCELLED)
            throw new BadRequestException("Booking này đã bị hủy rồi");

        if (booking.getStatus() == BookingStatus.COMPLETED)
            throw new BadRequestException("Không thể hủy vé đã xem");

        // Chỉ cho hủy trước suất chiếu 1 tiếng
        if (booking.getShowtime().getStartTime().minusHours(1).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Chỉ có thể hủy vé trước suất chiếu ít nhất 1 tiếng");

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancelReason(request != null ? request.getReason() : "Người dùng hủy");

        return toResponse(bookingRepository.save(booking));
    }

    private String generateBookingCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", (int)(Math.random() * 9999));
        return "BK" + date + random;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User không tìm thấy"));
    }

    private BookingResponse toResponse(Booking b) {
        List<String> seatNames = b.getBookingSeats() != null
                ? b.getBookingSeats().stream()
                    .map(bs -> bs.getSeat().getSeatName())
                    .collect(Collectors.toList())
                : List.of();

        return BookingResponse.builder()
                .id(b.getId())
                .bookingCode(b.getBookingCode())
                .movieTitle(b.getShowtime().getMovie().getTitle())
                .cinemaName(b.getShowtime().getRoom().getCinema().getName())
                .roomName(b.getShowtime().getRoom().getName())
                .showtime(b.getShowtime().getStartTime())
                .seats(seatNames)
                .totalPrice(b.getTotalPrice())
                .status(b.getStatus())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
