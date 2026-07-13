package io.github.some_example_name.model.inventory;

public class Charm {
    private final CharmType type;
    private boolean equipped;
    private boolean unlocked;

    public Charm(CharmType type) {
        this.type = type;
        this.unlocked = type != CharmType.VOID_HEART;
    }

    public CharmType getType() {
        return type;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void unlock() {
        unlocked = true;
    }

    public void lock() {
        unlocked = false;
        equipped = false;
    }

    public void equip() {
        if (unlocked) {
            equipped = true;
        }
    }

    public void unequip() {
        equipped = false;
    }

    public String getDisplayText() {
        if (!unlocked) {
            return "[LOCKED] " + type.getTitle();
        }

        String status = equipped
            ? "[EQUIPPED] "
            : "[ ] ";

        return status
            + type.getTitle()
            + " ("
            + type.getNotchCost()
            + " notches)";
    }
}
