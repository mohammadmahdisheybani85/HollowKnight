package io.github.some_example_name.model.game;

public class Enemy {
    private static final float HIT_EFFECT_DURATION = 0.18f;
    private static final float DEATH_EFFECT_DURATION = 0.55f;
    private static final float CORPSE_VISIBLE_DURATION = 2.4f;

    private final String name;
    private final EnemyType type;
    private final float width;
    private final float height;
    private final int maxHealth;
    private final float patrolMinX;
    private final float patrolMaxX;
    private final float spawnX;
    private final float spawnY;

    private float x;
    private float y;
    private float speed;
    private int health;
    private boolean alive = true;
    private boolean permanentlyDefeated;
    private boolean deathHandled;

    private HorizontalDirection direction = HorizontalDirection.LEFT;
    private HorizontalDirection spawnDirection = HorizontalDirection.LEFT;
    private EnemyState state;

    private float stateTimeLeft;
    private HorizontalDirection chargeDirection = HorizontalDirection.LEFT;

    private float targetX;
    private float targetY;
    private float velocityX;
    private float velocityY;

    private float hitEffectTimeLeft;
    private float deathEffectTimeLeft;
    private float deathElapsed;

    private BossMoveType bossMoveType = BossMoveType.PATROL;
    private boolean bossPhaseTwo;
    private boolean bossArmorOpen;

    public Enemy(
        String name,
        float x,
        float y,
        float width,
        float height,
        int maxHealth,
        float speed,
        float patrolMinX,
        float patrolMaxX
    ) {
        this(
            name,
            EnemyType.BASIC_GROUND,
            x,
            y,
            width,
            height,
            maxHealth,
            speed,
            patrolMinX,
            patrolMaxX
        );
    }

    public Enemy(
        String name,
        EnemyType type,
        float x,
        float y,
        float width,
        float height,
        int maxHealth,
        float speed,
        float patrolMinX,
        float patrolMaxX
    ) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.spawnX = x;
        this.spawnY = y;
        this.width = width;
        this.height = height;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.speed = speed;
        this.patrolMinX = patrolMinX;
        this.patrolMaxX = patrolMaxX;
        this.state = initialStateFor(type);
    }

    private EnemyState initialStateFor(EnemyType enemyType) {
        if (enemyType == EnemyType.MOSSFLY) {
            return EnemyState.CAMOUFLAGED;
        }
        if (enemyType == EnemyType.GUARDIAN_CRYSTAL) {
            return EnemyState.WATCHING;
        }
        return EnemyState.PATROLLING;
    }

    public void updateTimers(float delta) {
        hitEffectTimeLeft = Math.max(0f, hitEffectTimeLeft - delta);
        deathEffectTimeLeft = Math.max(0f, deathEffectTimeLeft - delta);

        if (!alive && !permanentlyDefeated) {
            deathElapsed += delta;
        }
    }

    public String getName() {
        return name;
    }

    public EnemyType getType() {
        return type;
    }

    public EnemyState getState() {
        return state;
    }

    public void setState(EnemyState state, float duration) {
        this.state = state;
        this.stateTimeLeft = Math.max(0f, duration);
    }

    public void reduceStateTime(float delta) {
        stateTimeLeft = Math.max(0f, stateTimeLeft - delta);
    }

    public boolean isStateTimeFinished() {
        return stateTimeLeft <= 0f;
    }

    public float getStateTimeLeft() {
        return stateTimeLeft;
    }

    public HorizontalDirection getChargeDirection() {
        return chargeDirection;
    }

    public void setChargeDirection(
        HorizontalDirection chargeDirection
    ) {
        this.chargeDirection = chargeDirection;
    }

    public void setTarget(float targetX, float targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public float getTargetX() {
        return targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    public void setVelocity(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getSpawnX() {
        return spawnX;
    }

    public float getSpawnY() {
        return spawnY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isPermanentlyDefeated() {
        return permanentlyDefeated;
    }

    public HorizontalDirection getDirection() {
        return direction;
    }

    public float getCenterX() {
        return x + width / 2f;
    }

    public float getCenterY() {
        return y + height / 2f;
    }

    public float getSpeed() {
        return speed;
    }

    public float getPatrolMinX() {
        return patrolMinX;
    }

    public float getPatrolMaxX() {
        return patrolMaxX;
    }



    public BossMoveType getBossMoveType() {
        return bossMoveType;
    }

    public void setBossMoveType(BossMoveType bossMoveType) {
        this.bossMoveType = bossMoveType == null
            ? BossMoveType.PATROL
            : bossMoveType;
    }

    public boolean isBossPhaseTwo() {
        return bossPhaseTwo;
    }

    public void setBossPhaseTwo(boolean bossPhaseTwo) {
        this.bossPhaseTwo = bossPhaseTwo;
    }

    public boolean isBossArmorOpen() {
        return bossArmorOpen;
    }

    public void setBossArmorOpen(boolean bossArmorOpen) {
        this.bossArmorOpen = bossArmorOpen;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void face(HorizontalDirection direction) {
        this.direction = direction;
    }

    public void setSpawnDirection(
        HorizontalDirection spawnDirection
    ) {
        this.spawnDirection = spawnDirection;
        this.direction = spawnDirection;
    }

    public void patrol(float delta) {
        moveHorizontally(speed, delta);
    }

    public void moveHorizontally(
        float movementSpeed,
        float delta
    ) {
        x += movementSpeed * direction.getSign() * delta;

        if (x <= patrolMinX) {
            x = patrolMinX;
            direction = HorizontalDirection.RIGHT;
        } else if (x + width >= patrolMaxX) {
            x = patrolMaxX - width;
            direction = HorizontalDirection.LEFT;
        }
    }

    public void moveInDirection(
        HorizontalDirection movementDirection,
        float movementSpeed,
        float delta
    ) {
        x += movementSpeed
            * movementDirection.getSign()
            * delta;

        x = Math.max(
            patrolMinX,
            Math.min(patrolMaxX - width, x)
        );
    }

    public void moveByVelocity(float delta) {
        x += velocityX * delta;
        y += velocityY * delta;
    }

    public void applyInstantKnockback(
        float distance,
        HorizontalDirection knockbackDirection
    ) {
        if (!alive || distance <= 0f) {
            return;
        }

        x += distance * knockbackDirection.getSign();
        x = Math.max(
            patrolMinX,
            Math.min(patrolMaxX - width, x)
        );
    }

    public boolean takeDamage(int damage) {
        if (!alive || damage <= 0) {
            return false;
        }

        health = Math.max(0, health - damage);
        hitEffectTimeLeft = HIT_EFFECT_DURATION;

        if (health == 0) {
            alive = false;
            deathElapsed = 0f;
            deathEffectTimeLeft = DEATH_EFFECT_DURATION;
        }

        return true;
    }

    public boolean isHitEffectActive() {
        return hitEffectTimeLeft > 0f;
    }

    public float getHitEffectProgress() {
        return 1f - hitEffectTimeLeft
            / HIT_EFFECT_DURATION;
    }

    public boolean isDeathEffectActive() {
        return deathEffectTimeLeft > 0f;
    }

    public float getDeathEffectProgress() {
        return 1f - deathEffectTimeLeft
            / DEATH_EFFECT_DURATION;
    }

    public boolean isCorpseVisible() {
        return !alive
            && !permanentlyDefeated
            && deathElapsed <= CORPSE_VISIBLE_DURATION;
    }

    public float getDeathElapsed() {
        return deathElapsed;
    }

    public boolean isDeathHandled() {
        return deathHandled;
    }

    public void markDeathHandled() {
        deathHandled = true;
    }

    public void reviveAtSpawn() {
        if (permanentlyDefeated) {
            return;
        }

        x = spawnX;
        y = spawnY;
        health = maxHealth;
        alive = true;
        deathHandled = false;
        deathElapsed = 0f;
        deathEffectTimeLeft = 0f;
        hitEffectTimeLeft = 0f;
        velocityX = 0f;
        velocityY = 0f;
        direction = spawnDirection;
        state = initialStateFor(type);
        stateTimeLeft = 0f;
        bossMoveType = BossMoveType.PATROL;
        bossPhaseTwo = false;
        bossArmorOpen = false;
    }

    public void markAsDefeated() {
        health = 0;
        alive = false;
        permanentlyDefeated = true;
        deathHandled = true;
        deathElapsed = CORPSE_VISIBLE_DURATION + 1f;
        deathEffectTimeLeft = 0f;
    }
}
