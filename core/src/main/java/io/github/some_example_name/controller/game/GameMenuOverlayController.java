package io.github.some_example_name.controller.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.game.GameSessionState;
import io.github.some_example_name.model.input.GameInputState;
import io.github.some_example_name.view.ui.InventoryMenuOverlay;
import io.github.some_example_name.view.ui.PauseMenuOverlay;

public class GameMenuOverlayController {
    private final GameSessionState sessionState;
    private final GameSessionController sessionController;
    private final PauseMenuOverlay pauseMenuOverlay;
    private final InventoryMenuOverlay inventoryMenuOverlay;

    public GameMenuOverlayController(
        Main game,
        GameSessionState sessionState,
        GameSessionController sessionController,
        Skin skin,
        Stage stage,
        Runnable onSaveAndQuit
    ) {
        this.sessionState = sessionState;
        this.sessionController = sessionController;

        this.pauseMenuOverlay = new PauseMenuOverlay(
            skin,
            this::resumeGame,
            onSaveAndQuit,
            game.getSettingsController(),
            game.getAudioManager()
        );

        this.inventoryMenuOverlay = new InventoryMenuOverlay(
            skin,
            game.getInventoryController()
        );

        stage.addActor(pauseMenuOverlay);
        stage.addActor(inventoryMenuOverlay);
    }

    public void update(GameInputState inputState) {
        toggleInventoryIfRequested(inputState);
        updatePauseMenuVisibility();
    }

    public boolean isGameplayBlocked() {
        return sessionState.isPaused() || inventoryMenuOverlay.isVisible();
    }

    public boolean canReceiveCheatInput() {
        return !sessionState.isPaused() && !inventoryMenuOverlay.isVisible();
    }

    private void resumeGame() {
        sessionController.resumeGame();
        pauseMenuOverlay.hideMenu();
    }

    private void toggleInventoryIfRequested(GameInputState inputState) {
        if (!inputState.isInventoryJustPressed()) {
            return;
        }

        if (sessionState.isPaused()) {
            return;
        }

        inventoryMenuOverlay.toggleMenu();
    }

    private void updatePauseMenuVisibility() {
        if (sessionState.isPaused()) {
            pauseMenuOverlay.showMenu();
        } else {
            pauseMenuOverlay.hideMenu();
        }
    }
}
