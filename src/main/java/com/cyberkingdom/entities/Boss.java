package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.CollisionComponent;

public class Boss extends GameEntity implements Collidable {
    private CollisionComponent collision;
    private String type;
    private int hitCount = 0;
    private int maxHitsToDefeat = 3;
    private float hitCooldown = 1.0f;
    private float timeSinceLastHit = 0f;
    private Player target;
    private float moveSpeed = 150f;
    private float attackCooldown = 2f;
    private float timeSinceLastAttack = 0f;

    public Boss(String name, float x, float y) {
        super(name);
        this.type = name;
        this.position.set(x, y);
        this.collision = new CollisionComponent(64, 64);
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

//    public void update(float deltaTime) {
//        collision.update(position);
//        if (timeSinceLastHit < hitCooldown) {
//            timeSinceLastHit += deltaTime;
//        }
//        if (timeSinceLastAttack < attackCooldown) {
//            timeSinceLastAttack += deltaTime;
//        }
//
//        if (target != null && isActive()) {
//            Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
//            velocity.set(direction.scl(moveSpeed));
//
//            if (collision.collidesWith(target.getCollisionComponent()) && timeSinceLastAttack >= attackCooldown) {
//                target.takeDamage(10f);
//                System.out.println("Ведьма VPN атакует игрока! Здоровье игрока: " + target.getHealth());
//                timeSinceLastAttack = 0f;
//            }
//        }
//    }

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
}}