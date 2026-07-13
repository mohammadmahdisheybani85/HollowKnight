package io.github.some_example_name.controller.inventory;

import io.github.some_example_name.model.inventory.Charm;
import io.github.some_example_name.model.inventory.CharmType;
import io.github.some_example_name.model.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class InventoryController {
    private static final int BASE_SOUL_GAIN = 11;
    private static final int SOUL_CATCHER_BONUS = 3;

    private static final float QUICK_SLASH_ATTACK_COOLDOWN_MULTIPLIER = 0.65f;
    private static final float DASH_MASTER_COOLDOWN_MULTIPLIER = 0.65f;
    private static final float STEADY_BODY_KNOCKBACK_MULTIPLIER = 0.55f;
    private static final float QUICK_FOCUS_DURATION_MULTIPLIER = 0.65f;
    private static final float SHARP_SHADOW_DASH_DURATION_MULTIPLIER = 1.20f;

    private final Inventory inventory;

    public InventoryController(Inventory inventory) {
        this.inventory = inventory;
    }

    public List<Charm> getCharms() { return inventory.getCharms(); }
    public int getUsedNotches() { return inventory.getUsedNotches(); }
    public int getMaxNotches() { return inventory.getMaxNotches(); }
    public boolean toggleCharm(CharmType type) { return inventory.toggleCharm(type); }
    public boolean isEquipped(CharmType type) { return inventory.getCharm(type).isEquipped(); }
    public boolean isUnlocked(CharmType type) { return inventory.isUnlocked(type); }
    public void unlockCharm(CharmType type) { inventory.unlockCharm(type); }

    public int getSoulGainOnEnemyHit() {
        return BASE_SOUL_GAIN + (isEquipped(CharmType.SOUL_CATCHER) ? SOUL_CATCHER_BONUS : 0);
    }

    public int applyNailDamageEffects(int baseDamage) {
        if (!isEquipped(CharmType.UNBREAKABLE_STRENGTH)) {
            return baseDamage;
        }
        return Math.max(baseDamage + 1, Math.round(baseDamage * 1.5f));
    }

    public int applySpellDamageEffects(int baseDamage) {
        if (!isEquipped(CharmType.VOID_HEART)) {
            return baseDamage;
        }
        return Math.max(baseDamage + 1, Math.round(baseDamage * 1.5f));
    }

    public float applyAttackCooldownEffects(float baseCooldown) {
        return isEquipped(CharmType.QUICK_SLASH)
            ? baseCooldown * QUICK_SLASH_ATTACK_COOLDOWN_MULTIPLIER
            : baseCooldown;
    }

    public float applyDashCooldownEffects(float baseCooldown) {
        return isEquipped(CharmType.DASH_MASTER)
            ? baseCooldown * DASH_MASTER_COOLDOWN_MULTIPLIER
            : baseCooldown;
    }

    public float applyDashDurationEffects(float baseDuration) {
        return isEquipped(CharmType.SHARP_SHADOW)
            ? baseDuration * SHARP_SHADOW_DASH_DURATION_MULTIPLIER
            : baseDuration;
    }

    public float applyFocusDurationEffects(float baseDuration) {
        return isEquipped(CharmType.QUICK_FOCUS)
            ? baseDuration * QUICK_FOCUS_DURATION_MULTIPLIER
            : baseDuration;
    }

    public float applyKnockbackEffects(float baseKnockback) {
        return isEquipped(CharmType.STEADY_BODY)
            ? baseKnockback * STEADY_BODY_KNOCKBACK_MULTIPLIER
            : baseKnockback;
    }

    public int getBonusHealth() {
        return isEquipped(CharmType.VITALITY_CHARM) ? 1 : 0;
    }

    public int applyMaxHealthEffects(int baseMaxHealth) {
        return baseMaxHealth + getBonusHealth();
    }


    public List<String> getUnlockedCharmNames() {
        List<String> unlockedCharmNames = new ArrayList<>();
        for (Charm charm : inventory.getCharms()) {
            if (charm.isUnlocked()) {
                unlockedCharmNames.add(charm.getType().name());
            }
        }
        return unlockedCharmNames;
    }

    public void loadCharmProgress(
        List<String> unlockedCharmNames,
        List<String> equippedCharmNames
    ) {
        inventory.resetForNewGame();

        if (unlockedCharmNames != null) {
            for (String charmName : unlockedCharmNames) {
                try {
                    inventory.unlockCharm(CharmType.valueOf(charmName));
                } catch (IllegalArgumentException ignored) {
                    // Old or removed charm from an earlier save.
                }
            }
        }

        loadEquippedCharms(equippedCharmNames);
    }

    public List<String> getEquippedCharmNames() {
        List<String> equippedCharmNames = new ArrayList<>();
        for (Charm charm : inventory.getCharms()) {
            if (charm.isEquipped()) {
                equippedCharmNames.add(charm.getType().name());
            }
        }
        return equippedCharmNames;
    }

    public void loadEquippedCharms(List<String> equippedCharmNames) {
        inventory.unequipAll();
        if (equippedCharmNames == null) return;

        for (String charmName : equippedCharmNames) {
            try {
                CharmType type = CharmType.valueOf(charmName);
                inventory.unlockCharm(type);
                inventory.equipIfPossible(type);
            } catch (IllegalArgumentException ignored) {
                // Old or removed charm from an earlier save.
            }
        }
    }

    public void resetForNewGame() {
        inventory.resetForNewGame();
    }
}
