package io.github.some_example_name.controller.game;

import io.github.some_example_name.controller.audio.AudioManager;
import io.github.some_example_name.model.game.EnemyLaser;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.Player;
import io.github.some_example_name.model.player.PlayerStats;
import io.github.some_example_name.util.CollisionUtils;

public class GuardianLaserDamageController {
    private final GameWorld world;
    private final PlayerStats playerStats;
    private final AudioManager audioManager;

    public GuardianLaserDamageController(
        GameWorld world,
        PlayerStats playerStats,
        AudioManager audioManager
    ) {
        this.world = world;
        this.playerStats = playerStats;
        this.audioManager = audioManager;
    }

    public void update() {
        Player player = world.getPlayer();

        for (EnemyLaser laser : world.getEnemyLasers()) {
            if (!laser.isActive()) {
                continue;
            }

            if (laser.hasDamagedPlayer()) {
                continue;
            }

            boolean hit = CollisionUtils.overlaps(
                laser.getX(), laser.getY(), laser.getWidth(), laser.getHeight(),
                player.getX(), player.getY(), player.getWidth(), player.getHeight()
            );

            if (hit) {
                boolean damaged = playerStats.takeDamage(
                    laser.getDamage()
                );

                if (damaged) {
                    audioManager.playCreatureHurt();
                }

                laser.markPlayerDamaged();
            }
        }
    }
}
