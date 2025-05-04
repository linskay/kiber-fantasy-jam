package com.cyberkingdom.gameengine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.input.InputHandler;
import com.cyberkingdom.physics.PhysicsSystem;
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
    private int currentLevelIndex = 0;
    private MainMenuScreen mainMenuScreen;
    private StoryScreen storyScreen;
    private GameScreen gameScreen;
    private InputHandler inputHandler;
    private BitmapFont menuFont;
    private Sound selectSound;
    private Texture mainMenuBackground;
    private Texture cursorTexture;

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
        physicsSystem = new PhysicsSystem(entitySystem);

        // Инициализация шрифта с поддержкой кириллицы
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/arial.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
            params.size = 32;
            params.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<> ";
            menuFont = generator.generateFont(params);
            generator.dispose();
            Gdx.app.log("GameEngine", "Menu font with Cyrillic support initialized successfully");
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Failed to initialize menu font with Cyrillic support, falling back to default", e);
            menuFont = new BitmapFont(); // Запасной вариант без кириллицы
        }

        selectSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/select.mp3"));
        mainMenuBackground = new Texture(Gdx.files.internal("assets/ui/background.png"));
        cursorTexture = new Texture(Gdx.files.internal("assets/kursor.png"));
    }

    private void initializeScreens() {
        mainMenuScreen = new MainMenuScreen(this);
        storyScreen = new StoryScreen(this);
    }

    public void startGame() {
        loadCurrentLevel();
        setScreen(gameScreen); // Убрано лишнее приведение к Screen
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
                    currentLevelIndex + 1
            );

            Player player = (Player) entityFactory.createPlayer(100, 200);
            entitySystem.addEntity(player);
            physicsSystem.setPlayer(player);

            inputHandler = new InputHandler(player);
            uiManager = new UIManager(spriteRenderer, player, inputHandler, this);

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
        physicsSystem.addPlatform(new Rectangle(0, 150, 1200, 50));
        Player player = (Player) entityFactory.createPlayer(100, 200);
        entitySystem.addEntity(player);
        physicsSystem.setPlayer(player);
        inputHandler = new InputHandler(player);
        uiManager = new UIManager(spriteRenderer, player, inputHandler, this);
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
        currentLevelIndex = (currentLevelIndex + 1) % 5;
        loadCurrentLevel();
        setScreen(gameScreen);
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

    public void showAchievementsScreen() {
        setScreen(new AchievementsScreen(this));
    }

    public void showCreditsScreen() {
        setScreen(new CreditsScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        spriteManager.dispose();
        if (gameScreen != null) gameScreen.dispose();
        mainMenuScreen.dispose();
        storyScreen.dispose();
        uiManager.dispose();
        menuFont.dispose();
        selectSound.dispose();
        mainMenuBackground.dispose();
        cursorTexture.dispose();
    }

    public EntitySystem getEntitySystem() { return entitySystem; }
    public PhysicsSystem getPhysicsSystem() { return physicsSystem; }
    public LevelLoader getLevelLoader() { return levelLoader; }
    public SpriteRenderer getSpriteRenderer() { return spriteRenderer; }
    public EntityFactory getEntityFactory() { return entityFactory; }
    public BitmapFont getMenuFont() { return menuFont; }
    public Sound getSelectSound() { return selectSound; }
    public Texture getMainMenuBackground() { return mainMenuBackground; }
    public Texture getCursorTexture() { return cursorTexture; }
    public void resumeMusic() { if (mainMenuScreen != null) mainMenuScreen.resumeMusic(); }
}