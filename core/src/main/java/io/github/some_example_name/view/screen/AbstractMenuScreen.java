package io.github.some_example_name.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.settings.MenuTheme;
import io.github.some_example_name.view.ui.GameSkinFactory;
import io.github.some_example_name.view.ui.MenuUiFactory;

public abstract class AbstractMenuScreen extends ScreenAdapter {
    private static final float UI_WIDTH = 1280f;
    private static final float UI_HEIGHT = 720f;
    private static final float READABILITY_OVERLAY_ALPHA = 0.18f;

    private static final String[] SUPPORTED_IMAGE_EXTENSIONS = {
        ".jpg", ".jpeg", ".png", ".jfif"
    };

    protected final Main game;
    protected Stage stage;
    protected Skin skin;
    protected MenuUiFactory uiFactory;

    private Texture backgroundTexture;
    private Image backgroundImage;
    private Image themeOverlay;
    private Image readabilityOverlay;
    private Image brightnessOverlay;

    protected AbstractMenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(UI_WIDTH, UI_HEIGHT));
        skin = GameSkinFactory.create();
        uiFactory = new MenuUiFactory(skin);

        addBackground();
        if (getBackgroundPath() != null) {
            addThemeOverlay();
            addReadabilityOverlay();
        }
        addBrightnessOverlay();
        buildUI();
        installMenuClickSound();

        stage.getViewport().apply(true);
        Gdx.input.setInputProcessor(createInputProcessor());
    }

    protected abstract void buildUI();

    protected InputProcessor createInputProcessor() {
        return stage;
    }

    protected String getBackgroundPath() {
        return null;
    }

    protected Table createCenteredTable() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        return table;
    }

    protected Table createGlassPanel(float alpha) {
        Table panel = new Table(skin);
        Color color = getThemePanelColor(alpha);
        panel.setBackground(skin.newDrawable("white", color));
        return panel;
    }

    protected void refreshMenuTheme() {
        refreshThemeOverlay();
        if (readabilityOverlay != null) {
            float alpha = getTheme() == MenuTheme.VOID
                ? READABILITY_OVERLAY_ALPHA + 0.06f
                : READABILITY_OVERLAY_ALPHA;
            readabilityOverlay.setColor(0f, 0f, 0f, alpha);
        }
    }

    private MenuTheme getTheme() {
        return game.getSettingsController().getMenuTheme();
    }

    private Color getThemePanelColor(float alpha) {
        return switch (getTheme()) {
            case CLASSIC -> new Color(0.015f, 0.02f, 0.04f, alpha);
            case VOID -> new Color(0.055f, 0.012f, 0.09f, alpha);
            case VERDANT -> new Color(0.01f, 0.065f, 0.035f, alpha);
        };
    }

    private void addBackground() {
        String requestedPath = getBackgroundPath();
        if (requestedPath == null || requestedPath.isBlank()) {
            return;
        }

        FileHandle backgroundFile = resolveImageFile(requestedPath);
        if (backgroundFile == null) {
            Gdx.app.error("AbstractMenuScreen", "Menu background not found: " + requestedPath);
            return;
        }

        backgroundTexture = new Texture(backgroundFile);
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        backgroundImage.setScaling(Scaling.fill);
        backgroundImage.setTouchable(Touchable.disabled);
        stage.addActor(backgroundImage);
    }

    private void addThemeOverlay() {
        themeOverlay = new Image(skin.newDrawable("white", Color.WHITE));
        themeOverlay.setFillParent(true);
        themeOverlay.setTouchable(Touchable.disabled);
        stage.addActor(themeOverlay);
        refreshThemeOverlay();
    }

    private void refreshThemeOverlay() {
        if (themeOverlay == null) {
            return;
        }

        switch (getTheme()) {
            case CLASSIC -> themeOverlay.setColor(0.10f, 0.16f, 0.28f, 0.07f);
            case VOID -> themeOverlay.setColor(0.34f, 0.05f, 0.46f, 0.24f);
            case VERDANT -> themeOverlay.setColor(0.04f, 0.36f, 0.15f, 0.18f);
        }
    }

    private void addReadabilityOverlay() {
        readabilityOverlay = new Image(skin.newDrawable("white", Color.BLACK));
        readabilityOverlay.setFillParent(true);
        readabilityOverlay.setTouchable(Touchable.disabled);
        stage.addActor(readabilityOverlay);
        refreshMenuTheme();
    }

    private void addBrightnessOverlay() {
        brightnessOverlay = new Image(skin.newDrawable("white", Color.BLACK));
        brightnessOverlay.setFillParent(true);
        brightnessOverlay.setTouchable(Touchable.disabled);
        stage.addActor(brightnessOverlay);
        refreshBrightnessOverlay();
    }

    protected void refreshBrightnessOverlay() {
        if (brightnessOverlay == null) {
            return;
        }

        float brightness = game.getSettingsController().getBrightness();
        float darknessAlpha = (1f - brightness) * 0.75f;
        brightnessOverlay.setColor(0f, 0f, 0f, darknessAlpha);
    }

    private void installMenuClickSound() {
        stage.getRoot().addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button
            ) {
                if (button != Input.Buttons.LEFT) {
                    return false;
                }

                Button clickedButton = findParentButton(event.getTarget());
                if (clickedButton != null && !clickedButton.isDisabled()) {
                    game.getAudioManager().playMenuClick();
                }
                return false;
            }
        });
    }

    private Button findParentButton(Actor actor) {
        Actor current = actor;
        while (current != null) {
            if (current instanceof Button button) {
                return button;
            }
            current = current.getParent();
        }
        return null;
    }

    private FileHandle resolveImageFile(String requestedPath) {
        FileHandle exactFile = Gdx.files.internal(requestedPath);
        if (exactFile.exists()) {
            return exactFile;
        }

        String basePath = removeExtension(requestedPath);
        for (String extension : SUPPORTED_IMAGE_EXTENSIONS) {
            FileHandle candidate = Gdx.files.internal(basePath + extension);
            if (candidate.exists()) {
                return candidate;
            }
        }
        return null;
    }

    private String removeExtension(String path) {
        int lastSlashIndex = path.lastIndexOf('/');
        int lastDotIndex = path.lastIndexOf('.');
        return lastDotIndex <= lastSlashIndex ? path : path.substring(0, lastDotIndex);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.01f, 0.01f, 0.025f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getAudioManager().update(delta);
        refreshBrightnessOverlay();
        refreshMenuTheme();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
