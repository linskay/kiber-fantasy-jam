package com.cyberkingdom.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.Player;

public class InputHandler {
    private final Player player;
    private final float speed = 200f;
    private boolean jumpPressed = false;

    public InputHandler(Player player) {
        this.player = player;
    }

    public void update(float deltaTime) {
        handleMovement();
        handleJump();
    }

    private void handleMovement() {
        float moveX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveX -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveX += speed;

        player.getVelocity().x = moveX;
    }

    private void handleJump() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !jumpPressed) {
            if (player.isOnGround()) {
                player.getVelocity().y = player.getJumpVelocity();
                player.setJumping(true);
                jumpPressed = true;
            }
        } else if (!Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            jumpPressed = false;
        }
    }
}