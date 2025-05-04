package com.cyberkingdom.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.cyberkingdom.entities.Player;

public class InputHandler {
    private final Player player;
    private final float speed = 200f;
    private boolean jumpPressed = false;
    private boolean inventoryPressed = false;
    private boolean inventoryVisible = false;

    public InputHandler(Player player) {
        this.player = player;
    }

    public void update(float deltaTime) {
        handleMovement();
        handleJump();
        handleInventory();
    }

    private void handleJump() {
        if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                (Gdx.input.isKeyPressed(Input.Keys.W)) && !jumpPressed)) {

            if (player.isOnGround()) {
                player.getVelocity().y = player.getJumpVelocity();
                player.setJumping(true);
                jumpPressed = true;
            }
        } else if (!Gdx.input.isKeyPressed(Input.Keys.SPACE) &&
                !Gdx.input.isKeyPressed(Input.Keys.W)) {
            jumpPressed = false;
        }
    }

    private void handleMovement() {
        float moveX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveX -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveX += speed;
        player.getVelocity().x = moveX;
    }

    private void handleInventory() {
        if (Gdx.input.isKeyPressed(Input.Keys.E) && !inventoryPressed) {
            inventoryVisible = !inventoryVisible;
            inventoryPressed = true;
        } else if (!Gdx.input.isKeyPressed(Input.Keys.E)) {
            inventoryPressed = false;
        }
    }

    public boolean isInventoryVisible() {
        return inventoryVisible;
    }
}