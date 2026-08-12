package com.gameflix.auth.model;

import jakarta.persistence.*;

/**
 * A single title in the GameFlix catalog.
 * `available` lets an admin pull a game from the catalog without deleting its
 * row, so subscription history that references it stays intact.
 */
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String platform;

    @Column(nullable = false)
    private String genre;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean available = true;

    public Game() {
    }

    public Game(String title, String platform, String genre, String description) {
        this.title = title;
        this.platform = platform;
        this.genre = genre;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
