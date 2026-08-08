package com.gameflix.auth;

import com.gameflix.auth.model.Movie;
import com.gameflix.auth.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MovieDataLoader implements CommandLineRunner {

    private final MovieRepository movieRepository;

    public MovieDataLoader(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(String... args) {
        if (movieRepository.count() > 0) {
            return;
        }

        movieRepository.save(new Movie("The Matrix", "Sci-Fi", 1999));
        movieRepository.save(new Movie("Inception", "Sci-Fi", 2010));
        movieRepository.save(new Movie("The Godfather", "Crime", 1972));
    }
}