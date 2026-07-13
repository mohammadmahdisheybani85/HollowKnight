package io.github.some_example_name.controller.game;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.input.GameInputState;

public class GamePlayerActionController {
    private final PlayerController playerController;
    private final FocusController focusController;
    private final PlayerMovementSoundController movementSoundController;
    private final GameMessageController gameMessageController;

    public GamePlayerActionController(
        GameWorld world,
        Main game,
        GameMessageController gameMessageController
    ) {
        this.playerController = new PlayerController(
            world,
            game.getInventoryController()
        );

        this.focusController = new FocusController(
            world,
            game.getPlayerStats(),
            game.getInventoryController(),
            game.getAudioManager()
        );

        this.movementSoundController = new PlayerMovementSoundController(
            world,
            game.getAudioManager()
        );

        this.gameMessageController = gameMessageController;
    }

    public boolean update(float delta, GameInputState inputState) {
        String focusMessage = focusController.update(delta, inputState);

        if (!focusMessage.isEmpty()) {
            gameMessageController.showTemporaryMessage(focusMessage);
        }

        boolean playerIsFocusing = focusController.isFocusing();

        if (!playerIsFocusing) {
            playerController.update(delta, inputState);
        }

        movementSoundController.update(
            delta,
            !playerIsFocusing
        );

        return playerIsFocusing;
    }
}
