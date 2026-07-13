package io.github.some_example_name.model.game;

public class GameSessionState {
    private boolean paused;

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void togglePause() {
        paused = !paused;
    }
}
