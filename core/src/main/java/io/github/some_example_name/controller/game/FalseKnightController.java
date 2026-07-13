package io.github.some_example_name.controller.game;

import io.github.some_example_name.model.game.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FalseKnightController {
    private static final String FALSE_KNIGHT_NAME = "False Knight";
    private static final float GROUND_Y = 120f;
    private static final float ARENA_MIN_X = 120f;
    private static final float ARENA_MAX_X = 1665f;

    private static final float PHASE_TWO_THRESHOLD = 0.5f;
    private static final float STUN_TIME = 2.0f;
    private static final float PATROL_TIME = 0.75f;
    private static final float MACE_SLAM_TIME = 0.72f;
    private static final float CHARGE_TIME = 0.82f;
    private static final float LEAP_TIME = 0.92f;
    private static final float POWER_SLAM_TIME = 1.05f;

    private final GameWorld world;
    private final Random random = new Random();

    private BossMoveType currentMove = BossMoveType.PATROL;
    private BossMoveType previousMove = BossMoveType.PATROL;
    private float moveElapsed;
    private float moveDuration = PATROL_TIME;
    private float startX;
    private float startY;
    private float targetX;
    private boolean phaseTransitionTriggered;
    private boolean attackSpawned;

    public FalseKnightController(GameWorld world) {
        this.world = world;
    }

    public void update(float delta) {
        world.updateBossEffects(delta);

        if (world.getCurrentArea() != WorldArea.BOSS_ARENA) {
            return;
        }

        Enemy boss = world.findEnemyByName(FALSE_KNIGHT_NAME);
        if (boss == null) return;

        if (!boss.isAlive()) {
            world.openBossGates();
            return;
        }

        boolean phaseTwo = boss.getHealth()
            <= Math.max(1, Math.round(boss.getMaxHealth() * PHASE_TWO_THRESHOLD));

        if (phaseTwo && !phaseTransitionTriggered) {
            phaseTransitionTriggered = true;
            boss.setBossPhaseTwo(true);
            startStun(boss);
        }

        float speedScale = boss.isBossPhaseTwo() ? 1.35f : 1f;
        moveElapsed += delta * speedScale;

        switch (currentMove) {
            case PATROL -> updatePatrol(boss, delta * speedScale);
            case MACE_SLAM -> updateMaceSlam(boss);
            case CHARGE -> updateCharge(boss, delta * speedScale);
            case OFFENSIVE_LEAP -> updateLeap(boss, false);
            case DEFENSIVE_LEAP -> updateLeap(boss, true);
            case POWER_SLAM -> updatePowerSlam(boss);
            case STUNNED -> updateStunned(boss);
        }
    }

    private void updatePatrol(Enemy boss, float delta) {
        facePlayer(boss);
        boss.moveInDirection(boss.getDirection(), 72f, delta);
        boss.setPosition(clampBossX(boss.getX(), boss), GROUND_Y);

        if (moveElapsed >= moveDuration) {
            chooseNextMove(boss);
        }
    }

    private void updateMaceSlam(Enemy boss) {
        float progress = progress();
        if (!attackSpawned && progress >= 0.48f) {
            attackSpawned = true;
            float attackX = boss.getDirection() == HorizontalDirection.RIGHT
                ? boss.getX() + boss.getWidth() - 4f
                : boss.getX() - 96f;
            world.addBossAttack(new BossAttack(
                attackX,
                GROUND_Y,
                100f,
                72f,
                1,
                0.18f
            ));
            world.requestCameraShake(0.16f, 7f);
        }
        if (progress >= 1f) startPatrol(boss);
    }

    private void updateCharge(Enemy boss, float delta) {
        float speed = boss.isBossPhaseTwo() ? 500f : 380f;
        boss.moveInDirection(boss.getChargeDirection(), speed, delta);
        boss.setPosition(clampBossX(boss.getX(), boss), GROUND_Y);

        boolean atWall = boss.getX() <= ARENA_MIN_X + 1f
            || boss.getX() + boss.getWidth() >= ARENA_MAX_X - 1f;
        if (atWall || progress() >= 1f) {
            world.requestCameraShake(0.14f, 5f);
            startPatrol(boss);
        }
    }

    private void updateLeap(Enemy boss, boolean defensive) {
        float p = progress();
        float arc = 4f * p * (1f - p);
        float height = defensive ? 125f : 190f;
        float x = startX + (targetX - startX) * p;
        float y = GROUND_Y + height * arc;
        boss.setPosition(clampBossX(x, boss), y);

        if (!attackSpawned && p >= 0.76f && !defensive) {
            attackSpawned = true;
            world.addBossAttack(new BossAttack(
                boss.getX() - 30f,
                GROUND_Y,
                boss.getWidth() + 60f,
                85f,
                1,
                0.22f
            ));
        }

        if (p >= 1f) {
            boss.setPosition(clampBossX(targetX, boss), GROUND_Y);
            if (!defensive) world.requestCameraShake(0.22f, 9f);
            startPatrol(boss);
        }
    }

    private void updatePowerSlam(Enemy boss) {
        float p = progress();
        if (p < 0.42f) {
            float rise = p / 0.42f;
            boss.setPosition(startX, GROUND_Y + 235f * rise);
            return;
        }

        float fall = (p - 0.42f) / 0.58f;
        boss.setPosition(startX, GROUND_Y + 235f * (1f - fall));

        if (!attackSpawned && p >= 0.84f) {
            attackSpawned = true;
            boss.setPosition(startX, GROUND_Y);
            HorizontalDirection left = HorizontalDirection.LEFT;
            HorizontalDirection right = HorizontalDirection.RIGHT;
            world.addBossShockwave(new BossShockwave(
                boss.getCenterX() - 40f,
                GROUND_Y,
                80f,
                38f,
                2,
                left,
                boss.isBossPhaseTwo() ? 470f : 390f,
                2.4f
            ));
            world.addBossShockwave(new BossShockwave(
                boss.getCenterX() - 40f,
                GROUND_Y,
                80f,
                38f,
                2,
                right,
                boss.isBossPhaseTwo() ? 470f : 390f,
                2.4f
            ));
            world.requestCameraShake(0.42f, 14f);
        }

        if (p >= 1f) startPatrol(boss);
    }

    private void updateStunned(Enemy boss) {
        boss.setPosition(boss.getX(), GROUND_Y);
        boss.setBossArmorOpen(true);
        if (progress() >= 1f) {
            boss.setBossArmorOpen(false);
            startPatrol(boss);
        }
    }

    private void chooseNextMove(Enemy boss) {
        Player player = world.getPlayer();
        float distance = Math.abs(player.getCenterX() - boss.getCenterX());

        List<BossMoveType> candidates = new ArrayList<>();
        if (distance < 150f) {
            candidates.add(BossMoveType.MACE_SLAM);
            candidates.add(BossMoveType.DEFENSIVE_LEAP);
            candidates.add(BossMoveType.CHARGE);
        } else if (distance < 420f) {
            candidates.add(BossMoveType.CHARGE);
            candidates.add(BossMoveType.OFFENSIVE_LEAP);
            candidates.add(BossMoveType.MACE_SLAM);
        } else {
            candidates.add(BossMoveType.OFFENSIVE_LEAP);
            candidates.add(BossMoveType.CHARGE);
        }

        if (boss.isBossPhaseTwo()) {
            candidates.add(BossMoveType.POWER_SLAM);
            candidates.add(BossMoveType.POWER_SLAM);
        }

        candidates.remove(previousMove);
        if (candidates.isEmpty()) candidates.add(BossMoveType.CHARGE);

        BossMoveType selected = candidates.get(random.nextInt(candidates.size()));
        startMove(boss, selected);
    }

    private void startMove(Enemy boss, BossMoveType move) {
        previousMove = currentMove;
        currentMove = move;
        boss.setBossMoveType(move);
        moveElapsed = 0f;
        attackSpawned = false;
        startX = boss.getX();
        startY = boss.getY();
        facePlayer(boss);

        switch (move) {
            case MACE_SLAM -> {
                moveDuration = MACE_SLAM_TIME;
                boss.setState(EnemyState.PREPARING_ATTACK, moveDuration);
                world.requestCameraShake(0.08f, 2.5f);
            }
            case CHARGE -> {
                moveDuration = CHARGE_TIME;
                boss.setChargeDirection(boss.getDirection());
                boss.setState(EnemyState.CHARGING, moveDuration);
                world.requestCameraShake(0.08f, 2.0f);
            }
            case OFFENSIVE_LEAP -> {
                moveDuration = LEAP_TIME;
                targetX = clampBossX(world.getPlayer().getX() - boss.getWidth() / 2f, boss);
                boss.setState(EnemyState.PREPARING_ATTACK, moveDuration);
                world.requestCameraShake(0.09f, 3.0f);
            }
            case DEFENSIVE_LEAP -> {
                moveDuration = LEAP_TIME * 0.82f;
                float away = boss.getDirection() == HorizontalDirection.RIGHT ? -280f : 280f;
                targetX = clampBossX(boss.getX() + away, boss);
                boss.setState(EnemyState.CHARGING, moveDuration);
                world.requestCameraShake(0.07f, 1.8f);
            }
            case POWER_SLAM -> {
                moveDuration = POWER_SLAM_TIME;
                boss.setState(EnemyState.PREPARING_ATTACK, moveDuration);
                world.requestCameraShake(0.12f, 4.0f);
            }
            default -> startPatrol(boss);
        }
    }

    private void startPatrol(Enemy boss) {
        currentMove = BossMoveType.PATROL;
        boss.setBossMoveType(BossMoveType.PATROL);
        boss.setBossArmorOpen(false);
        boss.setState(EnemyState.PATROLLING, PATROL_TIME);
        moveElapsed = 0f;
        moveDuration = boss.isBossPhaseTwo() ? PATROL_TIME * 0.65f : PATROL_TIME;
        attackSpawned = false;
    }

    private void startStun(Enemy boss) {
        currentMove = BossMoveType.STUNNED;
        boss.setBossMoveType(BossMoveType.STUNNED);
        boss.setBossArmorOpen(true);
        boss.setState(EnemyState.RESTING, STUN_TIME);
        moveElapsed = 0f;
        moveDuration = STUN_TIME;
        attackSpawned = false;
        world.requestCameraShake(0.30f, 11f);
    }

    private void facePlayer(Enemy boss) {
        if (world.getPlayer().getCenterX() < boss.getCenterX()) {
            boss.face(HorizontalDirection.LEFT);
        } else {
            boss.face(HorizontalDirection.RIGHT);
        }
    }

    private float progress() {
        return Math.min(1f, moveElapsed / Math.max(0.001f, moveDuration));
    }

    private float clampBossX(float x, Enemy boss) {
        return Math.max(ARENA_MIN_X, Math.min(ARENA_MAX_X - boss.getWidth(), x));
    }
}
