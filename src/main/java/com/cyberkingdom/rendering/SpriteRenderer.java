package com.cyberkingdom.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cyberkingdom.entities.GameEntity;

public class SpriteRenderer {
    private SpriteBatch batch;

    public SpriteRenderer(SpriteBatch batch) {
        this.batch = batch;
    }

    public void render(GameEntity entity) {
        if (entity.isActive() && entity.getAnimation() != null) {
            TextureRegion frame = entity.getAnimation().getCurrentFrame(0.016f); // ~60 FPS
            if (frame != null) {
                batch.draw(frame, entity.getPosition().x, entity.getPosition().y, 32, 32);
                System.out.println("Рендеринг " + entity.getName() + " на (" + entity.getPosition().x + ", " + entity.getPosition().y + ")");
            } else {
                System.err.println("Нет кадра для " + entity.getName());
            }
        }
    }

    public void begin() {
        batch.begin();
    }

    public void end() {
        batch.end();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void dispose() {
        // SpriteBatch освобождается в GameEngine
    }
}