package com.cyberkingdom.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cyberkingdom.entities.Player;

public class HealthBar {
    public void render(Player player, ShapeRenderer shapeRenderer) {
        float healthPercent = player.getHealth() / player.getMaxHealth();

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(10, 70, 200, 20);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(10, 70, 200 * healthPercent, 20);
    }
}