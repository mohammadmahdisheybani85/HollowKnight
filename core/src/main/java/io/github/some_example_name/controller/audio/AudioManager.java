package io.github.some_example_name.controller.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.model.game.WorldArea;

public class AudioManager {
    private static final String LOG_TAG = "AudioManager";
    private static final float MUSIC_TRANSITION_SECONDS = 0.55f;

    private static final String MENU_MUSIC_PATH = "music/menu_theme.mp3";
    private static final String CROSSROADS_MUSIC_PATH = "music/crossroads_theme.mp3";
    private static final String GREENPATH_MUSIC_PATH = "music/greenpath_theme.mp3";
    private static final String BOSS_MUSIC_PATH = "music/boss_theme.mp3";
    private static final String VICTORY_MUSIC_PATH = "music/victory_theme.ogg";

    private static final String FALSE_KNIGHT_DEATH_SOUND_PATH = "sounds/FKnight_death.wav";
    private static final String CREATURE_HURT_SOUND_PATH = "sounds/creature_hurt.mp3";
    private static final String FOCUS_SOUND_PATH = "sounds/focus.mp3";
    private static final String AXE_SOUND_PATH = "sounds/axe.mp3";
    private static final String STONE_BREAK_SOUND_PATH = "sounds/stone_break.ogg";
    private static final String MENU_CLICK_SOUND_PATH = "sounds/menu_click.ogg";
    private static final String FOOTSTEP_STONE_PATH = "sounds/footstep_stone.ogg";
    private static final String FOOTSTEP_GRASS_PATH = "sounds/footstep_grass.ogg";
    private static final String DEATH_STINGER_PATH = "sounds/death_stinger.ogg";

    private static final String[] ZOTE_VOICE_PATHS = {
        "sounds/zote_voice_01.mp3",
        "sounds/zote_voice_02.mp3",
        "sounds/zote_voice_03.mp3"
    };

    private Music menuMusic;
    private Music crossroadsMusic;
    private Music greenpathMusic;
    private Music bossMusic;
    private Music victoryMusic;
    private Music currentBackgroundMusic;
    private Music fadingOutMusic;
    private float musicTransitionProgress = 1f;

    private Music focusMusic;
    private boolean focusPlaying;
    private boolean previewingFocusSound;

    private Sound falseKnightDeathSound;
    private Sound creatureHurtSound;
    private Sound nailSlashSound;
    private Sound enemyDeathSound;
    private Sound spellCastSound;
    private Sound soulGainSound;
    private Sound axeSound;
    private Sound stoneBreakSound;
    private Sound menuClickSound;
    private Sound footstepStoneSound;
    private Sound footstepGrassSound;
    private Sound deathStingerSound;

    private final Sound[] zoteVoiceSounds = new Sound[ZOTE_VOICE_PATHS.length];

    private float musicVolume = 0.5f;
    private boolean musicMuted;
    private float soundVolume = 1f;
    private boolean soundMuted;

    private long creatureHurtSoundId = -1L;
    private long nailSlashSoundId = -1L;
    private long enemyDeathSoundId = -1L;
    private long spellCastSoundId = -1L;
    private long soulGainSoundId = -1L;
    private long axeSoundId = -1L;
    private long stoneBreakSoundId = -1L;
    private long menuClickSoundId = -1L;
    private long footstepSoundId = -1L;
    private Sound currentFootstepSound;

    private long currentZoteVoiceId = -1L;
    private Sound currentZoteVoice;

    public AudioManager() {
        loadAudio();
    }

    private void loadAudio() {
        menuMusic = loadMusic(MENU_MUSIC_PATH, true);
        crossroadsMusic = loadMusic(CROSSROADS_MUSIC_PATH, true);
        greenpathMusic = loadMusic(GREENPATH_MUSIC_PATH, true);
        bossMusic = loadMusic(BOSS_MUSIC_PATH, true);
        victoryMusic = loadMusic(VICTORY_MUSIC_PATH, false);
        focusMusic = loadMusic(FOCUS_SOUND_PATH, true);

        falseKnightDeathSound = loadSound(FALSE_KNIGHT_DEATH_SOUND_PATH);
        creatureHurtSound = loadSound(CREATURE_HURT_SOUND_PATH);
        nailSlashSound = loadFirstSound("sounds/axe.mp3", "sounds/nail_slash.mp3", "sounds/hitting_with_nail.mp3");
        enemyDeathSound = loadFirstSound("sounds/enemy_death.mp3", "sounds/monster_kill.mp3");
        spellCastSound = loadFirstSound("sounds/shooting.mp3", "sounds/spell_cast.mp3");
        soulGainSound = loadFirstSound("sounds/soul_gain.mp3", "sounds/soul_absorb.mp3");
        axeSound = loadFirstSound("sounds/hitting_with_nail.mp3", AXE_SOUND_PATH);
        stoneBreakSound = loadSound(STONE_BREAK_SOUND_PATH);
        menuClickSound = loadSound(MENU_CLICK_SOUND_PATH);
        footstepStoneSound = loadSound(FOOTSTEP_STONE_PATH);
        footstepGrassSound = loadSound(FOOTSTEP_GRASS_PATH);
        deathStingerSound = loadSound(DEATH_STINGER_PATH);

        for (int i = 0; i < ZOTE_VOICE_PATHS.length; i++) {
            zoteVoiceSounds[i] = loadSound(ZOTE_VOICE_PATHS[i]);
        }
    }

    private Music loadMusic(String path, boolean looping) {
        FileHandle fileHandle = Gdx.files.internal(path);
        if (!fileHandle.exists()) {
            Gdx.app.log(LOG_TAG, "Missing music file: " + path);
            return null;
        }

        Music music = Gdx.audio.newMusic(fileHandle);
        music.setLooping(looping);
        music.setVolume(musicVolume);
        return music;
    }

    private Sound loadFirstSound(String... paths) {
        for (String path : paths) {
            FileHandle fileHandle = Gdx.files.internal(path);
            if (fileHandle.exists()) {
                return Gdx.audio.newSound(fileHandle);
            }
        }

        if (paths.length > 0) {
            Gdx.app.log(LOG_TAG, "Missing sound file: " + paths[0]);
        }
        return null;
    }

    private Sound loadSound(String path) {
        return loadFirstSound(path);
    }

    public void playMenuMusic() {
        switchBackgroundMusic(menuMusic);
    }

    public void playVictoryMusic() {
        switchBackgroundMusic(victoryMusic);
    }

    public void playAreaMusic(WorldArea area) {
        if (area == null) return;
        switch (area) {
            case FORGOTTEN_CROSSROADS -> switchBackgroundMusic(crossroadsMusic);
            case GREENPATH -> switchBackgroundMusic(greenpathMusic);
            case BOSS_ARENA -> switchBackgroundMusic(bossMusic);
        }
    }

    private void switchBackgroundMusic(Music nextMusic) {
        if (nextMusic == null) return;

        if (currentBackgroundMusic == nextMusic && fadingOutMusic == null) {
            nextMusic.setVolume(musicMuted ? 0f : musicVolume);
            if (!musicMuted && !nextMusic.isPlaying()) nextMusic.play();
            return;
        }

        if (fadingOutMusic != null && fadingOutMusic != nextMusic) {
            fadingOutMusic.stop();
        }

        Music previous = currentBackgroundMusic;
        currentBackgroundMusic = nextMusic;
        currentBackgroundMusic.stop();
        currentBackgroundMusic.setPosition(0f);
        currentBackgroundMusic.setVolume(previous == null || musicMuted ? musicVolume : 0f);

        if (!musicMuted) currentBackgroundMusic.play();

        if (previous == null || previous == nextMusic || musicMuted) {
            if (previous != null && previous != nextMusic) previous.stop();
            fadingOutMusic = null;
            musicTransitionProgress = 1f;
            currentBackgroundMusic.setVolume(musicMuted ? 0f : musicVolume);
            return;
        }

        fadingOutMusic = previous;
        musicTransitionProgress = 0f;
    }

    public void update(float delta) {
        if (fadingOutMusic == null || currentBackgroundMusic == null) return;

        musicTransitionProgress = Math.min(
            1f,
            musicTransitionProgress + Math.max(0f, delta) / MUSIC_TRANSITION_SECONDS
        );

        if (!musicMuted) {
            currentBackgroundMusic.setVolume(musicVolume * musicTransitionProgress);
            fadingOutMusic.setVolume(musicVolume * (1f - musicTransitionProgress));
        }

        if (musicTransitionProgress >= 1f) {
            fadingOutMusic.stop();
            fadingOutMusic = null;
            currentBackgroundMusic.setVolume(musicMuted ? 0f : musicVolume);
        }
    }

    public void stopMusic() {
        stopBackgroundMusic(menuMusic);
        stopBackgroundMusic(crossroadsMusic);
        stopBackgroundMusic(greenpathMusic);
        stopBackgroundMusic(bossMusic);
        stopBackgroundMusic(victoryMusic);
        currentBackgroundMusic = null;
        fadingOutMusic = null;
        musicTransitionProgress = 1f;
    }

    public void pauseMusic() {
        pauseBackgroundMusic(menuMusic);
        pauseBackgroundMusic(crossroadsMusic);
        pauseBackgroundMusic(greenpathMusic);
        pauseBackgroundMusic(bossMusic);
        pauseBackgroundMusic(victoryMusic);
    }

    private void stopBackgroundMusic(Music music) {
        if (music != null) music.stop();
    }

    private void pauseBackgroundMusic(Music music) {
        if (music != null && music.isPlaying()) music.pause();
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = clamp01(musicVolume);
        updateMusicVolumes();
    }

    private void updateMusicVolumes() {
        if (musicMuted) return;

        if (fadingOutMusic != null && currentBackgroundMusic != null) {
            currentBackgroundMusic.setVolume(musicVolume * musicTransitionProgress);
            fadingOutMusic.setVolume(musicVolume * (1f - musicTransitionProgress));
        } else if (currentBackgroundMusic != null) {
            currentBackgroundMusic.setVolume(musicVolume);
        }
    }

    public void setMusicMuted(boolean musicMuted) {
        this.musicMuted = musicMuted;
        if (musicMuted) {
            pauseMusic();
            return;
        }

        if (currentBackgroundMusic != null && !currentBackgroundMusic.isPlaying()) {
            currentBackgroundMusic.play();
        }
        if (fadingOutMusic != null && !fadingOutMusic.isPlaying()) {
            fadingOutMusic.play();
        }
        updateMusicVolumes();
    }

    public void playFalseKnightDeath() { playOneShot(falseKnightDeathSound, 1f); }
    public void playDeathStinger() { playOneShot(deathStingerSound, 0.78f); }

    public void playCreatureHurt() {
        creatureHurtSoundId = playReplacingSound(creatureHurtSound, creatureHurtSoundId, 0.72f);
    }

    public void playNailSlash() {
        nailSlashSoundId = playReplacingSound(nailSlashSound, nailSlashSoundId, 0.78f);
    }

    public void playEnemyDeath() {
        enemyDeathSoundId = playReplacingSound(enemyDeathSound, enemyDeathSoundId, 0.86f);
    }

    public void playSpellCast() { playVengefulSpiritCast(); }

    public void playVengefulSpiritCast() {
        spellCastSoundId = playReplacingSound(spellCastSound, spellCastSoundId, 0.80f, 1.08f);
    }

    public void playHowlingWraithsCast() {
        spellCastSoundId = playReplacingSound(spellCastSound, spellCastSoundId, 0.88f, 0.82f);
    }

    public void playSoulGain() {
        soulGainSoundId = playReplacingSound(soulGainSound, soulGainSoundId, 0.58f);
    }

    public void playAxeImpact() {
        axeSoundId = playReplacingSound(axeSound, axeSoundId, 0.82f);
    }

    public void playStoneBreak() {
        stoneBreakSoundId = playReplacingSound(
            stoneBreakSound,
            stoneBreakSoundId,
            0.88f
        );
    }

    public void playMenuClick() {
        menuClickSoundId = playReplacingSound(menuClickSound, menuClickSoundId, 0.42f);
    }

    public void playFootstep(WorldArea area) {
        Sound nextSound = area == WorldArea.GREENPATH ? footstepGrassSound : footstepStoneSound;
        if (nextSound == null || soundMuted) return;

        if (currentFootstepSound != null && footstepSoundId >= 0L) {
            currentFootstepSound.stop(footstepSoundId);
        }

        currentFootstepSound = nextSound;
        footstepSoundId = nextSound.play(soundVolume * 0.30f, MathUtils.random(0.96f, 1.05f), 0f);
    }

    public void playTestSfx() {
        if (soulGainSound != null) playSoulGain();
        else playCreatureHurt();
    }

    public void startFocusSound() {
        if (focusMusic == null || soundMuted) return;
        previewingFocusSound = false;
        focusPlaying = true;
        focusMusic.stop();
        focusMusic.setLooping(true);
        focusMusic.setVolume(soundVolume * 0.50f);
        focusMusic.play();
    }

    public void stopFocusSound() {
        if (focusMusic != null) focusMusic.stop();
        focusPlaying = false;
        previewingFocusSound = false;
    }

    public void playRandomZoteVoice() {
        if (soundMuted) return;
        stopCurrentZoteVoice();

        int startIndex = MathUtils.random(zoteVoiceSounds.length - 1);
        for (int offset = 0; offset < zoteVoiceSounds.length; offset++) {
            int index = (startIndex + offset) % zoteVoiceSounds.length;
            Sound sound = zoteVoiceSounds[index];
            if (sound == null) continue;

            currentZoteVoice = sound;
            currentZoteVoiceId = sound.play(soundVolume * 0.76f);
            return;
        }
    }

    public void stopCurrentZoteVoice() {
        if (currentZoteVoice != null && currentZoteVoiceId >= 0L) {
            currentZoteVoice.stop(currentZoteVoiceId);
        }
        currentZoteVoice = null;
        currentZoteVoiceId = -1L;
    }

    public void startSfxPreview() {
        if (focusMusic == null || soundMuted) return;
        stopFocusSound();
        previewingFocusSound = true;
        focusMusic.setLooping(true);
        focusMusic.setVolume(soundVolume * 0.38f);
        focusMusic.play();
    }

    public void updateSfxPreviewVolume() {
        if (focusMusic == null || (!previewingFocusSound && !focusPlaying)) return;
        if (soundMuted) {
            stopFocusSound();
            return;
        }
        focusMusic.setVolume(soundVolume * (previewingFocusSound ? 0.38f : 0.50f));
    }

    public void stopSfxPreview() {
        if (previewingFocusSound) stopFocusSound();
    }

    public void stopAllSounds() {
        stopFocusSound();
        stopCurrentZoteVoice();
        stopTrackedSound(creatureHurtSound, creatureHurtSoundId);
        stopTrackedSound(nailSlashSound, nailSlashSoundId);
        stopTrackedSound(enemyDeathSound, enemyDeathSoundId);
        stopTrackedSound(spellCastSound, spellCastSoundId);
        stopTrackedSound(soulGainSound, soulGainSoundId);
        stopTrackedSound(axeSound, axeSoundId);
        stopTrackedSound(stoneBreakSound, stoneBreakSoundId);
        stopTrackedSound(menuClickSound, menuClickSoundId);
        if (currentFootstepSound != null && footstepSoundId >= 0L) currentFootstepSound.stop(footstepSoundId);

        creatureHurtSoundId = -1L;
        nailSlashSoundId = -1L;
        enemyDeathSoundId = -1L;
        spellCastSoundId = -1L;
        soulGainSoundId = -1L;
        axeSoundId = -1L;
        stoneBreakSoundId = -1L;
        menuClickSoundId = -1L;
        footstepSoundId = -1L;
        currentFootstepSound = null;
    }

    public void setSoundVolume(float soundVolume) {
        this.soundVolume = clamp01(soundVolume);
        updateSfxPreviewVolume();
    }

    public void setSoundMuted(boolean soundMuted) {
        this.soundMuted = soundMuted;
        if (soundMuted) stopAllSounds();
    }

    public float getSoundVolume() { return soundVolume; }
    public boolean isSoundMuted() { return soundMuted; }

    public void dispose() {
        stopAllSounds();
        stopMusic();

        disposeMusic(menuMusic);
        disposeMusic(crossroadsMusic);
        disposeMusic(greenpathMusic);
        disposeMusic(bossMusic);
        disposeMusic(victoryMusic);
        disposeMusic(focusMusic);

        disposeSound(falseKnightDeathSound);
        disposeSound(creatureHurtSound);
        disposeSound(nailSlashSound);
        disposeSound(enemyDeathSound);
        disposeSound(spellCastSound);
        disposeSound(soulGainSound);
        disposeSound(axeSound);
        disposeSound(stoneBreakSound);
        disposeSound(menuClickSound);
        disposeSound(footstepStoneSound);
        disposeSound(footstepGrassSound);
        disposeSound(deathStingerSound);
        for (Sound sound : zoteVoiceSounds) disposeSound(sound);
    }

    private void playOneShot(Sound sound, float gain) {
        if (sound == null || soundMuted) return;
        sound.play(soundVolume * clamp01(gain));
    }

    private long playReplacingSound(Sound sound, long previousId, float gain) {
        if (sound == null || soundMuted) return -1L;
        if (previousId >= 0L) sound.stop(previousId);
        return sound.play(soundVolume * clamp01(gain));
    }

    private long playReplacingSound(Sound sound, long previousId, float gain, float pitch) {
        if (sound == null || soundMuted) return -1L;
        if (previousId >= 0L) sound.stop(previousId);
        return sound.play(
            soundVolume * clamp01(gain),
            Math.max(0.5f, Math.min(2f, pitch)),
            0f
        );
    }

    private void stopTrackedSound(Sound sound, long soundId) {
        if (sound != null && soundId >= 0L) sound.stop(soundId);
    }

    private void disposeMusic(Music music) { if (music != null) music.dispose(); }
    private void disposeSound(Sound sound) { if (sound != null) sound.dispose(); }
    private float clamp01(float value) { return Math.max(0f, Math.min(1f, value)); }
}
