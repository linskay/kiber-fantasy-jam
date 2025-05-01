package com.cyberkingdom.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cyberkingdom.entities.Player;

public class HealthBar {
    private ShapeRenderer shapeRenderer;
    private float width = 200;
    private float height = 20;

    public HealthBar() {
        shapeRenderer = new ShapeRenderer();
    }

    public void render(SpriteBatch batch, Player player) {
        // Временная реализация - позже можно заменить на спрайты
        batch.end();

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Фон полосы
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(20, 20, width, height);

        // Полоса здоровья
        float healthPercentage = player.getHealth() / (float)player.getMaxHealth();
        shapeRenderer.setColor(healthPercentage > 0.6f ? Color.GREEN :
                healthPercentage > 0.3f ? Color.YELLOW : Color.RED);
        shapeRenderer.rect(20, 20, width * healthPercentage, height);

        shapeRenderer.end();
        batch.begin();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}