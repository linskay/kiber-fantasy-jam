package com.cyberkingdom.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.Player;

public class InputHandler {
    private Player player;
    private float speed = 200f;

    public InputHandler(Player player) {
        this.player = player;
    }

    public void update(Vector2 velocity) {
        if (player == null) return;

        velocity.set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) velocity.y -= speed; // Вверх
        if (Gdx.input.isKeyPressed(Input.Keys.S)) velocity.y += speed; // Вниз
        if (Gdx.input.isKeyPressed(Input.Keys.A)) velocity.x -= speed; // Влево
        if (Gdx.input.isKeyPressed(Input.Keys.D)) velocity.x += speed; // Вправо
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !player.isJumping()) {
            velocity.y -= player.getJumpVelocity();
            player.setJumping(true);
        }

        System.out.println("Ввод обработан: velocity (" + velocity.x + ", " + velocity.y + ")");
    }
}