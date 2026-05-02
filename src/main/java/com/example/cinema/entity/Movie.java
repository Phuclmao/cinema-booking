package com.example.cinema.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movies")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String genre; // Hành động, Tình cảm, Kinh dị...

    @Column(nullable = false)
    private Integer duration; // Thời lượng (phút)

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "poster_url")
    private String posterUrl;

    private String director;

    private String cast;

    @Column(nullable = false)
    private Double rating; // 0.0 - 10.0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Showtime> showtimes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = MovieStatus.NOW_SHOWING;
        if (rating == null) rating = 0.0;
    }

    public enum MovieStatus {
        COMING_SOON,    // Sắp chiếu
        NOW_SHOWING,    // Đang chiếu
        ENDED           // Đã kết thúc
    }
}
