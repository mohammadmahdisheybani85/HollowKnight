package io.github.some_example_name.controller.achievement;

import io.github.some_example_name.model.achievement.Achievement;
import io.github.some_example_name.model.achievement.AchievementType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AchievementController {
    private final Map<AchievementType, Achievement> achievements =
        new EnumMap<>(AchievementType.class);

    private final List<AchievementUnlockListener> listeners =
        new ArrayList<>();

    private Runnable saveCallback;

    public AchievementController() {
        for (AchievementType type : AchievementType.values()) {
            achievements.put(type, new Achievement(type));
        }
    }

    public void setSaveCallback(Runnable saveCallback) {
        this.saveCallback = saveCallback;
    }

    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements.values());
    }

    public void unlock(AchievementType type) {
        Achievement achievement = achievements.get(type);

        if (achievement == null) {
            return;
        }

        boolean newlyUnlocked = achievement.unlock();

        if (!newlyUnlocked) {
            return;
        }

        saveCurrentSlotAchievements();
        notifyAchievementUnlocked(achievement);
    }

    public boolean isUnlocked(AchievementType type) {
        Achievement achievement = achievements.get(type);
        return achievement != null && achievement.isUnlocked();
    }

    public void addListener(AchievementUnlockListener listener) {
        if (listener == null) {
            return;
        }

        listeners.add(listener);
    }

    public void removeListener(AchievementUnlockListener listener) {
        listeners.remove(listener);
    }

    public List<String> getUnlockedAchievementNames() {
        List<String> unlockedNames = new ArrayList<>();

        for (Achievement achievement : achievements.values()) {
            if (achievement.isUnlocked()) {
                unlockedNames.add(achievement.getType().name());
            }
        }

        return unlockedNames;
    }

    public void loadFromSave(List<String> unlockedAchievementNames) {
        resetAchievements();

        if (unlockedAchievementNames == null) {
            return;
        }

        for (String achievementName : unlockedAchievementNames) {
            unlockAchievementFromSave(achievementName);
        }
    }

    public void resetForNewGame() {
        resetAchievements();
        saveCurrentSlotAchievements();
    }

    private void unlockAchievementFromSave(String achievementName) {
        if (achievementName == null || achievementName.isBlank()) {
            return;
        }

        try {
            AchievementType type = AchievementType.valueOf(achievementName);
            Achievement achievement = achievements.get(type);

            if (achievement != null) {
                achievement.unlockSilently();
            }
        } catch (IllegalArgumentException ignored) {

        }
    }

    private void saveCurrentSlotAchievements() {
        if (saveCallback != null) {
            saveCallback.run();
        }
    }

    private void resetAchievements() {
        for (Achievement achievement : achievements.values()) {
            achievement.reset();
        }
    }

    private void notifyAchievementUnlocked(Achievement achievement) {
        for (AchievementUnlockListener listener : listeners) {
            listener.onAchievementUnlocked(achievement);
        }
    }
}
