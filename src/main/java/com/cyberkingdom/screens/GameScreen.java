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
import com.badlogic.gdx.audio.Music;
import com.cyberkingdom.items.ItemPickupSystem;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.boss.BossSpawnManager;
import com.cyberkingdom.rendering.SpriteManager;
import com.cyberkingdom.input.InputHandler;
import com.cyberkingdom.gameengine.GameEngine;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {
    private GameEngine engine;
    private EntitySystem entitySystem;
    private PhysicsSystem physicsSystem;
    private LevelLoader levelLoader;
    private UIManager uiManager;
    private SpriteRenderer spriteRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private InputHandler inputHandler;
    private ExtendViewport viewport;
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
    private EntityFactory entityFactory;

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
        // Очищаем экран
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновляем камеру
        camera.update();
        batch.setProjectionMatrix(camera.combined);

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

        // Рисуем все сущности в правильном порядке
        batch.begin();
        // Сначала рисуем платформы (кроме земли)
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Platform) {
                Platform platform = (Platform) entity;
                if (!platform.isGround()) {
                    platform.render(batch);
                }
            }
        }
        // Затем рисуем все остальные сущности (игрок, предметы, монеты и т.д.)
        for (GameEntity entity : entitySystem.getEntities()) {
            if (!(entity instanceof Platform)) {
                entity.render(batch);
            }
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
        // Инициализация камеры и viewport
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(LEVEL_WIDTH, LEVEL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(LEVEL_WIDTH / 2, LEVEL_HEIGHT / 2, 0);
        camera.update();

        // Инициализация систем
        entitySystem = new EntitySystem();
        physicsSystem = new PhysicsSystem(entitySystem);
        spriteManager = new SpriteManager();
        spriteManager.loadTextures();
        spriteManager.setupSpriteRegions();
        spriteRenderer = new SpriteRenderer(batch);
        entityFactory = new EntityFactory(spriteManager, physicsSystem);
        
        // Создаем игрока
        player = (Player) entityFactory.createPlayer(new Vector2(100, 200), this);
        physicsSystem.setPlayer(player);
        entitySystem.addEntity(player);
        
        // Генерируем уровень
        levelLoader = new LevelLoader(
            spriteManager,
            entitySystem,
            physicsSystem,
            entityFactory,
            1
        );
        
        // Инициализация системы сбора предметов
        itemPickupSystem = new ItemPickupSystem(entitySystem, player, levelLoader);
        
        // Инициализация менеджера босса
        bossSpawnManager = new BossSpawnManager(entitySystem, entityFactory, physicsSystem, player);
        
        // Настройка обработчика ввода
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(physicsSystem.getInputHandler());
        Gdx.input.setInputProcessor(inputMultiplexer);
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