package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;

public class Boss extends GameEntity implements Collidable {
    protected static final float DEFAULT_HEALTH = 500f;
    protected static final float DEFAULT_DAMAGE = 30f;
    protected static final float DEFAULT_SPEED = 150f;
    protected static final float DEFAULT_ATTACK_RANGE = 200f;
    protected static final float DEFAULT_ATTACK_COOLDOWN = 2f;
    protected static final int DEFAULT_HITS_TO_DEFEAT = 3;
    protected static final float DEFAULT_HIT_COOLDOWN = 1.0f;
    protected static final float DEFAULT_COLLISION_SIZE = 64f;

    protected String type;
    protected float health;
    protected float maxHealth;
    protected float damage;
    protected float speed;
    protected float attackRange;
    protected float attackCooldown;
    protected float timeSinceLastAttack;
    protected int hitCount;
    protected int maxHitsToDefeat;
    protected float hitCooldown;
    protected float timeSinceLastHit;
    protected Player target;
    protected float moveSpeed;
    protected CollisionComponent collision;

    public Boss(String name, float x, float y, SpriteManager spriteManager) {
        super(name, spriteManager);
        initializeBoss(x, y);
    }

    protected void initializeBoss(float x, float y) {
        this.position = new Vector2(x, y);
        this.health = DEFAULT_HEALTH;
        this.maxHealth = DEFAULT_HEALTH;
        this.damage = DEFAULT_DAMAGE;
        this.speed = DEFAULT_SPEED;
        this.attackRange = DEFAULT_ATTACK_RANGE;
        this.attackCooldown = DEFAULT_ATTACK_COOLDOWN;
        this.maxHitsToDefeat = DEFAULT_HITS_TO_DEFEAT;
        this.hitCooldown = DEFAULT_HIT_COOLDOWN;
        this.moveSpeed = DEFAULT_SPEED;
        this.collision = new CollisionComponent(DEFAULT_COLLISION_SIZE, DEFAULT_COLLISION_SIZE);
        this.collision.update(position);
        this.type = name;
    }

    public void setTarget(Player player) {
        this.target = player;
    }

    @Override
    public Rectangle getCollisionBounds() {
        return collision.getBounds();
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    public void setMaxHitsToDefeat(int hits) {
        this.maxHitsToDefeat = hits;
    }

    protected void die() {
        setActive(false);
    }

    public boolean tryRegisterHit() {
        if (!isActive()) return false;
        if (timeSinceLastHit >= hitCooldown) {
            hitCount++;
            timeSinceLastHit = 0f;
            if (hitCount >= maxHitsToDefeat) {
                die();
            }
            return true;
        }
        return false;
    }

    public String getType() {
        return type;
    }

    public void setMoveSpeed(float speed) {
        this.moveSpeed = speed;
    }

    public void setAttackCooldown(float cooldown) {
        this.attackCooldown = cooldown;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        collision.update(position);

        timeSinceLastHit += deltaTime;
        timeSinceLastAttack += deltaTime;

        if (target != null && isActive()) {
            updateMovement(deltaTime);
            updateAttack();
        }
    }

    protected void updateMovement(float deltaTime) {
        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        this.position.add(direction.scl(moveSpeed * deltaTime));
    }

    protected void updateAttack() {
        if (collision.collidesWith(target.getCollisionComponent()) &&
                timeSinceLastAttack >= attackCooldown) {
            target.takeDamage(damage);
            timeSinceLastAttack = 0f;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isActive()) {
            super.render(batch);
        }
    }
}