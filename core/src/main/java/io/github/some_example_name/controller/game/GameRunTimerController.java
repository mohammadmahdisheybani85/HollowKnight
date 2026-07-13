package io.github.some_example_name.controller.game;

public class GameRunTimerController {
    private float elapsedTimeSeconds;

    public GameRunTimerController(float initialElapsedTimeSeconds) {
        this.elapsedTimeSeconds = initialElapsedTimeSeconds;
    }

    public void update(float delta) {
        elapsedTimeSeconds += delta;
    }

    public float getElapsedTimeSeconds() {
        return elapsedTimeSeconds;
    }
}
