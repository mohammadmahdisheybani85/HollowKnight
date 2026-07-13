package io.github.some_example_name.model.game;

import java.util.HashSet;
import java.util.Set;

public class SpellProjectile {
    private static final float WIDTH = 52f;
    private static final float HEIGHT = 22f;

    private final float speed;
    private final int damage;
    private final Set<String> damagedEnemyNames = new HashSet<>();

    private float x;
    private float y;
    private float timeLeft;
    private boolean active = true;

    public SpellProjectile(Player player, float speed, float duration, int damage) {
        this.speed = speed * player.getFacingDirection().getSign();
        this.damage = damage;
        this.timeLeft = duration;

        if (player.getFacingDirection() == HorizontalDirection.RIGHT) {
            x = player.getX() + player.getWidth() + 8f;
        } else {
            x = player.getX() - WIDTH - 8f;
        }

        y = player.getY() + player.getHeight() / 2f - HEIGHT / 2f;
    }

    public void update(float delta) {
        x += speed * delta;
        timeLeft -= delta;

        if (timeLeft <= 0f) {
            active = false;
        }
    }

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public int getDamage() {
        return damage;
    }

    public boolean hasDamaged(String enemyName) {
        return damagedEnemyNames.contains(enemyName);
    }

    public void markDamaged(String enemyName) {
        damagedEnemyNames.add(enemyName);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return WIDTH;
    }

    public float getHeight() {
        return HEIGHT;
    }

    public boolean isMovingRight() {
        return speed > 0f;
    }
}
