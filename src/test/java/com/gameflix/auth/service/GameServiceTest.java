package com.gameflix.auth.service;

import com.gameflix.auth.model.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * Exercises the real GameService against an in-memory H2 database.
 * The interesting logic under test is that the catalog and search views hide
 * games flagged unavailable, and that search is case-insensitive.
 */
@SpringBootTest
@ActiveProfiles("test")
class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Test
    void catalog_hidesUnavailableGames() {
        Game visible = gameService.addGame("Hollow Knight", "PC", "Metroidvania", "Explore Hallownest.");
        Game hidden = gameService.addGame("Secret Title", "PC", "Adventure", "Not launched yet.");
        gameService.setAvailability(hidden.getId(), false);

        List<Game> catalog = gameService.getCatalog();

        Assertions.assertTrue(catalog.stream().anyMatch(g -> g.getId().equals(visible.getId())),
                "Available game should appear in the catalog");
        Assertions.assertTrue(catalog.stream().noneMatch(g -> g.getId().equals(hidden.getId())),
                "Unavailable game should be hidden from the catalog");
    }

    @Test
    void search_isCaseInsensitiveAndSkipsUnavailable() {
        gameService.addGame("Portal Two", "PC", "Puzzle", "Think with portals.");
        Game hidden = gameService.addGame("Portal Beta", "PC", "Puzzle", "Internal build.");
        gameService.setAvailability(hidden.getId(), false);

        List<Game> results = gameService.search("portal");

        Assertions.assertFalse(results.isEmpty(), "Lower-case query should still match 'Portal'");
        Assertions.assertTrue(results.stream().allMatch(Game::isAvailable),
                "Search must never return unavailable games");
        Assertions.assertTrue(results.stream().anyMatch(g -> g.getTitle().equals("Portal Two")),
                "Available matching game should be returned");
    }

    @Test
    void blankSearch_returnsFullCatalog() {
        int catalogSize = gameService.getCatalog().size();
        Assertions.assertEquals(catalogSize, gameService.search("   ").size(),
                "A blank query should fall back to the whole catalog");
    }
}
