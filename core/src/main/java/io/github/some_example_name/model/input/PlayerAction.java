package io.github.some_example_name.model.input;

public enum PlayerAction {
    MOVE_LEFT("Move Left"),
    MOVE_RIGHT("Move Right"),
    LOOK_UP("Look Up / Up Spell"),
    LOOK_DOWN("Look Down / Pogo"),
    JUMP("Jump"),
    DASH("Dash"),
    ATTACK("Attack"),
    CAST_SPELL("Cast Spell"),
    FOCUS("Focus / Heal"),
    INTERACT("Interact"),
    OPEN_INVENTORY("Open Inventory"),
    PAUSE("Pause");

    private final String displayName;

    PlayerAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
