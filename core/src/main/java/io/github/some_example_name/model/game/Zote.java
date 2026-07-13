package io.github.some_example_name.model.game;

/**
 * Non-damaging NPC. When provoked, Zote performs a short theatrical lunge
 * toward the player and then returns to his original spot.
 */
public class Zote {
    private static final float WIDTH = 42f;
    private static final float HEIGHT = 58f;

    private final float spawnX;
    private final float y;

    private float x;
    private boolean talking;
    private float attackReactionTimeLeft;
    private float attackReactionDuration;
    private float attackStartX;
    private float attackTargetX;
    private HorizontalDirection facingDirection = HorizontalDirection.RIGHT;

    public Zote(float x, float y) {
        this.spawnX = x;
        this.x = x;
        this.y = y;
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

    public float getCenterX() {
        return x + WIDTH / 2f;
    }

    public float getCenterY() {
        return y + HEIGHT / 2f;
    }

    public boolean isTalking() {
        return talking;
    }

    public void setTalking(boolean talking) {
        this.talking = talking;
    }

    public HorizontalDirection getFacingDirection() {
        return facingDirection;
    }

    /**
     * Starts a cosmetic attack. It deliberately has no damage hitbox.
     */
    public void startAttackReaction(float duration, float playerCenterX) {
        attackReactionDuration = Math.max(0.1f, duration);
        attackReactionTimeLeft = attackReactionDuration;
        attackStartX = x;

        float direction = playerCenterX >= getCenterX() ? 1f : -1f;
        facingDirection = direction > 0f
            ? HorizontalDirection.RIGHT
            : HorizontalDirection.LEFT;

        float desiredTargetX = x + direction * 82f;
        float minX = spawnX - 92f;
        float maxX = spawnX + 92f;
        attackTargetX = Math.max(minX, Math.min(maxX, desiredTargetX));
    }

    public void update(float delta) {
        if (attackReactionTimeLeft <= 0f) {
            x = moveToward(x, spawnX, Math.max(0f, delta) * 240f);
            return;
        }

        attackReactionTimeLeft = Math.max(
            0f,
            attackReactionTimeLeft - Math.max(0f, delta)
        );

        float progress = 1f - attackReactionTimeLeft / attackReactionDuration;

        if (progress < 0.46f) {
            float outwardProgress = smoothStep(progress / 0.46f);
            x = lerp(attackStartX, attackTargetX, outwardProgress);
        } else {
            float returnProgress = smoothStep((progress - 0.46f) / 0.54f);
            x = lerp(attackTargetX, spawnX, returnProgress);
        }

        if (attackReactionTimeLeft <= 0f) {
            x = spawnX;
        }
    }

    public boolean isReactingToAttack() {
        return attackReactionTimeLeft > 0f;
    }

    private float smoothStep(float value) {
        float clamped = Math.max(0f, Math.min(1f, value));
        return clamped * clamped * (3f - 2f * clamped);
    }

    private float lerp(float from, float to, float alpha) {
        return from + (to - from) * alpha;
    }

    private float moveToward(float current, float target, float maxDistance) {
        if (Math.abs(target - current) <= maxDistance) {
            return target;
        }
        return current + Math.signum(target - current) * maxDistance;
    }
}
