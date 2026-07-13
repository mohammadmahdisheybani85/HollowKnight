package io.github.some_example_name.view.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.util.L10n;

public class MenuUiFactory {
    private static final float BUTTON_WIDTH = 320f;
    private static final float BUTTON_HEIGHT = 60f;
    private static final float BUTTON_PADDING = 8f;

    private final Skin skin;

    public MenuUiFactory(Skin skin) {
        this.skin = skin;
    }

    public Label createTitle(String text) {
        Label label = new Label(L10n.tr(text), skin);
        label.setFontScale(1.15f);
        return label;
    }

    public TextButton createButton(String text) {
        TextButton button = new TextButton(L10n.dynamic(text), skin);
        makeTextFit(button);
        return button;
    }

    public TextButton createButton(String text, Runnable action) {
        TextButton button = createButton(text);
        button.addListener(createClickListener(action));
        return button;
    }

    public TextButton createBackButton(Runnable action) {
        return createButton("Back", action);
    }

    public void addMenuButton(Table table, TextButton button) {
        table.add(button)
            .width(BUTTON_WIDTH)
            .height(BUTTON_HEIGHT)
            .pad(BUTTON_PADDING)
            .row();
    }

    public void addBackButton(Table table, TextButton button) {
        table.add(button)
            .width(BUTTON_WIDTH)
            .height(BUTTON_HEIGHT)
            .padTop(20f)
            .row();
    }

    public void makeTextFit(TextButton button) {
        button.getLabel().setWrap(true);
        button.getLabel().setAlignment(Align.center);
        button.getLabel().setFontScale(1f);
    }

    private ChangeListener createClickListener(Runnable action) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        };
    }
}
