package io.github.some_example_name.controller.game;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.achievement.AchievementType;
import io.github.some_example_name.model.game.Enemy;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.WorldArea;
import io.github.some_example_name.view.screen.ScreenType;

public class GameVictoryController {
    private static final float SPEEDRUN_LIMIT_SECONDS = 300f;
    private static final String FALSE_KNIGHT_NAME = "False Knight";

    private final Main game;
    private final GameWorld world;

    private boolean victoryTriggered;

    public GameVictoryController(Main game, GameWorld world) {
        this.game = game;
        this.world = world;
        this.victoryTriggered = false;
    }

    public boolean checkVictory(float elapsedTimeSeconds) {
        if (victoryTriggered) {
            return true;
        }

        if (world.getCurrentArea() != WorldArea.BOSS_ARENA) {
            return false;
        }

        Enemy falseKnight = world.findEnemyByName(FALSE_KNIGHT_NAME);

        if (falseKnight != null && falseKnight.isAlive()) {
            return false;
        }

        completeGame(elapsedTimeSeconds);
        return true;
    }

    public boolean isVictoryTriggered() {
        return victoryTriggered;
    }

    private void completeGame(float elapsedTimeSeconds) {
        victoryTriggered = true;

        game.getAudioManager().playFalseKnightDeath();

        unlockVictoryAchievements(elapsedTimeSeconds);
        saveCompletedGame(elapsedTimeSeconds);
        showVictoryScreen();
    }

    private void unlockVictoryAchievements(float elapsedTimeSeconds) {
        game.getAchievementController().unlock(AchievementType.DEFEAT_FALSE_KNIGHT);
        game.getAchievementController().unlock(AchievementType.COMPLETION);

        if (elapsedTimeSeconds <= SPEEDRUN_LIMIT_SECONDS) {
            game.getAchievementController().unlock(AchievementType.SPEEDRUN);
        }
    }

    private void saveCompletedGame(float elapsedTimeSeconds) {
        game.getGameStartController().saveCompletedGame(
            game.getPlayerStats(),
            game.getInventoryController(),
            world,
            elapsedTimeSeconds
        );

        game.saveLastRunStats(
            elapsedTimeSeconds,
            game.getEnemyKillTracker().getKillCount(),
            game.getPlayerStats().getDeathCount()
        );
    }

    private void showVictoryScreen() {
        game.getScreenManager().show(ScreenType.VICTORY);
    }
}
