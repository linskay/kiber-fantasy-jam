package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.items.Inventory;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.screens.GameScreen;
import com.badlogic.gdx.Gdx;

public class Player extends GameEntity implements Collidable {
    private CollisionComponent collision;
    private Inventory inventory;
    private boolean isJumping;
    private boolean onGround;
    private int jumpsLeft = 2; // Максимальное количество прыжков
    private float jumpVelocity = 400f;
    private float moveSpeed = 200f;
    private float health = 100f;
    private float maxHealth = 100f;
    private int coins = 0;
    private float gravity = -500f;
    private GameScreen gameScreen;

    public Player(Vector2 position, GameScreen gameScreen) {
        super("Player");
        this.position = position;
        this.velocity = new Vector2();
        this.gameScreen = gameScreen;
        this.collision = new CollisionComponent(64, 64);
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
    public void setOnGround(boolean onGround) { 
        this.onGround = onGround;
        if (onGround) {
            jumpsLeft = 2; // Восстанавливаем прыжки при приземлении
        }
    }
    public boolean canJump() { return jumpsLeft > 0; }
    public void useJump() { jumpsLeft--; }
    public int getJumpsLeft() { return jumpsLeft; }
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

    public void collectCoin() {
        coins++;
        if (gameScreen != null) {
            gameScreen.updateCoinCount(coins);
        }
        Gdx.app.debug("Player", "Collected coin, total: " + coins);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (inventory != null) {
            inventory.dispose();
            inventory = null;
        }
        if (collision != null) {
            collision = null;
        }
        gameScreen = null;
    }
}