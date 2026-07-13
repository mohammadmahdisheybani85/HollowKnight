package io.github.some_example_name.controller.game;

import io.github.some_example_name.model.game.Enemy;
import io.github.some_example_name.model.game.EnemyLaser;
import io.github.some_example_name.model.game.EnemyState;
import io.github.some_example_name.model.game.EnemyType;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.HorizontalDirection;
import io.github.some_example_name.model.game.Platform;
import io.github.some_example_name.model.game.Player;
import io.github.some_example_name.util.CollisionUtils;

public class EnemyAiController {
    private static final float GROUND_PROBE_DISTANCE = 8f;
    private static final float GROUND_PROBE_DEPTH = 10f;
    private static final float WALL_PROBE_DISTANCE = 5f;

    private static final float RESPAWN_DELAY = 5.5f;
    private static final float RESPAWN_PLAYER_DISTANCE = 520f;

    private static final float HORNHEAD_WALK_TIME = 2.2f;
    private static final float HORNHEAD_REST_TIME = 0.8f;
    private static final float HORNHEAD_CHARGE_TIME = 1.1f;
    private static final float HORNHEAD_CHARGE_SPEED = 260f;
    private static final float HORNHEAD_VISION_RANGE = 260f;
    private static final float HORNHEAD_VISION_HEIGHT = 90f;

    private static final float MOSQUITO_DETECTION_RANGE = 430f;
    private static final float MOSQUITO_AIM_TIME = 0.42f;
    private static final float MOSQUITO_DASH_TIME = 0.88f;
    private static final float MOSQUITO_REST_TIME = 0.68f;
    private static final float MOSQUITO_DASH_SPEED = 340f;

    private static final float MOSSFLY_DETECTION_RANGE = 360f;
    private static final float MOSSFLY_SHAKE_TIME = 0.58f;
    private static final float MOSSFLY_REVEAL_TIME = 0.46f;
    private static final float MOSSFLY_SPEED = 155f;

    private static final float FLYING_MIN_Y = 70f;
    private static final float FLYING_MAX_Y = 650f;
    private static final float FLYING_STOP_DISTANCE = 22f;
    private static final float FLYING_MAX_SUBSTEP = 4f;

    private static final float GUARDIAN_VISION_RANGE = 420f;
    private static final float GUARDIAN_VISION_HEIGHT = 85f;
    private static final float GUARDIAN_PREPARE_TIME = 0.6f;
    private static final float GUARDIAN_ENRAGED_TIME = 1.15f;
    private static final float GUARDIAN_ENRAGED_SPEED = 190f;
    private static final float GUARDIAN_RETURN_SPEED = 125f;

    private static final float GUARDIAN_LASER_RANGE = 720f;
    private static final float GUARDIAN_LASER_HEIGHT = 18f;
    private static final float GUARDIAN_LASER_DURATION = 0.32f;
    private static final int GUARDIAN_LASER_DAMAGE = 1;

    private final GameWorld world;

    public EnemyAiController(GameWorld world) {
        this.world = world;
    }

    public void update(float delta) {
        world.updateEnemyLasers(delta);
        world.updateEnvironmentEffects(delta);

        for (Enemy enemy : world.getEnemies()) {
            enemy.updateTimers(delta);

            if (!enemy.isAlive()) {
                updateDeadEnemy(enemy);
                continue;
            }

            if (enemy.getType() == EnemyType.FALSE_KNIGHT) {
                continue;
            }

            switch (enemy.getType()) {
                case BASIC_GROUND, CRAWLID ->
                    updateCrawlid(enemy, delta);
                case MOSQUITO ->
                    updateMosquito(enemy, delta);
                case MOSSFLY ->
                    updateMossfly(enemy, delta);
                case HORNHEAD_HUSK ->
                    updateHornheadHusk(enemy, delta);
                case GUARDIAN_CRYSTAL ->
                    updateGuardianCrystal(enemy, delta);
                case FALSE_KNIGHT -> {
                    // Updated by FalseKnightController.
                }
            }
        }
    }

    private void updateDeadEnemy(Enemy enemy) {
        if (
            enemy.isPermanentlyDefeated()
                || enemy.getType() == EnemyType.FALSE_KNIGHT
                || enemy.getDeathElapsed() < RESPAWN_DELAY
        ) {
            return;
        }

        Player player = world.getPlayer();
        float dx = player.getCenterX() - enemy.getSpawnX();
        float dy = player.getCenterY() - enemy.getSpawnY();

        if (
            dx * dx + dy * dy
                < RESPAWN_PLAYER_DISTANCE
                * RESPAWN_PLAYER_DISTANCE
        ) {
            return;
        }

        enemy.reviveAtSpawn();
    }

    private void updateCrawlid(
        Enemy enemy,
        float delta
    ) {
        if (mustTurnAround(enemy, enemy.getDirection())) {
            turnAround(enemy);
        }

        moveGroundEnemy(
            enemy,
            enemy.getDirection(),
            enemy.getSpeed(),
            delta
        );
    }

    private void updateHornheadHusk(
        Enemy enemy,
        float delta
    ) {
        if (enemy.getState() == EnemyState.CHARGING) {
            enemy.reduceStateTime(delta);

            if (
                mustTurnAround(
                    enemy,
                    enemy.getChargeDirection()
                )
            ) {
                enemy.setState(
                    EnemyState.RESTING,
                    HORNHEAD_REST_TIME
                );
                return;
            }

            moveGroundEnemy(
                enemy,
                enemy.getChargeDirection(),
                HORNHEAD_CHARGE_SPEED,
                delta
            );

            if (enemy.isStateTimeFinished()) {
                enemy.setState(
                    EnemyState.RESTING,
                    HORNHEAD_REST_TIME
                );
            }
            return;
        }

        if (enemy.getState() == EnemyState.RESTING) {
            enemy.reduceStateTime(delta);

            if (enemy.isStateTimeFinished()) {
                enemy.setState(
                    EnemyState.PATROLLING,
                    HORNHEAD_WALK_TIME
                );
            }
            return;
        }

        if (canHornheadSeePlayer(enemy)) {
            enemy.setChargeDirection(enemy.getDirection());
            enemy.setState(
                EnemyState.CHARGING,
                HORNHEAD_CHARGE_TIME
            );
            return;
        }

        if (mustTurnAround(enemy, enemy.getDirection())) {
            turnAround(enemy);
        }

        moveGroundEnemy(
            enemy,
            enemy.getDirection(),
            enemy.getSpeed(),
            delta
        );

        enemy.reduceStateTime(delta);
        if (enemy.isStateTimeFinished()) {
            enemy.setState(
                EnemyState.RESTING,
                HORNHEAD_REST_TIME
            );
        }
    }

    private void updateMosquito(
        Enemy enemy,
        float delta
    ) {
        if (enemy.getState() == EnemyState.AIMING) {
            enemy.reduceStateTime(delta);

            if (enemy.isStateTimeFinished()) {
                startMosquitoDash(enemy);
            }
            return;
        }

        if (enemy.getState() == EnemyState.DASHING) {
            enemy.reduceStateTime(delta);

            boolean moved = moveFlyingByVelocity(
                enemy,
                delta
            );

            float dx = enemy.getTargetX()
                - enemy.getCenterX();
            float dy = enemy.getTargetY()
                - enemy.getCenterY();

            boolean reachedTarget =
                dx * dx + dy * dy <= 24f * 24f;

            if (
                !moved
                    || reachedTarget
                    || enemy.isStateTimeFinished()
            ) {
                enemy.setVelocity(0f, 0f);
                enemy.setState(
                    EnemyState.RESTING,
                    MOSQUITO_REST_TIME
                );
            }
            return;
        }

        if (enemy.getState() == EnemyState.RESTING) {
            enemy.reduceStateTime(delta);

            if (enemy.isStateTimeFinished()) {
                enemy.setState(
                    EnemyState.PATROLLING,
                    0f
                );
            }
            return;
        }

        if (isPlayerNear(enemy, MOSQUITO_DETECTION_RANGE)) {
            Player player = world.getPlayer();
            enemy.setTarget(
                player.getCenterX(),
                player.getCenterY()
            );
            facePlayer(enemy);
            enemy.setState(
                EnemyState.AIMING,
                MOSQUITO_AIM_TIME
            );
            return;
        }

        float hoverTargetY = enemy.getSpawnY()
            + (float) Math.sin(
            enemy.getX() * 0.02f
        ) * 22f;

        enemy.moveHorizontally(enemy.getSpeed(), delta);
        moveFlyingToward(
            enemy,
            enemy.getX(),
            hoverTargetY,
            enemy.getSpeed(),
            delta
        );
    }

    private void startMosquitoDash(Enemy enemy) {
        float dx = enemy.getTargetX()
            - enemy.getCenterX();
        float dy = enemy.getTargetY()
            - enemy.getCenterY();
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length <= 0.001f) {
            enemy.setState(
                EnemyState.RESTING,
                MOSQUITO_REST_TIME
            );
            return;
        }

        enemy.setVelocity(
            dx / length * MOSQUITO_DASH_SPEED,
            dy / length * MOSQUITO_DASH_SPEED
        );
        enemy.setState(
            EnemyState.DASHING,
            MOSQUITO_DASH_TIME
        );
    }

    private void updateMossfly(
        Enemy enemy,
        float delta
    ) {
        if (enemy.getState() == EnemyState.CAMOUFLAGED) {
            if (isPlayerNear(enemy, MOSSFLY_DETECTION_RANGE)) {
                enemy.setState(
                    EnemyState.SHAKING,
                    MOSSFLY_SHAKE_TIME
                );
            }
            return;
        }

        if (enemy.getState() == EnemyState.SHAKING) {
            enemy.reduceStateTime(delta);

            if (enemy.isStateTimeFinished()) {
                enemy.setState(
                    EnemyState.REVEALING,
                    MOSSFLY_REVEAL_TIME
                );
            }
            return;
        }

        if (enemy.getState() == EnemyState.REVEALING) {
            enemy.reduceStateTime(delta);

            if (enemy.isStateTimeFinished()) {
                enemy.setPosition(
                    enemy.getX(),
                    enemy.getY() + 58f
                );
                enemy.setState(
                    EnemyState.CHASING,
                    0f
                );
            }
            return;
        }

        Player player = world.getPlayer();
        float targetX = player.getCenterX()
            - enemy.getWidth() / 2f;
        float targetY = player.getCenterY()
            + 32f
            - enemy.getHeight() / 2f;

        moveFlyingToward(
            enemy,
            targetX,
            targetY,
            MOSSFLY_SPEED,
            delta
        );
        facePlayer(enemy);
    }

    private void updateGuardianCrystal(
        Enemy enemy,
        float delta
    ) {
        if (enemy.getState() == EnemyState.ENRAGED) {
            enemy.reduceStateTime(delta);

            if (
                mustTurnAround(
                    enemy,
                    enemy.getDirection()
                )
            ) {
                enemy.setState(
                    EnemyState.RETURNING,
                    0f
                );
                return;
            }

            moveGroundEnemy(
                enemy,
                enemy.getDirection(),
                GUARDIAN_ENRAGED_SPEED,
                delta
            );

            if (enemy.isStateTimeFinished()) {
                enemy.setState(
                    EnemyState.RETURNING,
                    0f
                );
            }
            return;
        }

        if (enemy.getState() == EnemyState.RETURNING) {
            float dx = enemy.getSpawnX() - enemy.getX();

            if (Math.abs(dx) <= 2f) {
                enemy.setPosition(
                    enemy.getSpawnX(),
                    enemy.getSpawnY()
                );
                enemy.setState(
                    EnemyState.WATCHING,
                    0f
                );
                return;
            }

            HorizontalDirection returnDirection = dx < 0f
                ? HorizontalDirection.LEFT
                : HorizontalDirection.RIGHT;

            enemy.moveInDirection(
                returnDirection,
                GUARDIAN_RETURN_SPEED,
                delta
            );
            return;
        }

        if (
            enemy.getState()
                == EnemyState.PREPARING_ATTACK
        ) {
            enemy.reduceStateTime(delta);

            if (enemy.isStateTimeFinished()) {
                fireGuardianLaser(enemy);
                enemy.setState(
                    EnemyState.ENRAGED,
                    GUARDIAN_ENRAGED_TIME
                );
            }
            return;
        }

        enemy.setState(EnemyState.WATCHING, 0f);

        if (canGuardianSeePlayer(enemy)) {
            enemy.setState(
                EnemyState.PREPARING_ATTACK,
                GUARDIAN_PREPARE_TIME
            );
        }
    }

    private boolean mustTurnAround(
        Enemy enemy,
        HorizontalDirection movementDirection
    ) {
        return isWallAhead(enemy, movementDirection)
            || !hasGroundAhead(enemy, movementDirection);
    }

    private boolean isWallAhead(
        Enemy enemy,
        HorizontalDirection movementDirection
    ) {
        float nextX = enemy.getX()
            + movementDirection.getSign()
            * WALL_PROBE_DISTANCE;

        for (Platform platform : world.getPlatforms()) {
            if (
                CollisionUtils.overlaps(
                    nextX,
                    enemy.getY() + 2f,
                    enemy.getWidth(),
                    Math.max(1f, enemy.getHeight() - 4f),
                    platform.getX(),
                    platform.getY(),
                    platform.getWidth(),
                    platform.getHeight()
                )
            ) {
                return true;
            }
        }

        return false;
    }

    private boolean hasGroundAhead(
        Enemy enemy,
        HorizontalDirection movementDirection
    ) {
        float probeX = movementDirection
            == HorizontalDirection.RIGHT
            ? enemy.getX()
            + enemy.getWidth()
            + GROUND_PROBE_DISTANCE
            : enemy.getX() - GROUND_PROBE_DISTANCE;

        float probeY = enemy.getY()
            - GROUND_PROBE_DEPTH;

        for (Platform platform : world.getPlatforms()) {
            float platformTop = platform.getY()
                + platform.getHeight();

            boolean horizontal = probeX
                >= platform.getX()
                && probeX <= platform.getX()
                + platform.getWidth();

            boolean vertical = probeY
                <= platformTop
                && probeY >= platform.getY() - 3f;

            if (horizontal && vertical) {
                return true;
            }
        }

        return false;
    }

    private void moveGroundEnemy(
        Enemy enemy,
        HorizontalDirection direction,
        float speed,
        float delta
    ) {
        enemy.face(direction);
        enemy.moveInDirection(direction, speed, delta);
    }

    private void turnAround(Enemy enemy) {
        enemy.face(
            enemy.getDirection()
                == HorizontalDirection.LEFT
                ? HorizontalDirection.RIGHT
                : HorizontalDirection.LEFT
        );
    }

    private boolean moveFlyingByVelocity(
        Enemy enemy,
        float delta
    ) {
        float totalX = enemy.getVelocityX() * delta;
        float totalY = enemy.getVelocityY() * delta;
        float distance = (float) Math.sqrt(
            totalX * totalX + totalY * totalY
        );
        int steps = Math.max(
            1,
            (int) Math.ceil(
                distance / FLYING_MAX_SUBSTEP
            )
        );

        float stepX = totalX / steps;
        float stepY = totalY / steps;

        for (int i = 0; i < steps; i++) {
            if (!tryMoveFlying(enemy, stepX, stepY)) {
                return false;
            }
        }

        return true;
    }

    private void moveFlyingToward(
        Enemy enemy,
        float targetX,
        float targetY,
        float speed,
        float delta
    ) {
        float dx = targetX - enemy.getX();
        float dy = targetY - enemy.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= FLYING_STOP_DISTANCE) {
            return;
        }

        float totalStep = Math.min(speed * delta, distance);
        int steps = Math.max(
            1,
            (int) Math.ceil(
                totalStep / FLYING_MAX_SUBSTEP
            )
        );

        float stepX = dx / distance * totalStep / steps;
        float stepY = dy / distance * totalStep / steps;

        for (int i = 0; i < steps; i++) {
            if (!tryMoveFlying(enemy, stepX, stepY)) {
                if (!tryMoveFlying(enemy, stepX, 0f)) {
                    tryMoveFlying(enemy, 0f, stepY);
                }
            }
        }
    }

    private boolean tryMoveFlying(
        Enemy enemy,
        float stepX,
        float stepY
    ) {
        float nextX = clamp(
            enemy.getX() + stepX,
            enemy.getPatrolMinX(),
            enemy.getPatrolMaxX()
                - enemy.getWidth()
        );
        float nextY = clamp(
            enemy.getY() + stepY,
            FLYING_MIN_Y,
            Math.min(
                FLYING_MAX_Y,
                world.getWorldHeight()
                    - enemy.getHeight()
            )
        );

        for (Platform platform : world.getPlatforms()) {
            if (
                CollisionUtils.overlaps(
                    nextX,
                    nextY,
                    enemy.getWidth(),
                    enemy.getHeight(),
                    platform.getX(),
                    platform.getY(),
                    platform.getWidth(),
                    platform.getHeight()
                )
            ) {
                return false;
            }
        }

        enemy.setPosition(nextX, nextY);
        return true;
    }

    private boolean canHornheadSeePlayer(Enemy enemy) {
        return canSeePlayerInFront(
            enemy,
            HORNHEAD_VISION_RANGE,
            HORNHEAD_VISION_HEIGHT
        );
    }

    private boolean canGuardianSeePlayer(Enemy enemy) {
        return canSeePlayerInFront(
            enemy,
            GUARDIAN_VISION_RANGE,
            GUARDIAN_VISION_HEIGHT
        );
    }

    private boolean canSeePlayerInFront(
        Enemy enemy,
        float range,
        float heightTolerance
    ) {
        Player player = world.getPlayer();

        if (
            Math.abs(
                player.getCenterY()
                    - enemy.getCenterY()
            ) > heightTolerance
        ) {
            return false;
        }

        if (
            enemy.getDirection()
                == HorizontalDirection.RIGHT
        ) {
            return player.getCenterX()
                > enemy.getCenterX()
                && player.getCenterX()
                <= enemy.getCenterX() + range;
        }

        return player.getCenterX()
            < enemy.getCenterX()
            && player.getCenterX()
            >= enemy.getCenterX() - range;
    }

    private boolean isPlayerNear(
        Enemy enemy,
        float range
    ) {
        Player player = world.getPlayer();
        float dx = player.getCenterX()
            - enemy.getCenterX();
        float dy = player.getCenterY()
            - enemy.getCenterY();

        return dx * dx + dy * dy <= range * range;
    }

    private void facePlayer(Enemy enemy) {
        Player player = world.getPlayer();
        enemy.face(
            player.getCenterX() < enemy.getCenterX()
                ? HorizontalDirection.LEFT
                : HorizontalDirection.RIGHT
        );
    }

    private void fireGuardianLaser(Enemy enemy) {
        float laserX = enemy.getDirection()
            == HorizontalDirection.RIGHT
            ? enemy.getX() + enemy.getWidth()
            : enemy.getX() - GUARDIAN_LASER_RANGE;

        float laserY = enemy.getCenterY()
            - GUARDIAN_LASER_HEIGHT / 2f;

        world.addEnemyLaser(new EnemyLaser(
            enemy.getName(),
            laserX,
            laserY,
            GUARDIAN_LASER_RANGE,
            GUARDIAN_LASER_HEIGHT,
            GUARDIAN_LASER_DURATION,
            GUARDIAN_LASER_DAMAGE
        ));
    }

    private float clamp(
        float value,
        float min,
        float max
    ) {
        return Math.max(min, Math.min(max, value));
    }
}
