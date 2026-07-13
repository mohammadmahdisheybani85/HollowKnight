package io.github.some_example_name.controller.game;

import io.github.some_example_name.controller.audio.AudioManager;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.Player;

public class PlayerMovementSoundController {
    private static final float MINIMUM_WALK_SPEED = 24f;
    private static final float BASE_STEP_INTERVAL = 0.27f;

    private final GameWorld world;
    private final AudioManager audioManager;
    private float timeUntilNextStep;

    public PlayerMovementSoundController(
        GameWorld world,
        AudioManager audioManager
    ) {
        this.world = world;
        this.audioManager = audioManager;
    }

    public void update(float delta, boolean movementEnabled) {
        Player player = world.getPlayer();

        if (!movementEnabled || !isWalking(player)) {
            timeUntilNextStep = 0f;
            return;
        }

        timeUntilNextStep -= Math.max(0f, delta);
        if (timeUntilNextStep > 0f) {
            return;
        }

        audioManager.playFootstep(world.getCurrentArea());
        float speedFactor = Math.min(1.35f, Math.max(0.8f, Math.abs(player.getVelocityX()) / 220f));
        timeUntilNextStep = BASE_STEP_INTERVAL / speedFactor;
    }

    private boolean isWalking(Player player) {
        return player.isOnGround()
            && !player.isDashing()
            && !player.isKnockbackActive()
            && !player.isFocusing()
            && !player.isNoclipEnabled()
            && Math.abs(player.getVelocityX()) >= MINIMUM_WALK_SPEED;
    }
}
