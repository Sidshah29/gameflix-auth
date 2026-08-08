package com.gameflix.auth.service;

import com.gameflix.auth.model.Movie;
import com.gameflix.auth.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    public Movie addMovie(String title, String genre, int year) {
        Movie movie = new Movie(title, genre, year);
        return movieRepository.save(movie);
    }
}