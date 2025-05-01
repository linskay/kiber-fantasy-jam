package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.rendering.AnimationComponent;
import com.cyberkingdom.physics.CollisionComponent;

public class GameEntity {
    private String id;
    private Vector2 position;
    private Vector2 velocity;
    private EntityType type;
    private AnimationComponent animation;
    private CollisionComponent collision;
    private boolean active;

    public GameEntity(String id, EntityType type) {
        this.id = id;
        this.type = type;
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.active = true;
        this.animation = new AnimationComponent();
        this.collision = new CollisionComponent(32, 32); // Размер по умолчанию
    }

    public void update(float deltaTime) {
        // Обновление позиции на основе velocity
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;

        // Обновление компонентов
        animation.update(deltaTime);
        collision.update(position);
    }

    public void destroy() {
        this.active = false;
    }

    // Getters и Setters
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public EntityType getType() { return type; }
    public boolean isActive() { return active; }
    public AnimationComponent getAnimation() { return animation; }
    public CollisionComponent getCollision() { return collision; }

    public enum EntityType {
        PLAYER,
        ENEMY,
        BOSS,
        NPC,
        ITEM,
        PROJECTILE,
        ENVIRONMENT
    }
}