package com.cyberkingdom.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Platform;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.entities.Player;

public class SpriteRenderer {
    private final SpriteBatch batch;
    private final SpriteManager spriteManager;

    public SpriteRenderer() {
        this.batch = new SpriteBatch();
        this.spriteManager = new SpriteManager();
    }

    public SpriteManager getSpriteManager() {
        return spriteManager;
    }

    public void render(GameEntity entity) {
        if (entity == null) {
            Gdx.app.error("SpriteRenderer", "Entity is null");
            return;
        }

        if (entity instanceof Player) {
            ((Player) entity).render(batch);
        } else if (entity instanceof Item) {
            ((Item) entity).render(batch);
        } else if (entity instanceof Platform) {
            Platform platform = (Platform) entity;
            if (!platform.isGround()) {
                platform.render(batch);
            }
        } else {
            // Для остальных сущностей используем стандартную отрисовку
            float x = entity.getPosition().x;
            float y = entity.getPosition().y;
            float width = entity.getTexture().getWidth();
            float height = entity.getTexture().getHeight();

            if (entity.getAnimation() != null) {
                TextureRegion currentFrame = entity.getAnimation().getCurrentFrame(Gdx.graphics.getDeltaTime());
                if (currentFrame != null) {
                    batch.draw(currentFrame, x, y, width, height);
                    return;
                }
            }

            batch.draw(entity.getTexture(), x, y, width, height);
        }
    }

    public void begin() {
        batch.begin();
    }

    public void end() {
        batch.end();
    }

    public void dispose() {
        batch.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}