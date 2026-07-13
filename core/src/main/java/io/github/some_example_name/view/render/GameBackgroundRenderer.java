package io.github.some_example_name.view.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.WorldArea;

public class GameBackgroundRenderer {
    private static final String CROSSROADS_BACKGROUND_BASE_PATH =
        "backgrounds/menu/crossroads";

    private static final String GREENPATH_BACKGROUND_BASE_PATH =
        "backgrounds/menu/greenpath";

    private static final String BOSS_ARENA_BACKGROUND_BASE_PATH =
        "backgrounds/menu/boss_arena";

    private static final String FOG_LAYER_PATH =
        "ui/background_effects/fog_layer.png";

    private static final String DUST_PARTICLES_PATH =
        "ui/background_effects/dust_particles.png";

    private static final String LIGHT_RAYS_PATH =
        "ui/background_effects/light_rays.png";

    private static final String[] SUPPORTED_EXTENSIONS = {
        ".jpg",
        ".jpeg",
        ".png",
        ".jfif"
    };

    private final SpriteBatch batch;

    private final Texture crossroadsBackground;
    private final Texture greenpathBackground;
    private final Texture bossArenaBackground;

    private final Texture fogLayer;
    private final Texture dustParticles;
    private final Texture lightRays;

    private float animationTime;

    public GameBackgroundRenderer() {
        batch = new SpriteBatch();

        crossroadsBackground =
            loadBackgroundTexture(CROSSROADS_BACKGROUND_BASE_PATH);

        greenpathBackground =
            loadBackgroundTexture(GREENPATH_BACKGROUND_BASE_PATH);

        bossArenaBackground =
            loadBackgroundTexture(BOSS_ARENA_BACKGROUND_BASE_PATH);

        fogLayer =
            loadTexture(FOG_LAYER_PATH);

        dustParticles =
            loadTexture(DUST_PARTICLES_PATH);

        lightRays =
            loadTexture(LIGHT_RAYS_PATH);
    }

    public void clear(GameWorld world) {
        Gdx.gl.glClearColor(
            0f,
            0f,
            0f,
            1f
        );

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void render(
        GameWorld world,
        OrthographicCamera camera
    ) {
        animationTime +=
            Gdx.graphics.getDeltaTime();

        Texture background =
            getBackgroundForArea(
                world.getCurrentArea()
            );

        float viewX =
            camera.position.x
                - camera.viewportWidth / 2f;

        float viewY =
            camera.position.y
                - camera.viewportHeight / 2f;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        drawCover(
            background,
            viewX,
            viewY,
            camera.viewportWidth,
            camera.viewportHeight
        );

        drawAnimatedBackgroundEffects(
            world,
            viewX,
            viewY,
            camera.viewportWidth,
            camera.viewportHeight
        );

        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }


    private void drawAnimatedBackgroundEffects(
        GameWorld world,
        float viewX,
        float viewY,
        float viewWidth,
        float viewHeight
    ) {
        float pulse =
            0.5f + 0.5f * (float) Math.sin(animationTime * 0.42f);

        float fogAlpha =
            getFogAlpha(world.getCurrentArea())
                * (0.90f + pulse * 0.10f);

        float rayAlpha =
            getRayAlpha(world.getCurrentArea())
                * (0.82f + pulse * 0.18f);

        batch.setColor(
            1f,
            1f,
            1f,
            rayAlpha
        );

        drawCover(
            lightRays,
            viewX,
            viewY,
            viewWidth,
            viewHeight
        );


        batch.setColor(
            1f,
            1f,
            1f,
            fogAlpha
        );

        drawScrollingLayer(
            fogLayer,
            viewX,
            viewY
                + viewHeight * 0.03f
                + (float) Math.sin(animationTime * 0.28f) * 5f,
            viewWidth,
            viewHeight * 0.48f,
            animationTime * 18f
        );


        setSecondaryFogColor(
            world.getCurrentArea(),
            fogAlpha * 0.42f
        );

        drawScrollingLayer(
            fogLayer,
            viewX,
            viewY + viewHeight * 0.18f,
            viewWidth,
            viewHeight * 0.34f,
            animationTime * -9f
        );


        setParticleColor(
            world.getCurrentArea(),
            0.36f
        );

        drawScrollingLayer(
            dustParticles,
            viewX,
            viewY
                + (float) Math.sin(animationTime * 0.65f) * 7f,
            viewWidth,
            viewHeight,
            animationTime * -10f
        );

        batch.setColor(
            1f,
            1f,
            1f,
            1f
        );
    }

    private void setSecondaryFogColor(WorldArea area, float alpha) {
        if (area == WorldArea.GREENPATH) {
            batch.setColor(0.62f, 1f, 0.70f, alpha);
            return;
        }
        if (area == WorldArea.BOSS_ARENA) {
            batch.setColor(0.84f, 0.66f, 1f, alpha);
            return;
        }
        batch.setColor(0.78f, 0.88f, 1f, alpha);
    }

    private void setParticleColor(WorldArea area, float alpha) {
        if (area == WorldArea.GREENPATH) {
            batch.setColor(0.68f, 1f, 0.55f, alpha);
            return;
        }
        if (area == WorldArea.BOSS_ARENA) {
            batch.setColor(0.92f, 0.74f, 1f, alpha);
            return;
        }
        batch.setColor(0.82f, 0.90f, 1f, alpha);
    }

    private float getFogAlpha(WorldArea area) {
        if (area == WorldArea.GREENPATH) {
            return 0.28f;
        }

        if (area == WorldArea.BOSS_ARENA) {
            return 0.22f;
        }

        return 0.34f;
    }

    private float getRayAlpha(WorldArea area) {
        if (area == WorldArea.GREENPATH) {
            return 0.18f;
        }

        if (area == WorldArea.BOSS_ARENA) {
            return 0.13f;
        }

        return 0.16f;
    }

    private void drawScrollingLayer(
        Texture texture,
        float viewX,
        float viewY,
        float viewWidth,
        float viewHeight,
        float offset
    ) {
        float drawHeight =
            viewHeight;

        float drawWidth =
            drawHeight
                * texture.getWidth()
                / texture.getHeight();

        float startX =
            viewX
                - drawWidth
                + offset % drawWidth;

        while (startX < viewX + viewWidth) {
            batch.draw(
                texture,
                startX,
                viewY,
                drawWidth,
                drawHeight
            );

            startX += drawWidth;
        }
    }

    private Texture getBackgroundForArea(WorldArea area) {
        if (area == WorldArea.GREENPATH) {
            return greenpathBackground;
        }

        if (area == WorldArea.BOSS_ARENA) {
            return bossArenaBackground;
        }

        return crossroadsBackground;
    }

    private Texture loadBackgroundTexture(String basePath) {
        FileHandle imageFile =
            findImageFile(basePath);

        if (imageFile == null) {
            throw new IllegalStateException(
                "Background file not found. Tried: "
                    + basePath
                    + ".jpg, .jpeg, .png, .jfif"
            );
        }

        Texture texture =
            new Texture(imageFile);

        texture.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );

        return texture;
    }

    private FileHandle findImageFile(String basePath) {
        for (String extension : SUPPORTED_EXTENSIONS) {
            FileHandle candidate =
                Gdx.files.internal(basePath + extension);

            if (candidate.exists()) {
                return candidate;
            }
        }

        return null;
    }

    private Texture loadTexture(String path) {
        if (!Gdx.files.internal(path).exists()) {
            throw new IllegalStateException(
                "Texture not found: " + path
            );
        }

        Texture texture =
            new Texture(
                Gdx.files.internal(path)
            );

        texture.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );

        return texture;
    }

    private void drawCover(
        Texture texture,
        float x,
        float y,
        float width,
        float height
    ) {
        float scale =
            Math.max(
                width / texture.getWidth(),
                height / texture.getHeight()
            );

        float drawWidth =
            texture.getWidth() * scale;

        float drawHeight =
            texture.getHeight() * scale;

        float drawX =
            x + (width - drawWidth) / 2f;

        float drawY =
            y + (height - drawHeight) / 2f;

        batch.draw(
            texture,
            drawX,
            drawY,
            drawWidth,
            drawHeight
        );
    }

    public void dispose() {
        batch.dispose();

        crossroadsBackground.dispose();
        greenpathBackground.dispose();
        bossArenaBackground.dispose();

        fogLayer.dispose();
        dustParticles.dispose();
        lightRays.dispose();
    }
}
