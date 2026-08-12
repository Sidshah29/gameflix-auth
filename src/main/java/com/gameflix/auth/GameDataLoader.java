package com.gameflix.auth;

import com.gameflix.auth.model.Game;
import com.gameflix.auth.model.Subscription;
import com.gameflix.auth.repository.GameRepository;
import com.gameflix.auth.repository.SubscriptionRepository;
import com.gameflix.auth.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds a fresh database so the app is demoable the first time it starts:
 * a handful of catalog games, a demo user, and one active subscription.
 * Everything is guarded by count()/existence checks so restarts don't
 * duplicate rows.
 */
@Component
public class GameDataLoader implements CommandLineRunner {

    private final GameRepository gameRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    public GameDataLoader(GameRepository gameRepository,
                          SubscriptionRepository subscriptionRepository,
                          UserService userService) {
        this.gameRepository = gameRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (gameRepository.count() == 0) {
            gameRepository.save(new Game("Celeste", "PC", "Platformer",
                    "Climb a haunted mountain in this tight, story-driven platformer."));
            gameRepository.save(new Game("Hades", "PC", "Roguelike",
                    "Fight out of the Underworld with fast, replayable combat."));
            gameRepository.save(new Game("Stardew Valley", "Switch", "Simulation",
                    "Inherit a farm and build a quiet life one season at a time."));
            gameRepository.save(new Game("Elden Ring", "PlayStation", "Action RPG",
                    "Explore a vast open world and topple demigods at your own pace."));
            gameRepository.save(new Game("Forza Horizon 5", "Xbox", "Racing",
                    "Open-world arcade racing across a stylized map of Mexico."));

            // One title starts unavailable so the admin availability toggle is demoable.
            Game hidden = new Game("Unreleased Beta", "PC", "Adventure",
                    "Placeholder title kept out of the public catalog until launch.");
            hidden.setAvailable(false);
            gameRepository.save(hidden);
        }

        // Demo account + active subscription so the subscription page has data on day one.
        if (userService.findByUsername("demo").isEmpty()) {
            userService.register("demo", "demo123", "demo@gameflix.test");
        }
        if (subscriptionRepository.findByUsername("demo").isEmpty()) {
            subscriptionRepository.save(new Subscription("demo", Subscription.Plan.STANDARD));
        }
    }
}
