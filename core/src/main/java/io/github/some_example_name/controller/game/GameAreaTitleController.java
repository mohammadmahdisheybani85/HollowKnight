package io.github.some_example_name.controller.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.some_example_name.view.ui.AreaTitlePopup;

public class GameAreaTitleController {
    private final AreaTitlePopup areaTitlePopup;

    public GameAreaTitleController(Skin skin, Stage stage) {
        this.areaTitlePopup = new AreaTitlePopup(skin);
        stage.addActor(areaTitlePopup);
    }

    public void showArea(String areaName) {
        areaTitlePopup.showArea(areaName);
    }
}
