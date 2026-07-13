package io.github.some_example_name.controller.game;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.game.GameWorld;

public class GameWorldLoadController {
    private final Main game;

    public GameWorldLoadController(Main game) {
        this.game = game;
    }

    public GameWorld createLoadedWorld() {
        GameWorld world = new GameWorld(
            game.getGameStartController()
                .getLoadedArea()
        );

        world.applyDefeatedEnemyNames(
            game.getGameStartController()
                .getLoadedDefeatedEnemyNames()
        );

        world.restoreSecretRoomState(
            game.getGameStartController().isLoadedSecretWallBroken(),
            game.getGameStartController().isLoadedSecretPickupCollected()
        );

        world.movePlayerTo(
            game.getGameStartController()
                .getLoadedPlayerX(),
            game.getGameStartController()
                .getLoadedPlayerY()
        );

        game.getAudioManager().playAreaMusic(
            world.getCurrentArea()
        );

        return world;
    }

    public float getLoadedElapsedTimeSeconds() {
        return game.getGameStartController()
            .getLoadedElapsedTimeSeconds();
    }
}
