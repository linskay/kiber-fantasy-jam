package com.cyberkingdom.ui;

import com.badlogic.gdx.Gdx;
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
        System.out.println("Рендеринг HealthBar");
        float healthPercent = player.getHealth() / player.getMaxHealth();
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(10, 70, 200 * healthPercent, 20);
        shapeRenderer.end();
        batch.begin();
        System.out.println("HealthBar отрендерен, здоровье: " + healthPercent);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}