package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.rendering.SpriteManager;

public class Enemy extends GameEntity implements Collidable {
    public enum EnemyType {
        TROLL_BOT,
        VIRUS,
        MALWARE
    }

    private EnemyType type;
    private float health;
    private float maxHealth;
    private float damage;
    private float speed;
    private CollisionComponent collision;

    public Enemy(EnemyType type, float x, float y, SpriteManager spriteManager) {
        super(type.name(), spriteManager);
        this.type = type;
        this.position = new Vector2(x, y);
        this.health = 100;
        this.maxHealth = 100;
        this.damage = 10;
        this.speed = 100;
        this.collision = new CollisionComponent(32, 32);
        this.collision.update(position);
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    @Override
    public Rectangle getCollisionBounds() {
        return collision.getBounds();
    }

    @Override
    public AnimationComponent getAnimation() {
        return super.getAnimation();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    public EnemyType getEnemyType() {
        return type;
    }

    public void update(float deltaTime) {
        collision.update(position);
        // Базовая логика движения врагов
    }
}