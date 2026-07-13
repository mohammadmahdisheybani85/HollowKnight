package io.github.some_example_name.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.Main;
import io.github.some_example_name.util.L10n;

public class MainMenuScreen extends AbstractMenuScreen {

    private static final String LOGO_PATH =
        "ui/main_menu_logo.png";

    private static final String FRENCH_LOGO_PATH =
        "ui/main_menu_logo_fr.png";

    private Texture logoTexture;

    public MainMenuScreen(Main game) {
        super(game);
    }

    @Override
    protected void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.top();
        stage.addActor(root);

        Image logoImage = createLogoImage();

        Table menuTable = new Table();
        menuTable.defaults()
            .width(520f)
            .height(62f)
            .padBottom(6f);

        addMenuButton(
            menuTable,
            "START GAME",
            () -> game.getScreenManager().show(
                ScreenType.GAME_START
            )
        );

        addMenuButton(
            menuTable,
            "SETTINGS",
            () -> game.getScreenManager().show(
                ScreenType.SETTINGS
            )
        );

        addMenuButton(
            menuTable,
            "GUIDE",
            () -> game.getScreenManager().show(
                ScreenType.GUIDE
            )
        );

        addMenuButton(
            menuTable,
            "ACHIEVEMENTS",
            () -> game.getScreenManager().show(
                ScreenType.ACHIEVEMENTS
            )
        );

        addMenuButton(
            menuTable,
            "QUIT GAME",
            Gdx.app::exit
        );

        root.add(logoImage)
            .width(900f)
            .height(265f)
            .padTop(12f)
            .padBottom(8f)
            .row();

        root.add(menuTable)
            .padBottom(20f)
            .row();
    }

    private Image createLogoImage() {
        String selectedLogoPath = L10n.isFrench()
            ? FRENCH_LOGO_PATH
            : LOGO_PATH;

        FileHandle logoFile =
            Gdx.files.internal(selectedLogoPath);

        if (!logoFile.exists()) {
            throw new GdxRuntimeException(
                "Main-menu logo was not found: "
                    + selectedLogoPath
            );
        }

        logoTexture = new Texture(logoFile);

        logoTexture.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );

        Image logoImage = new Image(logoTexture);
        logoImage.setScaling(Scaling.fit);
        logoImage.setTouchable(Touchable.disabled);

        return logoImage;
    }

    private void addMenuButton(
        Table table,
        String text,
        Runnable action
    ) {
        TextButton button = new TextButton(
            L10n.tr(text),
            skin,
            "menu"
        );

        button.getLabel().setAlignment(Align.center);
        button.getLabel().setFontScale(1.18f);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(
                ChangeEvent event,
                Actor actor
            ) {
                action.run();
            }
        });

        table.add(button).row();
    }

    @Override
    protected String getBackgroundPath() {
        return "backgrounds/menu/main_menu.jpg";
    }

    @Override
    public void dispose() {
        super.dispose();

        if (logoTexture != null) {
            logoTexture.dispose();
            logoTexture = null;
        }
    }
}
