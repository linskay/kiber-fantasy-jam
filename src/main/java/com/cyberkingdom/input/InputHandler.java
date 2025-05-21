package com.cyberkingdom.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.cyberkingdom.entities.Player;

public class InputHandler implements InputProcessor {
    private Player player;
    private boolean jumpPressed = false;
    private boolean inventoryPressed = false;
    private boolean inventoryVisible = false;

    public InputHandler(Player player) {
        this.player = player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void update(float deltaTime) {
        handleMovement();
        handleJump();
        handleInventory();
    }

    public void handleJump() {
        if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.W)) && !jumpPressed) {
            if (player.canJump()) {
                player.jump();
                Gdx.app.log("InputHandler", "Jump initiated via player.jump(), jumps left: " + player.getJumpsLeft());
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            inventoryVisible = !inventoryVisible;
            Gdx.app.log("InputHandler", "Inventory visibility changed via handleInventory to: " + inventoryVisible);
        }
    }

    public boolean isInventoryVisible() {
        return inventoryVisible;
    }

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log("InputHandler", "keyDown received: " + keycode);
        if (keycode == Input.Keys.E) {
            Gdx.app.log("InputHandler", "'E' key pressed");
            inventoryVisible = !inventoryVisible;
            Gdx.app.log("InputHandler", "Inventory visibility changed to: " + inventoryVisible);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void dispose() {
        // Не делаем ничего, так как player является final
    }
}