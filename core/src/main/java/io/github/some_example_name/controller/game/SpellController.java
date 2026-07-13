package io.github.some_example_name.controller.game;

import io.github.some_example_name.controller.achievement.AchievementController;
import io.github.some_example_name.controller.achievement.EnemyKillTracker;
import io.github.some_example_name.controller.audio.AudioManager;
import io.github.some_example_name.controller.inventory.InventoryController;
import io.github.some_example_name.model.game.Enemy;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.HowlingWraithsAttack;
import io.github.some_example_name.model.game.Platform;
import io.github.some_example_name.model.game.SpellProjectile;
import io.github.some_example_name.model.input.GameInputState;
import io.github.some_example_name.model.player.PlayerStats;
import io.github.some_example_name.util.CollisionUtils;

public class SpellController {
    private static final int SPELL_COST = 33;
    private static final int VENGEFUL_SPIRIT_DAMAGE = 2;
    private static final float PROJECTILE_SPEED = 560f;
    private static final float PROJECTILE_DURATION = 0.9f;
    private static final int HOWLING_WRAITHS_DAMAGE_PER_HIT = 1;
    private static final float HOWLING_WRAITHS_DURATION = 0.32f;
    private static final float SPELL_COOLDOWN_DURATION = 1f;
    private static final float HOWLING_WRAITHS_HORIZONTAL_PADDING = 35f;
    private static final float HOWLING_WRAITHS_VERTICAL_PADDING = 18f;

    private final GameWorld world;
    private final PlayerStats playerStats;
    private final InventoryController inventoryController;
    private final EnemyDeathHandler enemyDeathHandler;
    private final AudioManager audioManager;

    private float spellCooldownTimer;
    private float cooldownMessageTimer;

    public SpellController(
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
        this.audioManager = audioManager;
        this.enemyDeathHandler = new EnemyDeathHandler(world, achievementController, enemyKillTracker);
    }

    public String update(float delta, GameInputState inputState) {
        updateSpellCooldown(delta);
        updateProjectiles(delta);
        updateHowlingWraiths(delta);

        if (inputState.isHowlingWraithsJustPressed()) return tryCastHowlingWraiths();
        if (inputState.isSpellJustPressed()) return tryCastVengefulSpirit();
        return "";
    }

    private void updateSpellCooldown(float delta) {
        spellCooldownTimer = Math.max(0f, spellCooldownTimer - delta);
        cooldownMessageTimer = Math.max(0f, cooldownMessageTimer - delta);
    }

    private void updateProjectiles(float delta) {
        for (SpellProjectile projectile : world.getSpellProjectiles()) {
            projectile.update(delta);
            checkProjectileWallCollision(projectile);
            checkProjectileHits(projectile);
        }
        world.clearInactiveSpellProjectiles();
    }

    private void checkProjectileWallCollision(SpellProjectile projectile) {
        if (!projectile.isActive()) return;
        for (Platform platform : world.getPlatforms()) {
            if (CollisionUtils.overlaps(
                projectile.getX(), projectile.getY(), projectile.getWidth(), projectile.getHeight(),
                platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight()
            )) {
                projectile.deactivate();
                return;
            }
        }
    }

    private void updateHowlingWraiths(float delta) {
        HowlingWraithsAttack attack = world.getActiveHowlingWraithsAttack().orElse(null);
        if (attack == null) return;
        attack.update(delta);
        checkHowlingWraithsHits(attack);
        world.clearInactiveHowlingWraithsAttack();
    }

    private String tryCastVengefulSpirit() {
        if (spellCooldownTimer > 0f) return getCooldownMessage();
        if (!playerStats.spendSoul(SPELL_COST)) return "Not enough Soul.";

        int damage = inventoryController.applySpellDamageEffects(VENGEFUL_SPIRIT_DAMAGE);
        world.addSpellProjectile(new SpellProjectile(
            world.getPlayer(), PROJECTILE_SPEED, PROJECTILE_DURATION, damage
        ));

        audioManager.playVengefulSpiritCast();
        world.requestCameraShake(0.10f, 3.5f);
        spellCooldownTimer = SPELL_COOLDOWN_DURATION;
        return "Vengeful Spirit!";
    }

    private String tryCastHowlingWraiths() {
        if (spellCooldownTimer > 0f) return getCooldownMessage();
        if (!playerStats.spendSoul(SPELL_COST)) return "Not enough Soul.";

        int damage = inventoryController.applySpellDamageEffects(HOWLING_WRAITHS_DAMAGE_PER_HIT);
        world.setActiveHowlingWraithsAttack(new HowlingWraithsAttack(
            world.getPlayer(), HOWLING_WRAITHS_DURATION, damage
        ));

        audioManager.playHowlingWraithsCast();
        world.requestCameraShake(0.16f, 5.5f);
        spellCooldownTimer = SPELL_COOLDOWN_DURATION;
        return "Howling Wraiths!";
    }

    private void checkProjectileHits(SpellProjectile projectile) {
        if (!projectile.isActive()) return;
        for (Enemy enemy : world.getEnemies()) {
            if (!enemy.isAlive()) continue;
            String key = createEnemyDamageKey(enemy);
            if (projectile.hasDamaged(key)) continue;

            if (!CollisionUtils.overlaps(
                projectile.getX(), projectile.getY(), projectile.getWidth(), projectile.getHeight(),
                enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
            )) continue;

            enemy.takeDamage(projectile.getDamage());
            audioManager.playCreatureHurt();
            projectile.markDamaged(key);
            if (!enemy.isAlive()) audioManager.playEnemyDeath();
            enemyDeathHandler.handleDeathIfNeeded(enemy);
        }
    }

    private void checkHowlingWraithsHits(HowlingWraithsAttack attack) {
        if (!attack.isActive()) return;

        float x = attack.getX() - HOWLING_WRAITHS_HORIZONTAL_PADDING;
        float y = attack.getY() - HOWLING_WRAITHS_VERTICAL_PADDING;
        float width = attack.getWidth() + HOWLING_WRAITHS_HORIZONTAL_PADDING * 2f;
        float height = attack.getHeight() + HOWLING_WRAITHS_VERTICAL_PADDING * 2f;

        for (Enemy enemy : world.getEnemies()) {
            if (!enemy.isAlive()) continue;
            String key = createEnemyDamageKey(enemy);
            if (!attack.canDamage(key)) continue;

            if (!CollisionUtils.overlaps(
                x, y, width, height,
                enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
            )) continue;

            enemy.takeDamage(attack.getDamage());
            audioManager.playCreatureHurt();
            attack.markDamaged(key);
            if (!enemy.isAlive()) audioManager.playEnemyDeath();
            enemyDeathHandler.handleDeathIfNeeded(enemy);
        }
    }

    private String createEnemyDamageKey(Enemy enemy) {
        return enemy.getName() + "-" + System.identityHashCode(enemy);
    }

    private String getCooldownMessage() {
        if (cooldownMessageTimer > 0f) return "";
        cooldownMessageTimer = 0.35f;
        return "Spell is cooling down.";
    }
}
