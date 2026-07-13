package io.github.some_example_name.controller.game;

import io.github.some_example_name.controller.inventory.InventoryController;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.HorizontalDirection;
import io.github.some_example_name.model.game.Platform;
import io.github.some_example_name.model.game.Player;
import io.github.some_example_name.model.input.GameInputState;

public class PlayerController {
    private static final float MOVE_SPEED = 220f;
    private static final float JUMP_SPEED = 610f;
    private static final float DOUBLE_JUMP_SPEED = 575f;
    private static final float WALL_JUMP_SPEED_X = 320f;
    private static final float WALL_JUMP_SPEED_Y = 565f;
    private static final float GRAVITY = -1200f;
    private static final float JUMP_CUT_MULTIPLIER = 0.42f;

    private static final float DASH_SPEED = 620f;
    private static final float DASH_DURATION = 0.16f;
    private static final float DASH_COOLDOWN = 0.45f;

    private static final float WALL_CONTACT_EPSILON = 3.5f;
    private static final float WALL_SLIDE_MAX_FALL_SPEED = -155f;
    private static final float DOUBLE_JUMP_VISUAL_DURATION = 0.28f;

    private static final float WORLD_MIN_X = 0f;
    private static final float NOCLIP_SPEED = 420f;

    private final GameWorld world;
    private final InventoryController inventoryController;

    public PlayerController(GameWorld world, InventoryController inventoryController) {
        this.world = world;
        this.inventoryController = inventoryController;
    }

    public void update(float delta, GameInputState inputState) {
        Player player = world.getPlayer();

        if (player.isNoclipEnabled()) {
            updateNoclip(player, inputState, delta);
            return;
        }

        player.reduceDashCooldown(delta);
        player.reduceKnockbackTime(delta);
        player.updateVisualTimers(delta);

        updateWallContact(player, inputState);

        if (player.isKnockbackActive()) {
            applyGravity(player, delta);
        } else if (player.isDashing()) {
            updateDash(player, delta);
        } else {
            updateHorizontalMovement(player, inputState);
            updateJump(player, inputState);
            updateDashStart(player, inputState);
            applyGravity(player, delta);
            applyWallSlideLimit(player);
        }

        moveHorizontally(player, delta);
        updateWallContact(player, inputState);
        applyWallSlideLimit(player);
        moveVertically(player, delta);
        updateWallContact(player, inputState);
        keepPlayerInsideWorld(player);
    }



    private void updateNoclip(
        Player player,
        GameInputState inputState,
        float delta
    ) {
        float moveX = 0f;
        float moveY = 0f;

        if (inputState.isMoveLeftHeld()) moveX -= 1f;
        if (inputState.isMoveRightHeld()) moveX += 1f;
        if (inputState.isLookUpHeld()) moveY += 1f;
        if (inputState.isLookDownHeld()) moveY -= 1f;

        if (moveX < 0f) player.face(HorizontalDirection.LEFT);
        if (moveX > 0f) player.face(HorizontalDirection.RIGHT);

        float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
        if (length > 0f) {
            moveX /= length;
            moveY /= length;
        }

        player.setVelocityX(moveX * NOCLIP_SPEED);
        player.setVelocityY(moveY * NOCLIP_SPEED);
        player.setPosition(
            Math.max(0f, Math.min(world.getWorldWidth() - player.getWidth(),
                player.getX() + player.getVelocityX() * delta)),
            Math.max(0f, Math.min(world.getWorldHeight() - player.getHeight(),
                player.getY() + player.getVelocityY() * delta))
        );
        player.setOnGround(false);
    }

    private void updateHorizontalMovement(Player player, GameInputState inputState) {
        boolean movingLeft = inputState.isMoveLeftHeld();
        boolean movingRight = inputState.isMoveRightHeld();

        if (movingLeft && !movingRight) {
            player.face(HorizontalDirection.LEFT);
            player.setVelocityX(-MOVE_SPEED);
        } else if (movingRight && !movingLeft) {
            player.face(HorizontalDirection.RIGHT);
            player.setVelocityX(MOVE_SPEED);
        } else {
            player.setVelocityX(0f);
        }
    }

    private void updateJump(Player player, GameInputState inputState) {
        if (inputState.isJumpJustPressed()) {
            if (player.isOnGround()) {
                player.startNormalJump(JUMP_SPEED);
                return;
            }

            if (player.isWallSliding()) {
                HorizontalDirection awayFromWall =
                    player.getWallSide() == HorizontalDirection.LEFT
                        ? HorizontalDirection.RIGHT
                        : HorizontalDirection.LEFT;

                player.face(awayFromWall);
                player.setVelocityX(WALL_JUMP_SPEED_X * awayFromWall.getSign());
                player.startNormalJump(WALL_JUMP_SPEED_Y);
                return;
            }

            if (player.canDoubleJump()) {
                player.startDoubleJump(
                    DOUBLE_JUMP_SPEED,
                    DOUBLE_JUMP_VISUAL_DURATION
                );
            }
        }

        if (!inputState.isJumpHeld()) {
            player.applyJumpCut(JUMP_CUT_MULTIPLIER);
        }
    }

    private void updateDashStart(Player player, GameInputState inputState) {
        if (!inputState.isDashJustPressed() || !player.canDash()) {
            return;
        }

        float dashCooldown = inventoryController.applyDashCooldownEffects(DASH_COOLDOWN);
        float dashDuration = inventoryController.applyDashDurationEffects(DASH_DURATION);

        player.startDash(dashDuration, dashCooldown);
        player.setVelocityY(0f);
        player.setVelocityX(DASH_SPEED * player.getFacingDirection().getSign());
    }

    private void updateDash(Player player, float delta) {
        player.reduceDashTime(delta);
        player.setVelocityY(0f);
        player.setVelocityX(DASH_SPEED * player.getFacingDirection().getSign());

        if (player.getDashTimeLeft() <= 0f) {
            player.stopDash();
            player.setVelocityX(0f);
        }
    }

    private void applyGravity(Player player, float delta) {
        player.setVelocityY(player.getVelocityY() + GRAVITY * delta);
    }

    private void applyWallSlideLimit(Player player) {
        if (player.isWallSliding() && player.getVelocityY() < WALL_SLIDE_MAX_FALL_SPEED) {
            player.setVelocityY(WALL_SLIDE_MAX_FALL_SPEED);
        }
    }

    private void updateWallContact(Player player, GameInputState inputState) {
        if (player.isOnGround() || player.isDashing() || player.isKnockbackActive()) {
            player.setWallSliding(false, null);
            return;
        }

        boolean touchingLeft = false;
        boolean touchingRight = false;

        for (Platform platform : world.getPlatforms()) {
            if (!overlaps(
                player.getY() + 3f,
                player.getHeight() - 6f,
                platform.getY(),
                platform.getHeight()
            )) {
                continue;
            }

            float playerLeft = player.getX();
            float playerRight = player.getX() + player.getWidth();
            float platformLeft = platform.getX();
            float platformRight = platform.getX() + platform.getWidth();

            if (Math.abs(playerLeft - platformRight) <= WALL_CONTACT_EPSILON) {
                touchingLeft = true;
            }

            if (Math.abs(playerRight - platformLeft) <= WALL_CONTACT_EPSILON) {
                touchingRight = true;
            }
        }

        boolean slidingLeft = touchingLeft && inputState.isMoveLeftHeld();
        boolean slidingRight = touchingRight && inputState.isMoveRightHeld();
        boolean falling = player.getVelocityY() <= 0f;

        if (falling && slidingLeft) {
            player.setWallSliding(true, HorizontalDirection.LEFT);
        } else if (falling && slidingRight) {
            player.setWallSliding(true, HorizontalDirection.RIGHT);
        } else {
            player.setWallSliding(false, null);
        }
    }

    private void moveHorizontally(Player player, float delta) {
        float currentX = player.getX();
        float nextX = currentX + player.getVelocityX() * delta;

        for (Platform platform : world.getPlatforms()) {
            boolean overlapsY = overlaps(
                player.getY(),
                player.getHeight(),
                platform.getY(),
                platform.getHeight()
            );

            if (!overlapsY) {
                continue;
            }

            if (player.getVelocityX() > 0f) {
                boolean wasLeftOfPlatform = currentX + player.getWidth() <= platform.getX();
                boolean hitsLeftSide = nextX + player.getWidth() >= platform.getX();

                if (wasLeftOfPlatform && hitsLeftSide) {
                    nextX = platform.getX() - player.getWidth();
                    stopForcedMovement(player);
                }
            } else if (player.getVelocityX() < 0f) {
                boolean wasRightOfPlatform = currentX >= platform.getX() + platform.getWidth();
                boolean hitsRightSide = nextX <= platform.getX() + platform.getWidth();

                if (wasRightOfPlatform && hitsRightSide) {
                    nextX = platform.getX() + platform.getWidth();
                    stopForcedMovement(player);
                }
            }
        }

        player.setPosition(nextX, player.getY());
    }

    private void moveVertically(Player player, float delta) {
        float currentY = player.getY();
        float nextY = currentY + player.getVelocityY() * delta;

        player.setOnGround(false);

        for (Platform platform : world.getPlatforms()) {
            boolean overlapsX = overlaps(
                player.getX(),
                player.getWidth(),
                platform.getX(),
                platform.getWidth()
            );

            if (!overlapsX) {
                continue;
            }

            if (player.getVelocityY() < 0f) {
                boolean wasAbovePlatform = currentY >= platform.getY() + platform.getHeight();
                boolean hitsPlatformTop = nextY <= platform.getY() + platform.getHeight();

                if (wasAbovePlatform && hitsPlatformTop) {
                    nextY = platform.getY() + platform.getHeight();
                    player.land();
                }
            } else if (player.getVelocityY() > 0f) {
                boolean wasBelowPlatform = currentY + player.getHeight() <= platform.getY();
                boolean hitsPlatformBottom = nextY + player.getHeight() >= platform.getY();

                if (wasBelowPlatform && hitsPlatformBottom) {
                    nextY = platform.getY() - player.getHeight();
                    player.setVelocityY(0f);
                }
            }
        }

        player.setPosition(player.getX(), nextY);
    }

    private void keepPlayerInsideWorld(Player player) {
        if (player.getX() < WORLD_MIN_X) {
            player.setPosition(WORLD_MIN_X, player.getY());
            stopForcedMovement(player);
        }

        if (player.getX() + player.getWidth() > world.getWorldWidth()) {
            player.setPosition(world.getWorldWidth() - player.getWidth(), player.getY());
            stopForcedMovement(player);
        }
    }

    private void stopForcedMovement(Player player) {
        player.setVelocityX(0f);
        player.stopDash();
        player.clearKnockback();
    }

    private boolean overlaps(float firstStart, float firstSize, float secondStart, float secondSize) {
        return firstStart + firstSize > secondStart
            && firstStart < secondStart + secondSize;
    }
}
