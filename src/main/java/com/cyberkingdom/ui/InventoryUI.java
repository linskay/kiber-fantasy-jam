package com.cyberkingdom.ui;

import com.badlogic.gdx.Gdx;
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
        System.out.println("Рендеринг InventoryUI");
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(10, 10, 200, 50); // Уменьшенный размер рамки
        shapeRenderer.end();
        batch.begin();
        System.out.println("InventoryUI отрендерен");
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}