package com.example.cinema.service;

import com.example.cinema.dto.Dto.*;
import com.example.cinema.entity.Movie;
import com.example.cinema.entity.Movie.MovieStatus;
import com.example.cinema.exception.Exceptions.*;
import com.example.cinema.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public List<MovieResponse> getAllMovies(String status, String genre, String title) {
        List<Movie> movies;
        if (status != null) {
            movies = movieRepository.findByStatus(MovieStatus.valueOf(status));
        } else if (genre != null) {
            movies = movieRepository.findByGenreIgnoreCase(genre);
        } else if (title != null) {
            movies = movieRepository.findByTitleContainingIgnoreCase(title);
        } else {
            movies = movieRepository.findAll();
        }
        return movies.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public MovieResponse getMovieById(Long id) {
        return toResponse(findById(id));
    }

    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .genre(request.getGenre())
                .duration(request.getDuration())
                .releaseDate(request.getReleaseDate())
                .posterUrl(request.getPosterUrl())
                .director(request.getDirector())
                .cast(request.getCast())
                .rating(request.getRating() != null ? request.getRating() : 0.0)
                .status(request.getStatus() != null ? request.getStatus() : MovieStatus.NOW_SHOWING)
                .build();
        return toResponse(movieRepository.save(movie));
    }

    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = findById(id);
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setGenre(request.getGenre());
        movie.setDuration(request.getDuration());
        if (request.getReleaseDate() != null) movie.setReleaseDate(request.getReleaseDate());
        if (request.getPosterUrl() != null) movie.setPosterUrl(request.getPosterUrl());
        if (request.getDirector() != null) movie.setDirector(request.getDirector());
        if (request.getCast() != null) movie.setCast(request.getCast());
        if (request.getRating() != null) movie.setRating(request.getRating());
        if (request.getStatus() != null) movie.setStatus(request.getStatus());
        return toResponse(movieRepository.save(movie));
    }

    public void deleteMovie(Long id) {
        movieRepository.delete(findById(id));
    }

    public Movie findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phim không tồn tại: " + id));
    }

    private MovieResponse toResponse(Movie m) {
        return MovieResponse.builder()
                .id(m.getId()).title(m.getTitle()).description(m.getDescription())
                .genre(m.getGenre()).duration(m.getDuration()).releaseDate(m.getReleaseDate())
                .posterUrl(m.getPosterUrl()).director(m.getDirector()).cast(m.getCast())
                .rating(m.getRating()).status(m.getStatus()).build();
    }
}
