package io.github.some_example_name.model.player;

public class PlayerStats {
    public static final int BASE_MAX_HEALTH = 5;
    public static final int MAX_SOUL = 99;

    private static final float DAMAGE_INVINCIBILITY_DURATION = 1.0f;

    private int health = BASE_MAX_HEALTH;
    private int maxHealth = BASE_MAX_HEALTH;
    private int soul;
    private boolean godModeEnabled;
    private float damageInvincibilityTimeLeft;
    private int deathCount;

    public void update(float delta) {
        damageInvincibilityTimeLeft = Math.max(0f, damageInvincibilityTimeLeft - delta);
    }

    public int getHealth() {
        return health;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = Math.max(0, deathCount);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getSoul() {
        return soul;
    }

    public int getMaxSoul() {
        return MAX_SOUL;
    }

    public boolean isGodModeEnabled() {
        return godModeEnabled;
    }

    public boolean isDamageInvincible() {
        return damageInvincibilityTimeLeft > 0f;
    }

    public boolean isInvincible() {
        return godModeEnabled || isDamageInvincible();
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void setMaxHealth(int maxHealth) {
        if (maxHealth < 1) {
            throw new IllegalArgumentException("Max health must be positive.");
        }

        this.maxHealth = maxHealth;
        health = Math.min(health, maxHealth);
    }

    public boolean takeDamage(int amount) {
        if (amount <= 0 || isInvincible()) {
            return false;
        }

        health = Math.max(0, health - amount);

        if (!isDead()) {
            damageInvincibilityTimeLeft = DAMAGE_INVINCIBILITY_DURATION;
        }

        return true;
    }

    public void heal(int amount) {
        if (amount <= 0) {
            return;
        }

        health = Math.min(maxHealth, health + amount);
    }

    public void gainSoul(int amount) {
        if (amount <= 0) {
            return;
        }

        soul = Math.min(MAX_SOUL, soul + amount);
    }

    public void fillSoul() {
        soul = MAX_SOUL;
    }

    public void resetSoul() {
        soul = 0;
    }

    public void toggleGodMode() {
        godModeEnabled = !godModeEnabled;
    }

    public void startNewGame() {
        deathCount = 0;
        maxHealth = BASE_MAX_HEALTH;
        health = maxHealth;
        soul = 0;
        godModeEnabled = false;
        damageInvincibilityTimeLeft = 0f;
    }

    public void loadFromSave(int savedHealth, int savedSoul) {
        maxHealth = BASE_MAX_HEALTH;
        health = clamp(savedHealth, 1, maxHealth);
        soul = clamp(savedSoul, 0, MAX_SOUL);
        godModeEnabled = false;
        damageInvincibilityTimeLeft = 0f;
    }

    public void respawnAfterDeath() {
        deathCount++;
        health = maxHealth;
        soul = 0;
        damageInvincibilityTimeLeft = DAMAGE_INVINCIBILITY_DURATION;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public boolean hasEnoughSoul(int amount) {
        return soul >= amount;
    }

    public boolean spendSoul(int amount) {
        if (amount <= 0 || soul < amount) {
            return false;
        }

        soul -= amount;
        return true;
    }
}
