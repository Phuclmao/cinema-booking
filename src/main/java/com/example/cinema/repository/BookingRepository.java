package com.example.cinema.repository;
import com.example.cinema.entity.Booking;
import com.example.cinema.entity.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    Optional<Booking> findByBookingCode(String bookingCode);
    Optional<Booking> findByIdAndUserId(Long id, Long userId);
}
