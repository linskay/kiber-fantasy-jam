package com.cyberkingdom.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cyberkingdom.entities.Player;

public class HealthBar {
    private ShapeRenderer shapeRenderer;

    public HealthBar() {
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(Player player, SpriteBatch batch) {
        float healthPercent = player.getHealth() / player.getMaxHealth();

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Фон
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(10, 70, 200, 20);

        // Полоска здоровья
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(10, 70, 200 * healthPercent, 20);

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}