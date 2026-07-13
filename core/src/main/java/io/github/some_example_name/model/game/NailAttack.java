package io.github.some_example_name.model.game;

public class NailAttack {
    private static final float HORIZONTAL_WIDTH = 54f;
    private static final float HORIZONTAL_HEIGHT = 24f;
    private static final float VERTICAL_WIDTH = 28f;
    private static final float VERTICAL_HEIGHT = 58f;
    private static final float ATTACK_OFFSET = 8f;

    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final AttackDirection direction;

    private float timeLeft;

    public NailAttack(
        Player player,
        float duration,
        AttackDirection direction
    ) {
        this.direction = direction;
        this.timeLeft = duration;

        if (direction == AttackDirection.UP) {
            width = VERTICAL_WIDTH;
            height = VERTICAL_HEIGHT;
            x = player.getCenterX() - width / 2f;
            y = player.getY() + player.getHeight() + ATTACK_OFFSET;
        } else if (direction == AttackDirection.DOWN) {
            width = VERTICAL_WIDTH;
            height = VERTICAL_HEIGHT;
            x = player.getCenterX() - width / 2f;
            y = player.getY() - height - ATTACK_OFFSET;
        } else {
            width = HORIZONTAL_WIDTH;
            height = HORIZONTAL_HEIGHT;

            if (player.getFacingDirection() == HorizontalDirection.RIGHT) {
                x = player.getX() + player.getWidth() + ATTACK_OFFSET;
            } else {
                x = player.getX() - width - ATTACK_OFFSET;
            }

            y = player.getY() + player.getHeight() / 2f - height / 2f;
        }
    }

    public void update(float delta) {
        timeLeft = Math.max(0f, timeLeft - delta);
    }

    public boolean isActive() { return timeLeft > 0f; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public AttackDirection getDirection() { return direction; }
}
