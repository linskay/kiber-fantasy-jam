package com.cyberkingdom.gameengine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.cyberkingdom.items.ItemPickupSystem;
import com.cyberkingdom.boss.BossSpawnManager;
import com.badlogic.gdx.assets.AssetManager;

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
    private Player player;
    private LoadingScreen loadingScreen;
    private CreditsScreen creditsScreen;
    private AchievementsScreen achievementsScreen;
    private ItemPickupSystem itemPickupSystem;
    private BossSpawnManager bossSpawnManager;
    private AssetManager assetManager;
    private int nextLevelToLoad;

    @Override
    public void create() {
        try {
            assetManager = new AssetManager();
            initializeSystems();
            initializeScreens();
            showMainMenu();
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Failed to initialize game", e);
            throw new RuntimeException("Game initialization failed", e);
        }
    }

    private void initializeSystems() {
        try {
            Gdx.app.log("GameEngine", "Initializing systems");
            
            // Инициализация систем
            entitySystem = new EntitySystem();
            physicsSystem = new PhysicsSystemBuilder()
                    .setEntitySystem(entitySystem)
                    .createPhysicsSystem();
            
            // Инициализация SpriteManager с проверкой
            try {
                spriteManager = new SpriteManager();
                if (spriteManager == null) {
                    throw new RuntimeException("Failed to create SpriteManager");
                }
                
                // Загрузка текстур
                spriteManager.loadTextures();
                Gdx.app.log("GameEngine", "Textures loaded successfully");
                
                // Настройка регионов спрайтов
                spriteManager.setupSpriteRegions();
                Gdx.app.log("GameEngine", "Sprite regions setup complete");
            } catch (Exception e) {
                Gdx.app.error("GameEngine", "Error initializing SpriteManager", e);
                throw e;
            }
            
            // Создание фабрики сущностей
            entityFactory = new EntityFactory(spriteManager, physicsSystem);
            
            // Инициализация шрифта с поддержкой кириллицы
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/arial.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 32;
            parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<> ";
            menuFont = generator.generateFont(parameter);
            generator.dispose();
            Gdx.app.log("GameEngine", "Menu font initialized");
            
            // Инициализация SpriteBatch и SpriteRenderer
            batch = new SpriteBatch();
            spriteRenderer = new SpriteRenderer(batch);
            if (spriteRenderer == null) {
                 Gdx.app.error("GameEngine", "Failed to create SpriteRenderer");
                 return;
            }
            Gdx.app.log("GameEngine", "SpriteBatch and SpriteRenderer initialized");

            // Инициализация текстур для меню
            try {
                mainMenuBackground = new Texture(Gdx.files.internal("assets/background_loading.png"));
                cursorTexture = new Texture(Gdx.files.internal("assets/kursor.png"));
                selectSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/select.mp3"));
                Gdx.app.log("GameEngine", "Menu textures and sounds loaded successfully");
            } catch (Exception e) {
                Gdx.app.error("GameEngine", "Failed to load menu resources", e);
                throw e;
            }
            
            // Инициализируем игрока (создается один раз)
            // Игрока создаем здесь, но его позиция и другие данные будут обновляться при загрузке уровня
            // Создаем временный игрок для UIManager и InputHandler, его данные будут перезаписаны при загрузке уровня
            player = (Player) entityFactory.createPlayer(new Vector2(0, 0), null); // Временная позиция
            entitySystem.addEntity(player);
            physicsSystem.setPlayer(player);

            // Создаём UIManager и InputHandler (создаются один раз)
            inputHandler = new InputHandler(player);
            uiManager = new UIManager(batch, spriteRenderer, player, inputHandler, this);

            // Создаём LevelLoader (создается один раз)
            levelLoader = new LevelLoader(
                    spriteManager,
                    entitySystem,
                    physicsSystem,
                    entityFactory,
                    1 // Начальный уровень, будет обновлен при загрузке
            );

            // Создаём BossFightLogic (создается один раз)
            bossFightLogic = new BossFightLogic(levelLoader, this);
            
            // Инициализируем ItemPickupSystem (создается один раз)
            itemPickupSystem = new ItemPickupSystem(entitySystem, player, levelLoader);

            // Инициализируем BossSpawnManager (создается один раз)
            bossSpawnManager = new BossSpawnManager(entitySystem, levelLoader.getEntityFactory(), physicsSystem);

            // Создаём GameScreen (создается один раз)
            gameScreen = new GameScreen(
                this,
                entitySystem,
                physicsSystem,
                levelLoader,
                uiManager,
                spriteRenderer,
                itemPickupSystem,
                bossSpawnManager,
                1 // Передаем начальный уровень (1) в конструктор GameScreen
            );
            
            Gdx.app.log("GameEngine", "All systems and core screens initialized successfully");
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Error during system initialization", e);
            throw new RuntimeException("Failed to initialize game systems", e);
        }
    }

    private void initializeScreens() {
        mainMenuScreen = new MainMenuScreen(this);
        storyScreen = new StoryScreen(this);
        loadingScreen = new LoadingScreen(this);
        creditsScreen = new CreditsScreen(this);
        achievementsScreen = new AchievementsScreen(this);
    }

    public void startGame() {
        Gdx.app.log("GameEngine", "Starting game...");
        // Устанавливаем LoadingScreen и передаем ему номер первого уровня
        loadingScreen.setNextLevelNumber(1);
        setScreen(loadingScreen);
        Gdx.app.log("GameEngine", "Switched to LoadingScreen for initial game start.");
    }

    public void loadCurrentLevel() {
        try {
            Gdx.app.log("GameEngine", "Loading data for current level: " + currentLevelIndex);
            
            if (levelLoader == null) {
                Gdx.app.error("GameEngine", "LevelLoader is null, cannot load level data.");
                return;
            }

            // Загружаем данные уровня с помощью LevelLoader
            // Переносим эту логику в finishLoadingLevelAndTransition, так как LevelLoader работает с entitySystem,
            // который должен быть актуален после загрузки ресурсов в LoadingScreen.
            // levelLoader.loadLevel(currentLevelIndex, entitySystem);
            
            // Обновляем системы с данными нового уровня
            // EntitySystem и PhysicsSystem уже содержат сущности загруженного уровня через LevelLoader
            
            // Обновляем UIManager и BossFightLogic с новым игроком и данными уровня, если необходимо
            // В текущей архитектуре Player, UIManager и BossFightLogic создаются один раз.
            // Нужно убедиться, что LevelLoader правильно обновляет сущности в entitySystem и physicsSystem,
            // а UIManager и BossFightLogic работают с актуальными данными из этих систем.
            
            Gdx.app.log("GameEngine", "loadCurrentLevel called - data loading moved to finishLoadingLevelAndTransition.");
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Failed to load current level data in loadCurrentLevel", e);
        }
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
        if (assetManager != null) {
            assetManager.dispose();
        }
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
    public GameScreen getGameScreen() { return gameScreen; }
    public BossFightLogic getBossFightLogic() { return bossFightLogic; }
    public SpriteManager getSpriteManager() { return spriteManager; }
    public UIManager getUIManager() { return uiManager; }

    // Метод для загрузки следующего уровня и переключения экранов
    public void loadNextLevel(int levelNumber) {
        Gdx.app.log("GameEngine", "Attempting to load next level: " + levelNumber);
        this.nextLevelToLoad = levelNumber;
        loadingScreen.setNextLevelNumber(levelNumber);
        setScreen(loadingScreen);
    }

    public void transitionToGameScreen() {
        if (mainMenuScreen != null) {
            mainMenuScreen.hide(); // Вызываем hide(), который теперь останавливает музыку
            Gdx.app.log("GameEngine", "Main menu screen hidden and music stopped.");
        }
        if (gameScreen != null) {
            Gdx.app.log("GameEngine", "Transitioning to GameScreen for level: " + currentLevelIndex);
            setScreen(gameScreen);
            gameScreen.show(); // Убедимся, что метод show() вызывается для настройки InputProcessor и т.д.
        } else {
            Gdx.app.error("GameEngine", "GameScreen is null, cannot transition.");
        }
    }

    public void nextLevel() {
        // Увеличиваем номер уровня, но делаем это в finishLoadingLevelAndTransition
        // currentLevelIndex = (currentLevelIndex + 1) % 5;
        
        // Устанавливаем LoadingScreen и передаем ему номер следующего уровня
        loadingScreen.setNextLevelNumber(currentLevelIndex + 1);
        setScreen(loadingScreen);
        Gdx.app.log("GameEngine", "Switched to LoadingScreen for next level: " + (currentLevelIndex + 1));
    }

    // Новый метод для завершения загрузки уровня и перехода на GameScreen
    public void finishLoadingLevelAndTransition(int levelNumber, AssetManager assetManager) {
        Gdx.app.log("GameEngine", "Starting finishLoadingLevelAndTransition for level: " + levelNumber);
        try {
            // Останавливаем музыку предыдущего уровня и меню
            if (mainMenuScreen != null) {
                mainMenuScreen.hide();
                Gdx.app.log("GameEngine", "Main menu music stopped.");
            }
            
            if (gameScreen != null && gameScreen.getLevelMusic() != null) {
                gameScreen.getLevelMusic().stop();
                Gdx.app.log("GameEngine", "Previous level music stopped.");
            }

            // Устанавливаем текущий уровень
            this.currentLevelIndex = levelNumber;
            Gdx.app.log("GameEngine", "Current level index set to: " + currentLevelIndex);

            // Проверяем наличие необходимых компонентов
            if (levelLoader == null || entitySystem == null || uiManager == null) {
                Gdx.app.error("GameEngine", "Critical components are null: levelLoader=" + (levelLoader == null) + 
                    ", entitySystem=" + (entitySystem == null) + ", uiManager=" + (uiManager == null));
                throw new IllegalStateException("Required components are not initialized");
            }

            // Очищаем старые сущности
            entitySystem.clear();
            Gdx.app.log("GameEngine", "All entities cleared");

            // Создаем нового игрока для текущего уровня
            player = (Player) entityFactory.createPlayer(new Vector2(100, 200), null);
            entitySystem.addEntity(player);
            physicsSystem.setPlayer(player);
            // Обновляем игрока в ItemPickupSystem
            if (itemPickupSystem != null) {
                itemPickupSystem.setPlayer(player);
                Gdx.app.log("GameEngine", "Player updated in ItemPickupSystem");
            }
            // Обновляем игрока в BossSpawnManager
            if (bossSpawnManager != null) {
                bossSpawnManager.setPlayer(player);
                Gdx.app.log("GameEngine", "Player updated in BossSpawnManager");
            }
            Gdx.app.log("GameEngine", "New player created for level: " + currentLevelIndex);

            // Загружаем данные уровня
            levelLoader.loadLevel(currentLevelIndex, entitySystem, assetManager);
            Gdx.app.log("GameEngine", "Level data loaded for level: " + currentLevelIndex);

            // Создаем новый GameScreen
            gameScreen = new GameScreen(
                this,
                entitySystem,
                physicsSystem,
                levelLoader,
                uiManager,
                spriteRenderer,
                itemPickupSystem,
                bossSpawnManager,
                currentLevelIndex
            );
            gameScreen.setPlayer(player);
            Gdx.app.log("GameEngine", "New GameScreen created and player set");

            // Загружаем и устанавливаем музыку уровня
            String musicPath = "assets/musics/level" + currentLevelIndex + ".mp3";
            if (assetManager.isLoaded(musicPath)) {
                Music levelMusic = assetManager.get(musicPath, Music.class);
                gameScreen.setLevelMusic(levelMusic);
                Gdx.app.log("GameEngine", "Level music loaded and set: " + musicPath);
            } else {
                Gdx.app.error("GameEngine", "Level music not loaded: " + musicPath);
            }

            // Загружаем и устанавливаем фоновую текстуру
            String backgroundPath = "assets/background_level" + currentLevelIndex + ".png";
            if (currentLevelIndex == 2) {
                backgroundPath = "assets/background_level_bonus.png";
            }
            Gdx.app.log("GameEngine", "Loading background texture: " + backgroundPath);

            if (assetManager.isLoaded(backgroundPath)) {
                Texture backgroundTexture = assetManager.get(backgroundPath, Texture.class);
                gameScreen.setBackgroundTexture(backgroundTexture);
                Gdx.app.log("GameEngine", "Background texture set in GameScreen for level: " + currentLevelIndex);
            } else {
                Gdx.app.error("GameEngine", "Background texture not loaded: " + backgroundPath);
            }

            // Переходим на новый GameScreen
            setScreen(gameScreen);
            gameScreen.show();
            Gdx.app.log("GameEngine", "Switched to new GameScreen successfully");

        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Error during finishLoadingLevelAndTransition", e);
            showMainMenu();
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}