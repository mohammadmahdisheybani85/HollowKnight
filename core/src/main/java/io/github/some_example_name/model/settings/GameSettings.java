package io.github.some_example_name.model.settings;

public class GameSettings {
    public static final float DEFAULT_MUSIC_VOLUME = 0.5f;
    public static final float DEFAULT_SFX_VOLUME = 1f;
    public static final float DEFAULT_BRIGHTNESS = 1f;
    public static final String DEFAULT_LANGUAGE_CODE = "en";
    public static final MenuTheme DEFAULT_MENU_THEME = MenuTheme.CLASSIC;

    private float musicVolume = DEFAULT_MUSIC_VOLUME;
    private float sfxVolume = DEFAULT_SFX_VOLUME;

    private boolean musicMuted;
    private boolean sfxMuted;

    private float brightness = DEFAULT_BRIGHTNESS;
    private String languageCode = DEFAULT_LANGUAGE_CODE;
    private MenuTheme menuTheme = DEFAULT_MENU_THEME;

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = clamp01(musicVolume);
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(float sfxVolume) {
        this.sfxVolume = clamp01(sfxVolume);
    }

    public boolean isMusicMuted() {
        return musicMuted;
    }

    public void setMusicMuted(boolean musicMuted) {
        this.musicMuted = musicMuted;
    }

    public boolean isSfxMuted() {
        return sfxMuted;
    }

    public void setSfxMuted(boolean sfxMuted) {
        this.sfxMuted = sfxMuted;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = clamp01(brightness);
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = "fr".equals(languageCode)
            ? "fr"
            : DEFAULT_LANGUAGE_CODE;
    }

    public void toggleLanguage() {
        languageCode = "en".equals(languageCode) ? "fr" : "en";
    }

    public MenuTheme getMenuTheme() {
        return menuTheme;
    }

    public void setMenuTheme(MenuTheme menuTheme) {
        this.menuTheme = menuTheme == null ? DEFAULT_MENU_THEME : menuTheme;
    }

    public void cycleMenuTheme() {
        menuTheme = menuTheme.next();
    }

    public void resetSounds() {
        musicVolume = DEFAULT_MUSIC_VOLUME;
        sfxVolume = DEFAULT_SFX_VOLUME;
        musicMuted = false;
        sfxMuted = false;
    }

    public void loadFromData(GameSettingsData data) {
        if (data == null) {
            return;
        }

        musicVolume = clamp01(data.musicVolume);
        sfxVolume = clamp01(data.sfxVolume);
        musicMuted = data.musicMuted;
        sfxMuted = data.sfxMuted;
        brightness = clamp01(data.brightness);
        setLanguageCode(data.languageCode);
        setMenuTheme(parseTheme(data.menuThemeName));
    }

    public GameSettingsData toData() {
        GameSettingsData data = new GameSettingsData();
        data.musicVolume = musicVolume;
        data.sfxVolume = sfxVolume;
        data.musicMuted = musicMuted;
        data.sfxMuted = sfxMuted;
        data.brightness = brightness;
        data.languageCode = languageCode;
        data.menuThemeName = menuTheme.name();
        return data;
    }

    private MenuTheme parseTheme(String name) {
        if (name == null || name.isBlank()) {
            return DEFAULT_MENU_THEME;
        }

        try {
            return MenuTheme.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return DEFAULT_MENU_THEME;
        }
    }

    private float clamp01(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}
