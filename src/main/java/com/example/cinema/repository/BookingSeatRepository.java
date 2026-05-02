package com.example.cinema.repository;
import com.example.cinema.entity.BookingSeat;
import com.example.cinema.entity.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    boolean existsBySeatIdAndShowtimeIdAndBookingStatus(Long seatId, Long showtimeId, BookingStatus status);
}
