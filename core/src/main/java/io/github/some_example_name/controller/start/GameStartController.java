package io.github.some_example_name.controller.start;

import io.github.some_example_name.controller.inventory.InventoryController;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.WorldArea;
import io.github.some_example_name.model.player.PlayerStats;
import io.github.some_example_name.model.save.SaveSlot;
import io.github.some_example_name.repository.save.SaveRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameStartController {
    private static final float DEFAULT_PLAYER_X = 120f;
    private static final float DEFAULT_PLAYER_Y = 180f;

    private final SaveRepository saveRepository;
    private final List<SaveSlot> saveSlots;

    private int currentSlotNumber = 1;

    private WorldArea loadedArea = WorldArea.FORGOTTEN_CROSSROADS;
    private float loadedPlayerX = DEFAULT_PLAYER_X;
    private float loadedPlayerY = DEFAULT_PLAYER_Y;
    private float loadedElapsedTimeSeconds;
    private List<String> loadedDefeatedEnemyNames = new ArrayList<>();
    private List<String> loadedUnlockedCharmNames = new ArrayList<>();
    private List<String> loadedUnlockedAchievementNames = new ArrayList<>();
    private boolean loadedSecretWallBroken;
    private boolean loadedSecretPickupCollected;

    public GameStartController() {
        this(new SaveRepository());
    }

    public GameStartController(SaveRepository saveRepository) {
        this.saveRepository = saveRepository;
        this.saveSlots = saveRepository.loadSlots();
        this.currentSlotNumber = saveRepository.getLastLoadedSlotNumber();
        loadSnapshotFromCurrentSlot();
    }

    public List<SaveSlot> getSaveSlots() {
        return Collections.unmodifiableList(saveSlots);
    }

    public SaveSlot getSlot(int slotNumber) {
        if (slotNumber < 1 || slotNumber > saveSlots.size()) {
            throw new IllegalArgumentException("Invalid save slot number: " + slotNumber);
        }

        return saveSlots.get(slotNumber - 1);
    }

    public int getCurrentSlotNumber() {
        return currentSlotNumber;
    }

    public List<String> getCurrentSlotAchievementNames() {
        return getSlot(currentSlotNumber).getUnlockedAchievementNames();
    }

    public WorldArea getLoadedArea() {
        return loadedArea;
    }

    public float getLoadedPlayerX() {
        return loadedPlayerX;
    }

    public float getLoadedPlayerY() {
        return loadedPlayerY;
    }

    public float getLoadedElapsedTimeSeconds() {
        return loadedElapsedTimeSeconds;
    }

    public List<String> getLoadedDefeatedEnemyNames() {
        return new ArrayList<>(loadedDefeatedEnemyNames);
    }

    public List<String> getLoadedUnlockedCharmNames() {
        return new ArrayList<>(loadedUnlockedCharmNames);
    }

    public List<String> getLoadedUnlockedAchievementNames() {
        return new ArrayList<>(loadedUnlockedAchievementNames);
    }

    public boolean isLoadedSecretWallBroken() {
        return loadedSecretWallBroken;
    }

    public boolean isLoadedSecretPickupCollected() {
        return loadedSecretPickupCollected;
    }

    public boolean canLoadSlot(int slotNumber) {
        return getSlot(slotNumber).isOccupied();
    }

    public boolean canCreateNewGameInSlot(int slotNumber) {
        return !getSlot(slotNumber).isOccupied();
    }

    public boolean canDeleteSlot(int slotNumber) {
        return getSlot(slotNumber).isOccupied();
    }

    public void startNewGameInSlot(
        int slotNumber,
        PlayerStats playerStats,
        InventoryController inventoryController
    ) {
        SaveSlot slot = getSlot(slotNumber);

        if (slot.isOccupied()) {
            throw new IllegalStateException("Save slot is already occupied.");
        }

        currentSlotNumber = slotNumber;

        playerStats.startNewGame();
        inventoryController.resetForNewGame();
        slot.startNewGame();

        loadedArea = WorldArea.FORGOTTEN_CROSSROADS;
        loadedPlayerX = DEFAULT_PLAYER_X;
        loadedPlayerY = DEFAULT_PLAYER_Y;
        loadedElapsedTimeSeconds = 0f;
        loadedDefeatedEnemyNames = new ArrayList<>();
        loadedUnlockedCharmNames = new ArrayList<>();
        loadedUnlockedAchievementNames = new ArrayList<>();
        loadedSecretWallBroken = false;
        loadedSecretPickupCollected = false;

        saveRepository.saveSlots(saveSlots, currentSlotNumber);
    }

    public void loadGameFromSlot(
        int slotNumber,
        PlayerStats playerStats,
        InventoryController inventoryController
    ) {
        SaveSlot slot = getSlot(slotNumber);

        if (!slot.isOccupied()) {
            throw new IllegalStateException("Cannot load an empty save slot.");
        }

        currentSlotNumber = slotNumber;

        playerStats.loadFromSave(slot.getPlayerHealth(), slot.getPlayerSoul());
        playerStats.setDeathCount(slot.getDeathCount());

        inventoryController.loadCharmProgress(
            slot.getUnlockedCharms(),
            slot.getEquippedCharms()
        );

        loadSnapshotFromCurrentSlot();
        saveRepository.saveSlots(saveSlots, currentSlotNumber);
    }

    public void saveCurrentAchievements(List<String> unlockedAchievementNames) {
        SaveSlot currentSlot = getSlot(currentSlotNumber);

        if (!currentSlot.isOccupied()) {
            return;
        }

        currentSlot.setUnlockedAchievementNames(unlockedAchievementNames);
        loadedUnlockedAchievementNames = currentSlot.getUnlockedAchievementNames();
        saveRepository.saveSlots(saveSlots, currentSlotNumber);
    }

    public void saveCurrentGame(
        PlayerStats playerStats,
        InventoryController inventoryController,
        GameWorld world,
        float elapsedTimeSeconds
    ) {
        SaveSlot currentSlot = getSlot(currentSlotNumber);

        currentSlot.saveGameProgress(
            playerStats,
            inventoryController.getEquippedCharmNames(),
            inventoryController.getUnlockedCharmNames(),
            world.getCurrentArea(),
            world.getDefeatedEnemyNames(),
            world.getPlayer().getX(),
            world.getPlayer().getY(),
            elapsedTimeSeconds,
            world.isSecretWallBroken(),
            world.isSecretPickupCollected()
        );

        loadSnapshotFromCurrentSlot();
        saveRepository.saveSlots(saveSlots, currentSlotNumber);
    }

    public void saveCompletedGame(
        PlayerStats playerStats,
        InventoryController inventoryController,
        GameWorld world,
        float elapsedTimeSeconds
    ) {
        SaveSlot currentSlot = getSlot(currentSlotNumber);

        currentSlot.saveGameProgress(
            playerStats,
            inventoryController.getEquippedCharmNames(),
            inventoryController.getUnlockedCharmNames(),
            world.getCurrentArea(),
            world.getDefeatedEnemyNames(),
            world.getPlayer().getX(),
            world.getPlayer().getY(),
            elapsedTimeSeconds,
            world.isSecretWallBroken(),
            world.isSecretPickupCollected()
        );

        currentSlot.markGameCompleted();

        loadSnapshotFromCurrentSlot();
        saveRepository.saveSlots(saveSlots, currentSlotNumber);
    }

    public void restartCurrentGame(
        PlayerStats playerStats,
        InventoryController inventoryController
    ) {
        playerStats.startNewGame();
        inventoryController.resetForNewGame();

        SaveSlot currentSlot = getSlot(currentSlotNumber);
        currentSlot.startNewGame();
        loadSnapshotFromCurrentSlot();
        saveRepository.saveSlots(saveSlots, currentSlotNumber);
    }

    public void deleteSlot(int slotNumber) {
        SaveSlot slot = getSlot(slotNumber);

        if (!slot.isOccupied()) {
            throw new IllegalStateException("Cannot delete an empty save slot.");
        }

        slot.clear();

        if (slotNumber == currentSlotNumber) {
            currentSlotNumber = findBestCurrentSlotNumber();
        }

        loadSnapshotFromCurrentSlot();
        saveRepository.saveSlots(saveSlots, currentSlotNumber);
    }

    private int findBestCurrentSlotNumber() {
        for (SaveSlot slot : saveSlots) {
            if (slot.isOccupied()) {
                return slot.getSlotNumber();
            }
        }

        return 1;
    }

    private void loadSnapshotFromCurrentSlot() {
        SaveSlot slot = getSlot(currentSlotNumber);

        if (!slot.isOccupied()) {
            loadedArea = WorldArea.FORGOTTEN_CROSSROADS;
            loadedPlayerX = DEFAULT_PLAYER_X;
            loadedPlayerY = DEFAULT_PLAYER_Y;
            loadedElapsedTimeSeconds = 0f;
            loadedDefeatedEnemyNames = new ArrayList<>();
            loadedUnlockedCharmNames = new ArrayList<>();
            loadedUnlockedAchievementNames = new ArrayList<>();
            loadedSecretWallBroken = false;
            loadedSecretPickupCollected = false;
            return;
        }

        loadedArea = slot.getWorldArea();
        loadedPlayerX = slot.getPlayerX();
        loadedPlayerY = slot.getPlayerY();
        loadedElapsedTimeSeconds = slot.getElapsedTimeSeconds();
        loadedDefeatedEnemyNames = slot.getDefeatedEnemyNames();
        loadedUnlockedCharmNames = slot.getUnlockedCharms();
        loadedUnlockedAchievementNames = slot.getUnlockedAchievementNames();
        loadedSecretWallBroken = slot.isSecretWallBroken();
        loadedSecretPickupCollected = slot.isSecretPickupCollected();
    }
}
