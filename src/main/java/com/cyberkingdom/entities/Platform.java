package com.cyberkingdom.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.cyberkingdom.physics.CollisionComponent;
import com.badlogic.gdx.Gdx;

public class Platform extends GameEntity {
    private Rectangle rectangle;
    private CollisionComponent collision;
    private static final float DEFAULT_WIDTH = 64f; // Размер текстуры платформы
    private static final float DEFAULT_HEIGHT = 32f;
    private boolean isGround = false;

    public Platform(Rectangle rectangle, Texture texture) {
        super("Platform", null); // Платформы не используют SpriteManager
        this.texture = texture;
        this.rectangle = rectangle;
        this.collision = new CollisionComponent(rectangle.width, rectangle.height);
        this.position.set(rectangle.x, rectangle.y);
        collision.update(position);
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public float getX() {
        return rectangle.x;
    }

    public float getY() {
        return rectangle.y;
    }

    public float getWidth() {
        return rectangle.width;
    }

    public float getHeight() {
        return rectangle.height;
    }

    public void render(SpriteBatch batch) {
        if (texture != null) {
            float textureWidth = texture.getWidth();
            float textureHeight = texture.getHeight();
            
            // Вычисляем, сколько раз нужно повторить текстуру
            int repeatCount = (int) Math.ceil(rectangle.width / textureWidth);
            
            for (int i = 0; i < repeatCount; i++) {
                float x = rectangle.x + (i * textureWidth);
                float currentWidth = Math.min(textureWidth, rectangle.x + rectangle.width - x);
                
                // Рисуем текстуру с правильным масштабированием
                batch.draw(
                    texture,
                    x,
                    rectangle.y,
                    currentWidth,
                    rectangle.height,
                    0, 0,
                    (int)(currentWidth),
                    (int)textureHeight,
                    false, false
                );
            }
        } else {
            Gdx.app.error("Platform", "Texture is null for platform at: " + rectangle);
        }
    }

    public static Platform createDefaultPlatform(float x, float y, Texture texture) {
        Rectangle rect = new Rectangle(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        return new Platform(rect, texture);
    }

    @Override
    public void update(float deltaTime) {
        if (collision != null) {
            collision.update(position);
        }
    }

    public void setGround(boolean isGround) { this.isGround = isGround; }
    public boolean isGround() { return isGround; }
}