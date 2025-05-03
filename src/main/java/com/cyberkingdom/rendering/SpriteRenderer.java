package com.cyberkingdom.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cyberkingdom.entities.GameEntity;

public class SpriteRenderer {
    private final SpriteBatch batch;

    public SpriteRenderer(SpriteBatch batch) {
        this.batch = batch;
    }

    public void render(GameEntity entity) {
        if (entity.isActive() && entity.getAnimation() != null) {
            TextureRegion frame = entity.getAnimation().getCurrentFrame(0.016f);
            if (frame != null) {
                float width = frame.getRegionWidth();
                float height = frame.getRegionHeight();
                batch.draw(frame,
                        entity.getPosition().x - width / 2f,
                        entity.getPosition().y - height / 2f,
                        width,
                        height);
            } else {
                System.err.println("Нет кадра для " + entity.getName());
            }
        }
    }

    public void begin() { batch.begin(); }
    public void end() { batch.end(); }
    public SpriteBatch getBatch() { return batch; }
}