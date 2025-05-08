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

public class GameScreen implements Screen {
    private final EntitySystem entitySystem;
    private final PhysicsSystem physicsSystem;
    private final LevelLoader levelLoader;
    private final SpriteRenderer spriteRenderer;
    private final UIManager uiManager;
    private final BossFightLogic bossFightLogic;
    private final OrthographicCamera camera;
    private final ExtendViewport viewport;
    private BitmapFont font;
    private static final float LEVEL_WIDTH = 1200f;
    private static final float LEVEL_HEIGHT = 800f;
    private Texture background;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch gameBatch;

    public GameScreen(EntitySystem entitySystem, PhysicsSystem physicsSystem, LevelLoader levelLoader, 
                     SpriteRenderer spriteRenderer, UIManager uiManager, BossFightLogic bossFightLogic) {
        this.entitySystem = entitySystem;
        this.physicsSystem = physicsSystem;
        this.levelLoader = levelLoader;
        this.spriteRenderer = spriteRenderer;
        this.uiManager = uiManager;
        this.bossFightLogic = bossFightLogic;
        this.gameBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        
        // Инициализация камеры и вьюпорта
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(LEVEL_WIDTH, LEVEL_HEIGHT, camera);
        viewport.apply();
        
        // Инициализация шрифта
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<> ";
        font = generator.generateFont(parameter);
        generator.dispose();
        
        // Загрузка фона
        background = new Texture(Gdx.files.internal("assets/background_level1.png"));
        
        Gdx.app.log("GameScreen", "Initialized successfully");
    }

    @Override
    public void render(float delta) {
        // Обновление
        physicsSystem.update(delta);
        if (bossFightLogic != null) {
            bossFightLogic.update(delta);
        }
        
        // Очистка экрана
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Обновление камеры
        camera.update();
        spriteRenderer.getBatch().setProjectionMatrix(camera.combined);
        
        // Рендеринг
        spriteRenderer.getBatch().begin();
        
        // Рендеринг фона
        if (background != null) {
            spriteRenderer.getBatch().draw(background, 0, 0, LEVEL_WIDTH, LEVEL_HEIGHT);
        }
        
        // Рендеринг игровых объектов
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity.isActive() && !(entity instanceof Player)) {
                spriteRenderer.render(entity);
            }
        }
        
        // Рендеринг игрока поверх остальных объектов
        Player player = physicsSystem.getPlayer();
        if (player != null && player.isActive()) {
            spriteRenderer.render(player);
        }
        
        // Рендеринг UI
        if (uiManager != null) {
            uiManager.render(physicsSystem.getPlayer());
        }
        
        // Рендеринг счетчика монет
        if (player != null) {
            font.setColor(Color.YELLOW);
            font.draw(spriteRenderer.getBatch(), 
                     "Монеты: " + player.getCoins(), 
                     20, 50);
        }
        
        spriteRenderer.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        if (background != null) {
            background.dispose();
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
    }

    public void updateCoinCount(int coins) {
        // Обновляем счетчик монет в UI
        if (uiManager != null) {
            uiManager.updateCoinCount(coins);
        }
    }
}