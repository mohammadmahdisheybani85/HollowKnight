package io.github.some_example_name.model.game;

public class BossGate {
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final Platform collider;
    private boolean closed = true;

    public BossGate(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.collider = new Platform(x, y, width, height);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Platform getCollider() { return collider; }
    public boolean isClosed() { return closed; }
    public void open() { closed = false; }
}
