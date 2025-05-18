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
import com.cyberkingdom.minigames.WifiKeyMinigame;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;

public class GameScreen implements Screen {
    private GameEngine gameEngine;
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

    public GameScreen(GameEngine gameEngine, EntitySystem entitySystem, PhysicsSystem physicsSystem,
                     LevelLoader levelLoader, UIManager uiManager, SpriteRenderer spriteRenderer) {
        try {
            Gdx.app.log("GameScreen", "Initializing GameScreen");
            
            this.gameEngine = gameEngine;
            this.entitySystem = entitySystem;
            this.physicsSystem = physicsSystem;
            this.levelLoader = levelLoader;
            this.uiManager = uiManager;
            this.spriteRenderer = spriteRenderer;
            this.batch = spriteRenderer.getBatch();

            // Инициализация ShapeRenderer
            this.shapeRenderer = new ShapeRenderer();
            Gdx.app.log("GameScreen", "ShapeRenderer initialized");

            if (entitySystem == null || physicsSystem == null || spriteRenderer == null || batch == null) {
                Gdx.app.error("GameScreen", "Required systems or batch are null");
                // Возможно, здесь стоит выбросить исключение или предпринять другие действия для обработки ошибки
                return;
            }
            
            // Получаем игрока из physicsSystem
            player = physicsSystem.getPlayer();
            if (player == null) {
                Gdx.app.error("GameScreen", "Player is null after getting from physicsSystem");
                // Обработка ошибки: возможно, создать заглушку игрока или выбросить исключение
                return;
            }
            Gdx.app.log("GameScreen", "Player retrieved from physicsSystem");
            
            // Устанавливаем ссылку на GameScreen в объекте Player
            player.setGameScreen(this);
            Gdx.app.log("GameScreen", "GameScreen reference set in Player object");
            
            // Инициализация системы подбора предметов
            itemPickupSystem = new ItemPickupSystem(entitySystem, player, levelLoader);
            Gdx.app.log("GameScreen", "Item pickup system initialized");
            
            // Инициализация менеджера спавна боссов
            bossSpawnManager = new BossSpawnManager(entitySystem, levelLoader.getEntityFactory(), physicsSystem, player);
            Gdx.app.log("GameScreen", "Boss spawn manager initialized");

            // Инициализация камеры и viewport
            camera = new OrthographicCamera();
            viewport = new ExtendViewport(LEVEL_WIDTH, LEVEL_HEIGHT, camera);
            viewport.apply();
            camera.position.set(LEVEL_WIDTH / 2, LEVEL_HEIGHT / 2, 0);
            camera.update();
            Gdx.app.log("GameScreen", "Camera and Viewport initialized in constructor");

            // Фоновая текстура будет загружена позже через setBackgroundTexture
            this.backgroundTexture = null; // Изначально null
            
            Gdx.app.log("GameScreen", "GameScreen initialized successfully");
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Failed to initialize GameScreen", e);
            // Перебрасываем исключение, чтобы игра завершилась с ошибкой, а не работала некорректно
            throw new RuntimeException("Failed to initialize GameScreen", e);
        }
    }

    // Метод для установки фоновой текстуры уровня
    public void setBackgroundTexture(String texturePath) {
        if (backgroundTexture != null) {
            backgroundTexture.dispose(); // Освобождаем предыдущую текстуру, если она была
        }
        try {
            backgroundTexture = new Texture(Gdx.files.internal(texturePath));
            Gdx.app.log("GameScreen", "Background texture loaded successfully from path: " + texturePath);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Failed to load background texture from path: " + texturePath, e);
            // Создаем пустую текстуру как заглушку при ошибке загрузки
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.BLACK);
            pixmap.fill();
            backgroundTexture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновляем состояние игры
        update(delta);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Рисуем фон
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, LEVEL_WIDTH, LEVEL_HEIGHT);
        }

        // Рисуем UI элементы, использующие SpriteBatch (иконки сердечка, монеты, текст)
        if (uiManager != null) {
            uiManager.renderSpriteUI(player, batch);
        }

        if (!player.isInMinigame()) {
            // Обычная игровая логика
            levelLoader.update(delta, entitySystem, player);
            Gdx.app.log("GameScreen", "Player position: " + player.getPosition());
            
            // Отрисовка основной игры
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
                if (!(entity instanceof Platform) && entity != player) {
                    entity.render(batch);
                }
            }
            
            // Рисуем игрока
            Gdx.app.log("GameScreen", "Rendering player at position: " + player.getPosition());
            player.render(batch);
        } else {
            // Отрисовка мини-игры
            WifiKeyMinigame minigame = player.getWifiKeyMinigame();
            if (minigame != null) {
                minigame.render(batch);
            }
        }

        batch.end();

        // Рисуем полоску здоровья (использует ShapeRenderer) - Перемещено после batch.end()
        if (uiManager != null && shapeRenderer != null) {
            // Сброс цвета и включение стандартного смешивания для ShapeRenderer
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.setColor(Color.WHITE); // Сбросим цвет ShapeRenderer на всякий случай
            
            shapeRenderer.setProjectionMatrix(uiManager.getStage().getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            uiManager.renderShapeUI(player, shapeRenderer);
            shapeRenderer.end();
            // Отключим смешивание после использования ShapeRenderer, чтобы не влиять на отрисовку Stage
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        }

        // Рисуем Stage UI (окно инвентаря) - Теперь рисуется после ShapeRenderer
        if (uiManager != null && uiManager.getStage() != null) {
            uiManager.getStage().act(delta);
            uiManager.getStage().draw();
        }
    }

    private void update(float delta) {
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

        // Обновляем игру
        if (!player.isInMinigame()) {
            // Обычная игровая логика
            levelLoader.update(delta, entitySystem, player);
        } else {
            // Логика мини-игры
            WifiKeyMinigame minigame = player.getWifiKeyMinigame();
            if (minigame != null) {
                minigame.update(delta);
            }
        }

        // Обновляем UI
        if (uiManager != null) {
            uiManager.update(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        // Используем существующие системы вместо создания новых
        if (entitySystem == null || physicsSystem == null || spriteRenderer == null || batch == null) {
            Gdx.app.error("GameScreen", "Required systems or batch are null in show()!");
            return;
        }
        
        // Получаем игрока из physicsSystem
        player = physicsSystem.getPlayer();
        if (player == null) {
             Gdx.app.error("GameScreen", "Player is null in show()!");
             return;
        }
        
        // Настройка обработчика ввода
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        if (physicsSystem.getInputHandler() != null) {
             inputMultiplexer.addProcessor(physicsSystem.getInputHandler());
             Gdx.app.log("GameScreen", "Input handler added to InputMultiplexer");
        } else {
             Gdx.app.error("GameScreen", "InputHandler is null in show()!");
        }
        if (uiManager != null && uiManager.getStage() != null) {
             inputMultiplexer.addProcessor(uiManager.getStage());
             Gdx.app.log("GameScreen", "UI Stage added to InputMultiplexer");
        } else {
            Gdx.app.error("GameScreen", "UIManager or UI Stage is null, cannot add to InputMultiplexer");
        }

        Gdx.input.setInputProcessor(inputMultiplexer);
        
        Gdx.app.log("GameScreen", "Show completed successfully");
    }

    @Override
    public void hide() {
        if (levelMusic != null) {
            levelMusic.stop();
        }
        Gdx.input.setInputProcessor(null);
        Gdx.app.log("GameScreen", "GameScreen hidden, input processor set to null");
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameScreen", "Disposing GameScreen");
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
             Gdx.app.log("GameScreen", "Background texture disposed");
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
             Gdx.app.log("GameScreen", "ShapeRenderer disposed");
        }
        if (font != null) {
             Gdx.app.log("GameScreen", "Font dispose skipped (managed by UIManager/Skin)");
        }
        if (gameBatch != null) {
             Gdx.app.log("GameScreen", "GameBatch dispose skipped (possibly managed by SpriteRenderer)");
        }
        if (levelMusic != null) {
            levelMusic.dispose();
             Gdx.app.log("GameScreen", "Level music disposed");
        }
        if (itemPickupSystem != null) {
            itemPickupSystem.dispose();
             Gdx.app.log("GameScreen", "ItemPickupSystem disposed");
        }
        Gdx.app.log("GameScreen", "GameScreen disposed");
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

    public EntitySystem getEntitySystem() {
        return entitySystem;
    }
    
    public SpriteBatch getBatch() {
        return batch;
    }
}