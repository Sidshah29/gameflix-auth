package com.gameflix.auth.service;

import com.gameflix.auth.model.Movie;
import com.gameflix.auth.repository.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void getAllMovies_ShouldReturnList() {
        // Ensure there is at least one movie in the database
        if (movieRepository.count() == 0) {
            movieRepository.save(new Movie("Test Movie", "TestGenre", 2024));
        }

        var movies = movieService.getAllMovies();
        Assertions.assertFalse(movies.isEmpty(), "Movie list should not be empty");
    }

    @Test
    void getMoviesByGenre_ShouldFilterCorrectly() {
        movieRepository.save(new Movie("Sci-Fi Test 1", "Sci-Fi", 2021));
        movieRepository.save(new Movie("Sci-Fi Test 2", "Sci-Fi", 2022));
        movieRepository.save(new Movie("Drama Test", "Drama", 2023));

        List<Movie> sciFiMovies = movieService.getMoviesByGenre("Sci-Fi");

        Assertions.assertFalse(sciFiMovies.isEmpty(), "Sci-Fi movies should not be empty");
        sciFiMovies.forEach(movie ->
                Assertions.assertEquals("Sci-Fi", movie.getGenre(), "Genre should be Sci-Fi"));
    }

    @Test
    void addMovie_ShouldPersistAndReturnEntity() {
        Movie saved = movieService.addMovie("Interstellar", "Sci-Fi", 2014);

        Assertions.assertNotNull(saved.getId(), "Saved movie should have an ID");
        Assertions.assertEquals("Interstellar", saved.getTitle(), "Title should match");
        Assertions.assertEquals("Sci-Fi", saved.getGenre(), "Genre should match");
        Assertions.assertEquals(2014, saved.getReleaseYear(), "Year should match");
    }
}