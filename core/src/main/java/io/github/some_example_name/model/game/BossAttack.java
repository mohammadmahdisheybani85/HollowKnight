package io.github.some_example_name.model.game;

public class BossAttack {
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final int damage;
    private float timeLeft;
    private boolean hitPlayer;

    public BossAttack(
        float x,
        float y,
        float width,
        float height,
        int damage,
        float duration
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.damage = damage;
        this.timeLeft = Math.max(0f, duration);
    }

    public void update(float delta) {
        timeLeft = Math.max(0f, timeLeft - delta);
    }

    public boolean isActive() {
        return timeLeft > 0f;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getDamage() { return damage; }
    public boolean hasHitPlayer() { return hitPlayer; }
    public void markPlayerHit() { hitPlayer = true; }
}
