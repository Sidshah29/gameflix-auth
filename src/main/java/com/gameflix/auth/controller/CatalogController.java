package com.gameflix.auth.controller;

import com.gameflix.auth.model.Game;
import com.gameflix.auth.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CatalogController {

    private final GameService gameService;

    public CatalogController(GameService gameService) {
        this.gameService = gameService;
    }

    // GET /catalog          -> full catalog
    // GET /catalog?q=hades  -> title search within the catalog
    @GetMapping("/catalog")
    public String viewCatalog(@RequestParam(value = "q", required = false) String q, Model model) {
        List<Game> games = gameService.search(q);
        model.addAttribute("games", games);
        model.addAttribute("query", q);
        model.addAttribute("pageTitle", "GameFlix - Catalog");
        return "catalog";
    }
}
