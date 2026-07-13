package io.github.some_example_name.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public final class SoftwareCursor implements Disposable {
    private static final String CURSOR_TEXTURE_PATH =
        "ui/knight_cursor.png";

    private static final float CURSOR_SIZE = 48f;


    private static final float HOTSPOT_X = 11.25f;
    private static final float HOTSPOT_FROM_TOP = 6f;

    private final Stage stage;
    private final Texture cursorTexture;
    private final Image cursorImage;
    private final Vector2 mousePosition;

    private boolean disposed;

    public SoftwareCursor() {
        stage = new Stage(new ScreenViewport());

        if (!Gdx.files.internal(CURSOR_TEXTURE_PATH).exists()) {
            throw new IllegalStateException(
                "Cursor texture not found: "
                    + CURSOR_TEXTURE_PATH
            );
        }

        cursorTexture = new Texture(
            Gdx.files.internal(CURSOR_TEXTURE_PATH)
        );

        cursorTexture.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );

        cursorImage = new Image(cursorTexture);
        cursorImage.setSize(CURSOR_SIZE, CURSOR_SIZE);
        cursorImage.setTouchable(Touchable.disabled);

        stage.addActor(cursorImage);

        mousePosition = new Vector2();

        stage.getViewport().update(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            true
        );

        hideSystemCursor();
    }

    public void render(float delta) {
        if (disposed) {
            return;
        }

        updatePosition();

        stage.act(delta);
        stage.draw();
    }

    private void updatePosition() {
        mousePosition.set(
            Gdx.input.getX(),
            Gdx.input.getY()
        );


        stage.getViewport().unproject(mousePosition);

        float cursorX =
            mousePosition.x - HOTSPOT_X;

        float cursorY =
            mousePosition.y
                - CURSOR_SIZE
                + HOTSPOT_FROM_TOP;

        cursorImage.setPosition(
            cursorX,
            cursorY
        );
    }

    private void hideSystemCursor() {
        try {
            Gdx.graphics.setSystemCursor(
                Cursor.SystemCursor.None
            );
        } catch (Exception exception) {
            Gdx.app.error(
                "SoftwareCursor",
                "Could not hide the system cursor.",
                exception
            );
        }
    }

    public void resize(int width, int height) {
        if (disposed) {
            return;
        }

        stage.getViewport().update(
            width,
            height,
            true
        );
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }

        disposed = true;

        try {
            Gdx.graphics.setSystemCursor(
                Cursor.SystemCursor.Arrow
            );
        } catch (Exception exception) {
            Gdx.app.error(
                "SoftwareCursor",
                "Could not restore the system cursor.",
                exception
            );
        }

        stage.dispose();
        cursorTexture.dispose();
    }
}
