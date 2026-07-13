package io.github.some_example_name.view.ui;

import io.github.some_example_name.util.L10n;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class AreaTitlePopup extends Table {
    private static final float DISPLAY_DURATION = 2.2f;
    private static final float FADE_OUT_START_TIME = 0.8f;

    private static final float NORMAL_TOP_PADDING = 80f;
    private static final float BOSS_TOP_PADDING = 200f;

    private final Label areaNameLabel;
    private final Cell<Label> areaNameCell;

    private float timeLeft;

    public AreaTitlePopup(Skin skin) {
        super(skin);

        setFillParent(true);
        setVisible(false);
        top();

        areaNameLabel = new Label("", skin);
        areaNameLabel.setFontScale(1.8f);
        areaNameLabel.setColor(new Color(0.88f, 0.94f, 1f, 1f));

        areaNameCell = add(areaNameLabel)
            .padTop(NORMAL_TOP_PADDING);

        row();
    }

    public void showArea(String areaName) {
        if (areaName == null || areaName.isBlank()) {
            return;
        }

        areaNameLabel.setText(L10n.areaName(areaName).toUpperCase());
        areaNameLabel.setColor(new Color(0.88f, 0.94f, 1f, 1f));

        areaNameCell.padTop(getTopPaddingForArea(areaName));
        invalidateHierarchy();

        timeLeft = DISPLAY_DURATION;
        setVisible(true);
    }

    private float getTopPaddingForArea(String areaName) {
        if (areaName.toLowerCase().contains("false knight")) {
            return BOSS_TOP_PADDING;
        }

        return NORMAL_TOP_PADDING;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!isVisible()) {
            return;
        }

        timeLeft -= delta;

        if (timeLeft <= 0f) {
            setVisible(false);
            return;
        }

        updateFade();
    }

    private void updateFade() {
        if (timeLeft > FADE_OUT_START_TIME) {
            areaNameLabel.setColor(new Color(0.88f, 0.94f, 1f, 1f));
            return;
        }

        float alpha = timeLeft / FADE_OUT_START_TIME;
        areaNameLabel.setColor(new Color(0.88f, 0.94f, 1f, alpha));
    }
}
