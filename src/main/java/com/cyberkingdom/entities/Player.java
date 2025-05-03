package com.cyberkingdom.entities;

import com.cyberkingdom.items.Inventory;
import com.cyberkingdom.physics.CollisionComponent;

//public class Player extends GameEntity {
//    private float health = 100f;
//    private float maxHealth = 100f;
//    private Inventory inventory;
//    private boolean isJumping = false;
//    private float jumpVelocity = 300f;
//
//    public Player(float x, float y) {
//        super("Player");
//        position.set(x, y);
//        this.inventory = new Inventory();
//    }
//
//    public float getHealth() { return health; }
//    public float getMaxHealth() { return maxHealth; }
//    public void setHealth(float health) { this.health = Math.max(0, Math.min(maxHealth, health)); }
//    public void heal(float amount) { setHealth(health + amount); }
//    public Inventory getInventory() { return inventory; }
//    public boolean isJumping() { return isJumping; }
//    public void setJumping(boolean jumping) { this.isJumping = jumping; }
//    public float getJumpVelocity() { return jumpVelocity; }
//}

public class Player extends GameEntity {
    private CollisionComponent collision;
    private float health;
    private float maxHealth;
    private Inventory inventory;
    private boolean isJumping;
    private float jumpVelocity;

    public Player(float x, float y) {
        super("Player");
        this.position.set(x, y);
        this.collision = new CollisionComponent(32, 48); // размер коллизии игрока
        this.maxHealth = 100f;
        this.health = maxHealth;
        this.inventory = new Inventory();
        this.isJumping = false;
        this.jumpVelocity = 300f;
    }

    public void update(float deltaTime) {
        collision.update(position);
        // Здесь можно добавить логику движения, прыжков и т.д.
    }

    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setHealth(float health) {
        this.health = Math.max(0, Math.min(maxHealth, health));
    }

    public void heal(float amount) {
        setHealth(this.health + amount);
    }

    public void takeDamage(float damage) {
        setHealth(this.health - damage);
        if (health == 0) {
            onDeath();
        }
    }

    private void onDeath() {
        System.out.println("Игрок погиб!");
        setActive(false);
        // Добавьте логику при смерти игрока (рестарт, меню и т.п.)
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean jumping) {
        this.isJumping = jumping;
    }

    public float getJumpVelocity() {
        return jumpVelocity;
    }

    public void setJumpVelocity(float jumpVelocity) {
        this.jumpVelocity = jumpVelocity;
    }
}
