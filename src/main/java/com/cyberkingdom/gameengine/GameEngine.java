package com.cyberkingdom.gameengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.rendering.SpriteManager;
import com.cyberkingdom.rendering.SpriteRenderer;
import com.cyberkingdom.screens.GameScreen;
import com.cyberkingdom.ui.UIManager;
import com.cyberkingdom.world.LevelLoader;

//public class GameEngine extends ApplicationAdapter {
//    private SpriteBatch batch;
//    private SpriteManager spriteManager;
//    private SpriteRenderer spriteRenderer;
//    private UIManager uiManager;
//    private EntitySystem entitySystem;
//    private PhysicsSystem physicsSystem;
//    private LevelLoader levelLoader;
//    private GameScreen gameScreen;
//    private EntityFactory entityFactory;
//
//    @Override
//    public void create() {
//        System.out.println("Запуск GameEngine");
//        batch = new SpriteBatch();
//        spriteManager = new SpriteManager();
//        GameEntity.setSpriteManager(spriteManager); // Устанавливаем SpriteManager до создания сущностей
//        spriteRenderer = new SpriteRenderer(batch);
//        entitySystem = new EntitySystem();
//        levelLoader = new LevelLoader(spriteManager, entitySystem); // Загружаем уровень и сущности
//
//        // Динамически получаем размеры экрана
//        float worldWidth = Gdx.graphics.getWidth();
//        float worldHeight = Gdx.graphics.getHeight();
//
//        physicsSystem = new PhysicsSystem(entitySystem, worldWidth, worldHeight); // Передаём размеры мира
//
//        uiManager = new UIManager(spriteRenderer);
//        gameScreen = new GameScreen(entitySystem, physicsSystem, levelLoader, spriteRenderer, uiManager);
//        entityFactory = new EntityFactory();
//    }
//
//    @Override
//    public void render() {
//        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        gameScreen.render(Gdx.graphics.getDeltaTime());
//    }
//
//    @Override
//    public void dispose() {
//        System.out.println("Освобождение ресурсов GameEngine");
//        spriteRenderer.dispose();
//        uiManager.dispose();
//        gameScreen.dispose();
//        levelLoader.getCurrentMap().dispose();
//        spriteManager.dispose();
//        batch.dispose();
//    }
//}

//public class GameEngine extends ApplicationAdapter {
//    private SpriteBatch batch;
//    private SpriteManager spriteManager;
//    private SpriteRenderer spriteRenderer;
//    private UIManager uiManager;
//    private EntitySystem entitySystem;
//    private PhysicsSystem physicsSystem;
//    private LevelLoader levelLoader;
//    private GameScreen gameScreen;
//    private EntityFactory entityFactory;
//    private BossFightLogic bossFightLogic;
//
//    // Пути к картам для 5 уровней
//    private static final String[] MAP_PATHS = {
//            "levels/level1.tmx",
//            "levels/level1.tmx",
//            "levels/level1.tmx",
//            "levels/level1.tmx",
//            "levels/level1.tmx"
//    };
//
//    private int currentLevelIndex = 0; // Индекс текущего уровня
//
//    @Override
//    public void create() {
//        System.out.println("Запуск GameEngine");
//        batch = new SpriteBatch();
//        spriteManager = new SpriteManager();
//        GameEntity.setSpriteManager(spriteManager);
//        spriteRenderer = new SpriteRenderer(batch);
//        entitySystem = new EntitySystem();
//        entityFactory = new EntityFactory();
//
//        loadCurrentLevel();
//
//        // Получаем размеры экрана
//        float worldWidth = Gdx.graphics.getWidth();
//        float worldHeight = Gdx.graphics.getHeight();
//
//        physicsSystem = new PhysicsSystem(entitySystem, worldWidth, worldHeight);
//        uiManager = new UIManager(spriteRenderer);
//
//        gameScreen = new GameScreen(entitySystem, physicsSystem, levelLoader, spriteRenderer, uiManager,bossFightLogic);
//    }
//
//    private void loadCurrentLevel() {
//        if (currentLevelIndex < 0 || currentLevelIndex >= MAP_PATHS.length) {
//            System.err.println("Уровень вне диапазона: " + currentLevelIndex);
//            return;
//        }
//        String mapPath = MAP_PATHS[currentLevelIndex];
//        int levelNumber = currentLevelIndex + 1;
//
//        // Очищаем сущности перед загрузкой нового уровня
//        entitySystem.clear();
//
//        // Создаём LevelLoader с нужной картой и уровнем
//        levelLoader = new LevelLoader(spriteManager, entitySystem, mapPath, levelNumber);
//
//        System.out.println("Загружен уровень " + levelNumber + " с картой " + mapPath);
//
//        // Обновляем GameScreen с новым LevelLoader
//        if (gameScreen != null) {
//            gameScreen.dispose();
//        }
//        gameScreen = new GameScreen(entitySystem, physicsSystem, levelLoader, spriteRenderer, uiManager,bossFightLogic);
//    }
//
//    // Метод для перехода к следующему уровню
//    public void nextLevel() {
//        currentLevelIndex++;
//        if (currentLevelIndex >= MAP_PATHS.length) {
//            System.out.println("Все уровни пройдены!");
//            // Можно реализовать логику окончания игры или перезапуска
//            currentLevelIndex = 0; // Перезапуск с первого уровня
//        }
//        loadCurrentLevel();
//    }
//
//    @Override
//    public void render() {
//        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        gameScreen.render(Gdx.graphics.getDeltaTime());
//    }
//
//    @Override
//    public void dispose() {
//        System.out.println("Освобождение ресурсов GameEngine");
//        spriteRenderer.dispose();
//        uiManager.dispose();
//        gameScreen.dispose();
//        if (levelLoader != null && levelLoader.getCurrentMap() != null) {
//            levelLoader.getCurrentMap().dispose();
//        }
//        spriteManager.dispose();
//        batch.dispose();
//    }
//}

public class GameEngine extends ApplicationAdapter {
    private SpriteBatch batch;
    private SpriteManager spriteManager;
    private SpriteRenderer spriteRenderer;
    private UIManager uiManager;
    private EntitySystem entitySystem;
    private PhysicsSystem physicsSystem;
    private LevelLoader levelLoader;
    private GameScreen gameScreen;
    private EntityFactory entityFactory;
    private BossFightLogic bossFightLogic;

    private static final String[] MAP_PATHS = {
            "levels/level1.tmx",
            "levels/level1.tmx",
            "levels/level1.tmx",
            "levels/level1.tmx",
            "levels/level1.tmx"
    };

    private int currentLevelIndex = 0;

    @Override
    public void create() {
        System.out.println("Запуск GameEngine");
        batch = new SpriteBatch();
        spriteManager = new SpriteManager();
        GameEntity.setSpriteManager(spriteManager);
        spriteRenderer = new SpriteRenderer(batch);
        entitySystem = new EntitySystem();
        entityFactory = new EntityFactory();

        // Получаем размеры экрана
        float worldWidth = Gdx.graphics.getWidth();
        float worldHeight = Gdx.graphics.getHeight();

        physicsSystem = new PhysicsSystem(entitySystem, worldWidth, worldHeight);
        uiManager = new UIManager(spriteRenderer);

        loadCurrentLevel(); // Загрузка уровня и инициализация gameScreen и bossFightLogic
    }

    private void loadCurrentLevel() {
        if (currentLevelIndex < 0 || currentLevelIndex >= MAP_PATHS.length) {
            System.err.println("Уровень вне диапазона: " + currentLevelIndex);
            return;
        }
        String mapPath = MAP_PATHS[currentLevelIndex];
        int levelNumber = currentLevelIndex + 1;

        entitySystem.clear();

        levelLoader = new LevelLoader(spriteManager, entitySystem, mapPath, levelNumber);

        System.out.println("Загружен уровень " + levelNumber + " с картой " + mapPath);

        if (gameScreen != null) {
            gameScreen.dispose();
        }

        // Создаём GameScreen без bossFightLogic, он будет установлен позже
        gameScreen = new GameScreen(entitySystem, physicsSystem, levelLoader, spriteRenderer, uiManager, null);

        // Инициализируем bossFightLogic после загрузки сущностей
        Player player = null;
        Boss boss = null;
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Player) player = (Player) entity;
            else if (entity instanceof Boss) boss = (Boss) entity;
        }

        if (player != null && boss != null) {
            bossFightLogic = new BossFightLogic(boss, player, this);
            gameScreen.setBossFightLogic(bossFightLogic);
            System.out.println("BossFightLogic инициализирован");
        } else {
            System.err.println("Не удалось инициализировать BossFightLogic: игрок или босс не найдены");
        }
    }

    public void nextLevel() {
        currentLevelIndex++;
        if (currentLevelIndex >= MAP_PATHS.length) {
            System.out.println("Все уровни пройдены!");
            currentLevelIndex = 0; // Перезапуск с первого уровня
        }
        loadCurrentLevel();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (gameScreen != null) {
            gameScreen.render(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void dispose() {
        System.out.println("Освобождение ресурсов GameEngine");
        if (spriteRenderer != null) spriteRenderer.dispose();
        if (uiManager != null) uiManager.dispose();
        if (gameScreen != null) gameScreen.dispose();
        if (levelLoader != null && levelLoader.getCurrentMap() != null) {
            levelLoader.getCurrentMap().dispose();
        }
        if (spriteManager != null) spriteManager.dispose();
        if (batch != null) batch.dispose();
    }
}
