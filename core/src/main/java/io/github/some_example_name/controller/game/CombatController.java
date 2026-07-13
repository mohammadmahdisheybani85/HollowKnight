package io.github.some_example_name.controller.game;

import io.github.some_example_name.controller.achievement.AchievementController;
import io.github.some_example_name.controller.achievement.EnemyKillTracker;
import io.github.some_example_name.controller.audio.AudioManager;
import io.github.some_example_name.controller.inventory.InventoryController;
import io.github.some_example_name.model.achievement.AchievementType;
import io.github.some_example_name.model.game.AttackDirection;
import io.github.some_example_name.model.game.BreakableWall;
import io.github.some_example_name.model.game.Enemy;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.EnemyType;
import io.github.some_example_name.model.game.Hazard;
import io.github.some_example_name.model.game.HorizontalDirection;
import io.github.some_example_name.model.game.NailAttack;
import io.github.some_example_name.model.game.Player;
import io.github.some_example_name.model.game.SecretPickup;
import io.github.some_example_name.model.input.GameInputState;
import io.github.some_example_name.model.inventory.CharmType;
import io.github.some_example_name.model.player.PlayerStats;
import io.github.some_example_name.util.CollisionUtils;

import java.util.HashSet;
import java.util.Set;

public class CombatController {
    private static final float ATTACK_DURATION = 0.12f;
    private static final float ATTACK_COOLDOWN = 0.32f;
    private static final int BASE_NAIL_DAMAGE = 2;
    private static final float POGO_SPEED = 590f;
    private static final float POGO_VISUAL_DURATION = 0.24f;
    private static final int SOUL_MASTER_REQUIRED_SOUL = 99;

    private static final int SHARP_SHADOW_DAMAGE = 1;
    private static final float NORMAL_ENEMY_KNOCKBACK = 32f;
    private static final float HEAVY_BLOW_KNOCKBACK = 78f;

    private final GameWorld world;
    private final PlayerStats playerStats;
    private final InventoryController inventoryController;
    private final AchievementController achievementController;
    private final EnemyDeathHandler enemyDeathHandler;
    private final AudioManager audioManager;

    private final Set<Enemy> enemiesHitByCurrentAttack = new HashSet<>();
    private final Set<Enemy> enemiesHitByCurrentDash = new HashSet<>();

    private float attackCooldownLeft;
    private boolean currentAttackTriggeredPogo;
    private boolean wallHitByCurrentAttack;
    private boolean wasDashing;

    public CombatController(
        GameWorld world,
        PlayerStats playerStats,
        InventoryController inventoryController,
        AchievementController achievementController,
        EnemyKillTracker enemyKillTracker,
        AudioManager audioManager
    ) {
        this.world = world;
        this.playerStats = playerStats;
        this.inventoryController = inventoryController;
        this.achievementController = achievementController;
        this.audioManager = audioManager;
        this.enemyDeathHandler = new EnemyDeathHandler(
            world,
            achievementController,
            enemyKillTracker
        );
    }

    public void update(float delta, GameInputState inputState) {
        updateCooldown(delta);
        updateActiveAttack(delta);
        tryStartAttack(inputState);
        checkAttackHits();
        checkBreakableWallHit();
        checkPogoOnHazards();
        checkSharpShadowHits();
        checkSecretPickup();
    }

    private void updateCooldown(float delta) {
        attackCooldownLeft = Math.max(0f, attackCooldownLeft - delta);
    }

    private void updateActiveAttack(float delta) {
        world.getActiveNailAttack().ifPresent(attack -> attack.update(delta));
        world.clearInactiveNailAttack();
    }

    private void tryStartAttack(GameInputState inputState) {
        if (!inputState.isAttackJustPressed() || attackCooldownLeft > 0f) {
            return;
        }

        Player player = world.getPlayer();
        AttackDirection direction = resolveAttackDirection(inputState);

        world.setActiveNailAttack(
            new NailAttack(player, ATTACK_DURATION, direction)
        );

        audioManager.playNailSlash();
        attackCooldownLeft = inventoryController.applyAttackCooldownEffects(ATTACK_COOLDOWN);
        enemiesHitByCurrentAttack.clear();
        currentAttackTriggeredPogo = false;
        wallHitByCurrentAttack = false;
    }

    private AttackDirection resolveAttackDirection(GameInputState inputState) {
        if (inputState.isLookDownHeld()) {
            return AttackDirection.DOWN;
        }
        if (inputState.isLookUpHeld()) {
            return AttackDirection.UP;
        }
        return AttackDirection.HORIZONTAL;
    }

    private void checkAttackHits() {
        world.getActiveNailAttack().ifPresent(attack -> {
            for (Enemy enemy : world.getEnemies()) {
                if (!enemy.isAlive() || enemiesHitByCurrentAttack.contains(enemy)) {
                    continue;
                }

                boolean hit = CollisionUtils.overlaps(
                    attack.getX(), attack.getY(), attack.getWidth(), attack.getHeight(),
                    enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
                );

                if (!hit) {
                    continue;
                }

                hitEnemy(enemy, attack.getDirection());
                enemiesHitByCurrentAttack.add(enemy);

                if (attack.getDirection() == AttackDirection.DOWN) {
                    performPogo();
                }
            }
        });
    }


    private void checkBreakableWallHit() {
        if (wallHitByCurrentAttack) {
            return;
        }

        BreakableWall wall = world
            .getBreakableWall()
            .orElse(null);

        if (wall == null || wall.isBroken()) {
            return;
        }

        world.getActiveNailAttack().ifPresent(attack -> {
            boolean hit = CollisionUtils.overlaps(
                attack.getX(),
                attack.getY(),
                attack.getWidth(),
                attack.getHeight(),
                wall.getX(),
                wall.getY(),
                wall.getWidth(),
                wall.getHeight()
            );

            if (!hit) {
                return;
            }

            wallHitByCurrentAttack = true;
            audioManager.playStoneBreak();
            world.hitBreakableWall();
        });
    }

    private void checkSecretPickup() {
        SecretPickup pickup = world
            .getSecretPickup()
            .orElse(null);

        if (pickup == null || pickup.isCollected()) {
            return;
        }

        Player player = world.getPlayer();
        boolean touching = CollisionUtils.overlaps(
            player.getX(),
            player.getY(),
            player.getWidth(),
            player.getHeight(),
            pickup.getX(),
            pickup.getY(),
            pickup.getWidth(),
            pickup.getHeight()
        );

        if (!touching) {
            return;
        }

        world.collectSecretPickup();
        inventoryController.unlockCharm(
            CharmType.VOID_HEART
        );
        audioManager.playSoulGain();
    }

    private void checkPogoOnHazards() {
        if (currentAttackTriggeredPogo) {
            return;
        }

        world.getActiveNailAttack().ifPresent(attack -> {
            if (attack.getDirection() != AttackDirection.DOWN) {
                return;
            }

            for (Hazard hazard : world.getHazards()) {
                boolean hit = CollisionUtils.overlaps(
                    attack.getX(), attack.getY(), attack.getWidth(), attack.getHeight(),
                    hazard.getX(), hazard.getY(), hazard.getWidth(), hazard.getHeight()
                );

                if (hit) {
                    performPogo();
                    return;
                }
            }
        });
    }

    private void performPogo() {
        if (currentAttackTriggeredPogo) {
            return;
        }

        world.getPlayer().performPogo(POGO_SPEED, POGO_VISUAL_DURATION);
        currentAttackTriggeredPogo = true;
    }

    private void hitEnemy(Enemy enemy, AttackDirection attackDirection) {
        int nailDamage = inventoryController.applyNailDamageEffects(BASE_NAIL_DAMAGE);
        enemy.takeDamage(nailDamage);

        audioManager.playCreatureHurt();
        audioManager.playAxeImpact();
        gainSoulFromHit();
        applyEnemyKnockback(enemy, attackDirection);

        if (!enemy.isAlive()) {
            audioManager.playEnemyDeath();
        }

        enemyDeathHandler.handleDeathIfNeeded(enemy);
    }

    private void applyEnemyKnockback(Enemy enemy, AttackDirection attackDirection) {
        if (enemy.getType() == EnemyType.FALSE_KNIGHT) {
            return;
        }

        float distance = inventoryController.isEquipped(CharmType.HEAVY_BLOW)
            ? HEAVY_BLOW_KNOCKBACK
            : NORMAL_ENEMY_KNOCKBACK;

        HorizontalDirection direction;
        if (attackDirection == AttackDirection.HORIZONTAL) {
            direction = world.getPlayer().getFacingDirection();
        } else {
            direction = enemy.getCenterX() >= world.getPlayer().getCenterX()
                ? HorizontalDirection.RIGHT
                : HorizontalDirection.LEFT;
        }

        enemy.applyInstantKnockback(distance, direction);
    }

    private void checkSharpShadowHits() {
        Player player = world.getPlayer();
        boolean dashing = player.isDashing();

        if (!dashing) {
            if (wasDashing) {
                enemiesHitByCurrentDash.clear();
            }
            wasDashing = false;
            return;
        }

        if (!inventoryController.isEquipped(CharmType.SHARP_SHADOW)) {
            wasDashing = true;
            return;
        }

        if (!wasDashing) {
            enemiesHitByCurrentDash.clear();
        }
        wasDashing = true;

        for (Enemy enemy : world.getEnemies()) {
            if (!enemy.isAlive() || enemiesHitByCurrentDash.contains(enemy)) {
                continue;
            }

            boolean hit = CollisionUtils.overlaps(
                player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
            );

            if (!hit) {
                continue;
            }

            enemy.takeDamage(SHARP_SHADOW_DAMAGE);
            audioManager.playCreatureHurt();
            enemiesHitByCurrentDash.add(enemy);

            if (!enemy.isAlive()) {
                audioManager.playEnemyDeath();
            }

            enemyDeathHandler.handleDeathIfNeeded(enemy);
        }
    }

    private void gainSoulFromHit() {
        playerStats.gainSoul(inventoryController.getSoulGainOnEnemyHit());
        audioManager.playSoulGain();
        checkSoulMasterAchievement();
    }

    private void checkSoulMasterAchievement() {
        if (playerStats.getSoul() >= SOUL_MASTER_REQUIRED_SOUL) {
            achievementController.unlock(AchievementType.SOUL_MASTER);
        }
    }
}
