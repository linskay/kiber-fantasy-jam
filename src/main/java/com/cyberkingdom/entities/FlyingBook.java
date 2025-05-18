package com.cyberkingdom.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.CollisionComponent;
import com.badlogic.gdx.graphics.Texture;

public class FlyingBook extends GameEntity implements Collidable {
    private static final float FALL_SPEED = 400f; // Скорость падения книги
    private Vector2 velocity;
    private CollisionComponent collision;
    private TextureRegion texture;
    private Player target; // Цель (игрок)
    private boolean isCollected = false; // Флаг сбора

    public FlyingBook(Vector2 position, Texture texture) {
        super("FlyingBook", null);
        this.position = position;
        this.velocity = new Vector2(0, -FALL_SPEED); // Устанавливаем начальную скорость падения
        this.collision = new CollisionComponent(32, 32); // Размер коллизии книги
        this.collision.update(position);

        // Используем переданную текстуру
        if (texture != null) {
            this.texture = new TextureRegion(texture);
            Gdx.app.log("FlyingBook", "Texture loaded successfully");
        } else {
            Gdx.app.error("FlyingBook", "Texture is null in constructor!");
        }
        Gdx.app.log("FlyingBook", "Initialized at position: " + position.x + ", " + position.y);
    }

    public void setTarget(Player target) {
        this.target = target;
        Gdx.app.log("FlyingBook", "Target set to player at position: " + (target != null ? target.getPosition().x + ", " + target.getPosition().y : "null"));
    }

    @Override
    public void update(float deltaTime) {
        if (isCollected || target == null || collision == null) {
            return;
        }

        // Книга падает вниз
        position.y += velocity.y * deltaTime;
        collision.update(position);

        Gdx.app.log("FlyingBook", "Updating at position: " + position.x + ", " + position.y);

        // Проверяем столкновение с игроком
        if (target != null && collision.collidesWith(target.getCollisionComponent())) {
            Gdx.app.log("FlyingBook", "Collision with player detected");
            isCollected = true;
            setActive(false);
            // Не обнуляем collision здесь, так как это может вызвать проблемы
            // Вместо этого просто помечаем объект как неактивный
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null && isActive()) {
            batch.draw(texture, position.x, position.y, 32, 32);
        }
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    @Override
    public Rectangle getCollisionBounds() {
        return collision.getBounds();
    }

    public boolean isCollected() {
        return isCollected;
    }

    @Override
    public void dispose() {
        super.dispose();
        // Не обнуляем collision здесь, так как это может вызвать проблемы
        // Вместо этого просто помечаем объект как неактивный
        setActive(false);
        texture = null;
    }
} 