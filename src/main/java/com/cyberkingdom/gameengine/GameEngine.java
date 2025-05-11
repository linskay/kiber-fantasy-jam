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
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.boss.BossFightLogic;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.input.InputHandler;
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
        try {
            initializeCoreSystems();
            initializeScreens();
            showMainMenu();
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Failed to initialize game", e);
            throw new RuntimeException("Game initialization failed", e);
        }
    }

    private void initializeCoreSystems() {
        try {
            batch = new SpriteBatch();
            spriteManager = new SpriteManager();
            spriteRenderer = new SpriteRenderer(batch);
            entitySystem = new EntitySystem();
            physicsSystem = new PhysicsSystemBuilder()
                    .setEntitySystem(entitySystem)
                    .createPhysicsSystem();
            entityFactory = new EntityFactory(spriteManager, physicsSystem);

            // Инициализация шрифта с поддержкой кириллицы
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/arial.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 32;
            parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<> ";
            menuFont = generator.generateFont(parameter);
            generator.dispose();

            // Загрузка звуков и текстур
            selectSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/select.mp3"));
            mainMenuBackground = new Texture(Gdx.files.internal("assets/ui/background.png"));
            cursorTexture = new Texture(Gdx.files.internal("assets/kursor.png"));

            // Инициализация UI после загрузки всех ресурсов
            uiManager = new UIManager(spriteRenderer, null, null, this);

            Gdx.app.log("GameEngine", "Core systems initialized successfully");
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Failed to initialize core systems", e);
            throw new RuntimeException("Core systems initialization failed", e);
        }
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

            // Создаём игрока один раз
            Player player = (Player) entityFactory.createPlayer(new Vector2(100, 300), null);
            entitySystem.addEntity(player);
            physicsSystem.setPlayer(player);

            // Передаём этого игрока во все системы
            inputHandler = new InputHandler(player);
            uiManager = new UIManager(spriteRenderer, player, inputHandler, this);

            // Создаём LevelLoader
            levelLoader = new LevelLoader(
                    spriteManager,
                    entitySystem,
                    physicsSystem,
                    entityFactory,
                    currentLevelIndex + 1
            );

            // Создаём BossFightLogic
            bossFightLogic = new BossFightLogic(levelLoader, player, this);

            // Создаём GameScreen
            gameScreen = new GameScreen(
                this,
                entitySystem,
                physicsSystem,
                levelLoader,
                uiManager,
                spriteRenderer
            );
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Level loading failed", e);
            createFallbackEnvironment();
        }
    }

    private void createFallbackEnvironment() {
        try {
            // Создаём игрока один раз
            Player player = (Player) entityFactory.createPlayer(new Vector2(100, 300), null);
            entitySystem.addEntity(player);
            physicsSystem.setPlayer(player);

            // Передаём этого игрока во все системы
            inputHandler = new InputHandler(player);
            uiManager = new UIManager(spriteRenderer, player, inputHandler, this);

            // Создаём LevelLoader
            levelLoader = new LevelLoader(
                    spriteManager,
                    entitySystem,
                    physicsSystem,
                    entityFactory,
                    1
            );

            // Создаём BossFightLogic
            bossFightLogic = new BossFightLogic(levelLoader, player, this);

            // Создаём GameScreen
            gameScreen = new GameScreen(
                this,
                entitySystem,
                physicsSystem,
                levelLoader,
                uiManager,
                spriteRenderer
            );
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Fallback environment creation failed", e);
            throw new RuntimeException("Failed to create fallback environment", e);
        }
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