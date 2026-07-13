package io.github.some_example_name.model.game;

public class BreakableWall {
    private static final int REQUIRED_HITS = 3;
    private static final float BREAK_EFFECT_DURATION = 0.48f;

    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final Platform collider;

    private int hitCount;
    private boolean broken;
    private float breakEffectTimeLeft;

    public BreakableWall(
        float x,
        float y,
        float width,
        float height
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.collider = new Platform(x, y, width, height);
    }

    public boolean takeHit() {
        if (broken) {
            return false;
        }

        hitCount++;

        if (hitCount < REQUIRED_HITS) {
            return false;
        }

        broken = true;
        breakEffectTimeLeft = BREAK_EFFECT_DURATION;
        return true;
    }

    public void forceBroken() {
        hitCount = REQUIRED_HITS;
        broken = true;
        breakEffectTimeLeft = 0f;
    }

    public void update(float delta) {
        breakEffectTimeLeft = Math.max(
            0f,
            breakEffectTimeLeft - delta
        );
    }

    public int getVisualStage() {
        if (broken) {
            return 3;
        }

        return Math.min(2, hitCount);
    }

    public boolean isBroken() {
        return broken;
    }

    public boolean isBreakEffectActive() {
        return breakEffectTimeLeft > 0f;
    }

    public float getBreakEffectProgress() {
        return 1f - breakEffectTimeLeft
            / BREAK_EFFECT_DURATION;
    }

    public Platform getCollider() {
        return collider;
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
