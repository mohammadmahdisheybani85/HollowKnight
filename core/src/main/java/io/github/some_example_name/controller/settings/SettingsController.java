package io.github.some_example_name.controller.settings;

import io.github.some_example_name.model.settings.GameSettings;
import io.github.some_example_name.model.settings.GameSettingsData;
import io.github.some_example_name.model.settings.MenuTheme;
import io.github.some_example_name.repository.settings.GameSettingsRepository;

public class SettingsController {
    private final GameSettings settings;
    private final GameSettingsRepository repository;

    public SettingsController(GameSettings settings) {
        this.settings = settings;
        this.repository = new GameSettingsRepository();
        loadSavedSettings();
    }

    public float getMusicVolume() { return settings.getMusicVolume(); }
    public void setMusicVolume(float volume) { settings.setMusicVolume(volume); saveSettings(); }
    public void previewMusicVolume(float volume) { settings.setMusicVolume(volume); }

    public float getSfxVolume() { return settings.getSfxVolume(); }
    public void setSfxVolume(float volume) { settings.setSfxVolume(volume); saveSettings(); }
    public void previewSfxVolume(float volume) { settings.setSfxVolume(volume); }

    public boolean isMusicMuted() { return settings.isMusicMuted(); }
    public void setMusicMuted(boolean muted) { settings.setMusicMuted(muted); saveSettings(); }

    public boolean isSfxMuted() { return settings.isSfxMuted(); }
    public void setSfxMuted(boolean muted) { settings.setSfxMuted(muted); saveSettings(); }

    public void resetSounds() { settings.resetSounds(); saveSettings(); }

    public float getBrightness() { return settings.getBrightness(); }
    public void setBrightness(float brightness) { settings.setBrightness(brightness); saveSettings(); }
    public void previewBrightness(float brightness) { settings.setBrightness(brightness); }

    public String getLanguageCode() { return settings.getLanguageCode(); }
    public void toggleLanguage() { settings.toggleLanguage(); saveSettings(); }

    public MenuTheme getMenuTheme() { return settings.getMenuTheme(); }
    public void cycleMenuTheme() { settings.cycleMenuTheme(); saveSettings(); }

    public void saveCurrentSettings() { saveSettings(); }

    private void loadSavedSettings() {
        GameSettingsData data = repository.load();
        settings.loadFromData(data);
    }

    private void saveSettings() {
        repository.save(settings.toData());
    }
}
