package io.github.some_example_name.model.game;

public class Player {
    private static final float WIDTH = 32f;
    private static final float HEIGHT = 48f;

    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private boolean onGround;

    private HorizontalDirection facingDirection = HorizontalDirection.RIGHT;

    private boolean dashing;
    private float dashTimeLeft;
    private float dashCooldownLeft;
    private boolean airDashUsed;

    private boolean doubleJumpUsed;
    private boolean jumpCutApplied;
    private float doubleJumpVisualTimeLeft;

    private boolean wallSliding;
    private HorizontalDirection wallSide = HorizontalDirection.LEFT;
    private boolean focusing;

    private float knockbackTimeLeft;
    private boolean noclipEnabled;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return WIDTH; }
    public float getHeight() { return HEIGHT; }
    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public boolean isOnGround() { return onGround; }
    public HorizontalDirection getFacingDirection() { return facingDirection; }
    public boolean isDashing() { return dashing; }
    public float getDashTimeLeft() { return dashTimeLeft; }
    public float getDashCooldownLeft() { return dashCooldownLeft; }
    public boolean isKnockbackActive() { return knockbackTimeLeft > 0f; }
    public boolean isWallSliding() { return wallSliding; }
    public HorizontalDirection getWallSide() { return wallSide; }
    public boolean isFocusing() { return focusing; }
    public boolean isDoubleJumpVisualActive() { return doubleJumpVisualTimeLeft > 0f; }
    public boolean isNoclipEnabled() { return noclipEnabled; }
    public void toggleNoclip() {
        noclipEnabled = !noclipEnabled;
        velocityX = 0f;
        velocityY = 0f;
        stopDash();
        clearKnockback();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setVelocityX(float velocityX) { this.velocityX = velocityX; }
    public void setVelocityY(float velocityY) { this.velocityY = velocityY; }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
        if (onGround) {
            resetAirAbilities();
            wallSliding = false;
        }
    }

    public void land() {
        setOnGround(true);
        velocityY = 0f;
    }

    public void face(HorizontalDirection direction) {
        this.facingDirection = direction;
    }

    public void startNormalJump(float jumpSpeed) {
        velocityY = jumpSpeed;
        onGround = false;
        jumpCutApplied = false;
        wallSliding = false;
    }

    public void startDoubleJump(float jumpSpeed, float visualDuration) {
        velocityY = jumpSpeed;
        onGround = false;
        doubleJumpUsed = true;
        jumpCutApplied = false;
        doubleJumpVisualTimeLeft = visualDuration;
        wallSliding = false;
    }

    public boolean canDoubleJump() {
        return !onGround && !doubleJumpUsed && !isKnockbackActive();
    }

    public void applyJumpCut(float multiplier) {
        if (!jumpCutApplied && velocityY > 0f) {
            velocityY *= multiplier;
            jumpCutApplied = true;
        }
    }

    public void performPogo(float pogoSpeed, float visualDuration) {
        stopDash();
        velocityY = pogoSpeed;
        onGround = false;
        resetAirAbilities();
        doubleJumpVisualTimeLeft = visualDuration;
        jumpCutApplied = false;
        wallSliding = false;
    }

    public void resetAirAbilities() {
        doubleJumpUsed = false;
        airDashUsed = false;
    }

    public void updateVisualTimers(float delta) {
        doubleJumpVisualTimeLeft = Math.max(0f, doubleJumpVisualTimeLeft - delta);
    }

    public void setWallSliding(boolean wallSliding, HorizontalDirection wallSide) {
        this.wallSliding = wallSliding;
        if (wallSide != null) {
            this.wallSide = wallSide;
        }
    }

    public void setFocusing(boolean focusing) {
        this.focusing = focusing;
        if (focusing) {
            velocityX = 0f;
            stopDash();
            wallSliding = false;
        }
    }

    public void startDash(float dashDuration, float dashCooldown) {
        if (isKnockbackActive()) {
            return;
        }

        dashing = true;
        dashTimeLeft = dashDuration;
        dashCooldownLeft = dashCooldown;
        wallSliding = false;

        if (!onGround) {
            airDashUsed = true;
        }
    }

    public void stopDash() {
        dashing = false;
        dashTimeLeft = 0f;
    }

    public void reduceDashTime(float delta) {
        dashTimeLeft = Math.max(0f, dashTimeLeft - delta);
    }

    public void reduceDashCooldown(float delta) {
        dashCooldownLeft = Math.max(0f, dashCooldownLeft - delta);
    }

    public boolean canDash() {
        boolean airDashAvailable = onGround || !airDashUsed;
        return !dashing
            && !isKnockbackActive()
            && dashCooldownLeft <= 0f
            && airDashAvailable;
    }

    public void startKnockback(float velocityX, float velocityY, float duration) {
        stopDash();
        focusing = false;
        wallSliding = false;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.knockbackTimeLeft = duration;
        this.onGround = false;
    }

    public void reduceKnockbackTime(float delta) {
        knockbackTimeLeft = Math.max(0f, knockbackTimeLeft - delta);
    }

    public void clearKnockback() {
        knockbackTimeLeft = 0f;
    }

    public void resetAfterRespawn() {
        velocityX = 0f;
        velocityY = 0f;
        onGround = false;
        focusing = false;
        wallSliding = false;
        jumpCutApplied = false;
        doubleJumpVisualTimeLeft = 0f;
        stopDash();
        clearKnockback();
        resetAirAbilities();
    }

    public float getCenterX() { return x + WIDTH / 2f; }
    public float getCenterY() { return y + HEIGHT / 2f; }
}
