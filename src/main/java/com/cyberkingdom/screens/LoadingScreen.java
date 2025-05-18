package com.cyberkingdom.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cyberkingdom.gameengine.GameEngine;

public class LoadingScreen implements Screen {
    private final GameEngine game;
    private final SpriteBatch batch;
    private final Texture background;
    private final Array<Texture> kimchiFrames;
    private float stateTime;
    private float loadingProgress;
    private static final float ANIMATION_SPEED = 0.1f;
    private static final float LOADING_DURATION = 3.0f;
    private final Texture whitePixel;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private static final float WORLD_WIDTH = 1200;
    private static final float WORLD_HEIGHT = 800;
    private final BitmapFont font;
    private static final String LOADING_TEXT = "Олег сисадминит...";

    public LoadingScreen(GameEngine game) {
        this.game = game;
        this.batch = new SpriteBatch();
        
        // Инициализация камеры и viewport
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.viewport.apply();
        this.camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        this.camera.update();
        
        try {
            // Инициализация шрифта
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/arial.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 24;
            parameter.borderWidth = 2;
            parameter.borderColor = com.badlogic.gdx.graphics.Color.BLACK;
            parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<> ";
            this.font = generator.generateFont(parameter);
            generator.dispose();
            
            Gdx.app.log("LoadingScreen", "Loading background texture...");
            this.background = new Texture(Gdx.files.internal("assets/background_loading.png"));
            Gdx.app.log("LoadingScreen", "Background texture loaded successfully");
            
            this.kimchiFrames = new Array<>();
            
            // Создаем белую текстуру программно
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 1, 1, 1);
            pixmap.fill();
            this.whitePixel = new Texture(pixmap);
            
            // Загружаем кадры анимации таракана
            Gdx.app.log("LoadingScreen", "Loading kimchi frames...");
            for (int i = 1; i <= 3; i++) {
                String path = "assets/kimchi/" + i + "kimchi.png";
                Gdx.app.log("LoadingScreen", "Loading frame: " + path);
                kimchiFrames.add(new Texture(Gdx.files.internal(path)));
            }
            Gdx.app.log("LoadingScreen", "All kimchi frames loaded successfully");
            
            this.stateTime = 0;
            this.loadingProgress = 0;
        } catch (Exception e) {
            Gdx.app.error("LoadingScreen", "Error initializing loading screen", e);
            throw new RuntimeException("Failed to initialize loading screen", e);
        }
    }

    @Override
    public void render(float delta) {
        try {
            stateTime += delta;
            loadingProgress = Math.min(1.0f, stateTime / LOADING_DURATION);

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            // Отрисовка фона
            if (background != null) {
                batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            }

            // Отрисовка анимации загрузки
            if (kimchiFrames.size > 0) {
                int frameIndex = (int)(stateTime / ANIMATION_SPEED) % kimchiFrames.size;
                Texture currentFrame = kimchiFrames.get(frameIndex);
                float kimchiX = (WORLD_WIDTH - currentFrame.getWidth()) / 2;
                float kimchiY = (WORLD_HEIGHT - currentFrame.getHeight()) / 2;
                batch.draw(currentFrame, kimchiX, kimchiY);
                
                // Отрисовка текста под тараканом
                font.draw(batch, LOADING_TEXT, 
                    (WORLD_WIDTH - font.draw(batch, LOADING_TEXT, 0, 0, 0, 0, false).width) / 2,
                    kimchiY - 40);
                
                // Отрисовка полосы загрузки
                float barWidth = WORLD_WIDTH * 0.8f;
                float barHeight = 20;
                float barX = (WORLD_WIDTH - barWidth) / 2;
                float barY = kimchiY - 80;
                
                // Фон полосы загрузки
                batch.setColor(0.3f, 0.3f, 0.3f, 1);
                batch.draw(whitePixel, barX, barY, barWidth, barHeight);
                
                // Прогресс загрузки (фиолетовый цвет)
                batch.setColor(0.5f, 0.2f, 0.8f, 1);
                batch.draw(whitePixel, barX, barY, barWidth * loadingProgress, barHeight);
            } else {
                Gdx.app.error("LoadingScreen", "No kimchi frames loaded");
            }
            
            batch.setColor(1, 1, 1, 1);
            batch.end();

            // Переход к следующему экрану после завершения загрузки
            if (stateTime >= LOADING_DURATION) {
                Gdx.app.log("LoadingScreen", "Loading completed, transitioning to GameScreen");
                GameScreen gameScreen = game.getGameScreen();
                if (gameScreen != null) {
                    game.setScreen(gameScreen);
                } else {
                    Gdx.app.error("LoadingScreen", "GameScreen is null, cannot transition");
                }
            }
        } catch (Exception e) {
            Gdx.app.error("LoadingScreen", "Error in render method", e);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        try {
            batch.dispose();
            if (background != null) background.dispose();
            if (whitePixel != null) whitePixel.dispose();
            if (font != null) font.dispose();
            for (Texture frame : kimchiFrames) {
                if (frame != null) frame.dispose();
            }
        } catch (Exception e) {
            Gdx.app.error("LoadingScreen", "Error disposing resources", e);
        }
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
} 