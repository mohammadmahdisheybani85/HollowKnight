package io.github.some_example_name.view.screen;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.controller.input.KeyBindingController;
import io.github.some_example_name.controller.settings.SettingsController;
import io.github.some_example_name.model.input.PlayerAction;

import java.util.EnumMap;
import java.util.Map;

public class SettingsScreen extends AbstractMenuScreen {
    private SettingsController controller;
    private KeyBindingController keyBindingController;

    private Table root;
    private Table controlsOverlayRoot;
    private Table controlsWindow;

    private Slider musicSlider;
    private Slider sfxSlider;
    private Slider brightnessSlider;

    private Label titleLabel;
    private Label musicTextLabel;
    private Label sfxTextLabel;
    private Label brightnessTextLabel;
    private Label qualityLabel;

    private Label musicValueLabel;
    private Label sfxValueLabel;
    private Label brightnessValueLabel;

    private CheckBox muteMusicCheckBox;
    private CheckBox muteSfxCheckBox;

    private TextButton resetSoundsButton;
    private TextButton controlsButton;
    private TextButton resetControlsButton;
    private TextButton languageButton;
    private TextButton themeButton;
    private TextButton testSfxButton;
    private TextButton backButton;

    private TextButton overlayResetControlsButton;
    private TextButton overlayCloseButton;

    private Table controlsPanel;

    private PlayerAction waitingForAction;
    private Label keyBindingMessageLabel;

    private final Map<PlayerAction, TextButton> keyBindingButtons =
        new EnumMap<>(PlayerAction.class);

    public SettingsScreen(Main game) {
        super(game);
    }

    @Override
    protected void buildUI() {
        controller = game.getSettingsController();
        keyBindingController = game.getKeyBindingController();

        root = new Table();
        root.setFillParent(true);
        root.top();
        stage.addActor(root);

        titleLabel = new Label("", skin, "title");
        titleLabel.setFontScale(1f);
        titleLabel.setAlignment(Align.center);

        Table settingsTable = new Table();
        settingsTable.defaults().pad(4f);

        buildMainSettings(settingsTable);
        buildControlsOverlay();

        backButton = createMenuTextButton(
            "",
            () -> game.getScreenManager().show(ScreenType.MAIN_MENU)
        );

        root.add(titleLabel)
            .width(700f)
            .height(58f)
            .padTop(18f)
            .padBottom(8f)
            .row();

        root.add(settingsTable)
            .padBottom(4f)
            .row();

        root.add(backButton)
            .width(360f)
            .height(48f)
            .padTop(8f)
            .padBottom(12f)
            .row();

        syncViewWithSettings();
        applyAudioSettings();
        refreshLocalizedTexts();

        addListeners();
        addSliderReleaseListeners();
    }

    private void buildMainSettings(Table table) {
        musicTextLabel = createSettingLabel();
        musicSlider = new Slider(0f, 1f, 0.01f, false, skin);
        musicValueLabel = createValueLabel();

        addSliderRow(
            table,
            musicTextLabel,
            musicSlider,
            musicValueLabel
        );

        sfxTextLabel = createSettingLabel();
        sfxSlider = new Slider(0f, 1f, 0.01f, false, skin);
        sfxValueLabel = createValueLabel();

        addSliderRow(
            table,
            sfxTextLabel,
            sfxSlider,
            sfxValueLabel
        );

        brightnessTextLabel = createSettingLabel();
        brightnessSlider = new Slider(0.35f, 1f, 0.01f, false, skin);
        brightnessValueLabel = createValueLabel();

        addSliderRow(
            table,
            brightnessTextLabel,
            brightnessSlider,
            brightnessValueLabel
        );

        qualityLabel = new Label("", skin);
        qualityLabel.setAlignment(Align.center);
        qualityLabel.setFontScale(0.95f);

        table.add(qualityLabel)
            .colspan(3)
            .padTop(8f)
            .row();

        resetSoundsButton = createMenuTextButton(
            "",
            this::resetSounds
        );

        table.add(resetSoundsButton)
            .colspan(3)
            .width(420f)
            .height(40f)
            .row();

        controlsButton = createMenuTextButton(
            "",
            this::toggleControlsOverlay
        );

        table.add(controlsButton)
            .colspan(3)
            .width(420f)
            .height(40f)
            .row();

        resetControlsButton = createMenuTextButton(
            "",
            this::resetKeyBindings
        );

        table.add(resetControlsButton)
            .colspan(3)
            .width(420f)
            .height(40f)
            .row();

        languageButton = createMenuTextButton(
            "",
            this::toggleLanguage
        );

        table.add(languageButton)
            .colspan(3)
            .width(420f)
            .height(40f)
            .row();

        themeButton = createMenuTextButton(
            "",
            this::cycleMenuTheme
        );

        table.add(themeButton)
            .colspan(3)
            .width(420f)
            .height(40f)
            .row();

        muteMusicCheckBox = createSmallCheckBox("");
        muteSfxCheckBox = createSmallCheckBox("");

        table.add(muteMusicCheckBox)
            .colspan(3)
            .width(360f)
            .height(30f)
            .row();

        table.add(muteSfxCheckBox)
            .colspan(3)
            .width(360f)
            .height(30f)
            .row();

        testSfxButton = createMenuTextButton(
            "",
            this::testSfx
        );

        table.add(testSfxButton)
            .colspan(3)
            .width(420f)
            .height(40f)
            .row();
    }

    private void buildControlsOverlay() {
        controlsOverlayRoot = new Table();
        controlsOverlayRoot.setFillParent(true);
        controlsOverlayRoot.setVisible(false);
        controlsOverlayRoot.setTouchable(Touchable.enabled);

        controlsOverlayRoot.addListener(new InputListener() {
            @Override
            public boolean touchDown(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button
            ) {
                return true;
            }
        });

        stage.addActor(controlsOverlayRoot);

        /*
         * The controls chooser is drawn over the Settings screen.
         * It needs its own readable panel background so its labels
         * do not visually merge with the Settings labels behind it.
         * This is not a full-screen black cover; only the floating
         * window itself gets a themed glass background.
         */
        controlsWindow = createGlassPanel(0.94f);
        controlsWindow.pad(16f);
        controlsWindow.defaults().pad(4f);

        Label panelTitle = new Label("CHANGE CONTROLS", skin, "title");
        panelTitle.setFontScale(0.70f);
        panelTitle.setAlignment(Align.center);

        controlsWindow.add(panelTitle)
            .colspan(2)
            .width(660f)
            .height(44f)
            .padBottom(2f)
            .row();

        controlsPanel = new Table();
        controlsPanel.defaults().pad(3f);

        keyBindingMessageLabel = new Label("", skin);
        keyBindingMessageLabel.setWrap(true);
        keyBindingMessageLabel.setAlignment(Align.center);
        keyBindingMessageLabel.setFontScale(0.86f);

        controlsPanel.add(keyBindingMessageLabel)
            .colspan(2)
            .width(620f)
            .height(34f)
            .row();

        addKeyBindingButton(PlayerAction.MOVE_LEFT, 0);
        addKeyBindingButton(PlayerAction.MOVE_RIGHT, 1);
        addKeyBindingButton(PlayerAction.LOOK_UP, 2);
        addKeyBindingButton(PlayerAction.LOOK_DOWN, 3);
        addKeyBindingButton(PlayerAction.JUMP, 4);
        addKeyBindingButton(PlayerAction.DASH, 5);
        addKeyBindingButton(PlayerAction.ATTACK, 6);
        addKeyBindingButton(PlayerAction.CAST_SPELL, 7);
        addKeyBindingButton(PlayerAction.FOCUS, 8);
        addKeyBindingButton(PlayerAction.INTERACT, 9);
        addKeyBindingButton(PlayerAction.OPEN_INVENTORY, 10);
        addKeyBindingButton(PlayerAction.PAUSE, 11);

        controlsWindow.add(controlsPanel)
            .colspan(2)
            .row();

        overlayResetControlsButton = createMenuTextButton(
            "",
            this::resetKeyBindings
        );

        overlayCloseButton = createMenuTextButton(
            "",
            this::hideControlsOverlay
        );

        controlsWindow.add(overlayResetControlsButton)
            .width(300f)
            .height(42f)
            .padTop(6f);

        controlsWindow.add(overlayCloseButton)
            .width(300f)
            .height(42f)
            .padTop(6f)
            .row();

        controlsOverlayRoot.add(controlsWindow)
            .width(700f)
            .padTop(92f);
    }

    private Label createSettingLabel() {
        Label label = new Label("", skin);
        label.setFontScale(0.95f);
        return label;
    }

    private Label createValueLabel() {
        Label label = new Label("0", skin);
        label.setFontScale(0.95f);
        return label;
    }

    private void addSliderRow(
        Table table,
        Label textLabel,
        Slider slider,
        Label valueLabel
    ) {
        table.add(textLabel)
            .width(250f)
            .right()
            .padRight(12f);

        table.add(slider)
            .width(230f)
            .height(28f);

        table.add(valueLabel)
            .width(70f)
            .left()
            .padLeft(12f)
            .row();
    }

    private void addKeyBindingButton(
        PlayerAction action,
        int index
    ) {
        TextButton button = createMenuTextButton(
            "",
            () -> waitForNewKey(action)
        );

        button.getLabel().setFontScale(0.78f);

        keyBindingButtons.put(action, button);

        controlsPanel.add(button)
            .width(300f)
            .height(36f)
            .pad(3f);

        if (index % 2 == 1) {
            controlsPanel.row();
        }
    }

    private TextButton createMenuTextButton(
        String text,
        Runnable action
    ) {
        TextButton button = new TextButton(text, skin, "menu");

        button.getLabel().setAlignment(Align.center);
        button.getLabel().setWrap(true);
        button.getLabel().setFontScale(0.95f);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(
                ChangeEvent event,
                Actor actor
            ) {
                action.run();
            }
        });

        return button;
    }

    private CheckBox createSmallCheckBox(String text) {
        CheckBox checkBox = new CheckBox(text, skin);

        checkBox.getLabel().setFontScale(0.82f);
        checkBox.getImageCell().padRight(8f);

        return checkBox;
    }

    private void syncViewWithSettings() {
        musicSlider.setValue(controller.getMusicVolume());
        sfxSlider.setValue(controller.getSfxVolume());
        brightnessSlider.setValue(controller.getBrightness());

        muteMusicCheckBox.setChecked(controller.isMusicMuted());
        muteSfxCheckBox.setChecked(controller.isSfxMuted());

        updateValueLabels();
    }

    private void updateValueLabels() {
        musicValueLabel.setText(
            Math.round(musicSlider.getValue() * 100f)
        );

        sfxValueLabel.setText(
            Math.round(sfxSlider.getValue() * 100f)
        );

        brightnessValueLabel.setText(
            Math.round(brightnessSlider.getValue() * 100f)
        );
    }

    private void applyAudioSettings() {
        game.getAudioManager().setMusicVolume(
            controller.getMusicVolume()
        );

        game.getAudioManager().setMusicMuted(
            controller.isMusicMuted()
        );

        game.getAudioManager().setSoundVolume(
            controller.getSfxVolume()
        );

        game.getAudioManager().setSoundMuted(
            controller.isSfxMuted()
        );
    }

    private void addListeners() {
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(
                ChangeEvent event,
                Actor actor
            ) {
                controller.previewMusicVolume(
                    musicSlider.getValue()
                );

                game.getAudioManager().setMusicVolume(
                    musicSlider.getValue()
                );

                updateValueLabels();
            }
        });

        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(
                ChangeEvent event,
                Actor actor
            ) {
                controller.previewSfxVolume(
                    sfxSlider.getValue()
                );

                game.getAudioManager().setSoundVolume(
                    sfxSlider.getValue()
                );

                game.getAudioManager().updateSfxPreviewVolume();

                updateValueLabels();
            }
        });

        brightnessSlider.addListener(new ChangeListener() {
            @Override
            public void changed(
                ChangeEvent event,
                Actor actor
            ) {
                controller.previewBrightness(
                    brightnessSlider.getValue()
                );

                refreshBrightnessOverlay();
                updateValueLabels();
            }
        });

        muteMusicCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(
                ChangeEvent event,
                Actor actor
            ) {
                controller.setMusicMuted(
                    muteMusicCheckBox.isChecked()
                );

                applyAudioSettings();
            }
        });

        muteSfxCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(
                ChangeEvent event,
                Actor actor
            ) {
                controller.setSfxMuted(
                    muteSfxCheckBox.isChecked()
                );

                applyAudioSettings();
            }
        });
    }

    private void addSliderReleaseListeners() {
        musicSlider.addListener(new InputListener() {
            @Override
            public boolean touchDown(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button
            ) {
                return true;
            }

            @Override
            public void touchUp(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button
            ) {
                controller.saveCurrentSettings();
            }
        });

        sfxSlider.addListener(new InputListener() {
            @Override
            public boolean touchDown(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button
            ) {
                game.getAudioManager().startSfxPreview();
                return true;
            }

            @Override
            public void touchUp(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button
            ) {
                game.getAudioManager().stopSfxPreview();
                controller.saveCurrentSettings();
            }
        });

        brightnessSlider.addListener(new InputListener() {
            @Override
            public boolean touchDown(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button
            ) {
                return true;
            }

            @Override
            public void touchUp(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button
            ) {
                controller.saveCurrentSettings();
            }
        });
    }

    private void resetSounds() {
        controller.resetSounds();
        syncViewWithSettings();
        applyAudioSettings();
    }

    private void toggleControlsOverlay() {
        if (controlsOverlayRoot.isVisible()) {
            hideControlsOverlay();
            return;
        }

        showControlsOverlay();
    }

    private void showControlsOverlay() {
        waitingForAction = null;
        controlsOverlayRoot.setVisible(true);
        refreshLocalizedTexts();
    }

    private void hideControlsOverlay() {
        waitingForAction = null;
        controlsOverlayRoot.setVisible(false);
        refreshLocalizedTexts();
    }

    private void toggleLanguage() {
        controller.toggleLanguage();
        refreshLocalizedTexts();
    }

    private void cycleMenuTheme() {
        controller.cycleMenuTheme();
        refreshMenuTheme();
        refreshLocalizedTexts();
    }

    private void testSfx() {
        applyAudioSettings();
        game.getAudioManager().playTestSfx();
    }

    private void refreshLocalizedTexts() {
        titleLabel.setText(
            tr(
                "SETTINGS",
                "PARAMETRES"
            )
        );

        musicTextLabel.setText(
            tr(
                "MUSIC VOLUME",
                "VOLUME MUSIQUE"
            )
        );

        sfxTextLabel.setText(
            tr(
                "SFX VOLUME",
                "VOLUME EFFETS"
            )
        );

        brightnessTextLabel.setText(
            tr(
                "BRIGHTNESS",
                "LUMINOSITE"
            )
        );

        qualityLabel.setText(
            tr(
                "QUALITY: ULTRA HIGH",
                "QUALITE : ULTRA"
            )
        );

        resetSoundsButton.setText(
            tr(
                "RESET SOUNDS",
                "REINITIALISER LES SONS"
            )
        );

        controlsButton.setText(
            controlsOverlayRoot.isVisible()
                ? tr(
                "HIDE CONTROLS",
                "MASQUER LES COMMANDES"
            )
                : tr(
                "CHANGE CONTROLS",
                "MODIFIER LES COMMANDES"
            )
        );

        resetControlsButton.setText(
            tr(
                "RESET CONTROLS",
                "REINITIALISER LES COMMANDES"
            )
        );

        overlayResetControlsButton.setText(
            tr(
                "RESET CONTROLS",
                "REINITIALISER LES COMMANDES"
            )
        );

        overlayCloseButton.setText(
            tr(
                "CLOSE",
                "FERMER"
            )
        );

        languageButton.setText(
            getLanguageButtonText()
        );

        themeButton.setText(getThemeButtonText());

        muteMusicCheckBox.setText(
            tr(
                "Mute Music",
                "Couper la musique"
            )
        );

        muteSfxCheckBox.setText(
            tr(
                "Mute SFX",
                "Couper les effets"
            )
        );

        testSfxButton.setText(
            tr(
                "TEST SFX",
                "TESTER LES EFFETS"
            )
        );

        backButton.setText(
            tr(
                "BACK",
                "RETOUR"
            )
        );

        if (waitingForAction == null) {
            keyBindingMessageLabel.setText(
                tr(
                    "Click an action, then press a key.",
                    "Cliquez sur une action, puis appuyez sur une touche."
                )
            );
        } else {
            updateWaitingForKeyMessage();
        }

        refreshKeyBindingButtons();
    }

    private String getLanguageButtonText() {
        return isFrench()
            ? "LANGUE : FRANCAIS"
            : "LANGUAGE: ENGLISH";
    }

    private String getThemeButtonText() {
        String themeName = switch (controller.getMenuTheme()) {
            case CLASSIC -> isFrench() ? "CLASSIQUE" : "CLASSIC";
            case VOID -> isFrench() ? "NEANT" : "VOID";
            case VERDANT -> isFrench() ? "VERDOYANT" : "VERDANT";
        };

        return (isFrench() ? "THEME DU MENU : " : "MENU THEME: ") + themeName;
    }

    private boolean isFrench() {
        return "fr".equals(
            controller.getLanguageCode()
        );
    }

    private String tr(
        String english,
        String french
    ) {
        return isFrench()
            ? french
            : english;
    }

    private String getLocalizedActionName(
        PlayerAction action
    ) {
        if (!isFrench()) {
            return action.getDisplayName();
        }

        return switch (action) {
            case MOVE_LEFT -> "Deplacement gauche";
            case MOVE_RIGHT -> "Deplacement droite";
            case LOOK_UP -> "Regarder en haut / sort vertical";
            case LOOK_DOWN -> "Regarder en bas / pogo";
            case JUMP -> "Saut";
            case DASH -> "Ruee";
            case ATTACK -> "Attaque";
            case CAST_SPELL -> "Lancer un sort";
            case FOCUS -> "Concentration / soin";
            case INTERACT -> "Interagir";
            case OPEN_INVENTORY -> "Ouvrir l'inventaire";
            case PAUSE -> "Pause";
        };
    }

    private String getKeyBindingText(
        PlayerAction action
    ) {
        return getLocalizedActionName(action)
            + ": "
            + keyBindingController.getKeyName(action);
    }

    private void waitForNewKey(
        PlayerAction action
    ) {
        waitingForAction = action;
        updateWaitingForKeyMessage();
    }

    private void updateWaitingForKeyMessage() {
        keyBindingMessageLabel.setText(
            tr(
                "Press a new key for ",
                "Appuyez sur une nouvelle touche pour "
            ) + getLocalizedActionName(waitingForAction)
        );
    }

    @Override
    protected InputProcessor createInputProcessor() {
        return new InputMultiplexer(
            stage,
            new InputAdapter() {
                @Override
                public boolean keyDown(int keycode) {
                    if (waitingForAction == null) {
                        return false;
                    }

                    boolean changed =
                        keyBindingController.changeKey(
                            waitingForAction,
                            keycode
                        );

                    if (!changed) {
                        PlayerAction actionUsingKey =
                            keyBindingController.findActionUsingKey(
                                keycode
                            );

                        if (actionUsingKey != null) {
                            keyBindingMessageLabel.setText(
                                tr(
                                    "This key is already used by ",
                                    "Cette touche est deja utilisee par "
                                )
                                    + getLocalizedActionName(actionUsingKey)
                                    + "."
                            );
                        } else {
                            keyBindingMessageLabel.setText(
                                tr(
                                    "This key cannot be used.",
                                    "Cette touche ne peut pas etre utilisee."
                                )
                            );
                        }

                        waitingForAction = null;
                        return true;
                    }

                    TextButton button =
                        keyBindingButtons.get(
                            waitingForAction
                        );

                    if (button != null) {
                        button.setText(
                            getKeyBindingText(
                                waitingForAction
                            )
                        );
                    }

                    keyBindingMessageLabel.setText(
                        getLocalizedActionName(waitingForAction)
                            + tr(
                            " updated.",
                            " modifie."
                        )
                    );

                    waitingForAction = null;
                    return true;
                }
            }
        );
    }

    private void resetKeyBindings() {
        keyBindingController.resetToDefaults();
        refreshKeyBindingButtons();

        waitingForAction = null;

        keyBindingMessageLabel.setText(
            tr(
                "Controls reset to defaults.",
                "Les commandes ont ete reinitialisees."
            )
        );
    }

    private void refreshKeyBindingButtons() {
        for (PlayerAction action : keyBindingButtons.keySet()) {
            TextButton button =
                keyBindingButtons.get(action);

            button.setText(
                getKeyBindingText(action)
            );
        }
    }

    @Override
    protected String getBackgroundPath() {
        return "backgrounds/menu/settings.jpeg";
    }
}
