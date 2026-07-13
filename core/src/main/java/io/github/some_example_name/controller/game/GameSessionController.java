package io.github.some_example_name.controller.game;

import io.github.some_example_name.model.game.GameSessionState;
import io.github.some_example_name.model.input.GameInputState;

public class GameSessionController {
    private final GameSessionState sessionState;

    public GameSessionController(GameSessionState sessionState) {
        this.sessionState = sessionState;
    }

    public void update(GameInputState inputState) {
        if (inputState.isPauseJustPressed()) {
            sessionState.togglePause();
        }
    }

    public void resumeGame() {
        sessionState.resume();
    }

    public void pauseGame() {
        sessionState.pause();
    }
}
