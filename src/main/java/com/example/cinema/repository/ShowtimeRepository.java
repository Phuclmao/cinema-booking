package com.example.cinema.repository;
import com.example.cinema.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovieId(Long movieId);
    @Query("SELECT st FROM Showtime st WHERE st.movie.id = :movieId AND st.startTime >= :from AND st.status = 'ACTIVE' ORDER BY st.startTime")
    List<Showtime> findActiveByMovieId(@Param("movieId") Long movieId, @Param("from") LocalDateTime from);
    @Query("SELECT st FROM Showtime st WHERE st.room.cinema.id = :cinemaId AND st.startTime >= :from AND st.status = 'ACTIVE' ORDER BY st.startTime")
    List<Showtime> findActiveByCinemaId(@Param("cinemaId") Long cinemaId, @Param("from") LocalDateTime from);
    @Query("SELECT st FROM Showtime st WHERE st.room.id = :roomId AND st.status = 'ACTIVE' AND (st.startTime < :endTime AND st.endTime > :startTime)")
    List<Showtime> findConflicting(@Param("roomId") Long roomId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
