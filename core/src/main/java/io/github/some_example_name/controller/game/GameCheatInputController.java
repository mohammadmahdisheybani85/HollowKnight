package io.github.some_example_name.controller.game;

import io.github.some_example_name.Main;
import io.github.some_example_name.model.game.Enemy;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.WorldArea;
import io.github.some_example_name.util.L10n;

public class GameCheatInputController {
    private static final int MAX_BUFFER_LENGTH = 32;

    private final Main game;
    private final GameWorld world;
    private final GameMenuOverlayController menuOverlayController;
    private final GameMessageController gameMessageController;
    private final GameProgressFeedbackController progressFeedbackController;
    private final GameHudController hudController;
    private final EnemyDeathHandler enemyDeathHandler;
    private String typedBuffer = "";

    public GameCheatInputController(
        Main game,
        GameWorld world,
        GameMenuOverlayController menuOverlayController,
        GameMessageController gameMessageController,
        GameProgressFeedbackController progressFeedbackController,
        GameHudController hudController
    ) {
        this.game = game;
        this.world = world;
        this.menuOverlayController = menuOverlayController;
        this.gameMessageController = gameMessageController;
        this.progressFeedbackController = progressFeedbackController;
        this.hudController = hudController;
        this.enemyDeathHandler = new EnemyDeathHandler(
            world,
            game.getAchievementController(),
            game.getEnemyKillTracker()
        );
    }

    public boolean handleTypedCharacter(char character) {
        if (menuOverlayController == null
            || !menuOverlayController.canReceiveCheatInput()
            || !Character.isLetterOrDigit(character)) {
            return false;
        }

        typedBuffer += Character.toUpperCase(character);
        if (typedBuffer.length() > MAX_BUFFER_LENGTH) {
            typedBuffer = typedBuffer.substring(typedBuffer.length() - MAX_BUFFER_LENGTH);
        }

        String message = executeMatchingCheat();
        if (message.isEmpty()) return false;

        typedBuffer = "";
        gameMessageController.showTemporaryMessage(L10n.dynamic(message));
        progressFeedbackController.update();
        hudController.update(game.getPlayerStats());
        return true;
    }

    private String executeMatchingCheat() {
        if (typedBuffer.endsWith("GODMODE")) {
            game.getPlayerStats().toggleGodMode();
            return game.getPlayerStats().isGodModeEnabled()
                ? "GODMODE activated."
                : "GODMODE deactivated.";
        }
        if (typedBuffer.endsWith("FULLSOUL")) {
            game.getPlayerStats().fillSoul();
            return "Soul bar filled.";
        }
        if (typedBuffer.endsWith("BOSSTELEPORT")) {
            world.teleportToArea(WorldArea.BOSS_ARENA);
            game.getAudioManager().playAreaMusic(WorldArea.BOSS_ARENA);
            return "Teleported to Boss Arena.";
        }
        if (typedBuffer.endsWith("NOCLIP")) {
            world.getPlayer().toggleNoclip();
            return world.getPlayer().isNoclipEnabled()
                ? "NOCLIP activated."
                : "NOCLIP deactivated.";
        }
        if (typedBuffer.endsWith("HEAL")) {
            game.getPlayerStats().heal(game.getPlayerStats().getMaxHealth());
            return "Health restored.";
        }
        if (typedBuffer.endsWith("INSTAKILL")) {
            for (Enemy enemy : world.getEnemies()) {
                if (!enemy.isAlive()) continue;
                enemy.takeDamage(enemy.getMaxHealth());
                game.getAudioManager().playEnemyDeath();
                enemyDeathHandler.handleDeathIfNeeded(enemy);
            }
            return "All enemies defeated.";
        }
        return "";
    }
}
