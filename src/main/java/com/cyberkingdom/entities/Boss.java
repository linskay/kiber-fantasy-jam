package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Boss extends GameEntity implements Collidable {
    private String type;
    private float health;
    private float maxHealth;
    private float damage;
    private float speed;
    protected CollisionComponent collision;
    private float attackRange = 200f;
    private float attackCooldown = 2f;
    private float timeSinceLastAttack = 0f;
    private int hitCount = 0;
    private int maxHitsToDefeat = 3;
    private float hitCooldown = 1.0f;
    private float timeSinceLastHit = 0f;
    private Player target;
    protected float moveSpeed = 150f;

    public Boss(String name, float x, float y, SpriteManager spriteManager) {
        super(name, spriteManager);
        this.position = new Vector2(x, y);
        this.health = 500;
        this.maxHealth = 500;
        this.damage = 30;
        this.speed = 150;
        this.collision = new CollisionComponent(64, 64);
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
        System.out.println(getType() + " побежден!");
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
            // Движение к цели
            Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
            this.position.add(direction.scl(moveSpeed * deltaTime));

            // Проверка атаки
            if (collision.collidesWith(target.getCollisionComponent()) &&
                    timeSinceLastAttack >= attackCooldown) {

                target.takeDamage(15f);
                timeSinceLastAttack = 0f;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isActive()) {
            super.render(batch);
        }
    }
}