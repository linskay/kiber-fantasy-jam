package com.cyberkingdom.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cyberkingdom.entities.Player;

public class InventoryUI {
    private ShapeRenderer shapeRenderer;

    public InventoryUI() {
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(Player player, SpriteBatch batch) {
        // Рисуем инвентарь
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(10, 10, 200, 50);
        shapeRenderer.end();

        // Здесь можно добавить отрисовку предметов инвентаря
        // используя batch
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}