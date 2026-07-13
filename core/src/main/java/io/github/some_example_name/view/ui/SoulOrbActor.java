package io.github.some_example_name.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SoulOrbActor extends Actor {
    private static final int MAX_SOUL = 99;
    private static final int CIRCLE_SEGMENTS = 96;

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private int soul;

    public SoulOrbActor() {
        setSize(48f, 48f);
    }

    public void setSoul(int soul) {
        this.soul = Math.max(0, Math.min(MAX_SOUL, soul));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());

        float size = Math.min(getWidth(), getHeight());
        float centerX = getX() + getWidth() / 2f;
        float centerY = getY() + getHeight() / 2f;
        float radius = size / 2f;

        float soulRatio = soul / (float) MAX_SOUL;
        float fillRadius = radius * soulRatio;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(new Color(0.03f, 0.04f, 0.07f, parentAlpha));
        shapeRenderer.circle(centerX, centerY, radius, CIRCLE_SEGMENTS);

        if (soul > 0) {
            shapeRenderer.setColor(new Color(0.88f, 0.94f, 1f, parentAlpha));
            shapeRenderer.circle(centerX, centerY, fillRadius, CIRCLE_SEGMENTS);
        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(new Color(0.92f, 0.92f, 1f, parentAlpha));
        shapeRenderer.circle(centerX, centerY, radius, CIRCLE_SEGMENTS);

        shapeRenderer.end();

        batch.begin();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
