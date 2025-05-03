package com.cyberkingdom.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberkingdom.gameengine.GameEngine;

public class MainMenuScreen {
    private GameEngine game;
    private SpriteBatch batch;
    private BitmapFont font;

    public MainMenuScreen(GameEngine game) {
        this.game = game;
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    public void show() {
        // Инициализация меню
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "404: Сказка for</> взрослых", 300, 400);
        font.draw(batch, "1. Играть", 300, 350);
        font.draw(batch, "2. Ачивки? Ачивки!", 300, 300);
        font.draw(batch, "3. То, что обычно не читают", 300, 250);
        font.draw(batch, "4. Для вас старались", 300, 200);
        font.draw(batch, "5. Пойду я...", 300, 150);
        batch.end();

        handleInput();
    }

    private void handleInput() {
//        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
//            game.startGame();
//        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
//            Gdx.app.exit();
//        }
    } // toDo поженить с игрой

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}