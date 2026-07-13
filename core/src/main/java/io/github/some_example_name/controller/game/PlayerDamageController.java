package io.github.some_example_name.controller.game;

import io.github.some_example_name.controller.audio.AudioManager;
import io.github.some_example_name.controller.inventory.InventoryController;
import io.github.some_example_name.model.game.AttackDirection;
import io.github.some_example_name.model.game.BossAttack;
import io.github.some_example_name.model.game.BossShockwave;
import io.github.some_example_name.model.game.Enemy;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.Hazard;
import io.github.some_example_name.model.game.NailAttack;
import io.github.some_example_name.model.game.Player;
import io.github.some_example_name.model.inventory.CharmType;
import io.github.some_example_name.model.player.PlayerStats;
import io.github.some_example_name.util.CollisionUtils;

public class PlayerDamageController {
    private static final int ENEMY_TOUCH_DAMAGE = 1;
    private static final int FALL_DAMAGE = 1;
    private static final float FALL_LIMIT_Y = -160f;
    private static final float KNOCKBACK_SPEED_X = 360f;
    private static final float KNOCKBACK_SPEED_Y = 320f;
    private static final float KNOCKBACK_DURATION = 0.25f;

    private final GameWorld world;
    private final PlayerStats playerStats;
    private final InventoryController inventoryController;
    private final AudioManager audioManager;
    private String lastDamageMessage = "";

    public PlayerDamageController(
        GameWorld world,
        PlayerStats playerStats,
        InventoryController inventoryController,
        AudioManager audioManager
    ) {
        this.world = world;
        this.playerStats = playerStats;
        this.inventoryController = inventoryController;
        this.audioManager = audioManager;
    }

    public void update(float delta) {
        lastDamageMessage = "";
        playerStats.update(delta);
        rememberSafePositionIfPossible();
        handleEnemyTouchDamage();
        handleBossAttackDamage();
        handleBossShockwaveDamage();
        handleHazardDamage();
        handleFallDamage();
    }

    private void rememberSafePositionIfPossible() {
        Player player = world.getPlayer();
        if (!player.isOnGround() || isTouchingAnyHazard() || isTouchingAnyEnemy()) {
            return;
        }
        world.rememberSafePlayerPosition();
    }

    private void handleEnemyTouchDamage() {
        Player player = world.getPlayer();

        if (player.isDashing() && inventoryController.isEquipped(CharmType.SHARP_SHADOW)) {
            return;
        }

        for (Enemy enemy : world.getEnemies()) {
            if (!enemy.isAlive()) {
                continue;
            }

            boolean touchingEnemy = CollisionUtils.overlaps(
                player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
            );

            if (!touchingEnemy || isProtectedByDownAttack(enemy)) {
                continue;
            }

            DamageResult damageResult = damagePlayer(ENEMY_TOUCH_DAMAGE);

            if (damageResult == DamageResult.DAMAGED) {
                lastDamageMessage = "Enemy hit!";
                applyEnemyKnockback(player, enemy);
            } else if (damageResult == DamageResult.DEAD) {
                lastDamageMessage = "You died.";
                world.respawnPlayerAtLastSafePosition();
            }
            return;
        }
    }

    private boolean isProtectedByDownAttack(Enemy enemy) {
        NailAttack attack = world.getActiveNailAttack().orElse(null);
        if (attack == null || attack.getDirection() != AttackDirection.DOWN) {
            return false;
        }

        return CollisionUtils.overlaps(
            attack.getX(), attack.getY(), attack.getWidth(), attack.getHeight(),
            enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
        );
    }



    private void handleBossAttackDamage() {
        Player player = world.getPlayer();
        for (BossAttack attack : world.getBossAttacks()) {
            if (!attack.isActive() || attack.hasHitPlayer()) continue;
            if (!CollisionUtils.overlaps(
                player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                attack.getX(), attack.getY(), attack.getWidth(), attack.getHeight()
            )) continue;

            attack.markPlayerHit();
            DamageResult result = damagePlayer(attack.getDamage());
            if (result == DamageResult.DAMAGED) {
                lastDamageMessage = "Boss attack!";
                world.requestCameraShake(0.18f, 8f);
            } else if (result == DamageResult.DEAD) {
                lastDamageMessage = "You died.";
                world.respawnPlayerAtLastSafePosition();
            }
            return;
        }
    }

    private void handleBossShockwaveDamage() {
        Player player = world.getPlayer();
        for (BossShockwave shockwave : world.getBossShockwaves()) {
            if (!shockwave.isActive() || shockwave.hasHitPlayer()) continue;
            if (!CollisionUtils.overlaps(
                player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                shockwave.getX(), shockwave.getY(), shockwave.getWidth(), shockwave.getHeight()
            )) continue;

            shockwave.markPlayerHit();
            DamageResult result = damagePlayer(shockwave.getDamage());
            if (result == DamageResult.DAMAGED) {
                lastDamageMessage = "Shockwave!";
                world.requestCameraShake(0.22f, 10f);
            } else if (result == DamageResult.DEAD) {
                lastDamageMessage = "You died.";
                world.respawnPlayerAtLastSafePosition();
            }
            return;
        }
    }

    private void handleHazardDamage() {
        Player player = world.getPlayer();
        if (player.isDashing() && inventoryController.isEquipped(CharmType.SHARP_SHADOW)) {
            return;
        }

        Hazard touchingHazard = getTouchingHazard();
        if (touchingHazard == null || isPogoProtectedFromHazard(touchingHazard)) {
            return;
        }

        DamageResult damageResult = damagePlayer(touchingHazard.getDamage());

        if (damageResult != DamageResult.NO_DAMAGE) {
            lastDamageMessage = "Hazard damage!";
            world.respawnPlayerAtLastSafePosition();
        }
    }

    private boolean isPogoProtectedFromHazard(Hazard hazard) {
        NailAttack attack = world.getActiveNailAttack().orElse(null);
        if (attack == null || attack.getDirection() != AttackDirection.DOWN) {
            return false;
        }

        return CollisionUtils.overlaps(
            attack.getX(), attack.getY(), attack.getWidth(), attack.getHeight(),
            hazard.getX(), hazard.getY(), hazard.getWidth(), hazard.getHeight()
        );
    }

    private void handleFallDamage() {
        Player player = world.getPlayer();
        if (player.getY() >= FALL_LIMIT_Y) {
            return;
        }

        lastDamageMessage = "You fell into a pit.";
        damagePlayer(FALL_DAMAGE);
        world.respawnPlayerAtLastSafePosition();
    }

    private boolean isTouchingAnyEnemy() {
        Player player = world.getPlayer();
        for (Enemy enemy : world.getEnemies()) {
            if (enemy.isAlive() && CollisionUtils.overlaps(
                player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
            )) {
                return true;
            }
        }
        return false;
    }

    private boolean isTouchingAnyHazard() {
        return getTouchingHazard() != null;
    }

    private Hazard getTouchingHazard() {
        Player player = world.getPlayer();
        for (Hazard hazard : world.getHazards()) {
            if (CollisionUtils.overlaps(
                player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                hazard.getX(), hazard.getY(), hazard.getWidth(), hazard.getHeight()
            )) {
                return hazard;
            }
        }
        return null;
    }

    private DamageResult damagePlayer(int damage) {
        boolean damageTaken = playerStats.takeDamage(damage);
        if (!damageTaken) {
            return DamageResult.NO_DAMAGE;
        }

        world.getPlayer().setFocusing(false);
        audioManager.playCreatureHurt();
        world.requestCameraShake(0.16f, 6f);

        if (playerStats.isDead()) {
            playerStats.respawnAfterDeath();
            audioManager.playDeathStinger();
            return DamageResult.DEAD;
        }
        return DamageResult.DAMAGED;
    }

    private void applyEnemyKnockback(Player player, Enemy enemy) {
        float direction = player.getCenterX() < enemy.getCenterX() ? -1f : 1f;
        player.startKnockback(
            inventoryController.applyKnockbackEffects(KNOCKBACK_SPEED_X) * direction,
            inventoryController.applyKnockbackEffects(KNOCKBACK_SPEED_Y),
            KNOCKBACK_DURATION
        );
    }

    private enum DamageResult { NO_DAMAGE, DAMAGED, DEAD }

    public String consumeLastDamageMessage() {
        String message = lastDamageMessage;
        lastDamageMessage = "";
        return message;
    }
}
