package com.example.cinema;

import com.example.cinema.dto.Dto.*;
import com.example.cinema.entity.*;
import com.example.cinema.entity.Booking.BookingStatus;
import com.example.cinema.entity.Movie.MovieStatus;
import com.example.cinema.entity.Showtime.ShowtimeStatus;
import com.example.cinema.exception.Exceptions.BadRequestException;
import com.example.cinema.exception.Exceptions.ResourceNotFoundException;
import com.example.cinema.repository.*;
import com.example.cinema.service.BookingService;
import com.example.cinema.service.MovieService;
import com.example.cinema.service.ShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CinemaBookingTest {

    @Mock private MovieRepository movieRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private BookingSeatRepository bookingSeatRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private UserRepository userRepository;
    @Mock private ShowtimeService showtimeService;

    @InjectMocks private MovieService movieService;
    @InjectMocks private BookingService bookingService;

    private Movie mockMovie;
    private User mockUser;
    private Showtime mockShowtime;
    private Room mockRoom;
    private Cinema mockCinema;
    private Seat mockSeat;

    @BeforeEach
    void setUp() {
        mockCinema = Cinema.builder().id(1L).name("CGV Vincom").address("72 Lê Thánh Tôn").city("HCM").build();
        mockRoom = Room.builder().id(1L).name("Phòng 1").totalSeats(50).type(Room.RoomType.STANDARD).cinema(mockCinema).build();
        mockMovie = Movie.builder().id(1L).title("Avengers").genre("Hành động").duration(180)
                .releaseDate(LocalDate.now()).rating(8.5).status(MovieStatus.NOW_SHOWING).build();
        mockUser = User.builder().id(1L).username("phuc").email("phuc@test.com").role(User.Role.ROLE_USER).build();
        mockSeat = Seat.builder().id(1L).seatRow("A").seatNumber(1).type(Seat.SeatType.STANDARD).room(mockRoom).build();
        mockShowtime = Showtime.builder().id(1L).movie(mockMovie).room(mockRoom)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(3))
                .priceStandard(80000.0).priceVip(120000.0).priceCouple(150000.0)
                .status(ShowtimeStatus.ACTIVE).build();
    }

    // ===== MOVIE TESTS =====

    @Test
    @DisplayName("Lấy phim theo ID thành công")
    void getMovieById_Success() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(mockMovie));
        MovieResponse result = movieService.getMovieById(1L);
        assertThat(result.getTitle()).isEqualTo("Avengers");
        assertThat(result.getGenre()).isEqualTo("Hành động");
    }

    @Test
    @DisplayName("Lấy phim không tồn tại phải throw exception")
    void getMovieById_NotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> movieService.getMovieById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("không tồn tại");
    }

    @Test
    @DisplayName("Tạo phim mới thành công")
    void createMovie_Success() {
        MovieRequest req = new MovieRequest("Avengers", "Siêu anh hùng", "Hành động",
                180, LocalDate.now(), null, "Russo", "RDJ", 8.5, MovieStatus.NOW_SHOWING);
        when(movieRepository.save(any())).thenReturn(mockMovie);
        MovieResponse result = movieService.createMovie(req);
        assertThat(result).isNotNull();
        verify(movieRepository, times(1)).save(any());
    }

    // ===== BOOKING TESTS =====

    @Test
    @DisplayName("Đặt vé thành công")
    void createBooking_Success() {
        BookingRequest req = new BookingRequest(1L, List.of(1L));
        when(userRepository.findByUsername("phuc")).thenReturn(Optional.of(mockUser));
        when(showtimeService.findById(1L)).thenReturn(mockShowtime);
        when(seatRepository.findById(1L)).thenReturn(Optional.of(mockSeat));
        when(bookingSeatRepository.existsBySeatIdAndShowtimeIdAndBookingStatus(1L, 1L, BookingStatus.CONFIRMED)).thenReturn(false);
        when(showtimeService.getPriceForSeat(mockSeat, mockShowtime)).thenReturn(80000.0);

        Booking savedBooking = Booking.builder().id(1L).bookingCode("BK20241201001")
                .user(mockUser).showtime(mockShowtime).totalPrice(80000.0)
                .status(BookingStatus.CONFIRMED).bookingSeats(List.of()).build();
        when(bookingRepository.save(any())).thenReturn(savedBooking);
        when(bookingSeatRepository.saveAll(any())).thenReturn(List.of());

        BookingResponse result = bookingService.createBooking(req, "phuc");
        assertThat(result.getBookingCode()).isEqualTo("BK20241201001");
        assertThat(result.getTotalPrice()).isEqualTo(80000.0);
    }

    @Test
    @DisplayName("Đặt vé ghế đã được đặt phải throw exception")
    void createBooking_SeatAlreadyBooked() {
        BookingRequest req = new BookingRequest(1L, List.of(1L));
        when(userRepository.findByUsername("phuc")).thenReturn(Optional.of(mockUser));
        when(showtimeService.findById(1L)).thenReturn(mockShowtime);
        when(seatRepository.findById(1L)).thenReturn(Optional.of(mockSeat));
        when(bookingSeatRepository.existsBySeatIdAndShowtimeIdAndBookingStatus(1L, 1L, BookingStatus.CONFIRMED)).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(req, "phuc"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("đã được đặt");
    }

    @Test
    @DisplayName("Hủy vé đã hủy rồi phải throw exception")
    void cancelBooking_AlreadyCancelled() {
        Booking cancelled = Booking.builder().id(1L).user(mockUser).showtime(mockShowtime)
                .status(BookingStatus.CANCELLED).bookingSeats(List.of()).build();
        when(userRepository.findByUsername("phuc")).thenReturn(Optional.of(mockUser));
        when(bookingRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(cancelled));

        assertThatThrownBy(() -> bookingService.cancelBooking(1L, "phuc", null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("đã bị hủy");
    }

    @Test
    @DisplayName("Lấy lịch sử đặt vé của user")
    void getMyBookings_Success() {
        when(userRepository.findByUsername("phuc")).thenReturn(Optional.of(mockUser));
        Booking b = Booking.builder().id(1L).bookingCode("BK001").user(mockUser)
                .showtime(mockShowtime).totalPrice(80000.0)
                .status(BookingStatus.CONFIRMED).bookingSeats(List.of()).build();
        when(bookingRepository.findByUserId(1L)).thenReturn(List.of(b));

        List<BookingResponse> result = bookingService.getMyBookings("phuc");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookingCode()).isEqualTo("BK001");
    }
}
