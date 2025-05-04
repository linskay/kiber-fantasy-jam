package com.cyberkingdom.gameengine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.physics.PhysicsSystem;

import com.cyberkingdom.physics.PhysicsSystemBuilder;
import com.cyberkingdom.rendering.SpriteManager;
import com.cyberkingdom.rendering.SpriteRenderer;
import com.cyberkingdom.screens.*;
import com.cyberkingdom.ui.UIManager;
import com.cyberkingdom.world.LevelLoader;

public class GameEngine extends Game {
    private SpriteBatch batch;
    private SpriteManager spriteManager;
    private SpriteRenderer spriteRenderer;
    private UIManager uiManager;
    private EntitySystem entitySystem;
    private PhysicsSystem physicsSystem;
    private LevelLoader levelLoader;
    private EntityFactory entityFactory;
    private BossFightLogic bossFightLogic;

    private static final String[] MAP_PATHS = {"levels/level1.tmx"};
    private int currentLevelIndex = 0;

    private MainMenuScreen mainMenuScreen;
    private StoryScreen storyScreen;
    private GameScreen gameScreen;

    @Override
    public void create() {
        initializeCoreSystems();
        initializeScreens();
        showMainMenu();
    }

    private void initializeCoreSystems() {
        batch = new SpriteBatch();
        spriteManager = new SpriteManager();
        GameEntity.setSpriteManager(spriteManager);
        spriteRenderer = new SpriteRenderer(batch);
        entitySystem = new EntitySystem();
        entityFactory = new EntityFactory();

        physicsSystem = new PhysicsSystemBuilder()
                .setEntitySystem(entitySystem)
                .setWorldWidth(1200)
                .setWorldHeight(800)
                .createPhysicsSystem();

        uiManager = new UIManager(spriteRenderer);
    }

    private void initializeScreens() {
        mainMenuScreen = new MainMenuScreen(this);
        storyScreen = new StoryScreen(this);
    }

    public void startGame() {
        loadCurrentLevel();
        setScreen((Screen) gameScreen);
    }

    private void loadCurrentLevel() {
        try {
            entitySystem.clear();
            physicsSystem.clearPlatforms();

            levelLoader = new LevelLoader(
                    spriteManager,
                    entitySystem,
                    physicsSystem,
                    entityFactory,
                    MAP_PATHS[currentLevelIndex],
                    currentLevelIndex + 1
            );

            Player player = (Player) entityFactory.createPlayer(100, 200);
            entitySystem.addEntity(player);
            physicsSystem.setPlayer(player);

            bossFightLogic = new BossFightLogic(null, player, this, levelLoader);

            gameScreen = new GameScreen(
                    entitySystem,
                    physicsSystem,
                    levelLoader,
                    spriteRenderer,
                    uiManager,
                    bossFightLogic
            );

            Gdx.app.log("GameEngine", "Level " + (currentLevelIndex + 1) + " loaded");
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Level loading failed", e);
            createFallbackEnvironment();
        }
    }

    private void createFallbackEnvironment() {
        physicsSystem.addPlatform(new Rectangle(0, 0, 1200, 50));
        Player player = (Player) entityFactory.createPlayer(100, 200);
        entitySystem.addEntity(player);
        physicsSystem.setPlayer(player);
        gameScreen = new GameScreen(
                entitySystem,
                physicsSystem,
                null,
                spriteRenderer,
                uiManager,
                null
        );
    }

    public void nextLevel() {
        currentLevelIndex = (currentLevelIndex + 1) % MAP_PATHS.length;
        loadCurrentLevel();
        setScreen((Screen) gameScreen);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();
    }

    public void showStoryScreen() {
        if (storyScreen == null) {
            storyScreen = new StoryScreen(this);
        }
        setScreen(storyScreen);
    }

    public void showMainMenu() {
        mainMenuScreen.resumeMusic();
        setScreen(mainMenuScreen);
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        spriteManager.dispose();
        if (gameScreen != null) gameScreen.dispose();
        mainMenuScreen.dispose();
        storyScreen.dispose();
    }

    // Геттеры
    public EntitySystem getEntitySystem() { return entitySystem; }
    public PhysicsSystem getPhysicsSystem() { return physicsSystem; }
    public LevelLoader getLevelLoader() { return levelLoader; }
    public SpriteRenderer getSpriteRenderer() { return spriteRenderer; }
    public EntityFactory getEntityFactory() { return entityFactory; }
    public BitmapFont getMenuFont() { return mainMenuScreen.getMenuFont(); }
    public Sound getSelectSound() { return mainMenuScreen.getSelectSound(); }
    public boolean isMusicPlaying() { return mainMenuScreen.isMusicPlaying(); }
    public void pauseMusic() { mainMenuScreen.pauseMusic(); }
    public void resumeMusic() { mainMenuScreen.resumeMusic(); }
    public Texture getMainMenuBackground() {
        return mainMenuScreen.getBackgroundTexture();
    }
    public Texture getCursorTexture() {
        return mainMenuScreen.getCursorTexture();
    }
}