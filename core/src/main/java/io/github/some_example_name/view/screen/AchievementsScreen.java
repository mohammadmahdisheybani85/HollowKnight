package io.github.some_example_name.view.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.controller.achievement.AchievementController;
import io.github.some_example_name.model.achievement.Achievement;
import io.github.some_example_name.util.L10n;

public class AchievementsScreen extends AbstractMenuScreen {
    private AchievementController controller;

    public AchievementsScreen(Main game) {
        super(game);
    }

    @Override
    protected void buildUI() {
        controller = game.getAchievementController();
        controller.loadFromSave(
            game.getGameStartController()
                .getCurrentSlotAchievementNames()
        );

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = uiFactory.createTitle("ACHIEVEMENTS");
        title.setFontScale(1.25f);

        Table content = new Table();
        content.defaults().pad(6f);

        for (Achievement achievement : controller.getAchievements()) {
            addAchievementRow(content, achievement);
        }

        ScrollPane scrollPane = new ScrollPane(content, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        root.add(title)
            .padTop(12f)
            .padBottom(10f)
            .row();

        root.add(scrollPane)
            .width(760f)
            .height(430f)
            .padBottom(10f)
            .row();

        root.add(uiFactory.createBackButton(
                () -> game.getScreenManager().show(ScreenType.MAIN_MENU)
            ))
            .width(300f)
            .height(54f)
            .padBottom(8f)
            .row();
    }

    private void addAchievementRow(Table table, Achievement achievement) {
        Label label = new Label(L10n.achievementText(achievement), skin);
        label.setWrap(true);
        label.setAlignment(Align.center);
        label.setFontScale(1.1f);

        if (achievement.isUnlocked()) {
            label.setColor(Color.WHITE);
        } else {
            label.setColor(Color.GRAY);
        }

        table.add(label)
            .width(650f)
            .pad(8f)
            .row();
    }

    @Override
    protected String getBackgroundPath() {
        return "backgrounds/menu/achievements.jpeg";
    }
}
