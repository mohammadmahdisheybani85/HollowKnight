package io.github.some_example_name.model.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {
    private static final int MAX_NOTCHES = 3;

    private final List<Charm> charms = new ArrayList<>();

    public Inventory() {
        for (CharmType type : CharmType.values()) {
            charms.add(new Charm(type));
        }
    }

    public List<Charm> getCharms() {
        return Collections.unmodifiableList(charms);
    }

    public int getMaxNotches() {
        return MAX_NOTCHES;
    }

    public int getUsedNotches() {
        int usedNotches = 0;

        for (Charm charm : charms) {
            if (charm.isEquipped()) {
                usedNotches += charm.getType().getNotchCost();
            }
        }

        return usedNotches;
    }

    public Charm getCharm(CharmType type) {
        for (Charm charm : charms) {
            if (charm.getType() == type) {
                return charm;
            }
        }

        throw new IllegalArgumentException(
            "Unknown charm: " + type
        );
    }

    public boolean isUnlocked(CharmType type) {
        return getCharm(type).isUnlocked();
    }

    public void unlockCharm(CharmType type) {
        getCharm(type).unlock();
    }

    public boolean canEquip(CharmType type) {
        Charm charm = getCharm(type);

        if (!charm.isUnlocked()) {
            return false;
        }

        if (charm.isEquipped()) {
            return true;
        }

        return getUsedNotches()
            + type.getNotchCost()
            <= MAX_NOTCHES;
    }

    public boolean toggleCharm(CharmType type) {
        Charm charm = getCharm(type);

        if (!charm.isUnlocked()) {
            return false;
        }

        if (charm.isEquipped()) {
            charm.unequip();
            return true;
        }

        if (!canEquip(type)) {
            return false;
        }

        charm.equip();
        return true;
    }

    public void unequipAll() {
        for (Charm charm : charms) {
            charm.unequip();
        }
    }

    public void equipIfPossible(CharmType type) {
        Charm charm = getCharm(type);

        if (!charm.isUnlocked()) {
            return;
        }

        if (!charm.isEquipped() && canEquip(type)) {
            charm.equip();
        }
    }

    public void resetForNewGame() {
        unequipAll();
        getCharm(CharmType.VOID_HEART).lock();
    }
}
