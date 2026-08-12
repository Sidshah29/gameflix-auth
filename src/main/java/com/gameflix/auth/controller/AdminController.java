package com.gameflix.auth.controller;

import com.gameflix.auth.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Admin tools for managing the game catalog. Kept under /admin and open for the
 * prototype demo; a production build would gate this behind the ADMIN role.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GameService gameService;

    public AdminController(GameService gameService) {
        this.gameService = gameService;
    }

    // GET /admin -> manage every game, including unavailable ones
    @GetMapping
    public String manageGames(Model model) {
        model.addAttribute("games", gameService.getAllGames());
        model.addAttribute("pageTitle", "GameFlix - Admin");
        return "admin";
    }

    // POST /admin/games -> add a new game to the catalog
    @PostMapping("/games")
    public String addGame(@RequestParam("title") String title,
                          @RequestParam("platform") String platform,
                          @RequestParam("genre") String genre,
                          @RequestParam(value = "description", required = false) String description) {
        gameService.addGame(title, platform, genre, description);
        return "redirect:/admin";
    }

    // POST /admin/games/{id}/availability -> pull a game in/out of the catalog
    @PostMapping("/games/{id}/availability")
    public String toggleAvailability(@PathVariable("id") Long id,
                                     @RequestParam("available") boolean available) {
        gameService.setAvailability(id, available);
        return "redirect:/admin";
    }

    // POST /admin/games/{id}/delete -> remove a game entirely
    @PostMapping("/games/{id}/delete")
    public String deleteGame(@PathVariable("id") Long id) {
        gameService.deleteGame(id);
        return "redirect:/admin";
    }
}
