package com.cyberkingdom.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.rendering.SpriteRenderer;
import com.cyberkingdom.ui.UIManager;
import com.cyberkingdom.world.LevelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.cyberkingdom.boss.BossFightLogic;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Music;
import com.cyberkingdom.audio.MusicManager;
import com.cyberkingdom.items.ItemPickupSystem;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.boss.BossSpawnManager;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.InputProcessor;
import com.cyberkingdom.input.InputHandler;
import com.badlogic.gdx.Input;
import com.cyberkingdom.gameengine.GameEngine;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputAdapter;

public class GameScreen implements Screen {
    private final GameEngine engine;
    private final EntitySystem entitySystem;
    private final PhysicsSystem physicsSystem;
    private final LevelLoader levelLoader;
    private final UIManager uiManager;
    private final SpriteRenderer spriteRenderer;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private InputHandler inputHandler;
    private final ExtendViewport viewport;
    private BitmapFont font;
    private static final float LEVEL_WIDTH = 1200f;
    private static final float LEVEL_HEIGHT = 800f;
    private Texture backgroundTexture;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch gameBatch;
    private Music levelMusic;
    private ItemPickupSystem itemPickupSystem;
    private BossSpawnManager bossSpawnManager;
    private SpriteManager spriteManager;
    private Player player;

    public GameScreen(GameEngine engine, EntitySystem entitySystem, PhysicsSystem physicsSystem, 
                     LevelLoader levelLoader, UIManager uiManager, SpriteRenderer spriteRenderer) {
        this.engine = engine;
        this.entitySystem = entitySystem;
        this.physicsSystem = physicsSystem;
        this.levelLoader = levelLoader;
        this.uiManager = uiManager;
        this.batch = new SpriteBatch();
        this.spriteRenderer = new SpriteRenderer(this.batch);
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 600);
        this.inputHandler = physicsSystem.getInputHandler();
        this.viewport = new ExtendViewport(LEVEL_WIDTH, LEVEL_HEIGHT, camera);
        this.viewport.apply();
        this.backgroundTexture = new Texture(Gdx.files.internal("assets/background_level1.png"));
        
        // Инициализация SpriteManager
        this.spriteManager = new SpriteManager();
        this.spriteManager.loadTextures();
        this.spriteManager.setupSpriteRegions();
        
        // Инициализация системы сбора предметов
        Player player = physicsSystem.getPlayer();
        if (player != null) {
            this.itemPickupSystem = new ItemPickupSystem(entitySystem, player, levelLoader);
            this.bossSpawnManager = new BossSpawnManager(entitySystem, levelLoader.getEntityFactory(), physicsSystem, player);
            this.player = player;
        }

        Gdx.app.log("GameScreen", "Initialized successfully");
    }

    @Override
    public void render(float delta) {
        // Обновляем камеру
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Очищаем экран
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Рисуем фон
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, LEVEL_WIDTH, LEVEL_HEIGHT);
        batch.end();

        // Обновляем физику и сущности
        physicsSystem.update(delta);
        entitySystem.update(delta);
        
        // Обновляем систему сбора предметов
        if (itemPickupSystem != null) {
            itemPickupSystem.update();
        }

        // Обновляем менеджер босса
        if (bossSpawnManager != null) {
            bossSpawnManager.update();
        }

        // Рисуем все сущности
        batch.begin();
        for (GameEntity entity : entitySystem.getEntities()) {
            entity.render(batch);
        }
        batch.end();

        // Рисуем UI
        if (uiManager != null) {
            uiManager.render(physicsSystem.getPlayer());
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        int levelNumber = 1;
        if (levelLoader != null) {
            levelNumber = levelLoader.getLevelNumber();
        }
        String musicPath = "assets/musics/level1.mp3";
        if (levelNumber == 2) musicPath = "assets/musics/level2.mp3";
        if (levelNumber == 3) musicPath = "assets/musics/level3.mp3";
        MusicManager.play(musicPath, true);

        // Устанавливаем обработчик ввода
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiManager.getStage());
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (inputHandler != null) {
                    inputHandler.update(Gdx.graphics.getDeltaTime());
                }
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (inputHandler != null) {
                    inputHandler.update(Gdx.graphics.getDeltaTime());
                }
                return true;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void hide() {
        if (levelMusic != null) {
            levelMusic.stop();
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (gameBatch != null) {
            gameBatch.dispose();
        }
        if (levelMusic != null) {
            levelMusic.dispose();
        }
        if (itemPickupSystem != null) {
            itemPickupSystem.dispose();
        }
    }

    public void updateCoinCount(int coins) {
        // Обновляем счетчик монет в UI
        if (uiManager != null) {
            uiManager.updateCoinCount(coins);
        }
    }

    public SpriteManager getSpriteManager() {
        return spriteManager;
    }
}