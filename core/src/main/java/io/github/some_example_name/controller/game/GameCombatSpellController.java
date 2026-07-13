package io.github.some_example_name.controller.game;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.input.GameInputState;

public class GameCombatSpellController {
    private final CombatController combatController;
    private final SpellController spellController;
    private final GameMessageController gameMessageController;

    public GameCombatSpellController(
        GameWorld world,
        Main game,
        GameMessageController gameMessageController
    ) {
        this.combatController = new CombatController(
            world,
            game.getPlayerStats(),
            game.getInventoryController(),
            game.getAchievementController(),
            game.getEnemyKillTracker(),
            game.getAudioManager()
        );

        this.spellController = new SpellController(
            world,
            game.getPlayerStats(),
            game.getInventoryController(),
            game.getAchievementController(),
            game.getEnemyKillTracker(),
            game.getAudioManager()
        );

        this.gameMessageController = gameMessageController;
    }

    public void update(float delta, GameInputState inputState, boolean playerIsFocusing) {
        if (playerIsFocusing) return;
        combatController.update(delta, inputState);
        String message = spellController.update(delta, inputState);
        if (!message.isEmpty()) {
            gameMessageController.showTemporaryMessage(message);
        }
    }
}
