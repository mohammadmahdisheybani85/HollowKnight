package io.github.some_example_name.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.some_example_name.model.achievement.Achievement;
import io.github.some_example_name.util.L10n;

public class AchievementPopup extends Table {
    private final Label titleLabel;
    private final Label achievementLabel;

    public AchievementPopup(Skin skin) {
        super(skin);

        setVisible(false);
        setColor(1f, 1f, 1f, 0f);
        setBackground(skin.newDrawable("white", new Color(0.10f, 0.10f, 0.16f, 0.95f)));

        titleLabel = new Label(L10n.tr("Achievement Unlocked!"), skin);
        titleLabel.setColor(new Color(0.35f, 0.65f, 0.95f, 1f));

        achievementLabel = new Label("", skin);
        achievementLabel.setColor(Color.WHITE);

        pad(14f);
        add(titleLabel).left().row();
        add(achievementLabel).left().padTop(6f).row();

        setSize(320f, 86f);
    }

    public void show(Achievement achievement) {
        achievementLabel.setText(L10n.achievementTitle(achievement.getType()));

        clearActions();
        setVisible(true);
        getColor().a = 0f;

        addAction(Actions.sequence(
            Actions.fadeIn(0.25f),
            Actions.delay(2.2f),
            Actions.fadeOut(0.35f),
            Actions.visible(false)
        ));
    }
}
