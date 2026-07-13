package io.github.some_example_name.controller.game;

public class GameDamageProgressController {
    private final PlayerDamageController playerDamageController;
    private final GuardianLaserDamageController guardianLaserDamageController;
    private final GameProgressFeedbackController progressFeedbackController;
    private final GameMessageController gameMessageController;

    public GameDamageProgressController(
        PlayerDamageController playerDamageController,
        GuardianLaserDamageController guardianLaserDamageController,
        GameProgressFeedbackController progressFeedbackController,
        GameMessageController gameMessageController
    ) {
        this.playerDamageController = playerDamageController;
        this.guardianLaserDamageController = guardianLaserDamageController;
        this.progressFeedbackController = progressFeedbackController;
        this.gameMessageController = gameMessageController;
    }

    public void update(float delta) {
        updatePlayerDamage(delta);
        updateGuardianLaserDamage();
        updateProgressFeedback();
    }

    private void updatePlayerDamage(float delta) {
        playerDamageController.update(delta);

        String damageMessage = playerDamageController.consumeLastDamageMessage();

        if (!damageMessage.isEmpty()) {
            gameMessageController.showTemporaryMessage(damageMessage);
        }
    }

    private void updateGuardianLaserDamage() {
        guardianLaserDamageController.update();
    }

    private void updateProgressFeedback() {
        progressFeedbackController.update();
    }
}
