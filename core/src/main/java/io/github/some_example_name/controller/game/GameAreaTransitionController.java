package io.github.some_example_name.controller.game;

import io.github.some_example_name.controller.audio.AudioManager;
import io.github.some_example_name.model.game.GameWorld;

public class GameAreaTransitionController {
    private final GameWorld world;
    private final AudioManager audioManager;

    public GameAreaTransitionController(
        GameWorld world,
        AudioManager audioManager
    ) {
        this.world = world;
        this.audioManager = audioManager;
    }

    public String moveToNextAreaIfNeeded() {
        if (!world.shouldMoveToNextArea()) {
            return "";
        }

        world.moveToNextArea();
        audioManager.playAreaMusic(
            world.getCurrentArea()
        );

        return world.getCurrentArea()
            .getDisplayName();
    }
}
