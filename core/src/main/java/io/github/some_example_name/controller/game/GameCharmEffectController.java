package io.github.some_example_name.controller.game;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.player.PlayerStats;

public class GameCharmEffectController {
    private final Main game;

    public GameCharmEffectController(Main game) {
        this.game = game;
    }

    public void update() {
        applyMaxHealthEffects();
    }

    private void applyMaxHealthEffects() {
        int maxHealth = game.getInventoryController().applyMaxHealthEffects(
            PlayerStats.BASE_MAX_HEALTH
        );

        game.getPlayerStats().setMaxHealth(maxHealth);
    }
}
