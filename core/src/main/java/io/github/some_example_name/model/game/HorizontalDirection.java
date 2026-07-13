package io.github.some_example_name.model.game;

public enum HorizontalDirection {
    LEFT(-1f),
    RIGHT(1f);

    private final float sign;

    HorizontalDirection(float sign) {
        this.sign = sign;
    }

    public float getSign() {
        return sign;
    }
}
