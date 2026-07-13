package io.github.some_example_name.model.game;

import java.util.HashMap;
import java.util.Map;

public class HowlingWraithsAttack {
    private static final float WIDTH = 190f;
    private static final float HEIGHT = 240f;

    private static final float Y_OFFSET_FROM_PLAYER = -25f;

    private static final int MAX_HITS_PER_ENEMY = 3;
    private static final float HIT_INTERVAL = 0.08f;

    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final int damage;

    private final Map<String, Integer> hitCountByEnemyKey = new HashMap<>();

    private float timeLeft;
    private float elapsedTime;
    private float hitTimer;
    private boolean active = true;
    private boolean canHitThisFrame = true;

    public HowlingWraithsAttack(Player player, float duration, int damage) {
        this.width = WIDTH;
        this.height = HEIGHT;
        this.damage = damage;
        this.timeLeft = duration;

        this.x = player.getCenterX() - WIDTH / 2f;
        this.y = player.getY() + Y_OFFSET_FROM_PLAYER;

        this.hitTimer = 0f;
    }

    public void update(float delta) {
        timeLeft -= delta;
        elapsedTime += delta;
        hitTimer -= delta;

        canHitThisFrame = false;

        if (hitTimer <= 0f) {
            canHitThisFrame = true;
            hitTimer = HIT_INTERVAL;
        }

        if (timeLeft <= 0f) {
            active = false;
        }
    }

    public boolean isActive() {
        return active;
    }

    public int getDamage() {
        return damage;
    }

    public boolean canDamage(String enemyKey) {
        if (!canHitThisFrame) {
            return false;
        }

        int hitCount = hitCountByEnemyKey.getOrDefault(enemyKey, 0);
        return hitCount < MAX_HITS_PER_ENEMY;
    }

    public void markDamaged(String enemyKey) {
        int hitCount = hitCountByEnemyKey.getOrDefault(enemyKey, 0);
        hitCountByEnemyKey.put(enemyKey, hitCount + 1);
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

    public float getElapsedTime() {
        return elapsedTime;
    }
}
