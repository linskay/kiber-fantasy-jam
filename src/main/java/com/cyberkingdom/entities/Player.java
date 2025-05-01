package com.cyberkingdom.entities;

import com.cyberkingdom.items.Inventory;

public class Player extends GameEntity {
    private Inventory inventory;
    private float speed;
    private boolean isGrounded;

    public Player() {
        super("player", EntityType.PLAYER);
        inventory = new Inventory(10);
        speed = 200f;
        isGrounded = false;
    }

    public void move(float xAxis) {
        getVelocity().x = xAxis * speed;
    }

    public void jump() {
        if (isGrounded) {
            getVelocity().y = 400f;
            isGrounded = false;
        }
    }

    public void useItem(int slot) {
        inventory.useItem(slot, this);
    }

    // Getters
    public Inventory getInventory() { return inventory; }
    public boolean isGrounded() { return isGrounded; }
    public void setGrounded(boolean grounded) { isGrounded = grounded; }

    //toDo
    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
    }

    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }
}