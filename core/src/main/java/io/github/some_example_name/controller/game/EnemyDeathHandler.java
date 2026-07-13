package io.github.some_example_name.controller.game;

import io.github.some_example_name.controller.achievement.AchievementController;
import io.github.some_example_name.controller.achievement.EnemyKillTracker;
import io.github.some_example_name.model.achievement.AchievementType;
import io.github.some_example_name.model.game.Enemy;
import io.github.some_example_name.model.game.GameWorld;

public class EnemyDeathHandler {
    private final GameWorld world;
    private final AchievementController achievementController;
    private final EnemyKillTracker enemyKillTracker;

    public EnemyDeathHandler(
        GameWorld world,
        AchievementController achievementController,
        EnemyKillTracker enemyKillTracker
    ) {
        this.world = world;
        this.achievementController = achievementController;
        this.enemyKillTracker = enemyKillTracker;
    }

    public void handleDeathIfNeeded(Enemy enemy) {
        if (
            enemy == null
                || enemy.isAlive()
                || enemy.isDeathHandled()
        ) {
            return;
        }

        enemy.markDeathHandled();
        world.markEnemyDefeated(enemy.getName());
        enemyKillTracker.markKilled(enemy.getName());

        if (enemyKillTracker.hasKilledAllRequiredEnemies()) {
            achievementController.unlock(
                AchievementType.TRUE_HUNTER
            );
        }
    }
}
