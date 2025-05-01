package com.cyberkingdom.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.entities.Enemy;
import com.cyberkingdom.entities.Boss;
import com.cyberkingdom.items.Item;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends com.badlogic.gdx.ScreenAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private List<Enemy> enemies;
    private List<Boss> bosses;
    private List<Item> items;
    private List<Item> inventory;
    private Texture background;

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        background = new Texture("badlogic.jpg");

        // Initialize player with default texture
        player = new Player(new Vector2(100, 100));

        // Initialize enemies with default textures
        enemies = new ArrayList<>();
        enemies.add(new Enemy(new Vector2(300, 100), "TROLL_BOT"));
        enemies.add(new Enemy(new Vector2(400, 100), "VIRUS_FLYING"));

        // Initialize bosses with default textures
        bosses = new ArrayList<>();
        bosses.add(new Boss(new Vector2(600, 100), "TROLL_BOT"));
        bosses.add(new Boss(new Vector2(1000, 100), "DEAD_INSIDE.DLL"));

        // Initialize items with default textures
        items = new ArrayList<>();
        items.add(new Item(new Vector2(200, 100), "USB_SCATERT"));
        items.add(new Item(new Vector2(250, 100), "CRYPTO_SHOVEL"));
        items.add(new Item(new Vector2(300, 100), "RTX_4090"));

        inventory = new ArrayList<>();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, 1280, 720);

        // Render player
        player.render(batch);
        player.update(delta);

        // Render enemies
        for (Enemy enemy : enemies) {
            enemy.update(delta, player);
            enemy.render(batch);
        }

        // Render bosses
        for (Boss boss : bosses) {
            boss.update(delta, player);
            boss.render(batch);
        }

        // Render items
        for (Item item : items) {
            item.render(batch);
            if (player.collidesWith(item)) {
                inventory.add(item);
                items.remove(item);
                break;
            }
        }

        batch.end();

        // Handle input
        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.moveLeft();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.moveRight();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            player.useItem(inventory);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        player.dispose();
        for (Enemy enemy : enemies) enemy.dispose();
        for (Boss boss : bosses) boss.dispose();
        for (Item item : items) item.dispose();
    }
}