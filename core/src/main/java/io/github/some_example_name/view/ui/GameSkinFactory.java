package io.github.some_example_name.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.GdxRuntimeException;

public final class GameSkinFactory {

    private static final String FONT_PATH =
        "fonts/menu-font.otf";

    private static final int NORMAL_FONT_SIZE = 26;
    private static final int TITLE_FONT_SIZE = 52;

    private GameSkinFactory() {
    }

    public static Skin create() {
        Skin skin = new Skin();

        BitmapFont normalFont =
            createFont(NORMAL_FONT_SIZE);

        BitmapFont titleFont =
            createFont(TITLE_FONT_SIZE);

        skin.add("default-font", normalFont);
        skin.add("title-font", titleFont);

        addBaseTexture(skin);
        addLabelStyles(skin, normalFont, titleFont);
        addTextButtonStyles(skin, normalFont);
        addCheckBoxStyle(skin, normalFont);
        addSliderStyle(skin);
        addScrollPaneStyle(skin);

        return skin;
    }

    private static BitmapFont createFont(int size) {
        FileHandle fontFile =
            Gdx.files.internal(FONT_PATH);

        if (!fontFile.exists()) {
            throw new GdxRuntimeException(
                "Font file was not found: " + FONT_PATH
            );
        }

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(fontFile);

        try {
            FreeTypeFontGenerator.FreeTypeFontParameter
                parameter =
                new FreeTypeFontGenerator
                    .FreeTypeFontParameter();

            parameter.size = size;
            parameter.color = Color.WHITE;
            parameter.kerning = true;
            parameter.genMipMaps = false;

            parameter.minFilter =
                Texture.TextureFilter.Linear;

            parameter.magFilter =
                Texture.TextureFilter.Linear;

            BitmapFont font =
                generator.generateFont(parameter);

            font.setUseIntegerPositions(false);

            return font;
        } finally {
            generator.dispose();
        }
    }

    private static void addBaseTexture(Skin skin) {
        Pixmap pixmap = new Pixmap(
            1,
            1,
            Pixmap.Format.RGBA8888
        );

        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        skin.add("white", texture);
    }

    private static void addLabelStyles(
        Skin skin,
        BitmapFont normalFont,
        BitmapFont titleFont
    ) {
        Label.LabelStyle defaultStyle =
            new Label.LabelStyle();

        defaultStyle.font = normalFont;

        defaultStyle.fontColor = new Color(
            0.94f,
            0.95f,
            0.91f,
            1f
        );

        skin.add("default", defaultStyle);

        Label.LabelStyle titleStyle =
            new Label.LabelStyle();

        titleStyle.font = titleFont;

        titleStyle.fontColor = new Color(
            1f,
            0.98f,
            0.90f,
            1f
        );

        skin.add("title", titleStyle);
    }

    private static void addTextButtonStyles(
        Skin skin,
        BitmapFont font
    ) {
        TextButton.TextButtonStyle defaultStyle =
            new TextButton.TextButtonStyle();

        defaultStyle.font = font;
        defaultStyle.fontColor = Color.WHITE;

        defaultStyle.overFontColor = new Color(
            0.70f,
            0.88f,
            1f,
            1f
        );

        defaultStyle.downFontColor = new Color(
            0.45f,
            0.65f,
            0.86f,
            1f
        );

        defaultStyle.up = skin.newDrawable(
            "white",
            new Color(
                0.025f,
                0.035f,
                0.075f,
                0.88f
            )
        );

        defaultStyle.over = skin.newDrawable(
            "white",
            new Color(
                0.08f,
                0.15f,
                0.25f,
                0.94f
            )
        );

        defaultStyle.down = skin.newDrawable(
            "white",
            new Color(
                0.015f,
                0.025f,
                0.055f,
                0.98f
            )
        );

        skin.add("default", defaultStyle);

        TextButton.TextButtonStyle menuStyle =
            new TextButton.TextButtonStyle();

        menuStyle.font = font;

        menuStyle.fontColor = new Color(
            0.88f,
            0.92f,
            0.97f,
            1f
        );

        menuStyle.overFontColor = new Color(
            0.62f,
            0.84f,
            1f,
            1f
        );

        menuStyle.downFontColor = new Color(
            0.58f,
            0.80f,
            1f,
            1f
        );

        menuStyle.up = null;
        menuStyle.over = null;
        menuStyle.down = null;

        skin.add("menu", menuStyle);
    }

    private static void addCheckBoxStyle(
        Skin skin,
        BitmapFont font
    ) {
        CheckBox.CheckBoxStyle style =
            new CheckBox.CheckBoxStyle();

        style.font = font;
        style.fontColor = Color.WHITE;

        var checkboxOff = skin.newDrawable(
            "white",
            new Color(
                0.22f,
                0.25f,
                0.32f,
                1f
            )
        );

        checkboxOff.setMinWidth(20f);
        checkboxOff.setMinHeight(20f);

        var checkboxOn = skin.newDrawable(
            "white",
            new Color(
                0.70f,
                0.88f,
                1f,
                1f
            )
        );

        checkboxOn.setMinWidth(20f);
        checkboxOn.setMinHeight(20f);

        style.checkboxOff = checkboxOff;
        style.checkboxOn = checkboxOn;

        skin.add("default", style);
    }

    private static void addSliderStyle(Skin skin) {
        Slider.SliderStyle style =
            new Slider.SliderStyle();

        style.background = skin.newDrawable(
            "white",
            new Color(
                0.20f,
                0.24f,
                0.31f,
                0.92f
            )
        );

        style.knob = skin.newDrawable(
            "white",
            new Color(
                0.84f,
                0.92f,
                1f,
                1f
            )
        );

        style.background.setMinHeight(9f);
        style.knob.setMinWidth(18f);
        style.knob.setMinHeight(28f);

        skin.add("default-horizontal", style);
    }

    private static void addScrollPaneStyle(
        Skin skin
    ) {
        ScrollPane.ScrollPaneStyle style =
            new ScrollPane.ScrollPaneStyle();

        style.background = skin.newDrawable(
            "white",
            new Color(
                0.015f,
                0.025f,
                0.055f,
                0.56f
            )
        );

        style.vScroll = skin.newDrawable(
            "white",
            new Color(
                0.10f,
                0.14f,
                0.20f,
                0.75f
            )
        );

        style.vScrollKnob = skin.newDrawable(
            "white",
            new Color(
                0.55f,
                0.72f,
                0.90f,
                0.95f
            )
        );

        style.vScroll.setMinWidth(9f);
        style.vScrollKnob.setMinWidth(9f);
        style.vScrollKnob.setMinHeight(42f);

        skin.add("default", style);
    }
}
