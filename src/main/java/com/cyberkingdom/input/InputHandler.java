package com.cyberkingdom.input;

import com.badlogic.gdx.InputProcessor;
import com.cyberkingdom.entities.Player;

public abstract class InputHandler implements InputProcessor {
    private Player player;

    public InputHandler(Player player) {
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case com.badlogic.gdx.Input.Keys.A:
            case com.badlogic.gdx.Input.Keys.LEFT:
                player.move(-1);
                break;
            case com.badlogic.gdx.Input.Keys.D:
            case com.badlogic.gdx.Input.Keys.RIGHT:
                player.move(1);
                break;
            case com.badlogic.gdx.Input.Keys.SPACE:
                player.jump();
                break;
            case com.badlogic.gdx.Input.Keys.E:
                player.useItem(0); // Использовать первый предмет
                break;
        }
        return true;
    }

    // ... другие методы интерфейса
}