package io.github.some_example_name.controller.game;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.view.screen.ScreenType;

public class GameSaveController {
    private final Main game;
    private final GameWorld world;

    public GameSaveController(Main game, GameWorld world) {
        this.game = game;
        this.world = world;
    }

    public void saveAndQuit(float elapsedTimeSeconds) {
        saveCurrentGame(elapsedTimeSeconds);
        saveRunStats(elapsedTimeSeconds);
        goToMainMenu();
    }

    private void saveCurrentGame(float elapsedTimeSeconds) {
        game.getGameStartController().saveCurrentGame(
            game.getPlayerStats(),
            game.getInventoryController(),
            world,
            elapsedTimeSeconds
        );
    }

    private void saveRunStats(float elapsedTimeSeconds) {
        game.saveLastRunStats(
            elapsedTimeSeconds,
            game.getEnemyKillTracker().getKillCount(),
            game.getPlayerStats().getDeathCount()
        );
    }

    private void goToMainMenu() {
        game.getScreenManager().show(ScreenType.MAIN_MENU);
    }
}
