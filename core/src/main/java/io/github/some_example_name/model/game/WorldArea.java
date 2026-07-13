package io.github.some_example_name.model.game;

public enum WorldArea {
    FORGOTTEN_CROSSROADS("Forgotten Crossroads"),
    GREENPATH("Greenpath"),
    BOSS_ARENA("False Knight Arena");

    private final String displayName;

    WorldArea(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
