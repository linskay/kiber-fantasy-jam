package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.rendering.SpriteManager;

public class Enemy extends GameEntity implements Collidable {
    protected static final float DEFAULT_HEALTH = 100f;
    protected static final float DEFAULT_DAMAGE = 10f;
    protected static final float DEFAULT_SPEED = 100f;
    protected static final float DEFAULT_COLLISION_SIZE = 32f;

    public enum EnemyType {
        TROLL_BOT,
        VIRUS,
        MALWARE
    }

    protected EnemyType type;
    protected float health;
    protected float maxHealth;
    protected float damage;
    protected float speed;
    protected CollisionComponent collision;

    public Enemy(EnemyType type, float x, float y, SpriteManager spriteManager) {
        super(type.name(), spriteManager);
        initializeEnemy(type, x, y);
    }

    protected void initializeEnemy(EnemyType type, float x, float y) {
        this.type = type;
        this.position = new Vector2(x, y);
        this.health = DEFAULT_HEALTH;
        this.maxHealth = DEFAULT_HEALTH;
        this.damage = DEFAULT_DAMAGE;
        this.speed = DEFAULT_SPEED;
        this.collision = new CollisionComponent(DEFAULT_COLLISION_SIZE, DEFAULT_COLLISION_SIZE);
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

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        collision.update(position);
        updateMovement(deltaTime);
    }

    protected void updateMovement(float deltaTime) {
    }

    public void takeDamage(float damage) {
        health = Math.max(0, health - damage);
        if (health <= 0) {
            setActive(false);
        }
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getDamage() {
        return damage;
    }

    public float getSpeed() {
        return speed;
    }
}