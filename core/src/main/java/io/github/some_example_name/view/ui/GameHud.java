package io.github.some_example_name.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.some_example_name.model.player.PlayerStats;
import io.github.some_example_name.util.L10n;

public class GameHud extends Group {
    private static final int MAX_VISIBLE_MASKS = 8;
    private static final float HUD_WIDTH = 430f;
    private static final float HUD_HEIGHT = 170f;

    private final Texture knightTexture;
    private final Texture fullMaskTexture;
    private final Texture emptyMaskTexture;
    private final Texture soulVesselTexture;
    private final Texture soulFillTexture;
    private final Texture soulGlowTexture;
    private final Texture[] maskBreakTextures = new Texture[4];

    private final Image[] maskImages = new Image[MAX_VISIBLE_MASKS];
    private final Image maskBreakImage;
    private final Image soulFillImage;
    private final Image soulGlowImage;
    private final Label soulLabel;
    private final Label godModeLabel;
    private final Label invincibleLabel;

    private int previousHealth = -1;
    private int breakingMaskIndex = -1;
    private float maskBreakTime;

    private static final float MASK_BREAK_FRAME_DURATION = 0.075f;
    private static final Color STATUS_OFF_COLOR = new Color(0.64f, 0.67f, 0.72f, 0.88f);
    private static final Color GOD_MODE_ON_COLOR = new Color(1f, 0.86f, 0.30f, 1f);
    private static final Color INVINCIBLE_ON_COLOR = new Color(0.45f, 0.83f, 1f, 1f);

    public GameHud(Skin skin) {
        setSize(HUD_WIDTH, HUD_HEIGHT);

        knightTexture = loadTexture("ui/knight.png");
        fullMaskTexture = loadTexture("ui/hud/health/mask_full.png");
        emptyMaskTexture = loadTexture("ui/hud/health/mask_empty.png");
        soulVesselTexture = loadTexture("ui/hud/soul/soul_vessel.png");
        soulFillTexture = loadTexture("ui/hud/soul/soul_fill.png");
        soulGlowTexture = loadTexture("ui/hud/soul/soul_glow.png");
        for (int i = 0; i < maskBreakTextures.length; i++) {
            maskBreakTextures[i] = loadTexture(
                String.format("ui/hud/health/mask_break_%02d.png", i)
            );
        }

        Image portrait = new Image(knightTexture);
        portrait.setBounds(0f, 50f, 66f, 105f);
        addActor(portrait);

        for (int i = 0; i < MAX_VISIBLE_MASKS; i++) {
            Image mask = new Image(fullMaskTexture);
            mask.setBounds(72f + i * 38f, 115f, 34f, 34f);
            maskImages[i] = mask;
            addActor(mask);
        }

        maskBreakImage = new Image(maskBreakTextures[0]);
        maskBreakImage.setBounds(72f, 115f, 34f, 34f);
        maskBreakImage.setVisible(false);
        addActor(maskBreakImage);

        soulFillImage = new Image(soulFillTexture);
        soulFillImage.setBounds(14f, 9f, 44f, 76f);
        addActor(soulFillImage);

        Image vessel = new Image(soulVesselTexture);
        vessel.setBounds(0f, 0f, 72f, 92f);
        addActor(vessel);

        soulGlowImage = new Image(soulGlowTexture);
        soulGlowImage.setBounds(0f, 0f, 72f, 92f);
        addActor(soulGlowImage);

        soulLabel = new Label("", skin);
        soulLabel.setFontScale(0.62f);
        soulLabel.setColor(0.72f, 0.91f, 1f, 1f);
        soulLabel.setPosition(78f, 73f);
        addActor(soulLabel);

        godModeLabel = new Label("", skin);
        godModeLabel.setFontScale(0.54f);
        godModeLabel.setPosition(78f, 46f);
        addActor(godModeLabel);

        invincibleLabel = new Label("", skin);
        invincibleLabel.setFontScale(0.54f);
        invincibleLabel.setPosition(78f, 24f);
        addActor(invincibleLabel);
    }

    public void update(PlayerStats playerStats) {
        updateMasks(playerStats);
        updateSoul(playerStats);
        updateStatusLabels(playerStats);
    }

    private void updateMasks(PlayerStats playerStats) {
        int health = Math.max(0, playerStats.getHealth());
        int maxHealth = Math.min(
            MAX_VISIBLE_MASKS,
            Math.max(1, playerStats.getMaxHealth())
        );

        if (previousHealth >= 0 && health < previousHealth) {
            breakingMaskIndex = Math.min(maxHealth - 1, previousHealth - 1);
            maskBreakTime = 0f;
            maskBreakImage.setPosition(
                72f + breakingMaskIndex * 38f,
                115f
            );
            maskBreakImage.setVisible(true);
        }
        previousHealth = health;

        for (int i = 0; i < maskImages.length; i++) {
            Image mask = maskImages[i];
            mask.setVisible(i < maxHealth);
            if (i < maxHealth) {
                Texture texture = i < health
                    ? fullMaskTexture
                    : emptyMaskTexture;
                mask.setDrawable(
                    new TextureRegionDrawable(
                        new TextureRegion(texture)
                    )
                );
            }
        }

        updateMaskBreakAnimation();
    }

    private void updateMaskBreakAnimation() {
        if (!maskBreakImage.isVisible()) {
            return;
        }

        maskBreakTime += Gdx.graphics.getDeltaTime();
        int frameIndex = (int) (
            maskBreakTime / MASK_BREAK_FRAME_DURATION
        );

        if (frameIndex >= maskBreakTextures.length) {
            maskBreakImage.setVisible(false);
            breakingMaskIndex = -1;
            return;
        }

        maskBreakImage.setDrawable(
            new TextureRegionDrawable(
                new TextureRegion(maskBreakTextures[frameIndex])
            )
        );
    }

    private void updateSoul(PlayerStats playerStats) {
        int maxSoul = Math.max(1, playerStats.getMaxSoul());
        int soul = Math.max(0, Math.min(maxSoul, playerStats.getSoul()));
        float ratio = (float) soul / maxSoul;

        float maxFillHeight = 76f;
        float fillHeight = Math.max(1f, maxFillHeight * ratio);
        soulFillImage.setHeight(fillHeight);
        soulFillImage.setY(9f);
        soulFillImage.setVisible(soul > 0);
        soulGlowImage.setColor(1f, 1f, 1f, 0.20f + ratio * 0.80f);

        soulLabel.setText((L10n.isFrench() ? "AME : " : "SOUL: ") + soul + " / " + maxSoul);
    }

    private void updateStatusLabels(PlayerStats playerStats) {
        boolean god = playerStats.isGodModeEnabled();
        boolean invincible = playerStats.isDamageInvincible();

        godModeLabel.setText((L10n.isFrench() ? "MODE DIVIN : " : "GOD MODE: ") + yesNo(god));
        godModeLabel.setColor(god ? GOD_MODE_ON_COLOR : STATUS_OFF_COLOR);

        invincibleLabel.setText((L10n.isFrench() ? "INVINCIBLE : " : "INVINCIBLE: ") + yesNo(invincible));
        invincibleLabel.setColor(invincible ? INVINCIBLE_ON_COLOR : STATUS_OFF_COLOR);
    }

    private String yesNo(boolean enabled) {
        if (L10n.isFrench()) return enabled ? "OUI" : "NON";
        return enabled ? "YES" : "NO";
    }

    private Texture loadTexture(String path) {
        if (!Gdx.files.internal(path).exists()) {
            throw new IllegalStateException("HUD texture not found: " + path);
        }
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    public void dispose() {
        knightTexture.dispose();
        fullMaskTexture.dispose();
        emptyMaskTexture.dispose();
        soulVesselTexture.dispose();
        soulFillTexture.dispose();
        soulGlowTexture.dispose();
        for (Texture texture : maskBreakTextures) {
            texture.dispose();
        }
    }
}
