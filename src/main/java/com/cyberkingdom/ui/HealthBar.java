package com.cyberkingdom.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.Gdx;

public class HealthBar {
    private SpriteManager spriteManager;
    public static final float HEART_SIZE = 24f;
    public static final float BAR_WIDTH = 100f;
    public static final float BAR_HEIGHT = 24f;
    public static final float START_X = 30f;
    public static final float BAR_Y = 100f;
    private static final float BAR_X = START_X + HEART_SIZE + 3f;

    public HealthBar(SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
        // Проверяем загрузку текстуры сердечка при создании
        Texture heartTexture = spriteManager.getTexture("HEART");
        if (heartTexture != null) {
            Gdx.app.log("HealthBar", "Heart texture loaded successfully");
        } else {
            Gdx.app.error("HealthBar", "Failed to load heart texture");
        }
    }

    public void render(Player player, ShapeRenderer shapeRenderer) {
        if (player == null || shapeRenderer == null) {
            Gdx.app.error("HealthBar", "Player or ShapeRenderer is null in render");
            return;
        }
        Gdx.app.log("HealthBar", "Rendering health bar...");

        // Удалена временная отладочная отрисовка квадрата
        // shapeRenderer.setColor(Color.MAGENTA); // Используем яркий цвет
        // shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 - 50, 100, 100); // Квадрат в центре экрана

        float healthPercent = player.getHealth() / player.getMaxHealth();

        // Рисуем сердечко (теперь это будет делаться в другом месте, использующем SpriteBatch)
        // Texture heartTexture = spriteManager.getTexture("HEART");
        // if (heartTexture != null) {
        // // batch.draw(heartTexture, START_X, BAR_Y - 2, HEART_SIZE, HEART_SIZE);
        // } else {
        // // Gdx.app.error("HealthBar", "Heart texture is null during render");
        // }

        // Рисуем белую обводку полоски здоровья
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(BAR_X - 1, BAR_Y - 1, BAR_WIDTH + 2, BAR_HEIGHT + 2);

        // Рисуем фон полоски здоровья
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT);

        // Рисуем заполнение полоски здоровья (розовый цвет)
        shapeRenderer.setColor(Color.PINK);
        shapeRenderer.rect(BAR_X, BAR_Y, BAR_WIDTH * healthPercent, BAR_HEIGHT);
    }
}