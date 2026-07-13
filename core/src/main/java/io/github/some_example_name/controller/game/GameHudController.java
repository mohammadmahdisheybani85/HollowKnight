package io.github.some_example_name.controller.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.some_example_name.model.player.PlayerStats;
import io.github.some_example_name.view.ui.GameHud;

public class GameHudController {
    private static final float LEFT_MARGIN = 18f;
    private static final float TOP_MARGIN = 14f;

    private final GameHud hud;

    public GameHudController(
        Skin skin,
        Stage stage
    ) {
        hud = new GameHud(skin);

        stage.addActor(hud);

        position(stage);
    }

    public void update(PlayerStats playerStats) {
        hud.update(playerStats);
    }

    public void position(Stage stage) {
        float stageHeight =
            stage.getViewport().getWorldHeight();

        hud.setPosition(
            LEFT_MARGIN,
            stageHeight
                - hud.getHeight()
                - TOP_MARGIN
        );
    }

    public void dispose() {
        hud.dispose();
    }
}
