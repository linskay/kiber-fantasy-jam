package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;
import com.cyberkingdom.items.Inventory;
import com.cyberkingdom.physics.CollisionComponent;

public class Player extends GameEntity implements Collidable {
    private CollisionComponent collision;
    private Inventory inventory;
    private boolean isJumping;
    private boolean onGround;
    private float jumpVelocity = 800f;
    private float moveSpeed = 200f;
    private float health = 100f;
    private float maxHealth = 100f;
    private int coins = 0;
    private float gravity = -1200f;

    public Player(float x, float y) {
        super("Player");
        this.position.set(x, y);
        this.collision = new CollisionComponent(32, 48);
        this.inventory = new Inventory();
        collision.update(position);
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    @Override
    public Rectangle getCollisionBounds() {
        return collision.getBounds();
    }

    public float getMaxHealth() { return maxHealth; }
    public Inventory getInventory() { return inventory; }
    public boolean isJumping() { return isJumping; }
    public void setJumping(boolean jumping) { isJumping = jumping; }
    public boolean isOnGround() { return onGround; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }
    public float getJumpVelocity() { return jumpVelocity; }
    public float getMoveSpeed() { return moveSpeed; }
    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = Math.max(0, Math.min(maxHealth, health)); }
    public void takeDamage(float damage) { setHealth(health - damage); }
    public int getCoins() { return coins; }
    public void addCoin() { coins++; }

    public void update(float deltaTime) {
        collision.update(position);
    }

    public void move(float deltaX, float deltaY) {
        position.add(deltaX, deltaY);
        collision.update(position);
    }
}