package io.github.some_example_name.model.inventory;

public enum CharmType {
    SOUL_CATCHER(
        "Soul Catcher",
        "Gain more Soul whenever the Nail hits an enemy.",
        1
    ),
    DASH_MASTER(
        "Dashmaster",
        "Reduces dash cooldown.",
        1
    ),
    UNBREAKABLE_STRENGTH(
        "Unbreakable Strength",
        "Increases Nail damage by fifty percent.",
        1
    ),
    QUICK_SLASH(
        "Quick Slash",
        "Reduces the time between Nail attacks.",
        1
    ),
    QUICK_FOCUS(
        "Quick Focus",
        "Focuses Soul faster when healing.",
        1
    ),
    HEAVY_BLOW(
        "Heavy Blow",
        "Greatly increases enemy knockback.",
        1
    ),
    SHARP_SHADOW(
        "Sharp Shadow",
        "Dash through enemies, deal damage, and travel farther.",
        1
    ),
    VOID_HEART(
        "Void Heart",
        "Increases spell damage by fifty percent.",
        1
    ),
    STEADY_BODY(
        "Steady Body",
        "Reduces knockback after taking damage.",
        1
    ),
    VITALITY_CHARM(
        "Vitality Charm",
        "Adds one extra health mask.",
        1
    );

    private final String title;
    private final String description;
    private final int notchCost;

    CharmType(String title, String description, int notchCost) {
        this.title = title;
        this.description = description;
        this.notchCost = notchCost;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getNotchCost() { return notchCost; }
}
