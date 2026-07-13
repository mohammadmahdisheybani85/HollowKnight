package io.github.some_example_name.model.game;

public class Hazard {
    private final HazardType type;
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final int damage;

    public Hazard(
        HazardType type,
        float x,
        float y,
        float width,
        float height,
        int damage
    ) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.damage = damage;
    }

    public Hazard(
        float x,
        float y,
        float width,
        float height,
        int damage
    ) {
        this(
            HazardType.CROSSROADS_SPIKES,
            x,
            y,
            width,
            height,
            damage
        );
    }

    public HazardType getType() {
        return type;
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

    public int getDamage() {
        return damage;
    }
}
