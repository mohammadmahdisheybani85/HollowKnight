package io.github.some_example_name.controller.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.some_example_name.model.game.Enemy;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.WorldArea;
import io.github.some_example_name.view.ui.BossHud;

public class GameBossHudController {
    private static final String FALSE_KNIGHT_NAME =
        "False Knight";

    private static final float TOP_MARGIN = 55f;

    private final GameWorld world;
    private final BossHud bossHud;

    public GameBossHudController(
        GameWorld world,
        Skin skin,
        Stage stage
    ) {
        this.world = world;
        this.bossHud = new BossHud(skin);

        stage.addActor(bossHud);
        position(stage);
    }

    public void update() {
        if (
            world.getCurrentArea()
                != WorldArea.BOSS_ARENA
        ) {
            bossHud.setVisible(false);
            return;
        }

        Enemy falseKnight =
            world.findEnemyByName(
                FALSE_KNIGHT_NAME
            );

        bossHud.update(falseKnight);
    }

    public void position(Stage stage) {
        float stageWidth =
            stage.getViewport().getWorldWidth();

        float stageHeight =
            stage.getViewport().getWorldHeight();

        bossHud.setPosition(
            (stageWidth - bossHud.getWidth()) / 2f,
            stageHeight
                - bossHud.getHeight()
                - TOP_MARGIN
        );
    }
}
