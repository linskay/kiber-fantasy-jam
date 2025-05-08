package com.cyberkingdom.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Platform;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.entities.Player;

public class SpriteRenderer {
    private final SpriteBatch batch;

    public SpriteRenderer() {
        this.batch = new SpriteBatch();
    }

    public void render(GameEntity entity) {
        if (entity == null || entity.getTexture() == null) {
            Gdx.app.error("SpriteRenderer", "Entity or texture is null");
            return;
        }

        float x = entity.getPosition().x;
        float y = entity.getPosition().y;
        float width = 32; // Стандартный размер по умолчанию
        float height = 32;

        // Специальная обработка для платформ
        if (entity instanceof Platform) {
            Platform platform = (Platform) entity;
            if (platform.isGround()) {
                Gdx.app.debug("SpriteRenderer", "Пропускаем рендеринг земли (ground platform)");
                return;
            }
            platform.render(batch);
            return;
        }
        // Специальная обработка для игрока
        else if (entity instanceof Player) {
            width = 64;
            height = 64;
        }
        // Специальная обработка для монет
        else if (entity instanceof Item && ((Item) entity).getItemType().equals(Item.ITEM_COIN)) {
            width = 32;
            height = 32;
        }

        batch.draw(entity.getTexture(), x, y, width, height);
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