package com.gameflix.auth.service;

import com.gameflix.auth.model.Game;
import com.gameflix.auth.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /** Catalog browsing only ever shows titles that are currently available. */
    public List<Game> getCatalog() {
        return gameRepository.findByAvailableTrue();
    }

    /** Admin view: every game, including ones pulled from the catalog. */
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    /**
     * Search the live catalog by title. A blank query returns the whole catalog
     * so the search box degrades gracefully instead of showing nothing.
     */
    public List<Game> search(String query) {
        if (query == null || query.isBlank()) {
            return getCatalog();
        }
        return gameRepository.findByTitleContainingIgnoreCase(query.trim())
                .stream()
                .filter(Game::isAvailable)
                .toList();
    }

    public Game addGame(String title, String platform, String genre, String description) {
        Game game = new Game(title, platform, genre, description);
        return gameRepository.save(game);
    }

    /** Flip a game in or out of the public catalog without deleting its row. */
    public Game setAvailability(Long id, boolean available) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game id: " + id));
        game.setAvailable(available);
        return gameRepository.save(game);
    }

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }
}
