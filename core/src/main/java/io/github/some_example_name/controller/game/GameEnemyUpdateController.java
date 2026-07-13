package io.github.some_example_name.controller.game;

import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.WorldArea;

public class GameEnemyUpdateController {
    private final GameWorld world;
    private final FalseKnightController falseKnightController;
    private final EnemyAiController enemyAiController;

    public GameEnemyUpdateController(GameWorld world) {
        this.world = world;
        this.falseKnightController = new FalseKnightController(world);
        this.enemyAiController = new EnemyAiController(world);
    }

    public void update(float delta) {
        if (world.getCurrentArea() == WorldArea.BOSS_ARENA) {
            falseKnightController.update(delta);
        } else {
            world.updateBossEffects(delta);
            enemyAiController.update(delta);
        }
    }
}
