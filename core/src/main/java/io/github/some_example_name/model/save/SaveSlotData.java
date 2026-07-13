package io.github.some_example_name.model.save;

import java.util.ArrayList;
import java.util.List;

public class SaveSlotData {
    public int slotNumber;
    public boolean occupied;
    public boolean gameCompleted;

    public String areaName;
    public String worldAreaName;

    public int playerHealth;
    public int playerSoul;
    public int deathCount;

    public float playerX;
    public float playerY;
    public float elapsedTimeSeconds;

    public List<String> equippedCharms = new ArrayList<>();
    public List<String> unlockedCharms = new ArrayList<>();
    public List<String> defeatedEnemyNames = new ArrayList<>();
    public List<String> unlockedAchievementNames = new ArrayList<>();

    public boolean secretWallBroken;
    public boolean secretPickupCollected;

    public SaveSlotData() {
    }
}
