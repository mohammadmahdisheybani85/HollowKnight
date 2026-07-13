package io.github.some_example_name.controller.game;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.achievement.AchievementType;

public class GameLoadedProgressController {
    private final Main game;

    public GameLoadedProgressController(Main game) {
        this.game = game;
    }

    public void syncEnemyKillTrackerWithLoadedSave() {
        game.getEnemyKillTracker().loadKilledEnemies(
            game.getGameStartController().getLoadedDefeatedEnemyNames()
        );

        unlockTrueHunterIfAlreadyCompleted();
    }

    private void unlockTrueHunterIfAlreadyCompleted() {
        if (!game.getEnemyKillTracker().hasKilledAllRequiredEnemies()) {
            return;
        }

        game.getAchievementController().unlock(AchievementType.TRUE_HUNTER);
    }
}
