package com.gameflix.auth.repository;

import com.gameflix.auth.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByGenre(String genre);

    List<Movie> findByTitleContainingIgnoreCase(String titlePart);
}