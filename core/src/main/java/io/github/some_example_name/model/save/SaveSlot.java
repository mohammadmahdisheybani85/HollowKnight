package io.github.some_example_name.model.save;

import io.github.some_example_name.model.game.WorldArea;
import io.github.some_example_name.model.player.PlayerStats;

import java.util.ArrayList;
import java.util.List;

public class SaveSlot {
    private static final String EMPTY_TEXT = "Empty";
    private static final float DEFAULT_PLAYER_X = 120f;
    private static final float DEFAULT_PLAYER_Y = 180f;

    private final int slotNumber;

    private boolean occupied;
    private boolean gameCompleted;

    private String areaName = EMPTY_TEXT;
    private String worldAreaName = WorldArea.FORGOTTEN_CROSSROADS.name();

    private int playerHealth;
    private int playerSoul;
    private int deathCount;

    private float playerX = DEFAULT_PLAYER_X;
    private float playerY = DEFAULT_PLAYER_Y;
    private float elapsedTimeSeconds;

    private List<String> equippedCharms = new ArrayList<>();
    private List<String> unlockedCharms = new ArrayList<>();
    private List<String> defeatedEnemyNames = new ArrayList<>();
    private List<String> unlockedAchievementNames = new ArrayList<>();

    private boolean secretWallBroken;
    private boolean secretPickupCollected;

    public SaveSlot(int slotNumber) {
        if (slotNumber < 1) {
            throw new IllegalArgumentException("Slot number must be positive.");
        }
        this.slotNumber = slotNumber;
    }

    public int getSlotNumber() { return slotNumber; }
    public boolean isOccupied() { return occupied; }
    public boolean isGameCompleted() { return gameCompleted; }
    public String getAreaName() { return areaName; }
    public int getPlayerHealth() { return playerHealth; }
    public int getPlayerSoul() { return playerSoul; }
    public int getDeathCount() { return deathCount; }
    public float getPlayerX() { return playerX; }
    public float getPlayerY() { return playerY; }
    public float getElapsedTimeSeconds() { return elapsedTimeSeconds; }
    public boolean isSecretWallBroken() { return secretWallBroken; }
    public boolean isSecretPickupCollected() { return secretPickupCollected; }

    public WorldArea getWorldArea() {
        try {
            return WorldArea.valueOf(worldAreaName);
        } catch (IllegalArgumentException exception) {
            return WorldArea.FORGOTTEN_CROSSROADS;
        }
    }

    public List<String> getEquippedCharms() { return new ArrayList<>(equippedCharms); }
    public List<String> getUnlockedCharms() { return new ArrayList<>(unlockedCharms); }
    public List<String> getDefeatedEnemyNames() { return new ArrayList<>(defeatedEnemyNames); }
    public List<String> getUnlockedAchievementNames() { return new ArrayList<>(unlockedAchievementNames); }

    public void setUnlockedAchievementNames(List<String> unlockedAchievementNames) {
        this.unlockedAchievementNames = copyList(unlockedAchievementNames);
    }

    public void startNewGame() {
        occupied = true;
        gameCompleted = false;
        areaName = WorldArea.FORGOTTEN_CROSSROADS.getDisplayName();
        worldAreaName = WorldArea.FORGOTTEN_CROSSROADS.name();
        playerHealth = PlayerStats.BASE_MAX_HEALTH;
        playerSoul = 0;
        deathCount = 0;
        playerX = DEFAULT_PLAYER_X;
        playerY = DEFAULT_PLAYER_Y;
        elapsedTimeSeconds = 0f;
        equippedCharms.clear();
        unlockedCharms.clear();
        defeatedEnemyNames.clear();
        unlockedAchievementNames.clear();
        secretWallBroken = false;
        secretPickupCollected = false;
    }

    public void saveGameProgress(
        PlayerStats playerStats,
        List<String> equippedCharmNames,
        List<String> unlockedCharmNames,
        WorldArea currentArea,
        List<String> defeatedEnemyNames,
        float playerX,
        float playerY,
        float elapsedTimeSeconds,
        boolean secretWallBroken,
        boolean secretPickupCollected
    ) {
        occupied = true;
        areaName = currentArea.getDisplayName();
        worldAreaName = currentArea.name();
        playerHealth = playerStats.getHealth();
        playerSoul = playerStats.getSoul();
        deathCount = playerStats.getDeathCount();
        this.playerX = playerX;
        this.playerY = playerY;
        this.elapsedTimeSeconds = elapsedTimeSeconds;
        equippedCharms = new ArrayList<>(equippedCharmNames);
        unlockedCharms = new ArrayList<>(unlockedCharmNames);
        this.defeatedEnemyNames = new ArrayList<>(defeatedEnemyNames);
        this.secretWallBroken = secretWallBroken;
        this.secretPickupCollected = secretPickupCollected;
    }

    public void markGameCompleted() { gameCompleted = true; }

    public void clear() {
        occupied = false;
        gameCompleted = false;
        areaName = EMPTY_TEXT;
        worldAreaName = WorldArea.FORGOTTEN_CROSSROADS.name();
        playerHealth = 0;
        playerSoul = 0;
        deathCount = 0;
        playerX = DEFAULT_PLAYER_X;
        playerY = DEFAULT_PLAYER_Y;
        elapsedTimeSeconds = 0f;
        equippedCharms.clear();
        unlockedCharms.clear();
        defeatedEnemyNames.clear();
        unlockedAchievementNames.clear();
        secretWallBroken = false;
        secretPickupCollected = false;
    }

    public String getDisplayText() {
        if (!occupied) return "Save Slot " + slotNumber + " - Empty";
        String statusText = gameCompleted ? "COMPLETED" : areaName;
        return "Save Slot " + slotNumber
            + " - " + statusText
            + " | HP: " + playerHealth
            + " | Soul: " + playerSoul;
    }

    public SaveSlotData toData() {
        SaveSlotData data = new SaveSlotData();
        data.slotNumber = slotNumber;
        data.occupied = occupied;
        data.gameCompleted = gameCompleted;
        data.areaName = areaName;
        data.worldAreaName = worldAreaName;
        data.playerHealth = playerHealth;
        data.playerSoul = playerSoul;
        data.deathCount = deathCount;
        data.playerX = playerX;
        data.playerY = playerY;
        data.elapsedTimeSeconds = elapsedTimeSeconds;
        data.equippedCharms = new ArrayList<>(equippedCharms);
        data.unlockedCharms = new ArrayList<>(unlockedCharms);
        data.defeatedEnemyNames = new ArrayList<>(defeatedEnemyNames);
        data.unlockedAchievementNames = new ArrayList<>(unlockedAchievementNames);
        data.secretWallBroken = secretWallBroken;
        data.secretPickupCollected = secretPickupCollected;
        return data;
    }

    public void loadFromData(SaveSlotData data) {
        if (data == null) return;

        occupied = data.occupied;
        gameCompleted = data.gameCompleted;
        areaName = data.areaName == null ? EMPTY_TEXT : data.areaName;
        worldAreaName = data.worldAreaName == null
            ? WorldArea.FORGOTTEN_CROSSROADS.name()
            : data.worldAreaName;
        playerHealth = data.playerHealth;
        playerSoul = data.playerSoul;
        deathCount = Math.max(0, data.deathCount);
        loadPlayerPosition(data);
        elapsedTimeSeconds = Math.max(0f, data.elapsedTimeSeconds);
        equippedCharms = copyList(data.equippedCharms);
        unlockedCharms = copyList(data.unlockedCharms);
        defeatedEnemyNames = copyList(data.defeatedEnemyNames);
        unlockedAchievementNames = copyList(data.unlockedAchievementNames);
        secretWallBroken = data.secretWallBroken;
        secretPickupCollected = data.secretPickupCollected;


        if (unlockedCharms.isEmpty() && equippedCharms.contains("VOID_HEART")) {
            unlockedCharms.add("VOID_HEART");
            secretWallBroken = true;
            secretPickupCollected = true;
        }
    }

    private List<String> copyList(List<String> source) {
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }

    private void loadPlayerPosition(SaveSlotData data) {
        boolean oldSaveWithoutPosition = data.playerX == 0f && data.playerY == 0f;
        playerX = oldSaveWithoutPosition ? DEFAULT_PLAYER_X : data.playerX;
        playerY = oldSaveWithoutPosition ? DEFAULT_PLAYER_Y : data.playerY;
    }
}
