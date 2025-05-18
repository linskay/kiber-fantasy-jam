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
    private int currentLevel;
    private boolean levelTransitionInitiated = false;
    private boolean isLevel1BossDefeated = false;
    private boolean isLevel2BossDefeated = false;
    private boolean isLevel3BossDefeated = false;

    public GameScreen(GameEngine gameEngine, EntitySystem entitySystem, PhysicsSystem physicsSystem,
                     LevelLoader levelLoader, UIManager uiManager, SpriteRenderer spriteRenderer,
                     ItemPickupSystem itemPickupSystem,
                     BossSpawnManager bossSpawnManager, int initialLevel) {
        try {
            Gdx.app.log("GameScreen", "Initializing GameScreen");
            Gdx.app.log("GameScreen", "EntitySystem: " + (entitySystem != null ? "not null" : "null"));
            
            this.gameEngine = gameEngine;
            this.entitySystem = entitySystem;
            this.physicsSystem = physicsSystem;
            this.levelLoader = levelLoader;
            this.uiManager = uiManager;
            this.spriteRenderer = spriteRenderer;
            this.batch = spriteRenderer.getBatch();
            this.itemPickupSystem = itemPickupSystem;
            this.bossSpawnManager = bossSpawnManager;

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
            
            // Инициализация InputHandler здесь
            this.inputHandler = new InputHandler(player);
            Gdx.app.log("GameScreen", "InputHandler initialized in GameScreen");

            // Устанавливаем ссылку на GameScreen в объекте Player
            player.setGameScreen(this);
            Gdx.app.log("GameScreen", "GameScreen reference set in Player object");
            
            // Инициализация системы подбора предметов
            // itemPickupSystem = new ItemPickupSystem(entitySystem, player, levelLoader); // Удаляем эту строку
            // Gdx.app.log("GameScreen", "Item pickup system initialized");
            
            // Инициализация камеры и viewport
            camera = new OrthographicCamera();
            viewport = new ExtendViewport(LEVEL_WIDTH, LEVEL_HEIGHT, camera);
            viewport.apply();
            camera.position.set(LEVEL_WIDTH / 2, LEVEL_HEIGHT / 2, 0);
            camera.update();
            Gdx.app.log("GameScreen", "Camera and Viewport initialized in constructor");

            // Устанавливаем начальный уровень
            this.currentLevel = initialLevel;
            Gdx.app.log("GameScreen", "GameScreen initialized with level: " + this.currentLevel);

            // Сбрасываем флаги боссов
            this.isLevel1BossDefeated = false;
            this.isLevel2BossDefeated = false;
            this.isLevel3BossDefeated = false;
            this.levelTransitionInitiated = false;
            Gdx.app.log("GameScreen", "Boss defeat flags reset");

            // Фоновая текстура будет установлена позже через setBackgroundTexture
            this.backgroundTexture = null; // Изначально null
            
            Gdx.app.log("GameScreen", "GameScreen initialized successfully");
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Failed to initialize GameScreen", e);
            // Перебрасываем исключение, чтобы игра завершилась с ошибкой, а не работала некорректно
            throw new RuntimeException("Failed to initialize GameScreen", e);
        }
    }

    // Метод для установки фоновой текстуры уровня
    public void setBackgroundTexture(Texture texture) {
        if (backgroundTexture != null) {
            backgroundTexture.dispose(); // Освобождаем предыдущую текстуру, если она была
        }
        this.backgroundTexture = texture; // Принимаем уже загруженную текстуру
        Gdx.app.log("GameScreen", "Background texture set.");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновляем состояние игры (включая inputHandler.update)
        update(delta);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Устанавливаем режим смешивания для прозрачности
        batch.enableBlending();
        batch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

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
        // Добавляем лог в начало update
        Gdx.app.log("GameScreen", "GameScreen update called");

        // Обновляем обработчик ввода
        if (inputHandler != null) {
            inputHandler.update(delta);
        }

        // Обновляем физику и сущности
        physicsSystem.update(delta);
        entitySystem.update(delta);
        
        // Устанавливаем текущий номер уровня в EntitySystem
        entitySystem.setLevelNumber(currentLevel);
        
        // Проверяем коллизии с землей и платформами
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Platform) {
                Platform platform = (Platform) entity;
                if (platform.isGround()) {
                    // Если игрок падает ниже земли, останавливаем его
                    if (player.getPosition().y < platform.getPosition().y + platform.getHeight()) {
                        player.getPosition().y = platform.getPosition().y + platform.getHeight();
                        player.setVelocity(player.getVelocity().x, 0);
                        player.setOnGround(true);
                    }
                } else {
                    // Проверяем коллизии с платформами
                    if (player.getCollisionBounds().overlaps(platform.getBounds())) {
                        // Если игрок падает на платформу сверху
                        if (player.getVelocity().y < 0 && 
                            player.getPosition().y > platform.getPosition().y + platform.getHeight() - 10) {
                            player.getPosition().y = platform.getPosition().y + platform.getHeight();
                            player.setVelocity(player.getVelocity().x, 0);
                            player.setOnGround(true);
                        }
                    }
                }
            }
        }
        
        // Проверяем состояние боссов и переход на следующий уровень
        if (!levelTransitionInitiated) {
            boolean bossFound = false;
            GameEntity currentLevelBoss = null;

            // Находим босса текущего уровня, если он есть, и проверяем, активен ли он
            for (GameEntity entity : entitySystem.getEntities()) {
                if (currentLevel == 1 && entity instanceof WitchVPN) {
                    WitchVPN boss = (WitchVPN) entity;
                    currentLevelBoss = boss;
                    bossFound = true;
                    if (!boss.isActive()) {
                        isLevel1BossDefeated = true;
                        Gdx.app.log("GameScreen", "Level 1 boss (WitchVPN) defeated!");
                    }
                    break;
                } else if (currentLevel == 2 && entity instanceof DedinsaidBoss) {
                    DedinsaidBoss boss = (DedinsaidBoss) entity;
                    currentLevelBoss = boss;
                    bossFound = true;
                    Gdx.app.log("GameScreen", "Found DedinsaidBoss, active: " + boss.isActive() + ", hits: " + boss.getCurrentHits() + "/" + boss.getHitsRequired());
                    if (!boss.isActive() || boss.isDefeated()) {
                        isLevel2BossDefeated = true;
                        Gdx.app.log("GameScreen", "Level 2 boss (DedinsaidBoss) defeated!");
                    }
                    break;
                } else if (currentLevel == 3 && entity instanceof CatMiner) {
                    CatMiner boss = (CatMiner) entity;
                    currentLevelBoss = boss;
                    bossFound = true;
                    if (!boss.isActive()) {
                        isLevel3BossDefeated = true;
                        Gdx.app.log("GameScreen", "Level 3 boss (CatMiner) defeated!");
                    }
                    break;
                }
            }

            // Добавляем проверку здоровья босса для второго уровня
            if (currentLevel == 2 && currentLevelBoss instanceof DedinsaidBoss) {
                DedinsaidBoss boss = (DedinsaidBoss) currentLevelBoss;
                if (boss.isDefeated()) {
                    isLevel2BossDefeated = true;
                    Gdx.app.log("GameScreen", "Level 2 boss defeated by isDefeated() check!");
                }
            }

            // Если босс текущего уровня найден и неактивен (побежден)
            // Добавляем логирование для отладки условия перехода
            Gdx.app.log("GameScreen", String.format(
                "Level %d transition check: bossFound=%b, currentLevelBoss=%s, isActive=%b",
                currentLevel,
                bossFound,
                currentLevelBoss != null ? currentLevelBoss.getName() : "null",
                currentLevelBoss != null ? currentLevelBoss.isActive() : false
            ));

            // Новая проверка условия перехода на следующий уровень
            boolean readyForNextLevel = false;
            if (currentLevel == 1 && isLevel1BossDefeated) {
                readyForNextLevel = true;
                Gdx.app.log("GameScreen", "Level 1 boss defeated, ready for next level");
            } else if (currentLevel == 2 && isLevel2BossDefeated) {
                readyForNextLevel = true;
                Gdx.app.log("GameScreen", "Level 2 boss defeated, ready for next level");
            } else if (currentLevel == 3 && isLevel3BossDefeated) {
                readyForNextLevel = true;
                Gdx.app.log("GameScreen", "Level 3 boss defeated, ready for next level");
            }

            Gdx.app.log("GameScreen", String.format(
                "Level transition check: currentLevel=%d, readyForNextLevel=%b, isLevel1Defeated=%b, isLevel2Defeated=%b, isLevel3Defeated=%b",
                currentLevel, readyForNextLevel, isLevel1BossDefeated, isLevel2BossDefeated, isLevel3BossDefeated
            ));

            if (readyForNextLevel) {
                Gdx.app.log("GameScreen", "Boss of level " + currentLevel + " defeated! Transitioning to next level...");
                levelTransitionInitiated = true;
                
                // Очищаем текущий уровень (кроме игрока)
                Gdx.app.log("GameScreen", "Clearing entities and platforms before level transition.");
                entitySystem.removeAllEntitiesExceptPlayer();
                physicsSystem.clearPlatforms();
                Gdx.app.log("GameScreen", "Entities and platforms cleared.");

                int nextLevel = currentLevel + 1;
                
                // Загружаем следующий уровень через LoadingScreen
                if (nextLevel <= 3) { // Предполагаем 3 уровня в игре
                    Gdx.app.log("GameScreen", "Loading level " + nextLevel);
                    // Передаем загрузку следующего уровня в GameEngine через LoadingScreen
                    gameEngine.loadNextLevel(nextLevel);
                } else {
                    // Игра пройдена
                    Gdx.app.log("GameScreen", "Game completed!");
                    // TODO: Показать экран победы или перейти в главное меню
                }
            }
        }
        
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
        Gdx.app.log("GameScreen", "GameScreen show called");
        // Устанавливаем InputProcessor для обработки ввода
        if (inputHandler != null) {
            Gdx.input.setInputProcessor(new InputMultiplexer(inputHandler, uiManager.getStage()));
            Gdx.app.log("GameScreen", "InputProcessor set to InputMultiplexer with InputHandler and UIManager Stage");
        } else {
            Gdx.app.error("GameScreen", "InputHandler is null in show()");
        }
        
        // Запускаем музыку уровня, если она установлена
        if (levelMusic != null) {
            levelMusic.setLooping(true);
            levelMusic.play();
            Gdx.app.log("GameScreen", "Level music started.");
        } else {
            Gdx.app.log("GameScreen", "Level music is null, cannot play.");
        }
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen", "GameScreen hide called");
        // Убираем InputProcessor
        Gdx.input.setInputProcessor(null);
        if (levelMusic != null) {
            levelMusic.stop();
        }
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

    // Метод для установки музыки уровня
    public void setLevelMusic(Music music) {
        if (levelMusic != null) {
            levelMusic.stop(); // Останавливаем текущую музыку, если есть
            levelMusic.dispose(); // Освобождаем предыдущую музыку
        }
        this.levelMusic = music;
        Gdx.app.log("GameScreen", "Level music set in GameScreen.");
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void updateCoinCount(int coins) {
        // Обновляем счетчик монет в UI
        if (uiManager != null) {
            uiManager.updateCoinCount(coins);
        }
    }

    public SpriteManager getSpriteManager() {
        // Возвращаем SpriteManager, если он доступен (например, через GameEngine)
        // В текущей структуре GameScreen напрямую не хранит SpriteManager, он есть в GameEngine.
        // Возможно, стоит передавать SpriteManager в конструктор GameScreen или получать его из GameEngine.
        // Пока возвращаем null, если нет прямого доступа.
        // TODO: Добавить правильный доступ к SpriteManager
        // return gameEngine.getSpriteManager(); // Если в GameEngine есть getSpriteManager()
        return null; // Заглушка
    }

    public EntitySystem getEntitySystem() {
        return entitySystem;
    }
    
    public SpriteBatch getBatch() {
        return batch;
    }

    public LevelLoader getLevelLoader() {
        return this.levelLoader;
    }

    public UIManager getUIManager() {
        return this.uiManager;
    }

    public Music getLevelMusic() {
        return levelMusic;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }
}