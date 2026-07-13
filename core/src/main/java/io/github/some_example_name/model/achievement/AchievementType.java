package io.github.some_example_name.model.achievement;

public enum AchievementType {
    COMPLETION("Completion", "Finish the game."),
    SPEEDRUN("Speedrun", "Finish the game in a limited time."),
    TRUE_HUNTER("True Hunter", "Defeat every enemy type."),
    DEFEAT_FALSE_KNIGHT("Defeat False Knight", "Defeat the False Knight boss."),
    SOUL_MASTER("Soul Master", "Fill the Soul orb completely.");

    private final String title;
    private final String description;

    AchievementType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
