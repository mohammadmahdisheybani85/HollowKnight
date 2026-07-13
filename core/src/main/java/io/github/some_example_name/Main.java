package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.controller.achievement.AchievementController;
import io.github.some_example_name.controller.achievement.EnemyKillTracker;
import io.github.some_example_name.controller.audio.AudioManager;
import io.github.some_example_name.controller.cheat.CheatCodeController;
import io.github.some_example_name.controller.input.KeyBindingController;
import io.github.some_example_name.controller.inventory.InventoryController;
import io.github.some_example_name.controller.screen.ScreenManager;
import io.github.some_example_name.controller.settings.SettingsController;
import io.github.some_example_name.controller.start.GameStartController;
import io.github.some_example_name.model.input.KeyBindingSettings;
import io.github.some_example_name.model.inventory.Inventory;
import io.github.some_example_name.model.player.PlayerStats;
import io.github.some_example_name.model.settings.GameSettings;
import io.github.some_example_name.view.screen.ScreenType;
import io.github.some_example_name.view.ui.SoftwareCursor;
import io.github.some_example_name.util.L10n;

public class Main extends Game {
    private SpriteBatch batch;
    private ScreenManager screenManager;

    private GameSettings gameSettings;
    private SettingsController settingsController;

    private GameStartController gameStartController;
    private AchievementController achievementController;

    private KeyBindingSettings keyBindingSettings;
    private KeyBindingController keyBindingController;

    private PlayerStats playerStats;
    private CheatCodeController cheatCodeController;

    private Inventory inventory;
    private InventoryController inventoryController;

    private EnemyKillTracker enemyKillTracker;
    private AudioManager audioManager;

    private float lastRunElapsedTimeSeconds;
    private int lastRunEnemyKills;
    private int lastRunDeathCount;

    private SoftwareCursor softwareCursor;


    @Override
    public void create() {
        batch = new SpriteBatch();


        gameSettings = new GameSettings();
        settingsController = new SettingsController(gameSettings);
        L10n.initialize(settingsController);

        gameStartController = new GameStartController();
        achievementController = new AchievementController();
        achievementController.setSaveCallback(
            () -> gameStartController.saveCurrentAchievements(
                achievementController.getUnlockedAchievementNames()
            )
        );
        achievementController.loadFromSave(
            gameStartController.getCurrentSlotAchievementNames()
        );

        keyBindingSettings = new KeyBindingSettings();
        keyBindingController = new KeyBindingController(keyBindingSettings);

        playerStats = new PlayerStats();
        cheatCodeController = new CheatCodeController(playerStats);

        inventory = new Inventory();
        inventoryController = new InventoryController(inventory);

        enemyKillTracker = new EnemyKillTracker();

        audioManager = new AudioManager();
        applyInitialAudioSettings();

        screenManager = new ScreenManager(this);
        screenManager.show(ScreenType.MAIN_MENU);
        softwareCursor = new SoftwareCursor();
    }

    private void applyInitialAudioSettings() {
        audioManager.setMusicVolume(settingsController.getMusicVolume());
        audioManager.setMusicMuted(settingsController.isMusicMuted());

        audioManager.setSoundVolume(settingsController.getSfxVolume());
        audioManager.setSoundMuted(settingsController.isSfxMuted());
    }

    public EnemyKillTracker getEnemyKillTracker() {
        return enemyKillTracker;
    }

    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    public CheatCodeController getCheatCodeController() {
        return cheatCodeController;
    }

    public KeyBindingController getKeyBindingController() {
        return keyBindingController;
    }

    public AchievementController getAchievementController() {
        return achievementController;
    }

    public GameStartController getGameStartController() {
        return gameStartController;
    }

    public SettingsController getSettingsController() {
        return settingsController;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public InventoryController getInventoryController() {
        return inventoryController;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public void saveLastRunStats(float elapsedTimeSeconds, int enemyKills, int deathCount) {
        this.lastRunElapsedTimeSeconds = elapsedTimeSeconds;
        this.lastRunEnemyKills = enemyKills;
        this.lastRunDeathCount = deathCount;
    }

    public float getLastRunElapsedTimeSeconds() {
        return lastRunElapsedTimeSeconds;
    }

    public int getLastRunEnemyKills() {
        return lastRunEnemyKills;
    }

    public int getLastRunDeathCount() {
        return lastRunDeathCount;
    }

    @Override
    public void dispose() {
        Screen currentScreen = getScreen();

        if (currentScreen != null) {
            currentScreen.dispose();
        }

        if (batch != null) {
            batch.dispose();
        }

        if (audioManager != null) {
            audioManager.dispose();
        }

        if (softwareCursor != null) {
            softwareCursor.dispose();
            softwareCursor = null;
        }
    }

    @Override
    public void render() {

        super.render();

        if (softwareCursor != null) {
            softwareCursor.render(
                Gdx.graphics.getDeltaTime()
            );
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (softwareCursor != null) {
            softwareCursor.resize(width, height);
        }
    }
}
