package io.github.some_example_name.controller.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.some_example_name.controller.inventory.InventoryController;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.player.PlayerStats;
import io.github.some_example_name.view.render.GameBackgroundRenderer;
import io.github.some_example_name.view.render.GameWorldRenderer;

public class GameRenderController {
    private static final float CAMERA_VIEWPORT_WIDTH = 900f;
    private static final float CAMERA_VIEWPORT_HEIGHT = 520f;

    private final GameBackgroundRenderer backgroundRenderer;
    private final GameWorldRenderer worldRenderer;
    private final OrthographicCamera camera;
    private final GameCameraController cameraController;

    public GameRenderController(InventoryController inventoryController) {
        backgroundRenderer = new GameBackgroundRenderer();
        worldRenderer = new GameWorldRenderer(inventoryController);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, CAMERA_VIEWPORT_WIDTH, CAMERA_VIEWPORT_HEIGHT);
        cameraController = new GameCameraController(camera);
    }

    public void clear(GameWorld world) {
        backgroundRenderer.clear(world);
    }

    public void renderWorld(
        GameWorld world,
        PlayerStats playerStats,
        float delta
    ) {
        cameraController.update(world, delta);
        backgroundRenderer.render(world, camera);
        worldRenderer.render(world, camera, playerStats);
    }

    public void dispose() {
        backgroundRenderer.dispose();
        worldRenderer.dispose();
    }
}
