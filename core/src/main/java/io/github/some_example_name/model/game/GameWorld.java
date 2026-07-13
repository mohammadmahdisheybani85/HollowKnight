package io.github.some_example_name.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GameWorld {
    private static final float WORLD_WIDTH = 2400f;
    private static final float WORLD_HEIGHT = 900f;

    private static final float PLAYER_SPAWN_X = 120f;
    private static final float PLAYER_SPAWN_Y = 180f;

    private static final float AREA_EXIT_X = 2320f;

    private static final String FALSE_KNIGHT_NAME =
        "False Knight";

    private final Player player;
    private final List<Platform> platforms = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Hazard> hazards = new ArrayList<>();
    private final List<Decoration> decorations = new ArrayList<>();
    private final List<SpellProjectile> spellProjectiles =
        new ArrayList<>();
    private final List<EnemyLaser> enemyLasers =
        new ArrayList<>();
    private final List<BossAttack> bossAttacks = new ArrayList<>();
    private final List<BossShockwave> bossShockwaves = new ArrayList<>();
    private final List<BossGate> bossGates = new ArrayList<>();
    private final Set<String> defeatedEnemyNames =
        new HashSet<>();

    private NailAttack activeNailAttack;
    private HowlingWraithsAttack activeHowlingWraithsAttack;
    private WorldArea currentArea =
        WorldArea.FORGOTTEN_CROSSROADS;
    private Zote zote;
    private BreakableWall breakableWall;
    private SecretPickup secretPickup;
    private boolean secretWallBrokenPersisted;
    private boolean secretPickupCollectedPersisted;

    private float lastSafePlayerX = PLAYER_SPAWN_X;
    private float lastSafePlayerY = PLAYER_SPAWN_Y;
    private float cameraShakeTimeLeft;
    private float cameraShakeIntensity;

    public GameWorld() {
        this(WorldArea.FORGOTTEN_CROSSROADS);
    }

    public GameWorld(WorldArea startingArea) {
        player = new Player(
            PLAYER_SPAWN_X,
            PLAYER_SPAWN_Y
        );
        loadArea(startingArea);
    }

    public Player getPlayer() {
        return player;
    }

    public List<Platform> getPlatforms() {
        return Collections.unmodifiableList(platforms);
    }

    public List<Enemy> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    public Enemy findEnemyByName(String enemyName) {
        for (Enemy enemy : enemies) {
            if (enemy.getName().equals(enemyName)) {
                return enemy;
            }
        }
        return null;
    }

    public List<Hazard> getHazards() {
        return Collections.unmodifiableList(hazards);
    }

    public List<Decoration> getDecorations() {
        return Collections.unmodifiableList(decorations);
    }

    public List<EnemyLaser> getEnemyLasers() {
        return Collections.unmodifiableList(enemyLasers);
    }

    public void addEnemyLaser(EnemyLaser laser) {
        enemyLasers.add(laser);
    }

    public void updateEnemyLasers(float delta) {
        for (EnemyLaser laser : enemyLasers) {
            laser.update(delta);
        }
        enemyLasers.removeIf(laser -> !laser.isActive());
    }



    public List<BossAttack> getBossAttacks() {
        return Collections.unmodifiableList(bossAttacks);
    }

    public void addBossAttack(BossAttack attack) {
        if (attack != null) bossAttacks.add(attack);
    }

    public List<BossShockwave> getBossShockwaves() {
        return Collections.unmodifiableList(bossShockwaves);
    }

    public void addBossShockwave(BossShockwave shockwave) {
        if (shockwave != null) bossShockwaves.add(shockwave);
    }

    public List<BossGate> getBossGates() {
        return Collections.unmodifiableList(bossGates);
    }

    public void openBossGates() {
        for (BossGate gate : bossGates) {
            if (gate.isClosed()) {
                gate.open();
                platforms.remove(gate.getCollider());
            }
        }
    }

    public boolean areBossGatesClosed() {
        for (BossGate gate : bossGates) {
            if (gate.isClosed()) return true;
        }
        return false;
    }

    public void updateBossEffects(float delta) {
        for (BossAttack attack : bossAttacks) attack.update(delta);
        bossAttacks.removeIf(attack -> !attack.isActive());

        for (BossShockwave shockwave : bossShockwaves) shockwave.update(delta);
        bossShockwaves.removeIf(shockwave -> !shockwave.isActive());

        cameraShakeTimeLeft = Math.max(0f, cameraShakeTimeLeft - delta);
        if (cameraShakeTimeLeft <= 0f) cameraShakeIntensity = 0f;
    }

    public void requestCameraShake(float duration, float intensity) {
        cameraShakeTimeLeft = Math.max(cameraShakeTimeLeft, Math.max(0f, duration));
        cameraShakeIntensity = Math.max(cameraShakeIntensity, Math.max(0f, intensity));
    }

    public float getCameraShakeTimeLeft() { return cameraShakeTimeLeft; }
    public float getCameraShakeIntensity() { return cameraShakeIntensity; }

    public void teleportToArea(WorldArea area) {
        if (area != null) loadArea(area);
    }

    public List<SpellProjectile> getSpellProjectiles() {
        return Collections.unmodifiableList(spellProjectiles);
    }

    public Optional<NailAttack> getActiveNailAttack() {
        return Optional.ofNullable(activeNailAttack);
    }

    public void setActiveNailAttack(
        NailAttack activeNailAttack
    ) {
        this.activeNailAttack = activeNailAttack;
    }

    public void clearInactiveNailAttack() {
        if (
            activeNailAttack != null
                && !activeNailAttack.isActive()
        ) {
            activeNailAttack = null;
        }
    }

    public void addSpellProjectile(
        SpellProjectile projectile
    ) {
        spellProjectiles.add(projectile);
    }

    public void clearInactiveSpellProjectiles() {
        spellProjectiles.removeIf(
            projectile -> !projectile.isActive()
        );
    }

    public Optional<Zote> getZote() {
        return Optional.ofNullable(zote);
    }

    public Optional<BreakableWall> getBreakableWall() {
        return Optional.ofNullable(breakableWall);
    }

    public Optional<SecretPickup> getSecretPickup() {
        return Optional.ofNullable(secretPickup);
    }

    public boolean isSecretWallBroken() {
        return secretWallBrokenPersisted
            || (breakableWall != null && breakableWall.isBroken());
    }

    public boolean isSecretPickupCollected() {
        return secretPickupCollectedPersisted
            || (secretPickup != null && secretPickup.isCollected());
    }

    public void collectSecretPickup() {
        secretPickupCollectedPersisted = true;
        if (secretPickup != null) {
            secretPickup.collect();
        }
    }

    public void restoreSecretRoomState(
        boolean wallBroken,
        boolean pickupCollected
    ) {
        secretWallBrokenPersisted = wallBroken || pickupCollected;
        secretPickupCollectedPersisted = pickupCollected;
        applySecretRoomStateToCurrentArea();
    }

    public void updateEnvironmentEffects(float delta) {
        if (breakableWall != null) {
            breakableWall.update(delta);
        }
    }

    public boolean hitBreakableWall() {
        if (breakableWall == null) {
            return false;
        }

        boolean brokeNow = breakableWall.takeHit();

        if (brokeNow) {
            secretWallBrokenPersisted = true;
            platforms.remove(breakableWall.getCollider());
        }

        return brokeNow;
    }

    public WorldArea getCurrentArea() {
        return currentArea;
    }

    public boolean shouldMoveToNextArea() {
        return player.getX() >= AREA_EXIT_X
            && currentArea != WorldArea.BOSS_ARENA;
    }

    public void moveToNextArea() {
        if (
            currentArea
                == WorldArea.FORGOTTEN_CROSSROADS
        ) {
            loadArea(WorldArea.GREENPATH);
        } else if (
            currentArea == WorldArea.GREENPATH
        ) {
            loadArea(WorldArea.BOSS_ARENA);
        }
    }

    public void respawnPlayer() {
        respawnPlayerAt(
            PLAYER_SPAWN_X,
            PLAYER_SPAWN_Y
        );
    }

    public void rememberSafePlayerPosition() {
        lastSafePlayerX = player.getX();
        lastSafePlayerY = player.getY();
    }

    public void respawnPlayerAtLastSafePosition() {
        respawnPlayerAt(
            lastSafePlayerX,
            lastSafePlayerY
        );
    }

    public float getWorldWidth() {
        return WORLD_WIDTH;
    }

    public float getWorldHeight() {
        return WORLD_HEIGHT;
    }

    private void loadArea(WorldArea area) {
        currentArea = area;

        platforms.clear();
        enemies.clear();
        hazards.clear();
        decorations.clear();
        enemyLasers.clear();
        bossAttacks.clear();
        bossShockwaves.clear();
        bossGates.clear();
        cameraShakeTimeLeft = 0f;
        cameraShakeIntensity = 0f;
        zote = null;
        breakableWall = null;
        secretPickup = null;

        respawnPlayerAt(
            PLAYER_SPAWN_X,
            PLAYER_SPAWN_Y
        );

        lastSafePlayerX = PLAYER_SPAWN_X;
        lastSafePlayerY = PLAYER_SPAWN_Y;

        if (
            area == WorldArea.FORGOTTEN_CROSSROADS
        ) {
            createForgottenCrossroads();
        } else if (area == WorldArea.GREENPATH) {
            createGreenpath();
        } else if (area == WorldArea.BOSS_ARENA) {
            createBossArena();
        }

        markDefeatedEnemiesInCurrentArea();
    }

    private void respawnPlayerAt(float x, float y) {
        player.setPosition(x, y);
        player.resetAfterRespawn();

        activeNailAttack = null;
        spellProjectiles.clear();
        activeHowlingWraithsAttack = null;
        enemyLasers.clear();
    }

    private void createForgottenCrossroads() {
        platforms.add(new Platform(0f, 80f, 1250f, 40f));
        platforms.add(new Platform(1450f, 80f, 950f, 40f));

        platforms.add(new Platform(220f, 190f, 160f, 28f));
        platforms.add(new Platform(460f, 290f, 180f, 28f));
        platforms.add(new Platform(760f, 210f, 220f, 28f));
        platforms.add(new Platform(1120f, 330f, 180f, 28f));
        platforms.add(new Platform(1550f, 240f, 220f, 28f));
        platforms.add(new Platform(1850f, 360f, 260f, 28f));

        hazards.add(new Hazard(
            HazardType.CROSSROADS_SPIKES,
            620f,
            120f,
            90f,
            28f,
            1
        ));

        hazards.add(new Hazard(
            HazardType.CROSSROADS_ROCK,
            1320f,
            120f,
            120f,
            80f,
            1
        ));

        hazards.add(new Hazard(
            HazardType.CROSSROADS_SPIKES,
            1720f,
            120f,
            100f,
            28f,
            1
        ));

        decorations.add(new Decoration(
            DecorationType.CROSSROADS_ARCH,
            60f,
            120f,
            330f,
            280f
        ));
        decorations.add(new Decoration(
            DecorationType.CROSSROADS_WALL,
            900f,
            120f,
            310f,
            250f
        ));
        decorations.add(new Decoration(
            DecorationType.HANGING_CHAIN,
            1370f,
            360f,
            70f,
            290f
        ));
        decorations.add(new Decoration(
            DecorationType.CROSSROADS_ARCH,
            1880f,
            120f,
            360f,
            300f
        ));

        Enemy crawlid = new Enemy(
            "Crawlid",
            EnemyType.CRAWLID,
            680f,
            120f,
            42f,
            30f,
            2,
            70f,
            500f,
            900f
        );
        crawlid.setSpawnDirection(
            HorizontalDirection.RIGHT
        );
        enemies.add(crawlid);

        Enemy mosscreep = new Enemy(
            "Mosscreep",
            EnemyType.CRAWLID,
            1020f,
            120f,
            48f,
            34f,
            3,
            55f,
            920f,
            1220f
        );
        enemies.add(mosscreep);

        Enemy hornhead = new Enemy(
            "Hornhead Husk",
            EnemyType.HORNHEAD_HUSK,
            1500f,
            120f,
            62f,
            54f,
            4,
            75f,
            1450f,
            1840f
        );
        hornhead.setState(
            EnemyState.PATROLLING,
            2.2f
        );
        hornhead.setSpawnDirection(
            HorizontalDirection.RIGHT
        );
        enemies.add(hornhead);

        Enemy mosquito = new Enemy(
            "Mosquito",
            EnemyType.MOSQUITO,
            2040f,
            280f,
            42f,
            42f,
            2,
            55f,
            1900f,
            2260f
        );
        enemies.add(mosquito);

        zote = new Zote(350f, 120f);
    }

    private void createGreenpath() {
        // Platforming gauntlet: two ground gaps require jump, air dash,
        // and optionally a downward Nail pogo on the thorn/acid anchors.
        platforms.add(new Platform(0f, 80f, 650f, 40f));
        platforms.add(new Platform(900f, 80f, 550f, 40f));
        platforms.add(new Platform(1750f, 80f, 650f, 40f));

        platforms.add(new Platform(260f, 180f, 220f, 28f));
        platforms.add(new Platform(580f, 260f, 200f, 28f));
        platforms.add(new Platform(920f, 190f, 260f, 28f));
        platforms.add(new Platform(1300f, 310f, 220f, 28f));
        platforms.add(new Platform(1650f, 230f, 260f, 28f));
        platforms.add(new Platform(2050f, 350f, 220f, 28f));

        hazards.add(new Hazard(
            HazardType.GREENPATH_ACID,
            650f,
            80f,
            250f,
            38f,
            1
        ));

        hazards.add(new Hazard(
            HazardType.GREENPATH_ACID,
            1450f,
            80f,
            300f,
            38f,
            1
        ));

        hazards.add(new Hazard(
            HazardType.GREENPATH_THORNS,
            1540f,
            118f,
            80f,
            34f,
            1
        ));

        hazards.add(new Hazard(
            HazardType.GREENPATH_THORNS,
            1500f,
            120f,
            140f,
            34f,
            1
        ));

        hazards.add(new Hazard(
            HazardType.GREENPATH_PLANT,
            1960f,
            120f,
            76f,
            82f,
            1
        ));

        decorations.add(new Decoration(
            DecorationType.GREENPATH_RUINS,
            80f,
            120f,
            360f,
            260f
        ));
        decorations.add(new Decoration(
            DecorationType.GREENPATH_VINES,
            570f,
            330f,
            100f,
            320f
        ));
        decorations.add(new Decoration(
            DecorationType.GREENPATH_RUINS,
            1180f,
            120f,
            390f,
            280f
        ));
        decorations.add(new Decoration(
            DecorationType.GREENPATH_VINES,
            1720f,
            310f,
            110f,
            340f
        ));
        decorations.add(new Decoration(
            DecorationType.CRYSTAL_CLUSTER,
            2260f,
            120f,
            130f,
            120f
        ));

        Enemy crawler = new Enemy(
            "Greenpath Crawler",
            EnemyType.CRAWLID,
            700f,
            120f,
            46f,
            32f,
            3,
            80f,
            620f,
            860f
        );
        crawler.setSpawnDirection(
            HorizontalDirection.RIGHT
        );
        enemies.add(crawler);

        Enemy leafStalker = new Enemy(
            "Leaf Stalker",
            EnemyType.CRAWLID,
            1480f,
            120f,
            50f,
            36f,
            3,
            65f,
            1380f,
            1660f
        );
        enemies.add(leafStalker);

        Enemy mossfly = new Enemy(
            "Mossfly",
            EnemyType.MOSSFLY,
            1180f,
            120f,
            48f,
            40f,
            3,
            0f,
            1030f,
            1450f
        );
        enemies.add(mossfly);

        Enemy guardian = new Enemy(
            "Guardian Crystal",
            EnemyType.GUARDIAN_CRYSTAL,
            1880f,
            120f,
            58f,
            80f,
            5,
            0f,
            1780f,
            2120f
        );
        guardian.setSpawnDirection(
            HorizontalDirection.LEFT
        );
        guardian.setState(
            EnemyState.WATCHING,
            0f
        );
        enemies.add(guardian);

        breakableWall = new BreakableWall(
            2175f,
            120f,
            62f,
            190f
        );
        platforms.add(breakableWall.getCollider());

        secretPickup = new SecretPickup(
            2285f,
            135f,
            54f,
            54f
        );

        applySecretRoomStateToCurrentArea();
    }

    private void applySecretRoomStateToCurrentArea() {
        if (breakableWall != null && secretWallBrokenPersisted) {
            breakableWall.forceBroken();
            platforms.remove(breakableWall.getCollider());
        }

        if (secretPickup != null && secretPickupCollectedPersisted) {
            secretPickup.collect();
        }
    }

    private void createBossArena() {
        platforms.add(new Platform(0f, 80f, 2400f, 40f));

        platforms.add(new Platform(260f, 210f, 220f, 28f));
        platforms.add(new Platform(760f, 250f, 220f, 28f));
        platforms.add(new Platform(1160f, 210f, 220f, 28f));

        hazards.add(new Hazard(
            HazardType.BOSS_CRACK,
            500f,
            120f,
            120f,
            42f,
            1
        ));

        hazards.add(new Hazard(
            HazardType.BOSS_LASER,
            1050f,
            120f,
            150f,
            30f,
            1
        ));

        hazards.add(new Hazard(
            HazardType.BOSS_PIT_EDGE,
            1540f,
            120f,
            130f,
            70f,
            1
        ));

        BossGate entranceGate = new BossGate(45f, 120f, 64f, 230f);
        BossGate exitGate = new BossGate(1745f, 120f, 64f, 230f);
        bossGates.add(entranceGate);
        bossGates.add(exitGate);
        platforms.add(entranceGate.getCollider());
        platforms.add(exitGate.getCollider());

        enemies.add(new Enemy(
            FALSE_KNIGHT_NAME,
            EnemyType.FALSE_KNIGHT,
            760f,
            120f,
            82f,
            92f,
            8,
            35f,
            620f,
            980f
        ));
    }

    public void markEnemyDefeated(String enemyName) {
        if (FALSE_KNIGHT_NAME.equals(enemyName)) {
            defeatedEnemyNames.add(enemyName);
        }
    }

    public List<String> getDefeatedEnemyNames() {
        return new ArrayList<>(defeatedEnemyNames);
    }

    public void applyDefeatedEnemyNames(
        List<String> enemyNames
    ) {
        if (enemyNames == null) {
            return;
        }

        for (String enemyName : enemyNames) {
            if (FALSE_KNIGHT_NAME.equals(enemyName)) {
                defeatedEnemyNames.add(enemyName);
            }
        }

        markDefeatedEnemiesInCurrentArea();
    }

    private void markDefeatedEnemiesInCurrentArea() {
        for (Enemy enemy : enemies) {
            if (
                enemy.getType() == EnemyType.FALSE_KNIGHT
                    && defeatedEnemyNames.contains(
                    enemy.getName()
                )
            ) {
                enemy.markAsDefeated();
            }
        }
    }

    public void movePlayerTo(float x, float y) {
        player.setPosition(x, y);
        player.resetAfterRespawn();

        activeNailAttack = null;
        spellProjectiles.clear();
        activeHowlingWraithsAttack = null;
        enemyLasers.clear();
        bossAttacks.clear();
        bossShockwaves.clear();

        lastSafePlayerX = x;
        lastSafePlayerY = y;
    }

    public Optional<HowlingWraithsAttack>
    getActiveHowlingWraithsAttack() {
        return Optional.ofNullable(
            activeHowlingWraithsAttack
        );
    }

    public void setActiveHowlingWraithsAttack(
        HowlingWraithsAttack attack
    ) {
        this.activeHowlingWraithsAttack = attack;
    }

    public void clearInactiveHowlingWraithsAttack() {
        if (
            activeHowlingWraithsAttack != null
                && !activeHowlingWraithsAttack
                .isActive()
        ) {
            activeHowlingWraithsAttack = null;
        }
    }
}
