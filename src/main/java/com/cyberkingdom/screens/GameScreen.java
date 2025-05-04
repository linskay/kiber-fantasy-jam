package com.cyberkingdom.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.rendering.SpriteRenderer;
import com.cyberkingdom.ui.UIManager;
import com.cyberkingdom.world.LevelLoader;


public class GameScreen implements com.badlogic.gdx.Screen {
    private static final float MIN_WORLD_Y = 150f;
    private EntitySystem entitySystem;
    private PhysicsSystem physicsSystem;
    private LevelLoader levelLoader;
    private SpriteRenderer spriteRenderer;
    private UIManager uiManager;
    private OrthographicCamera camera;
    private Player player;
    private BossFightLogic bossFightLogic;
    private Texture background;

    public GameScreen(EntitySystem entitySystem, PhysicsSystem physicsSystem, LevelLoader levelLoader,
                      SpriteRenderer spriteRenderer, UIManager uiManager, BossFightLogic bossFightLogic) {
        this.entitySystem = entitySystem;
        this.physicsSystem = physicsSystem;
        this.levelLoader = levelLoader;
        this.spriteRenderer = spriteRenderer;
        this.uiManager = uiManager;
        this.bossFightLogic = bossFightLogic;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        findPlayer();

        if (player != null) {
            camera.position.set(
                    player.getPosition().x,
                    Math.max(player.getPosition().y, MIN_WORLD_Y),
                    0
            );
        } else {
            camera.position.set(100, MIN_WORLD_Y, 0);
        }
        camera.update();

        try {
            background = new Texture(Gdx.files.internal("assets/ui/background.png"));
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Failed to load background", e);
        }
    }

    private void findPlayer() {
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Player) {
                this.player = (Player) entity;
                break;
            }
        }
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (player != null) {
            camera.position.set(
                    player.getPosition().x,
                    Math.max(player.getPosition().y, MIN_WORLD_Y),
                    0
            );
        }
        camera.update();

        SpriteBatch gameBatch = spriteRenderer.getBatch();
        gameBatch.setProjectionMatrix(camera.combined);
        gameBatch.begin();

        if (background != null) {
            gameBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        for (Platform platform : levelLoader.getPlatforms()) {
            if (platform.getTexture() != null) {
                Rectangle rect = platform.getRectangle();
                gameBatch.draw(platform.getTexture(), rect.x, rect.y, rect.width, rect.height);
            }
        }

        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity.isActive()) {
                if (entity instanceof Item) {
                    ((Item) entity).render(gameBatch);
                } else if (!(entity instanceof Platform)) {
                    spriteRenderer.render(entity);
                }
            }
        }

        gameBatch.end();

        if (player != null) {
            uiManager.render(player);
        }

        physicsSystem.update(deltaTime);
        if (bossFightLogic != null) {
            bossFightLogic.update(deltaTime);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void dispose() {
        if (background != null) {
            background.dispose();
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}