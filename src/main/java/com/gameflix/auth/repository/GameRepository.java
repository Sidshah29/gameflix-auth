package com.gameflix.auth.repository;

import com.gameflix.auth.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByAvailableTrue();

    List<Game> findByGenreIgnoreCase(String genre);

    List<Game> findByTitleContainingIgnoreCase(String titlePart);
}
