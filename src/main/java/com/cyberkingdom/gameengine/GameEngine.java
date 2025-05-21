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
import com.badlogic.gdx.graphics.Color;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.audio.MusicManager;

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

    private MusicManager musicManager;
    private BitmapFont storyCreditsFont;

    public GameEngine() {
        this.assetManager = new AssetManager();
        // ... existing code ...
    }

    @Override
    public void create() {
        try {
            initializeSystems();
            initializeScreens();
            showMainMenu();
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Failed to initialize game", e);
            throw new RuntimeException("Game initialization failed", e);
        }
    }

    private void initializeSystems() {
        Gdx.app.log("GameEngine", "Initializing systems...");
        
        // Инициализация SpriteBatch
        batch = new SpriteBatch();
        Gdx.app.log("GameEngine", "SpriteBatch initialized");
        
        // Инициализация SpriteManager
        spriteManager = new SpriteManager();
        Gdx.app.log("GameEngine", "SpriteManager initialized");
        
        // Инициализация SpriteRenderer
        spriteRenderer = new SpriteRenderer(batch, spriteManager);
        Gdx.app.log("GameEngine", "SpriteRenderer initialized");
        
        // Инициализация EntitySystem
        entitySystem = new com.cyberkingdom.entities.EntitySystem();
        Gdx.app.log("GameEngine", "EntitySystem initialized: " + (entitySystem != null ? "not null" : "null"));
        
        // Инициализация PhysicsSystem
        physicsSystem = new PhysicsSystem(entitySystem);
        Gdx.app.log("GameEngine", "PhysicsSystem initialized");
        
        // Инициализация EntityFactory (требует EntitySystem и PhysicsSystem)
        entityFactory = new EntityFactory(spriteManager, physicsSystem);
        Gdx.app.log("GameEngine", "EntityFactory initialized");

        // Устанавливаем EntityFactory в EntitySystem
        if (entitySystem != null) {
            entitySystem.setFactory(entityFactory);
            Gdx.app.log("GameEngine", "EntityFactory set in EntitySystem");
        } else {
            Gdx.app.error("GameEngine", "EntitySystem is null, cannot set EntityFactory");
        }
        
        // Инициализация шрифта меню
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<> ";
        menuFont = generator.generateFont(parameter);
        Gdx.app.log("GameEngine", "Menu font initialized");

        // Инициализация шрифта для экранов истории и титров (без обводки)
        FreeTypeFontGenerator.FreeTypeFontParameter storyCreditsParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        storyCreditsParameter.size = 32;
        storyCreditsParameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<> ";
        storyCreditsFont = generator.generateFont(storyCreditsParameter);
        // Явно устанавливаем цвет для нового шрифта (например, синий), чтобы проверить, применяется ли он
        storyCreditsFont.setColor(0.0f, 0.0f, 1.0f, 1.0f); // Синий цвет
        Gdx.app.log("GameEngine", "Story/Credits font initialized");

        generator.dispose();
        
        // Звук выбора меню
        try {
            selectSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/select.mp3"));
            Gdx.app.log("GameEngine", "Select sound loaded.");
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Failed to load select sound", e);
        }

        // Загрузка текстур меню
        try {
            mainMenuBackground = new Texture(Gdx.files.internal("assets/menu.png"));
            Gdx.app.log("GameEngine", "Main menu background loaded.");
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Failed to load main menu background", e);
        }
        try {
            cursorTexture = new Texture(Gdx.files.internal("assets/kursor.png"));
            Gdx.app.log("GameEngine", "Cursor texture loaded.");
        } catch (Exception e) {
            Gdx.app.error("GameEngine", "Failed to load cursor texture", e);
        }

        // GameScreen, Player, LevelLoader, UIManager, ItemPickupSystem, BossSpawnManager будут созданы позже в finishLoadingLevelAndTransition
    }

    private void initializeScreens() {
        mainMenuScreen = new MainMenuScreen(this);
        storyScreen = new StoryScreen(this, storyCreditsFont);
        loadingScreen = new LoadingScreen(this);
        creditsScreen = new CreditsScreen(this, storyCreditsFont);
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
            storyScreen = new StoryScreen(this, storyCreditsFont);
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
        setScreen(new CreditsScreen(this, storyCreditsFont));
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
        storyCreditsFont.dispose();
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
                gameScreen.getLevelMusic().dispose(); // Освобождаем ресурсы музыки предыдущего уровня
                Gdx.app.log("GameEngine", "Previous level music stopped and disposed.");
            }

            // Устанавливаем текущий уровень
            this.currentLevelIndex = levelNumber;
            Gdx.app.log("GameEngine", "Current level index set to: " + currentLevelIndex);

            // Очищаем старые сущности перед загрузкой нового уровня
            if (entitySystem != null) {
                entitySystem.clear();
                Gdx.app.log("GameEngine", "All entities cleared");
            }
            
            // Создаем или обновляем LevelLoader
            levelLoader = new LevelLoader(spriteManager, entitySystem, physicsSystem, entityFactory, currentLevelIndex);
            Gdx.app.log("GameEngine", "LevelLoader created/updated for level: " + currentLevelIndex);
            
            // Создаем нового игрока для текущего уровня
            // Создаем временного игрока для инициализации UIManager
            player = (Player) entityFactory.createPlayer(new Vector2(100, 200), null); // Передаем null GameScreen пока
            entitySystem.addEntity(player);
            physicsSystem.setPlayer(player);
            Gdx.app.log("GameEngine", "New player created and added to systems for level: " + currentLevelIndex);

            // Создаем или обновляем ItemPickupSystem (требует Player и LevelLoader)
            itemPickupSystem = new ItemPickupSystem(entitySystem, player, levelLoader);
            Gdx.app.log("GameEngine", "ItemPickupSystem created/updated");

            // Создаем или обновляем BossSpawnManager (требует EntitySystem, EntityFactory, PhysicsSystem)
            bossSpawnManager = new BossSpawnManager(entitySystem, entityFactory, physicsSystem);
            if (bossSpawnManager != null) { // Обновляем игрока в BossSpawnManager
                bossSpawnManager.setPlayer(player);
                 Gdx.app.log("GameEngine", "Player set in BossSpawnManager");
            }
             Gdx.app.log("GameEngine", "BossSpawnManager created/updated");

            // Создаем или обновляем UIManager (требует много зависимостей, включая Player)
            uiManager = new UIManager(batch, spriteRenderer, player, null, this); // Передаем null пока
            Gdx.app.log("GameEngine", "UIManager created/updated");

            // Передаем шрифт в Player
            if (uiManager.getFont() != null) {
                player.setFont(uiManager.getFont());
                Gdx.app.log("GameEngine", "Font set for player");
            } else {
                Gdx.app.error("GameEngine", "Failed to get font from UIManager");
            }

            // Загружаем данные уровня
            levelLoader.loadLevel(currentLevelIndex, entitySystem, assetManager);
            Gdx.app.log("GameEngine", "Level data loaded for level: " + currentLevelIndex);

            // Создаем новый GameScreen с нужными зависимостями
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
            // Теперь устанавливаем правильную ссылку на GameScreen в Player
            if (player != null) {
                 player.setGameScreen(gameScreen);
                 Gdx.app.log("GameEngine", "Correct GameScreen reference set in Player");
            }

            // Передаем InputHandler из GameScreen в UIManager
            if (uiManager != null && gameScreen != null && gameScreen.getInputHandler() != null) {
                uiManager.setInputHandler(gameScreen.getInputHandler());
                 Gdx.app.log("GameEngine", "InputHandler set in UIManager");
            } else {
                Gdx.app.error("GameEngine", "Failed to set InputHandler in UIManager");
            }

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
            if (currentLevelIndex == 2) { // Пример для уровня 2
                backgroundPath = "assets/background_level_bonus.png";
            }
            Gdx.app.log("GameEngine", "Loading background texture: " + backgroundPath);

            if (assetManager.isLoaded(backgroundPath)) {
                Texture backgroundTexture = assetManager.get(backgroundPath, Texture.class);
                gameScreen.setBackgroundTexture(backgroundTexture); // Устанавливаем текстуру через setBackgroundTexture
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
            // В случае ошибки, возможно, стоит вернуться в главное меню или показать сообщение об ошибке
            showMainMenu(); // Возвращаемся в главное меню при ошибке загрузки уровня
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }
}