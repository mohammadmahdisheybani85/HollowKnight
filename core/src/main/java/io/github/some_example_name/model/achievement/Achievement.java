package io.github.some_example_name.model.achievement;

public class Achievement {
    private final AchievementType type;
    private boolean unlocked;

    public Achievement(AchievementType type) {
        this.type = type;
    }

    public AchievementType getType() {
        return type;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public boolean unlock() {
        if (unlocked) {
            return false;
        }

        unlocked = true;
        return true;
    }

    public String getDisplayText() {
        if (unlocked) {
            return "[UNLOCKED] " + type.getTitle() + " - " + type.getDescription();
        }

        return "[LOCKED] " + type.getTitle();
    }

    public void unlockSilently() {
        unlocked = true;
    }

    public void reset() {
        unlocked = false;
    }
}
