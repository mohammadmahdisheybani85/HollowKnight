package io.github.some_example_name.model.game;

public class BossShockwave {
    private final float y;
    private final float width;
    private final float height;
    private final int damage;
    private final HorizontalDirection direction;
    private final float speed;
    private float x;
    private float timeLeft;
    private boolean hitPlayer;

    public BossShockwave(
        float x,
        float y,
        float width,
        float height,
        int damage,
        HorizontalDirection direction,
        float speed,
        float duration
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.damage = damage;
        this.direction = direction;
        this.speed = speed;
        this.timeLeft = duration;
    }

    public void update(float delta) {
        x += speed * direction.getSign() * delta;
        timeLeft = Math.max(0f, timeLeft - delta);
    }

    public boolean isActive() { return timeLeft > 0f; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getDamage() { return damage; }
    public HorizontalDirection getDirection() { return direction; }
    public boolean hasHitPlayer() { return hitPlayer; }
    public void markPlayerHit() { hitPlayer = true; }
}
