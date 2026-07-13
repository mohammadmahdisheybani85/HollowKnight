package io.github.some_example_name.controller.game;

import io.github.some_example_name.controller.audio.AudioManager;
import io.github.some_example_name.controller.inventory.InventoryController;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.Player;
import io.github.some_example_name.model.input.GameInputState;
import io.github.some_example_name.model.player.PlayerStats;

public class FocusController {
    private static final int FOCUS_SOUL_COST = 33;
    private static final float BASE_FOCUS_DURATION = 1.5f;

    private final GameWorld world;
    private final PlayerStats playerStats;
    private final InventoryController inventoryController;
    private final AudioManager audioManager;

    private float focusTimer;

    public FocusController(
        GameWorld world,
        PlayerStats playerStats,
        InventoryController inventoryController,
        AudioManager audioManager
    ) {
        this.world = world;
        this.playerStats = playerStats;
        this.inventoryController = inventoryController;
        this.audioManager = audioManager;
    }

    public String update(float delta, GameInputState inputState) {
        Player player = world.getPlayer();

        if (!canContinueFocusing(inputState)) {
            cancelFocus();
            return "";
        }

        if (focusTimer <= 0f) {
            audioManager.startFocusSound();
        }

        player.setFocusing(true);
        focusTimer += delta;

        float requiredDuration = inventoryController.applyFocusDurationEffects(
            BASE_FOCUS_DURATION
        );

        if (focusTimer < requiredDuration) {
            return "Focusing...";
        }

        focusTimer = 0f;
        player.setFocusing(false);
        audioManager.stopFocusSound();

        if (!playerStats.spendSoul(FOCUS_SOUL_COST)) {
            return "Not enough Soul.";
        }

        playerStats.heal(1);
        return "Healed!";
    }

    public boolean isFocusing() {
        return focusTimer > 0f;
    }

    public void cancelFocus() {
        world.getPlayer().setFocusing(false);
        if (focusTimer > 0f) {
            focusTimer = 0f;
            audioManager.stopFocusSound();
        }
    }

    private boolean canContinueFocusing(GameInputState inputState) {
        if (!inputState.isFocusHeld()) return false;
        if (!world.getPlayer().isOnGround()) return false;
        if (world.getPlayer().isKnockbackActive()) return false;
        if (playerStats.isDamageInvincible()) return false;
        if (playerStats.getHealth() >= playerStats.getMaxHealth()) return false;
        return playerStats.hasEnoughSoul(FOCUS_SOUL_COST);
    }
}
