package io.github.some_example_name.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.some_example_name.controller.audio.AudioManager;
import io.github.some_example_name.controller.settings.SettingsController;
import io.github.some_example_name.util.L10n;

public class PauseMenuOverlay extends Table {
    private final SettingsController settingsController;
    private final AudioManager audioManager;

    private final Table mainMenuBox;
    private final Table settingsBox;

    private Label cheatCodesLabel;

    private Slider musicSlider;
    private Slider sfxSlider;
    private Slider brightnessSlider;

    private Label musicValueLabel;
    private Label sfxValueLabel;
    private Label brightnessValueLabel;

    private CheckBox muteMusicCheckBox;
    private CheckBox muteSfxCheckBox;

    public PauseMenuOverlay(
        Skin skin,
        Runnable onContinue,
        Runnable onSaveAndQuit,
        SettingsController settingsController,
        AudioManager audioManager
    ) {
        super(skin);

        this.settingsController = settingsController;
        this.audioManager = audioManager;

        setFillParent(true);
        setVisible(false);

        setBackground(
            skin.newDrawable(
                "white",
                new Color(0f, 0f, 0f, 0.72f)
            )
        );

        mainMenuBox = createMainMenuBox(
            skin,
            onContinue,
            onSaveAndQuit
        );

        settingsBox = createSettingsBox(skin);
        settingsBox.setVisible(false);

        Stack stack = new Stack();
        stack.add(mainMenuBox);
        stack.add(settingsBox);

        add(stack).center();
    }

    private Table createMainMenuBox(
        Skin skin,
        Runnable onContinue,
        Runnable onSaveAndQuit
    ) {
        Table menuBox = createMenuBox(skin);

        Label titleLabel = new Label(
            L10n.tr("Pause Menu"),
            skin
        );
        titleLabel.setFontScale(1.7f);

        cheatCodesLabel = new Label(
            L10n.tr("GODMODE - Toggle infinite health\nFULLSOUL - Fill Soul bar\nBOSSTELEPORT - Boss arena\nNOCLIP - Spectator mode\nHEAL - Restore health\nINSTAKILL - Defeat enemies"),
            skin
        );
        cheatCodesLabel.setVisible(false);

        menuBox.add(titleLabel)
            .padBottom(22f)
            .row();

        menuBox.add(
                createButton(
                    L10n.tr("Continue"),
                    skin,
                    onContinue
                )
            )
            .width(300f)
            .height(48f)
            .padBottom(10f)
            .row();

        menuBox.add(
                createButton(
                    L10n.tr("Show Cheat Codes"),
                    skin,
                    this::toggleCheatCodes
                )
            )
            .width(300f)
            .height(48f)
            .padBottom(10f)
            .row();

        menuBox.add(
                createButton(
                    L10n.tr("Settings"),
                    skin,
                    this::showSettingsPanel
                )
            )
            .width(300f)
            .height(48f)
            .padBottom(10f)
            .row();

        menuBox.add(
                createButton(
                    L10n.tr("Save & Quit"),
                    skin,
                    onSaveAndQuit
                )
            )
            .width(300f)
            .height(48f)
            .padBottom(14f)
            .row();

        menuBox.add(cheatCodesLabel)
            .padTop(10f)
            .row();

        return menuBox;
    }

    private Table createSettingsBox(Skin skin) {
        Table menuBox = createMenuBox(skin);

        Label titleLabel = new Label(
            L10n.tr("Pause Settings"),
            skin
        );
        titleLabel.setFontScale(1.45f);

        menuBox.add(titleLabel)
            .colspan(3)
            .padBottom(14f)
            .row();

        musicSlider = new Slider(
            0f,
            1f,
            0.01f,
            false,
            skin
        );

        sfxSlider = new Slider(
            0f,
            1f,
            0.01f,
            false,
            skin
        );

        brightnessSlider = new Slider(
            0.35f,
            1f,
            0.01f,
            false,
            skin
        );

        musicValueLabel = new Label("0", skin);
        sfxValueLabel = new Label("0", skin);
        brightnessValueLabel = new Label("0", skin);

        addSliderRow(
            menuBox,
            skin,
            L10n.tr("Music Volume"),
            musicSlider,
            musicValueLabel
        );

        addSliderRow(
            menuBox,
            skin,
            L10n.tr("SFX Volume"),
            sfxSlider,
            sfxValueLabel
        );

        addSliderRow(
            menuBox,
            skin,
            L10n.tr("Brightness"),
            brightnessSlider,
            brightnessValueLabel
        );

        muteMusicCheckBox =
            createCheckBox(L10n.tr("Mute Music"), skin);

        muteSfxCheckBox =
            createCheckBox(L10n.tr("Mute SFX"), skin);

        menuBox.add(muteMusicCheckBox)
            .colspan(3)
            .width(330f)
            .height(34f)
            .padTop(8f)
            .padBottom(4f)
            .row();

        menuBox.add(muteSfxCheckBox)
            .colspan(3)
            .width(330f)
            .height(34f)
            .padBottom(10f)
            .row();

        menuBox.add(
                createButton(
                    L10n.tr("Test SFX"),
                    skin,
                    this::playTestSound
                )
            )
            .colspan(3)
            .width(300f)
            .height(44f)
            .padBottom(7f)
            .row();

        menuBox.add(
                createButton(
                    L10n.tr("Reset Sounds"),
                    skin,
                    this::resetSounds
                )
            )
            .colspan(3)
            .width(300f)
            .height(44f)
            .padBottom(10f)
            .row();

        Label noteLabel = new Label(
            L10n.tr("Controls and language can be changed\nfrom Main Menu Settings."),
            skin
        );

        menuBox.add(noteLabel)
            .colspan(3)
            .padBottom(10f)
            .row();

        menuBox.add(
                createButton(
                    L10n.tr("Back to Pause"),
                    skin,
                    this::showMainPanel
                )
            )
            .colspan(3)
            .width(300f)
            .height(48f)
            .row();

        addSettingsListeners();
        return menuBox;
    }

    private void addSliderRow(
        Table table,
        Skin skin,
        String text,
        Slider slider,
        Label valueLabel
    ) {
        Label label = new Label(text, skin);

        table.add(label)
            .width(190f)
            .right()
            .padRight(10f);

        table.add(slider)
            .width(260f)
            .height(32f);

        table.add(valueLabel)
            .width(52f)
            .left()
            .padLeft(10f)
            .row();
    }

    private Table createMenuBox(Skin skin) {
        Table menuBox = new Table(skin);

        menuBox.setBackground(
            skin.newDrawable(
                "white",
                new Color(
                    0.02f,
                    0.02f,
                    0.04f,
                    0.40f
                )
            )
        );

        menuBox.pad(26f);
        return menuBox;
    }

    private CheckBox createCheckBox(
        String text,
        Skin skin
    ) {
        CheckBox checkBox = new CheckBox(text, skin);
        checkBox.getImageCell().padRight(12f);
        checkBox.getLabelCell().padLeft(4f);
        checkBox.left();
        return checkBox;
    }

    private void showSettingsPanel() {
        syncSettingsView();
        applyAudioSettings();

        cheatCodesLabel.setVisible(false);
        mainMenuBox.setVisible(false);
        settingsBox.setVisible(true);
    }

    private void showMainPanel() {
        audioManager.stopSfxPreview();

        settingsBox.setVisible(false);
        mainMenuBox.setVisible(true);
    }

    public void showMenu() {
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
        cheatCodesLabel.setVisible(false);
        settingsBox.setVisible(false);
        mainMenuBox.setVisible(true);
        audioManager.stopSfxPreview();
    }

    private void toggleCheatCodes() {
        cheatCodesLabel.setVisible(
            !cheatCodesLabel.isVisible()
        );
    }

    private void syncSettingsView() {
        musicSlider.setValue(
            settingsController.getMusicVolume()
        );

        sfxSlider.setValue(
            settingsController.getSfxVolume()
        );

        brightnessSlider.setValue(
            settingsController.getBrightness()
        );

        muteMusicCheckBox.setChecked(
            settingsController.isMusicMuted()
        );

        muteSfxCheckBox.setChecked(
            settingsController.isSfxMuted()
        );

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
            Math.round(
                brightnessSlider.getValue() * 100f
            )
        );
    }

    private void applyAudioSettings() {
        audioManager.setMusicVolume(
            settingsController.getMusicVolume()
        );

        audioManager.setMusicMuted(
            settingsController.isMusicMuted()
        );

        audioManager.setSoundVolume(
            settingsController.getSfxVolume()
        );

        audioManager.setSoundMuted(
            settingsController.isSfxMuted()
        );
    }

    private void playTestSound() {
        applyAudioSettings();
        audioManager.playTestSfx();
    }

    private void resetSounds() {
        settingsController.resetSounds();
        syncSettingsView();
        applyAudioSettings();
        audioManager.stopSfxPreview();
        audioManager.playTestSfx();
    }

    private void addSettingsListeners() {
        musicSlider.addListener(
            new ChangeListener() {
                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {
                    settingsController.setMusicVolume(
                        musicSlider.getValue()
                    );

                    audioManager.setMusicVolume(
                        musicSlider.getValue()
                    );

                    updateValueLabels();
                }
            }
        );

        sfxSlider.addListener(
            new ChangeListener() {
                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {
                    settingsController.setSfxVolume(
                        sfxSlider.getValue()
                    );

                    audioManager.setSoundVolume(
                        sfxSlider.getValue()
                    );

                    updateValueLabels();
                }
            }
        );

        brightnessSlider.addListener(
            new ChangeListener() {
                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {
                    settingsController.setBrightness(
                        brightnessSlider.getValue()
                    );

                    updateValueLabels();
                }
            }
        );

        sfxSlider.addListener(
            new InputListener() {
                @Override
                public boolean touchDown(
                    InputEvent event,
                    float x,
                    float y,
                    int pointer,
                    int button
                ) {
                    audioManager.startSfxPreview();
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
                    audioManager.stopSfxPreview();
                    audioManager.playTestSfx();
                }
            }
        );

        muteMusicCheckBox.addListener(
            new ChangeListener() {
                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {
                    settingsController.setMusicMuted(
                        muteMusicCheckBox.isChecked()
                    );

                    audioManager.setMusicMuted(
                        muteMusicCheckBox.isChecked()
                    );
                }
            }
        );

        muteSfxCheckBox.addListener(
            new ChangeListener() {
                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {
                    settingsController.setSfxMuted(
                        muteSfxCheckBox.isChecked()
                    );

                    audioManager.setSoundMuted(
                        muteSfxCheckBox.isChecked()
                    );
                }
            }
        );
    }

    private TextButton createButton(
        String text,
        Skin skin,
        Runnable action
    ) {
        TextButton button = new TextButton(
            text,
            skin
        );

        button.addListener(
            new ChangeListener() {
                @Override
                public void changed(
                    ChangeEvent event,
                    Actor actor
                ) {
                    action.run();
                }
            }
        );

        return button;
    }
}
