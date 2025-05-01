package com.cyberkingdom.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberkingdom.entities.GameEntity;

public class SpriteRenderer {
    private SpriteBatch batch;

    public SpriteRenderer() {
        batch = new SpriteBatch();
    }

    public void render(GameEntity entity) {
        batch.begin();
        if (entity.getAnimation() != null) {
            batch.draw(
                    entity.getAnimation().getCurrentFrame(),
                    entity.getPosition().x,
                    entity.getPosition().y
            );
        }
        batch.end();
    }

    public void dispose() {
        batch.dispose();
    }
}