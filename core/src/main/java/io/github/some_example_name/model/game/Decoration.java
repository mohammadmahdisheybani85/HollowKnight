package io.github.some_example_name.model.game;

public class Decoration {
    private final DecorationType type;
    private final float x;
    private final float y;
    private final float width;
    private final float height;

    public Decoration(
        DecorationType type,
        float x,
        float y,
        float width,
        float height
    ) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public DecorationType getType() {
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
}
