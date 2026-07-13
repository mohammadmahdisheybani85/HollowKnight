package io.github.some_example_name.controller.achievement;

import io.github.some_example_name.model.achievement.Achievement;

public interface AchievementUnlockListener {
    void onAchievementUnlocked(Achievement achievement);
}
