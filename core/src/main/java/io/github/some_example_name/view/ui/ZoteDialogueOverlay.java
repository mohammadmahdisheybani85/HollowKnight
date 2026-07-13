package io.github.some_example_name.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.util.L10n;

public class ZoteDialogueOverlay extends Group {
    private static final String BOX_TEXTURE_PATH =
        "ui/dialogue/dialogue_box.png";

    private static final float WIDTH = 820f;
    private static final float HEIGHT = 164f;

    private final Texture boxTexture;
    private final Label speakerLabel;
    private final Label dialogueLabel;
    private final Label continueLabel;

    public ZoteDialogueOverlay(
        Skin skin,
        Stage stage
    ) {
        setSize(WIDTH, HEIGHT);
        setVisible(false);

        boxTexture = new Texture(
            Gdx.files.internal(BOX_TEXTURE_PATH)
        );
        boxTexture.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );

        Image background = new Image(
            new TextureRegionDrawable(
                new TextureRegion(boxTexture)
            )
        );
        background.setBounds(0f, 0f, WIDTH, HEIGHT);

        speakerLabel = new Label(L10n.tr("ZOTE"), skin);
        speakerLabel.setBounds(34f, 112f, 220f, 34f);
        speakerLabel.setFontScale(1.05f);
        speakerLabel.setColor(
            0.78f,
            0.88f,
            1f,
            1f
        );

        dialogueLabel = new Label("", skin);
        dialogueLabel.setBounds(34f, 42f, 750f, 75f);
        dialogueLabel.setWrap(true);
        dialogueLabel.setAlignment(Align.topLeft);
        dialogueLabel.setFontScale(0.84f);

        continueLabel = new Label(
            L10n.isFrench() ? "ENTREE / E  -  continuer" : "ENTER / E  -  continue",
            skin
        );
        continueLabel.setBounds(
            470f,
            12f,
            310f,
            26f
        );
        continueLabel.setAlignment(Align.right);
        continueLabel.setFontScale(0.63f);
        continueLabel.setColor(
            0.70f,
            0.78f,
            0.88f,
            0.95f
        );

        addActor(background);
        addActor(speakerLabel);
        addActor(dialogueLabel);
        addActor(continueLabel);

        stage.addActor(this);
        position(stage);
    }

    public void showLine(String text) {
        dialogueLabel.setText(text);
        setVisible(true);
        toFront();
    }

    public void hideDialogue() {
        setVisible(false);
    }

    public void position(Stage stage) {
        float stageWidth =
            stage.getViewport().getWorldWidth();

        setPosition(
            (stageWidth - WIDTH) / 2f,
            70f
        );
    }

    public void dispose() {
        boxTexture.dispose();
        remove();
    }
}
