package io.github.some_example_name.controller.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.Player;
import io.github.some_example_name.model.game.WorldArea;

public class GameCameraController {
    private static final float FOLLOW_SPEED = 5.5f;
    private static final float BOSS_ARENA_MAX_X = 1810f;

    private final OrthographicCamera camera;

    public GameCameraController(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void update(GameWorld world, float delta) {
        Player player = world.getPlayer();
        float targetX = player.getCenterX();
        float targetY = player.getCenterY();

        float halfW = camera.viewportWidth / 2f;
        float halfH = camera.viewportHeight / 2f;
        float maxWorldX = world.getCurrentArea() == WorldArea.BOSS_ARENA
            ? BOSS_ARENA_MAX_X
            : world.getWorldWidth();

        targetX = clamp(targetX, halfW, maxWorldX - halfW);
        targetY = clamp(targetY, halfH, world.getWorldHeight() - halfH);

        float alpha = 1f - (float) Math.exp(-FOLLOW_SPEED * Math.max(0f, delta));
        float smoothX = MathUtils.lerp(camera.position.x, targetX, alpha);
        float smoothY = MathUtils.lerp(camera.position.y, targetY, alpha);

        if (world.getCameraShakeTimeLeft() > 0f) {
            float intensity = world.getCameraShakeIntensity();
            smoothX += MathUtils.random(-intensity, intensity);
            smoothY += MathUtils.random(-intensity, intensity);
        }

        camera.position.set(smoothX, smoothY, 0f);
        camera.update();
    }

    private float clamp(float value, float min, float max) {
        if (min > max) return value;
        return Math.max(min, Math.min(max, value));
    }
}
