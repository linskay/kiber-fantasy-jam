package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.items.Inventory;

public class Player extends GameEntity {
    private float health = 100f;
    private float maxHealth = 100f;
    private Inventory inventory;
    private boolean isJumping = false;
    private float jumpVelocity = 300f;

    public Player(float x, float y) {
        super("Player");
        position.set(x, y);
        this.inventory = new Inventory();
    }

    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public void setHealth(float health) { this.health = Math.max(0, Math.min(maxHealth, health)); }
    public void heal(float amount) { setHealth(health + amount); }
    public Inventory getInventory() { return inventory; }
    public boolean isJumping() { return isJumping; }
    public void setJumping(boolean jumping) { this.isJumping = jumping; }
    public float getJumpVelocity() { return jumpVelocity; }
}