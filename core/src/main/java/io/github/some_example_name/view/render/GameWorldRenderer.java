package io.github.some_example_name.view.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.controller.inventory.InventoryController;
import io.github.some_example_name.model.game.*;
import io.github.some_example_name.model.inventory.CharmType;
import io.github.some_example_name.model.player.PlayerStats;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class GameWorldRenderer {
    private static final float PLAYER_BLINK_SPEED = 12f;
    private static final float PLAYER_VISUAL_SCALE = 1.78f;
    private static final float NORMAL_ENEMY_VISUAL_SCALE = 1.72f;
    private static final float BOSS_VISUAL_SCALE = 1.76f;
    private static final float SMALL_PLATFORM_WIDTH_LIMIT = 230f;
    private static final float MOVEMENT_EPSILON = 1f;
    private static final float ZOTE_VISUAL_SCALE = 1.68f;
    private static final float ZOTE_INTERACTION_DISTANCE = 112f;
    private static final float CORPSE_VISUAL_SCALE = 1.35f;

    private final SpriteBatch batch = new SpriteBatch();
    private final List<Texture> ownedTextures = new ArrayList<>();
    private final InventoryController inventoryController;
    private final Texture secretDarknessTexture;

    private final Texture crossroadsPlatformTexture;
    private final Texture crossroadsSmallPlatformTexture;
    private final Texture greenpathPlatformTexture;
    private final Texture greenpathSmallPlatformTexture;
    private final Texture bossPlatformTexture;
    private final Texture bossSmallPlatformTexture;

    private final Texture crossroadsSpikesTexture;
    private final Texture crossroadsRockTexture;
    private final Texture greenpathThornsTexture;
    private final Texture greenpathAcidTexture;
    private final Texture greenpathPlantTexture;
    private final Texture bossCrackTexture;
    private final Texture bossLaserTexture;
    private final Texture bossPitEdgeTexture;

    private final Texture crossroadsArchTexture;
    private final Texture crossroadsWallTexture;
    private final Texture hangingChainTexture;
    private final Texture greenpathRuinsTexture;
    private final Texture greenpathVinesTexture;
    private final Texture crystalClusterTexture;
    private final Texture bossGateTexture;

    private final Texture crackedWall0Texture;
    private final Texture crackedWall1Texture;
    private final Texture crackedWall2Texture;
    private final Texture crackedWallBrokenTexture;
    private final Texture rubbleTexture;
    private final Texture voidHeartTexture;

    private final Texture crawlidCorpseTexture;
    private final Texture mossflyCorpseTexture;
    private final Texture hornheadCorpseTexture;
    private final Texture crystalGuardianCorpseTexture;

    private final Animation<TextureRegion> playerIdleAnimation;
    private final Animation<TextureRegion> playerRunAnimation;
    private final Animation<TextureRegion> playerJumpAnimation;
    private final Animation<TextureRegion> playerFallAnimation;
    private final Animation<TextureRegion> playerDoubleJumpAnimation;
    private final Animation<TextureRegion> playerDashAnimation;
    private final Animation<TextureRegion> playerAttackAnimation;
    private final Animation<TextureRegion> playerAttackUpAnimation;
    private final Animation<TextureRegion> playerAttackDownAnimation;
    private final Animation<TextureRegion> playerHurtAnimation;
    private final Animation<TextureRegion> playerFocusAnimation;
    private final Animation<TextureRegion> playerWallSlideAnimation;
    private final Animation<TextureRegion> playerCastAnimation;

    private final Animation<TextureRegion> vengefulSpiritAnimation;
    private final Animation<TextureRegion> howlingWraithsAnimation;

    private final Animation<TextureRegion> zoteIdleAnimation;
    private final Animation<TextureRegion> zoteTalkAnimation;
    private final Animation<TextureRegion> zoteAttackAnimation;
    private final Texture interactionIconTexture;

    private final Animation<TextureRegion> crawlidWalkAnimation;
    private final Animation<TextureRegion> mosquitoHoverAnimation;
    private final Animation<TextureRegion> mossflyCamouflageAnimation;
    private final Animation<TextureRegion> mossflyRevealAnimation;
    private final Animation<TextureRegion> mossflyFlyAnimation;
    private final Animation<TextureRegion> hornheadWalkAnimation;
    private final Animation<TextureRegion> flyingHoverAnimation;
    private final Animation<TextureRegion> crystalIdleAnimation;
    private final Animation<TextureRegion> crystalAttackAnimation;
    private final Animation<TextureRegion> falseKnightIdleAnimation;
    private final Animation<TextureRegion> falseKnightAttackUpAnimation;
    private final Animation<TextureRegion> falseKnightAttackSideAnimation;
    private final Animation<TextureRegion> falseKnightStunAnimation;
    private final Animation<TextureRegion> falseKnightMaceSlamAnimation;
    private final Animation<TextureRegion> falseKnightOffensiveLeapAnimation;
    private final Animation<TextureRegion> falseKnightDefensiveLeapAnimation;
    private final Animation<TextureRegion> falseKnightPowerSlamAnimation;
    private final Animation<TextureRegion> falseKnightStunOpenAnimation;
    private final Animation<TextureRegion> falseKnightInnerAnimation;
    private final Animation<TextureRegion> bossShockwaveAnimation;
    private final Animation<TextureRegion> enemyHitAnimation;
    private final Animation<TextureRegion> enemyDeathAnimation;
    private final Animation<TextureRegion> stoneBreakAnimation;

    private final Map<Enemy, EnemyAnimationClock> enemyClocks = new IdentityHashMap<>();
    private PlayerVisualState playerVisualState = PlayerVisualState.IDLE;
    private float playerStateTime;
    private float animationTime;
    private float frameDelta;
    private WorldArea lastRenderedArea;
    private int previousProjectileCount;
    private boolean previousHowlingActive;
    private float castVisualTime;

    public GameWorldRenderer(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
        secretDarknessTexture = createSolidTexture(new Color(0.005f, 0.008f, 0.018f, 1f));

        crossroadsPlatformTexture = loadTexture("ui/platforms/crossroads_platform.png");
        crossroadsSmallPlatformTexture = loadTexture("ui/platforms/crossroads_platform_small.png");
        greenpathPlatformTexture = loadTexture("ui/platforms/greenpath_platform.png");
        greenpathSmallPlatformTexture = loadTexture("ui/platforms/greenpath_platform_small.png");
        bossPlatformTexture = loadTexture("ui/platforms/boss_platform.png");
        bossSmallPlatformTexture = loadTexture("ui/platforms/boss_platform_small.png");

        crossroadsSpikesTexture = loadTexture("ui/hazards/crossroads_spikes.png");
        crossroadsRockTexture = loadTexture("ui/hazards/crossroads_rock.png");
        greenpathThornsTexture = loadTexture("ui/hazards/greenpath_thorns.png");
        greenpathAcidTexture = loadTexture("ui/hazards/greenpath_acid.png");
        greenpathPlantTexture = loadTexture("ui/hazards/greenpath_plant.png");
        bossCrackTexture = loadTexture("ui/hazards/boss_crack.png");
        bossLaserTexture = loadTexture("ui/hazards/boss_laser.png");
        bossPitEdgeTexture = loadTexture("ui/hazards/boss_pit_edge.png");

        crossroadsArchTexture = loadTexture("ui/decorations/crossroads_arch.png");
        crossroadsWallTexture = loadTexture("ui/decorations/crossroads_wall.png");
        hangingChainTexture = loadTexture("ui/decorations/hanging_chain.png");
        greenpathRuinsTexture = loadTexture("ui/decorations/greenpath_ruins.png");
        greenpathVinesTexture = loadTexture("ui/decorations/greenpath_vines.png");
        crystalClusterTexture = loadTexture("ui/decorations/crystal_cluster.png");
        bossGateTexture = loadTexture("ui/decorations/boss_gate.png");

        crackedWall0Texture = loadTexture("ui/secret_room/cracked_wall_0.png");
        crackedWall1Texture = loadTexture("ui/secret_room/cracked_wall_1.png");
        crackedWall2Texture = loadTexture("ui/secret_room/cracked_wall_2.png");
        crackedWallBrokenTexture = loadTexture("ui/secret_room/cracked_wall_broken.png");
        rubbleTexture = loadTexture("ui/secret_room/rubble.png");
        voidHeartTexture = loadTexture("ui/charms/void_heart.png");

        crawlidCorpseTexture = loadTexture("ui/corpses/crawlid_corpse.png");
        mossflyCorpseTexture = loadTexture("ui/corpses/mossfly_corpse.png");
        hornheadCorpseTexture = loadTexture("ui/corpses/hornhead_corpse.png");
        crystalGuardianCorpseTexture = loadTexture("ui/corpses/crystal_guardian_corpse.png");

        playerIdleAnimation = loadAnimation("ui/animations/player/idle", "idle", 4, 0.18f, Animation.PlayMode.LOOP_PINGPONG);
        playerRunAnimation = loadAnimation("ui/animations/player/run", "run", 8, 0.075f, Animation.PlayMode.LOOP);
        playerJumpAnimation = loadAnimation("ui/animations/player/jump", "jump", 3, 0.11f, Animation.PlayMode.NORMAL);
        playerFallAnimation = loadAnimation("ui/animations/player/fall", "fall", 3, 0.10f, Animation.PlayMode.NORMAL);
        playerDoubleJumpAnimation = loadAnimation("ui/animations/player/double_jump", "double_jump", 4, 0.065f, Animation.PlayMode.NORMAL);
        playerDashAnimation = loadAnimation("ui/animations/player/dash", "dash", 3, 0.052f, Animation.PlayMode.NORMAL);
        playerAttackAnimation = loadAnimation("ui/animations/player/attack", "attack", 4, 0.055f, Animation.PlayMode.NORMAL);
        playerAttackUpAnimation = loadAnimation("ui/animations/player/attack_up", "attack_up", 4, 0.055f, Animation.PlayMode.NORMAL);
        playerAttackDownAnimation = loadAnimation("ui/animations/player/attack_down", "attack_down", 4, 0.055f, Animation.PlayMode.NORMAL);
        playerHurtAnimation = loadAnimation("ui/animations/player/hurt", "hurt", 2, 0.10f, Animation.PlayMode.LOOP_PINGPONG);
        playerFocusAnimation = loadAnimation("ui/animations/player/focus", "focus", 6, 0.11f, Animation.PlayMode.LOOP);
        playerWallSlideAnimation = loadAnimation("ui/animations/player/wall_slide", "wall_slide", 3, 0.12f, Animation.PlayMode.LOOP_PINGPONG);
        playerCastAnimation = loadAnimation("ui/animations/player/cast", "cast", 4, 0.055f, Animation.PlayMode.NORMAL);

        vengefulSpiritAnimation = loadAnimation("ui/animations/spells/vengeful_spirit", "spirit", 4, 0.075f, Animation.PlayMode.LOOP);
        howlingWraithsAnimation = loadAnimation("ui/animations/spells/howling_wraiths", "wraiths", 6, 0.053f, Animation.PlayMode.NORMAL);

        zoteIdleAnimation = loadAnimation("ui/animations/npc/zote_idle", "idle", 4, 0.20f, Animation.PlayMode.LOOP_PINGPONG);
        zoteTalkAnimation = loadAnimation("ui/animations/npc/zote_talk", "talk", 3, 0.16f, Animation.PlayMode.LOOP);
        zoteAttackAnimation = loadAnimation("ui/animations/npc/zote_attack", "attack", 4, 0.08f, Animation.PlayMode.NORMAL);
        interactionIconTexture = loadTexture("ui/dialogue/interaction_icon.png");

        crawlidWalkAnimation = loadAnimation("ui/animations/enemies/crawlid_walk", "walk", 4, 0.12f, Animation.PlayMode.LOOP);
        mosquitoHoverAnimation = loadAnimation("ui/animations/enemies/flying_hover", "hover", 8, 0.09f, Animation.PlayMode.LOOP);
        mossflyCamouflageAnimation = loadAnimation("ui/animations/enemies/mossfly_camouflage", "camouflage", 3, 0.18f, Animation.PlayMode.LOOP_PINGPONG);
        mossflyRevealAnimation = loadAnimation("ui/animations/enemies/mossfly_reveal", "reveal", 4, 0.11f, Animation.PlayMode.NORMAL);
        mossflyFlyAnimation = loadAnimation("ui/animations/enemies/mossfly_fly", "fly", 6, 0.09f, Animation.PlayMode.LOOP);
        hornheadWalkAnimation = loadAnimation("ui/animations/enemies/hornhead_walk", "walk", 4, 0.12f, Animation.PlayMode.LOOP);
        flyingHoverAnimation = mosquitoHoverAnimation;
        crystalIdleAnimation = loadAnimation("ui/animations/enemies/crystal_guardian", "idle", 4, 0.16f, Animation.PlayMode.LOOP_PINGPONG);
        crystalAttackAnimation = loadAnimation("ui/animations/enemies/crystal_guardian_attack", "attack", 3, 0.09f, Animation.PlayMode.NORMAL);
        falseKnightIdleAnimation = loadAnimation("ui/animations/enemies/false_knight_idle", "idle", 3, 0.18f, Animation.PlayMode.LOOP_PINGPONG);
        falseKnightAttackUpAnimation = loadAnimation("ui/animations/enemies/false_knight_attack_up", "attack_up", 4, 0.095f, Animation.PlayMode.NORMAL);
        falseKnightAttackSideAnimation = loadAnimation("ui/animations/enemies/false_knight_attack_side", "attack_side", 3, 0.09f, Animation.PlayMode.NORMAL);
        falseKnightStunAnimation = loadAnimation("ui/animations/enemies/false_knight_stun", "stun", 2, 0.13f, Animation.PlayMode.LOOP_PINGPONG);
        falseKnightMaceSlamAnimation = loadAnimation("ui/animations/enemies/false_knight_mace_slam", "slam", 5, 0.11f, Animation.PlayMode.NORMAL);
        falseKnightOffensiveLeapAnimation = loadAnimation("ui/animations/enemies/false_knight_offensive_leap", "leap", 4, 0.12f, Animation.PlayMode.NORMAL);
        falseKnightDefensiveLeapAnimation = loadAnimation("ui/animations/enemies/false_knight_defensive_leap", "leap", 4, 0.11f, Animation.PlayMode.NORMAL);
        falseKnightPowerSlamAnimation = loadAnimation("ui/animations/enemies/false_knight_power_slam", "slam", 5, 0.12f, Animation.PlayMode.NORMAL);
        falseKnightStunOpenAnimation = loadAnimation("ui/animations/enemies/false_knight_stun_open", "stun", 4, 0.14f, Animation.PlayMode.LOOP_PINGPONG);
        falseKnightInnerAnimation = loadAnimation("ui/animations/enemies/false_knight_inner", "inner", 3, 0.16f, Animation.PlayMode.LOOP_PINGPONG);
        bossShockwaveAnimation = loadAnimation("ui/animations/effects/boss_shockwave", "shockwave", 4, 0.08f, Animation.PlayMode.LOOP);
        enemyHitAnimation = loadAnimation("ui/animations/effects/enemy_hit", "hit", 4, 0.045f, Animation.PlayMode.NORMAL);
        enemyDeathAnimation = loadAnimation("ui/animations/effects/enemy_death", "death", 5, 0.09f, Animation.PlayMode.NORMAL);
        stoneBreakAnimation = loadAnimation("ui/animations/effects/stone_break", "break", 5, 0.08f, Animation.PlayMode.NORMAL);
    }

    public void render(GameWorld world, OrthographicCamera camera, PlayerStats playerStats) {
        frameDelta = Gdx.graphics.getDeltaTime();
        animationTime += frameDelta;
        updateCastVisualState(world);
        if (lastRenderedArea != world.getCurrentArea()) {
            lastRenderedArea = world.getCurrentArea();
            enemyClocks.clear();
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawPlatforms(world);
        drawHazards(world);
        drawSecretRoomDarkness(world);
        drawBreakableWall(world);
        drawSecretPickup(world);
        drawZote(world);
        drawEnemies(world);
        drawPlayer(world, playerStats);
        drawSpellProjectiles(world);
        drawHowlingWraithsAttack(world);
        drawEnemyLasers(world);
        drawBossShockwaves(world);
        drawBossGates(world);
        batch.end();
    }

    private void drawDecorations(GameWorld world) {
        // Intentionally disabled: oversized decorative foreground assets hid gameplay.
    }

    private void drawSecretRoomDarkness(GameWorld world) {
        BreakableWall wall = world.getBreakableWall().orElse(null);
        if (world.getCurrentArea() != WorldArea.GREENPATH
            || wall == null
            || wall.isBroken()) {
            return;
        }

        float roomX = wall.getX() + wall.getWidth();
        float roomWidth = Math.max(0f, world.getWorldWidth() - roomX);

        batch.setColor(0.01f, 0.015f, 0.035f, 0.97f);
        batch.draw(
            secretDarknessTexture,
            roomX,
            80f,
            roomWidth,
            world.getWorldHeight() - 80f
        );
        batch.setColor(Color.WHITE);
    }

    private void drawBreakableWall(GameWorld world) {
        BreakableWall wall = world.getBreakableWall().orElse(null);
        if (wall == null) {
            return;
        }

        if (!wall.isBroken()) {
            Texture texture = switch (wall.getVisualStage()) {
                case 1 -> crackedWall1Texture;
                case 2 -> crackedWall2Texture;
                default -> crackedWall0Texture;
            };

            batch.draw(
                texture,
                wall.getX(),
                wall.getY(),
                wall.getWidth(),
                wall.getHeight()
            );
            return;
        }

        batch.draw(
            crackedWallBrokenTexture,
            wall.getX(),
            wall.getY(),
            wall.getWidth(),
            wall.getHeight()
        );
        batch.draw(
            rubbleTexture,
            wall.getX() - 20f,
            wall.getY() - 8f,
            wall.getWidth() + 55f,
            64f
        );

        if (wall.isBreakEffectActive()) {
            float effectTime = wall.getBreakEffectProgress()
                * stoneBreakAnimation.getAnimationDuration();
            TextureRegion frame = stoneBreakAnimation.getKeyFrame(
                effectTime,
                false
            );
            drawRegion(
                frame,
                wall.getX() - 50f,
                wall.getY() - 15f,
                wall.getWidth() + 100f,
                wall.getHeight() + 40f,
                1f,
                false
            );
        }
    }

    private void drawSecretPickup(GameWorld world) {
        SecretPickup pickup = world.getSecretPickup().orElse(null);
        BreakableWall wall = world.getBreakableWall().orElse(null);
        if (pickup == null
            || pickup.isCollected()
            || (wall != null && !wall.isBroken())) {
            return;
        }

        float bob = (float) Math.sin(animationTime * 2.6f) * 7f;
        float pulse = 0.75f + 0.25f
            * (float) Math.sin(animationTime * 4f);

        batch.setColor(0.78f, 0.62f, 1f, 0.72f + pulse * 0.22f);
        batch.draw(
            voidHeartTexture,
            pickup.getX() - 8f,
            pickup.getY() + bob - 8f,
            pickup.getWidth() + 16f,
            pickup.getHeight() + 16f
        );
        batch.setColor(Color.WHITE);
    }

    private void drawPlatforms(GameWorld world) {
        for (Platform platform : world.getPlatforms()) {
            boolean small = platform.getWidth() <= SMALL_PLATFORM_WIDTH_LIMIT;
            Texture texture = getPlatformTexture(world.getCurrentArea(), small);
            batch.draw(texture, platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
        }
    }

    private Texture getPlatformTexture(WorldArea area, boolean small) {
        if (area == WorldArea.GREENPATH) {
            return small ? greenpathSmallPlatformTexture : greenpathPlatformTexture;
        }
        if (area == WorldArea.BOSS_ARENA) {
            return small ? bossSmallPlatformTexture : bossPlatformTexture;
        }
        return small ? crossroadsSmallPlatformTexture : crossroadsPlatformTexture;
    }

    private void drawHazards(GameWorld world) {
        for (Hazard hazard : world.getHazards()) {
            batch.draw(getHazardTexture(hazard.getType()), hazard.getX(), hazard.getY(), hazard.getWidth(), hazard.getHeight());
        }
    }

    private Texture getHazardTexture(HazardType type) {
        return switch (type) {
            case CROSSROADS_ROCK -> crossroadsRockTexture;
            case GREENPATH_THORNS -> greenpathThornsTexture;
            case GREENPATH_ACID -> greenpathAcidTexture;
            case GREENPATH_PLANT -> greenpathPlantTexture;
            case BOSS_CRACK -> bossCrackTexture;
            case BOSS_LASER -> bossLaserTexture;
            case BOSS_PIT_EDGE -> bossPitEdgeTexture;
            default -> crossroadsSpikesTexture;
        };
    }

    private void drawZote(GameWorld world) {
        Zote zote = world.getZote().orElse(null);

        if (zote == null) {
            return;
        }

        Animation<TextureRegion> animation;
        boolean looping;

        if (zote.isReactingToAttack()) {
            animation = zoteAttackAnimation;
            looping = false;
        } else if (zote.isTalking()) {
            animation = zoteTalkAnimation;
            looping = true;
        } else {
            animation = zoteIdleAnimation;
            looping = true;
        }

        TextureRegion frame = animation.getKeyFrame(
            animationTime,
            looping
        );

        drawRegion(
            frame,
            zote.getX(),
            zote.getY(),
            zote.getWidth(),
            zote.getHeight(),
            ZOTE_VISUAL_SCALE,
            zote.getFacingDirection() == HorizontalDirection.LEFT
        );

        if (!zote.isTalking() && isPlayerNearZote(world, zote)) {
            float iconSize = 34f;
            float iconX = zote.getCenterX() - iconSize / 2f;
            float iconY = zote.getY() + zote.getHeight() + 28f;

            batch.draw(
                interactionIconTexture,
                iconX,
                iconY,
                iconSize,
                iconSize
            );
        }
    }

    private boolean isPlayerNearZote(
        GameWorld world,
        Zote zote
    ) {
        Player player = world.getPlayer();

        float dx = player.getCenterX() - zote.getCenterX();
        float dy = player.getCenterY() - zote.getCenterY();

        return dx * dx + dy * dy
            <= ZOTE_INTERACTION_DISTANCE
            * ZOTE_INTERACTION_DISTANCE;
    }

    private void drawEnemies(GameWorld world) {
        for (Enemy enemy : world.getEnemies()) {
            if (!enemy.isAlive()) {
                drawDeadEnemy(enemy);
                continue;
            }

            EnemyVisualState state = resolveEnemyVisualState(enemy);
            EnemyAnimationClock clock = enemyClocks.computeIfAbsent(
                enemy,
                ignored -> new EnemyAnimationClock(state)
            );

            if (clock.state != state) {
                clock.state = state;
                clock.time = 0f;
            } else {
                clock.time += frameDelta;
            }

            TextureRegion frame = getEnemyAnimation(state).getKeyFrame(
                clock.time,
                isEnemyAnimationLooping(state)
            );

            float scale = enemy.getType() == EnemyType.FALSE_KNIGHT
                ? BOSS_VISUAL_SCALE
                : NORMAL_ENEMY_VISUAL_SCALE;

            boolean flipX = enemy.getDirection()
                == HorizontalDirection.LEFT;

            float drawX = enemy.getX();
            float drawY = enemy.getY();

            if (enemy.getState() == EnemyState.SHAKING) {
                drawX += (float) Math.sin(animationTime * 42f) * 3f;
            }

            drawRegion(
                frame,
                drawX,
                drawY,
                enemy.getWidth(),
                enemy.getHeight(),
                scale,
                flipX
            );

            if (enemy.getType() == EnemyType.FALSE_KNIGHT
                && enemy.isBossArmorOpen()) {
                TextureRegion innerFrame = falseKnightInnerAnimation.getKeyFrame(
                    clock.time,
                    true
                );
                drawRegion(
                    innerFrame,
                    enemy.getX() + enemy.getWidth() * 0.18f,
                    enemy.getY() + 10f,
                    enemy.getWidth() * 0.64f,
                    enemy.getHeight() * 0.58f,
                    1.0f,
                    flipX
                );
            }

            if (enemy.isHitEffectActive()) {
                float effectTime = enemy.getHitEffectProgress()
                    * enemyHitAnimation.getAnimationDuration();
                TextureRegion hitFrame = enemyHitAnimation.getKeyFrame(
                    effectTime,
                    false
                );
                drawRegion(
                    hitFrame,
                    enemy.getX() - 12f,
                    enemy.getY() - 8f,
                    enemy.getWidth() + 24f,
                    enemy.getHeight() + 20f,
                    1.35f,
                    false
                );
            }
        }
    }

    private void drawDeadEnemy(Enemy enemy) {
        if (enemy.isCorpseVisible()) {
            Texture corpse = getCorpseTexture(enemy);
            if (corpse != null) {
                float corpseWidth = enemy.getWidth() * 2.15f;
                float corpseHeight = enemy.getHeight() * CORPSE_VISUAL_SCALE;
                float elapsed = enemy.getDeathElapsed();
                float slide = Math.min(28f, elapsed * 46f);
                float fall = Math.min(10f, elapsed * 22f);
                float slideDirection = -enemy.getDirection().getSign();
                float fade = Math.max(0.30f, 0.90f - elapsed * 0.18f);

                batch.setColor(1f, 1f, 1f, fade);
                batch.draw(
                    corpse,
                    enemy.getX()
                        + enemy.getWidth() / 2f
                        - corpseWidth / 2f
                        + slideDirection * slide,
                    enemy.getY() - 4f - fall,
                    corpseWidth,
                    corpseHeight
                );
                batch.setColor(Color.WHITE);
            }
        }

        if (enemy.isDeathEffectActive()) {
            float effectTime = enemy.getDeathEffectProgress()
                * enemyDeathAnimation.getAnimationDuration();
            TextureRegion frame = enemyDeathAnimation.getKeyFrame(
                effectTime,
                false
            );
            drawRegion(
                frame,
                enemy.getX() - 18f,
                enemy.getY() - 10f,
                enemy.getWidth() + 36f,
                enemy.getHeight() + 32f,
                1.55f,
                false
            );
        }
    }

    private Texture getCorpseTexture(Enemy enemy) {
        return switch (enemy.getType()) {
            case CRAWLID, BASIC_GROUND -> crawlidCorpseTexture;
            case MOSQUITO, MOSSFLY -> mossflyCorpseTexture;
            case HORNHEAD_HUSK -> hornheadCorpseTexture;
            case GUARDIAN_CRYSTAL -> crystalGuardianCorpseTexture;
            case FALSE_KNIGHT -> null;
        };
    }

    private EnemyVisualState resolveEnemyVisualState(Enemy enemy) {
        if (enemy.getType() == EnemyType.FALSE_KNIGHT) {
            if (enemy.getState() == EnemyState.RESTING) {
                return EnemyVisualState.FALSE_STUN;
            }
            if (enemy.getState() == EnemyState.PREPARING_ATTACK) {
                return EnemyVisualState.FALSE_UP;
            }
            if (enemy.getState() == EnemyState.CHARGING) {
                return EnemyVisualState.FALSE_SIDE;
            }
            return EnemyVisualState.FALSE_IDLE;
        }

        return switch (enemy.getType()) {
            case CRAWLID, BASIC_GROUND -> EnemyVisualState.CRAWLID;
            case MOSQUITO -> EnemyVisualState.MOSQUITO;
            case MOSSFLY -> {
                if (
                    enemy.getState() == EnemyState.CAMOUFLAGED
                        || enemy.getState() == EnemyState.SHAKING
                ) {
                    yield EnemyVisualState.MOSSFLY_CAMOUFLAGED;
                }
                if (enemy.getState() == EnemyState.REVEALING) {
                    yield EnemyVisualState.MOSSFLY_REVEAL;
                }
                yield EnemyVisualState.MOSSFLY_FLY;
            }
            case HORNHEAD_HUSK -> EnemyVisualState.HORNHEAD;
            case GUARDIAN_CRYSTAL -> {
                if (
                    enemy.getState() == EnemyState.PREPARING_ATTACK
                        || enemy.getState() == EnemyState.ENRAGED
                ) {
                    yield EnemyVisualState.CRYSTAL_ATTACK;
                }
                yield EnemyVisualState.CRYSTAL_IDLE;
            }
            case FALSE_KNIGHT -> {
                if (enemy.isBossArmorOpen()) yield EnemyVisualState.FALSE_STUN_OPEN;
                yield switch (enemy.getBossMoveType()) {
                    case MACE_SLAM -> EnemyVisualState.FALSE_MACE_SLAM;
                    case CHARGE -> EnemyVisualState.FALSE_SIDE;
                    case OFFENSIVE_LEAP -> EnemyVisualState.FALSE_OFFENSIVE_LEAP;
                    case DEFENSIVE_LEAP -> EnemyVisualState.FALSE_DEFENSIVE_LEAP;
                    case POWER_SLAM -> EnemyVisualState.FALSE_POWER_SLAM;
                    case STUNNED -> EnemyVisualState.FALSE_STUN;
                    default -> EnemyVisualState.FALSE_IDLE;
                };
            }
        };
    }

    private Animation<TextureRegion> getEnemyAnimation(
        EnemyVisualState state
    ) {
        return switch (state) {
            case CRAWLID -> crawlidWalkAnimation;
            case MOSQUITO -> mosquitoHoverAnimation;
            case MOSSFLY_CAMOUFLAGED -> mossflyCamouflageAnimation;
            case MOSSFLY_REVEAL -> mossflyRevealAnimation;
            case MOSSFLY_FLY -> mossflyFlyAnimation;
            case HORNHEAD -> hornheadWalkAnimation;
            case CRYSTAL_IDLE -> crystalIdleAnimation;
            case CRYSTAL_ATTACK -> crystalAttackAnimation;
            case FALSE_IDLE -> falseKnightIdleAnimation;
            case FALSE_UP -> falseKnightAttackUpAnimation;
            case FALSE_SIDE -> falseKnightAttackSideAnimation;
            case FALSE_STUN -> falseKnightStunAnimation;
            case FALSE_MACE_SLAM -> falseKnightMaceSlamAnimation;
            case FALSE_OFFENSIVE_LEAP -> falseKnightOffensiveLeapAnimation;
            case FALSE_DEFENSIVE_LEAP -> falseKnightDefensiveLeapAnimation;
            case FALSE_POWER_SLAM -> falseKnightPowerSlamAnimation;
            case FALSE_STUN_OPEN -> falseKnightStunOpenAnimation;
        };
    }

    private boolean isEnemyAnimationLooping(
        EnemyVisualState state
    ) {
        return state == EnemyVisualState.CRAWLID
            || state == EnemyVisualState.MOSQUITO
            || state == EnemyVisualState.MOSSFLY_CAMOUFLAGED
            || state == EnemyVisualState.MOSSFLY_FLY
            || state == EnemyVisualState.HORNHEAD
            || state == EnemyVisualState.CRYSTAL_IDLE
            || state == EnemyVisualState.FALSE_IDLE
            || state == EnemyVisualState.FALSE_STUN;
    }

    private void drawPlayer(GameWorld world, PlayerStats playerStats) {
        Player player = world.getPlayer();
        if (playerStats.isDamageInvincible() && shouldHidePlayerDuringBlink()) {
            return;
        }

        PlayerVisualState newState = resolvePlayerVisualState(world, player);
        if (newState != playerVisualState) {
            playerVisualState = newState;
            playerStateTime = 0f;
        } else {
            playerStateTime += frameDelta;
        }

        TextureRegion frame = getPlayerAnimation(playerVisualState).getKeyFrame(
            playerStateTime,
            isPlayerAnimationLooping(playerVisualState)
        );
        boolean flipX = player.getFacingDirection()
            == HorizontalDirection.LEFT;

        drawSharpShadowTrail(player, frame, flipX);
        applyPlayerColor(playerStats, player);
        drawRegion(
            frame,
            player.getX(),
            player.getY(),
            player.getWidth(),
            player.getHeight(),
            PLAYER_VISUAL_SCALE,
            flipX
        );
        batch.setColor(Color.WHITE);
    }


    private void drawSharpShadowTrail(
        Player player,
        TextureRegion frame,
        boolean flipX
    ) {
        if (!player.isDashing()
            || !inventoryController.isEquipped(
                CharmType.SHARP_SHADOW
            )) {
            return;
        }

        float direction = player.getFacingDirection().getSign();
        for (int index = 3; index >= 1; index--) {
            float alpha = 0.09f + (4 - index) * 0.07f;
            batch.setColor(0.12f, 0.025f, 0.24f, alpha);
            drawRegion(
                frame,
                player.getX() - direction * index * 14f,
                player.getY(),
                player.getWidth(),
                player.getHeight(),
                PLAYER_VISUAL_SCALE,
                flipX
            );
        }
        batch.setColor(Color.WHITE);
    }

    private PlayerVisualState resolvePlayerVisualState(GameWorld world, Player player) {
        if (player.isKnockbackActive()) return PlayerVisualState.HURT;

        NailAttack attack = world.getActiveNailAttack().orElse(null);
        if (attack != null) {
            return switch (attack.getDirection()) {
                case UP -> PlayerVisualState.ATTACK_UP;
                case DOWN -> PlayerVisualState.ATTACK_DOWN;
                case HORIZONTAL -> PlayerVisualState.ATTACK;
            };
        }

        if (player.isFocusing()) return PlayerVisualState.FOCUS;
        if (castVisualTime > 0f) return PlayerVisualState.CAST;
        if (player.isDashing()) return PlayerVisualState.DASH;
        if (player.isWallSliding()) return PlayerVisualState.WALL_SLIDE;
        if (player.isDoubleJumpVisualActive()) return PlayerVisualState.DOUBLE_JUMP;
        if (!player.isOnGround() && player.getVelocityY() < -10f) return PlayerVisualState.FALL;
        if (!player.isOnGround()) return PlayerVisualState.JUMP;
        if (Math.abs(player.getVelocityX()) > MOVEMENT_EPSILON) return PlayerVisualState.RUN;
        return PlayerVisualState.IDLE;
    }

    private Animation<TextureRegion> getPlayerAnimation(PlayerVisualState state) {
        return switch (state) {
            case IDLE -> playerIdleAnimation;
            case RUN -> playerRunAnimation;
            case JUMP -> playerJumpAnimation;
            case FALL -> playerFallAnimation;
            case DOUBLE_JUMP -> playerDoubleJumpAnimation;
            case DASH -> playerDashAnimation;
            case ATTACK -> playerAttackAnimation;
            case ATTACK_UP -> playerAttackUpAnimation;
            case ATTACK_DOWN -> playerAttackDownAnimation;
            case HURT -> playerHurtAnimation;
            case FOCUS -> playerFocusAnimation;
            case WALL_SLIDE -> playerWallSlideAnimation;
            case CAST -> playerCastAnimation;
        };
    }

    private boolean isPlayerAnimationLooping(PlayerVisualState state) {
        return state == PlayerVisualState.IDLE
            || state == PlayerVisualState.RUN
            || state == PlayerVisualState.HURT
            || state == PlayerVisualState.FOCUS
            || state == PlayerVisualState.WALL_SLIDE;
    }

    private void drawEnemyLasers(GameWorld world) {
        for (EnemyLaser laser : world.getEnemyLasers()) {
            batch.draw(bossLaserTexture, laser.getX(), laser.getY(), laser.getWidth(), laser.getHeight());
        }
    }



    private void drawBossShockwaves(GameWorld world) {
        TextureRegion frame = bossShockwaveAnimation.getKeyFrame(animationTime, true);
        for (BossShockwave shockwave : world.getBossShockwaves()) {
            drawRegion(
                frame,
                shockwave.getX(),
                shockwave.getY(),
                shockwave.getWidth(),
                shockwave.getHeight(),
                2.2f,
                shockwave.getDirection() == HorizontalDirection.LEFT
            );
        }
    }

    private void drawBossGates(GameWorld world) {
        for (BossGate gate : world.getBossGates()) {
            if (!gate.isClosed()) continue;
            batch.draw(
                bossGateTexture,
                gate.getX(),
                gate.getY(),
                gate.getWidth(),
                gate.getHeight()
            );
        }
    }

    private void applyPlayerColor(PlayerStats playerStats, Player player) {
        if (player.isDashing()
            && inventoryController.isEquipped(CharmType.SHARP_SHADOW)) {
            batch.setColor(0.20f, 0.10f, 0.34f, 0.96f);
        } else if (playerStats.isGodModeEnabled()) {
            batch.setColor(1f, 0.92f, 0.35f, 1f);
        } else if (playerStats.isDamageInvincible()) {
            batch.setColor(0.58f, 0.86f, 1f, 0.82f);
        } else {
            batch.setColor(Color.WHITE);
        }
    }

    private void drawRegion(TextureRegion region, float x, float y, float collisionWidth,
                            float collisionHeight, float heightScale, boolean flipX) {
        float visualHeight = collisionHeight * heightScale;
        float visualWidth = visualHeight * region.getRegionWidth() / region.getRegionHeight();
        float visualX = x + collisionWidth / 2f - visualWidth / 2f;
        batch.draw(region.getTexture(), visualX, y, 0f, 0f, visualWidth, visualHeight,
            1f, 1f, 0f, region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
            region.getRegionHeight(), flipX, false);
    }

    private boolean shouldHidePlayerDuringBlink() {
        return ((int) (animationTime * PLAYER_BLINK_SPEED)) % 2 == 0;
    }

    private void updateCastVisualState(GameWorld world) {
        if (castVisualTime > 0f) {
            castVisualTime = Math.max(0f, castVisualTime - frameDelta);
        }

        int projectileCount = world.getSpellProjectiles().size();
        boolean howlingActive = world.getActiveHowlingWraithsAttack().isPresent();

        if (projectileCount > previousProjectileCount
            || (howlingActive && !previousHowlingActive)) {
            castVisualTime = 0.24f;
        }

        previousProjectileCount = projectileCount;
        previousHowlingActive = howlingActive;
    }

    private void drawSpellProjectiles(GameWorld world) {
        TextureRegion frame = vengefulSpiritAnimation.getKeyFrame(
            animationTime,
            true
        );

        boolean voidHeartActive =
            inventoryController.isEquipped(CharmType.VOID_HEART);

        if (voidHeartActive) {
            batch.setColor(0.18f, 0.06f, 0.28f, 0.98f);
        }

        for (SpellProjectile projectile : world.getSpellProjectiles()) {
            drawRegion(
                frame,
                projectile.getX(),
                projectile.getY(),
                projectile.getWidth(),
                projectile.getHeight(),
                3.2f,
                !projectile.isMovingRight()
            );
        }

        if (voidHeartActive) {
            batch.setColor(Color.WHITE);
        }
    }

    private void drawHowlingWraithsAttack(GameWorld world) {
        if (world.getActiveHowlingWraithsAttack().isEmpty()) {
            return;
        }

        HowlingWraithsAttack attack =
            world.getActiveHowlingWraithsAttack().get();

        TextureRegion frame = howlingWraithsAnimation.getKeyFrame(
            attack.getElapsedTime(),
            false
        );

        boolean voidHeartActive =
            inventoryController.isEquipped(CharmType.VOID_HEART);

        if (voidHeartActive) {
            batch.setColor(0.20f, 0.07f, 0.32f, 0.98f);
        }

        drawRegion(
            frame,
            attack.getX(),
            attack.getY(),
            attack.getWidth(),
            attack.getHeight(),
            1.15f,
            false
        );

        if (voidHeartActive) {
            batch.setColor(Color.WHITE);
        }
    }

    private Animation<TextureRegion> loadAnimation(String directory, String prefix, int count,
                                                   float frameDuration, Animation.PlayMode playMode) {
        Array<TextureRegion> frames = new Array<>(count);
        for (int index = 0; index < count; index++) {
            frames.add(new TextureRegion(loadTexture(String.format("%s/%s_%02d.png", directory, prefix, index))));
        }
        return new Animation<>(frameDuration, frames, playMode);
    }

    private Texture createSolidTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        texture.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );
        ownedTextures.add(texture);
        return texture;
    }

    private Texture loadTexture(String path) {
        if (!Gdx.files.internal(path).exists()) {
            throw new IllegalStateException("Texture not found: " + path);
        }
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        ownedTextures.add(texture);
        return texture;
    }

    public void dispose() {
        batch.dispose();
        for (Texture texture : ownedTextures) texture.dispose();
        ownedTextures.clear();
        enemyClocks.clear();
    }

    private enum PlayerVisualState {
        IDLE, RUN, JUMP, FALL, DOUBLE_JUMP, DASH,
        ATTACK, ATTACK_UP, ATTACK_DOWN,
        HURT, FOCUS, WALL_SLIDE, CAST
    }

    private enum EnemyVisualState {
        CRAWLID,
        MOSQUITO,
        MOSSFLY_CAMOUFLAGED,
        MOSSFLY_REVEAL,
        MOSSFLY_FLY,
        HORNHEAD,
        CRYSTAL_IDLE,
        CRYSTAL_ATTACK,
        FALSE_IDLE,
        FALSE_UP,
        FALSE_SIDE,
        FALSE_STUN,
        FALSE_MACE_SLAM,
        FALSE_OFFENSIVE_LEAP,
        FALSE_DEFENSIVE_LEAP,
        FALSE_POWER_SLAM,
        FALSE_STUN_OPEN
    }

    private static final class EnemyAnimationClock {
        private EnemyVisualState state;
        private float time;

        private EnemyAnimationClock(EnemyVisualState state) {
            this.state = state;
        }
    }
}
