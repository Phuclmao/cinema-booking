package com.example.cinema.repository;
import com.example.cinema.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByRoomId(Long roomId);
    @Query("SELECT s FROM Seat s WHERE s.room.id = :roomId AND s.id NOT IN (SELECT bs.seat.id FROM BookingSeat bs WHERE bs.showtime.id = :showtimeId AND bs.booking.status = 'CONFIRMED')")
    List<Seat> findAvailableSeats(@Param("roomId") Long roomId, @Param("showtimeId") Long showtimeId);
}
