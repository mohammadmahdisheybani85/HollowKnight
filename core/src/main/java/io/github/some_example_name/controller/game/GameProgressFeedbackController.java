package io.github.some_example_name.controller.game;

import io.github.some_example_name.controller.achievement.AchievementController;
import io.github.some_example_name.model.achievement.AchievementType;
import io.github.some_example_name.model.player.PlayerStats;

public class GameProgressFeedbackController {
    private static final int SOUL_MASTER_REQUIRED_SOUL = 99;

    private final PlayerStats playerStats;
    private final AchievementController achievementController;
    private final GameMessageController gameMessageController;

    private int lastObservedDeathCount;
    private boolean soulMasterChecked;

    public GameProgressFeedbackController(
        PlayerStats playerStats,
        AchievementController achievementController,
        GameMessageController gameMessageController
    ) {
        this.playerStats = playerStats;
        this.achievementController = achievementController;
        this.gameMessageController = gameMessageController;
        this.lastObservedDeathCount = playerStats.getDeathCount();
        this.soulMasterChecked = false;
    }

    public void update() {
        checkDeathFeedback();
        checkSoulMasterAchievement();
    }

    private void checkDeathFeedback() {
        int currentDeathCount = playerStats.getDeathCount();

        if (currentDeathCount <= lastObservedDeathCount) {
            return;
        }

        lastObservedDeathCount = currentDeathCount;

        gameMessageController.showTemporaryMessage(
            "You died. Deaths: " + currentDeathCount + " | Soul lost."
        );
    }

    private void checkSoulMasterAchievement() {
        if (soulMasterChecked) {
            return;
        }

        if (playerStats.getSoul() < SOUL_MASTER_REQUIRED_SOUL) {
            return;
        }

        soulMasterChecked = true;
        achievementController.unlock(AchievementType.SOUL_MASTER);
        gameMessageController.showTemporaryMessage("Achievement unlocked: Soul Master");
    }
}
