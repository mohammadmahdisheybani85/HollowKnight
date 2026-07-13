package io.github.some_example_name.model.game;

public class EnemyLaser {
    private final String ownerEnemyName;
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final int damage;

    private float timeLeft;
    private boolean active = true;
    private boolean playerDamaged;

    public EnemyLaser(
        String ownerEnemyName,
        float x,
        float y,
        float width,
        float height,
        float duration,
        int damage
    ) {
        this.ownerEnemyName = ownerEnemyName;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.timeLeft = duration;
        this.damage = damage;
    }

    public void update(float delta) {
        timeLeft -= delta;

        if (timeLeft <= 0f) {
            active = false;
        }
    }

    public String getOwnerEnemyName() {
        return ownerEnemyName;
    }

    public boolean isActive() {
        return active;
    }

    public boolean hasDamagedPlayer() {
        return playerDamaged;
    }

    public void markPlayerDamaged() {
        playerDamaged = true;
    }

    public int getDamage() {
        return damage;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
