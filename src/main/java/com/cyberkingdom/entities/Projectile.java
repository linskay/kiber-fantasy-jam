package com.cyberkingdom.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.rendering.SpriteManager;

public class Projectile extends GameEntity {
    private Vector2 position;
    private Vector2 velocity;
    private float damage;
    private CollisionComponent collision;
    private TextureRegion texture;

    public Projectile(float x, float y, float vx, float vy, float damage, SpriteManager spriteManager) {
        super("Projectile", spriteManager);
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(vx, vy);
        this.damage = damage;
        this.collision = new CollisionComponent(16, 16);
        this.collision.update(position);

        // Получаем текстуру снаряда
        TextureRegion[] frames = spriteManager.getFrames("WITCH_VPN"); // Возможно, нужна отдельная текстура для снарядов Дединсайда
        if (frames != null && frames.length > 0) {
            this.texture = frames[0];
        }
    }

    public void update(float deltaTime) {
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        collision.update(position);
    }

    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x - 8, position.y - 8, 16, 16);
        }
    }

    public Rectangle getCollisionBounds() {
        return collision.getBounds();
    }

    public float getDamage() {
        return damage;
    }

    public boolean isOutOfBounds() {
        return position.x < 0 || position.x > Gdx.graphics.getWidth() ||
               position.y < 0 || position.y > Gdx.graphics.getHeight();
    }

    public void dispose() {
        collision = null;
        texture = null;
    }
} 