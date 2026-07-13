package io.github.some_example_name.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.some_example_name.model.game.Enemy;
import io.github.some_example_name.util.L10n;

public class BossHud extends Group {
    private static final int SEGMENT_COUNT = 8;

    private static final float HUD_WIDTH = 620f;
    private static final float HUD_HEIGHT = 70f;

    private static final float BAR_WIDTH = 520f;
    private static final float BAR_HEIGHT = 16f;

    private static final float SEGMENT_GAP = 5f;

    private static final float BAR_X =
        (HUD_WIDTH - BAR_WIDTH) / 2f;

    private static final float BAR_Y = 12f;

    private static final float SEGMENT_WIDTH =
        (
            BAR_WIDTH
                - (SEGMENT_COUNT - 1) * SEGMENT_GAP
        ) / SEGMENT_COUNT;

    private static final Color BOSS_NAME_COLOR =
        new Color(
            0.96f,
            0.96f,
            0.92f,
            1f
        );

    private static final Color SEGMENT_BACKGROUND_COLOR =
        new Color(
            0.015f,
            0.015f,
            0.02f,
            0.96f
        );

    private static final Color SEGMENT_FILL_COLOR =
        new Color(
            0.94f,
            0.95f,
            0.97f,
            1f
        );

    private static final Color BAR_SHADOW_COLOR =
        new Color(
            0f,
            0f,
            0f,
            0.48f
        );

    private final Label bossNameLabel;
    private final Image barShadow;

    private final Image[] segmentFills;

    public BossHud(Skin skin) {
        setSize(
            HUD_WIDTH,
            HUD_HEIGHT
        );

        setVisible(false);
        setTouchable(Touchable.disabled);

        bossNameLabel = new Label(
            L10n.tr("FALSE KNIGHT"),
            skin
        );

        bossNameLabel.setColor(
            BOSS_NAME_COLOR
        );

        bossNameLabel.setFontScale(0.95f);
        bossNameLabel.pack();

        centerBossName();

        barShadow = new Image(
            skin.newDrawable(
                "white",
                BAR_SHADOW_COLOR
            )
        );

        barShadow.setBounds(
            BAR_X + 3f,
            BAR_Y - 3f,
            BAR_WIDTH,
            BAR_HEIGHT
        );

        addActor(barShadow);

        segmentFills =
            new Image[SEGMENT_COUNT];

        createHealthSegments(skin);

        addActor(bossNameLabel);
    }

    private void createHealthSegments(Skin skin) {
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            float segmentX =
                BAR_X
                    + i
                    * (
                    SEGMENT_WIDTH
                        + SEGMENT_GAP
                );


            Image segmentBackground = new Image(
                skin.newDrawable(
                    "white",
                    SEGMENT_BACKGROUND_COLOR
                )
            );

            segmentBackground.setBounds(
                segmentX,
                BAR_Y,
                SEGMENT_WIDTH,
                BAR_HEIGHT
            );

            Image segmentFill = new Image(
                skin.newDrawable(
                    "white",
                    SEGMENT_FILL_COLOR
                )
            );

            segmentFill.setBounds(
                segmentX,
                BAR_Y,
                SEGMENT_WIDTH,
                BAR_HEIGHT
            );

            segmentFills[i] = segmentFill;

            addActor(segmentBackground);
            addActor(segmentFill);
        }
    }

    public void update(Enemy boss) {
        if (
            boss == null
                || !boss.isAlive()
        ) {
            setVisible(false);
            return;
        }

        setVisible(true);
        updateHealthSegments(boss);
    }

    private void updateHealthSegments(
        Enemy boss
    ) {
        float maxHealth = Math.max(
            1f,
            boss.getMaxHealth()
        );

        float healthRatio =
            boss.getHealth() / maxHealth;

        healthRatio = Math.max(
            0f,
            Math.min(
                1f,
                healthRatio
            )
        );

        float filledSegmentAmount =
            healthRatio * SEGMENT_COUNT;

        for (
            int i = 0;
            i < SEGMENT_COUNT;
            i++
        ) {

            float segmentFillRatio =
                Math.max(
                    0f,
                    Math.min(
                        1f,
                        filledSegmentAmount - i
                    )
                );

            segmentFills[i].setWidth(
                SEGMENT_WIDTH
                    * segmentFillRatio
            );
        }
    }

    private void centerBossName() {
        bossNameLabel.setPosition(
            (
                HUD_WIDTH
                    - bossNameLabel.getWidth()
            ) / 2f,
            40f
        );
    }
}
