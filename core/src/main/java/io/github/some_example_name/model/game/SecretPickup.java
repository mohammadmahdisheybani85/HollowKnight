package io.github.some_example_name.model.game;

public class SecretPickup {
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private boolean collected;

    public SecretPickup(
        float x,
        float y,
        float width,
        float height
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void collect() {
        collected = true;
    }

    public boolean isCollected() {
        return collected;
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
