package com.cyberkingdom.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.cyberkingdom.entities.Player;

public class InputHandler {
    private final Player player;
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

    public void handleJump() {
        if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.W)) && !jumpPressed) {
            Gdx.app.log("InputHandler", "Jump key pressed, canJump: " + player.canJump());
            if (player.canJump()) {
                player.setVelocity(player.getVelocity().x, player.getJumpVelocity());
                player.setJumping(true);
                player.setOnGround(false);
                player.useJump();
                Gdx.app.log("InputHandler", "Jump initiated, jumps left: " + player.getJumpsLeft());
            }
            jumpPressed = true;
        } else if (!Gdx.input.isKeyPressed(Input.Keys.SPACE) && !Gdx.input.isKeyPressed(Input.Keys.W)) {
            jumpPressed = false;
        }
    }

    public void handleMovement() {
        float targetVelocity = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            targetVelocity = -player.getMoveSpeed();
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            targetVelocity = player.getMoveSpeed();
        }

        player.setVelocity(targetVelocity, player.getVelocity().y);
    }

    public void handleInventory() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && !inventoryPressed) {
            inventoryVisible = !inventoryVisible;
            inventoryPressed = true;
            Gdx.app.log("InputHandler", "Inventory visibility changed to: " + inventoryVisible);
        } else if (!Gdx.input.isKeyPressed(Input.Keys.E)) {
            inventoryPressed = false;
        }
    }

    public boolean isInventoryVisible() {
        return inventoryVisible;
    }

    public void dispose() {
        // Не делаем ничего, так как player является final
    }
}