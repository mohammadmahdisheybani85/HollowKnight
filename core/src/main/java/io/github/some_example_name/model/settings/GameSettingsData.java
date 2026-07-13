package io.github.some_example_name.model.settings;

public class GameSettingsData {
    public float musicVolume = GameSettings.DEFAULT_MUSIC_VOLUME;
    public float sfxVolume = GameSettings.DEFAULT_SFX_VOLUME;

    public boolean musicMuted;
    public boolean sfxMuted;

    public float brightness = GameSettings.DEFAULT_BRIGHTNESS;
    public String languageCode = GameSettings.DEFAULT_LANGUAGE_CODE;
    public String menuThemeName = GameSettings.DEFAULT_MENU_THEME.name();

    public GameSettingsData() {
    }
}
