package io.github.some_example_name.controller.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.some_example_name.controller.achievement.AchievementController;
import io.github.some_example_name.controller.achievement.AchievementUnlockListener;
import io.github.some_example_name.model.achievement.Achievement;
import io.github.some_example_name.view.ui.AchievementPopup;

public class GameAchievementPopupController {
    private static final float MARGIN = 20f;

    private final AchievementController achievementController;
    private final AchievementPopup achievementPopup;
    private final AchievementUnlockListener achievementUnlockListener;

    public GameAchievementPopupController(
        AchievementController achievementController,
        Skin skin,
        Stage stage
    ) {
        this.achievementController = achievementController;
        this.achievementPopup = new AchievementPopup(skin);

        stage.addActor(achievementPopup);
        position(stage);

        this.achievementUnlockListener = new AchievementUnlockListener() {
            @Override
            public void onAchievementUnlocked(Achievement achievement) {
                achievementPopup.show(achievement);
            }
        };

        achievementController.addListener(achievementUnlockListener);
    }

    public void position(Stage stage) {
        achievementPopup.setPosition(
            stage.getViewport().getWorldWidth() - achievementPopup.getWidth() - MARGIN,
            stage.getViewport().getWorldHeight() - achievementPopup.getHeight() - MARGIN
        );
    }

    public void dispose() {
        achievementController.removeListener(achievementUnlockListener);
    }
}
