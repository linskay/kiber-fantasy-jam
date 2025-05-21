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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.assets.AssetManager;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.entities.EntityFactory;
import com.cyberkingdom.rendering.SpriteRenderer;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class LoadingScreen implements Screen, Disposable {
    private final GameEngine game;
    private final SpriteBatch batch;
    private final Texture background;
    private final Array<Texture> kimchiFrames;
    private float stateTime;
    private float loadingProgress;
    private static final float ANIMATION_SPEED = 0.1f;
    private static final float LOADING_DURATION = 3.0f; // Это, возможно, больше не используется напрямую для длительности
    private final Texture whitePixel;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private static final float WORLD_WIDTH = 1200;
    private static final float WORLD_HEIGHT = 800;
    private final BitmapFont font;
    private static final String LOADING_TEXT = "Олег сисадминит...";
    private AssetManager assetManager;
    private int nextLevelNumber;
    private float minDisplayTimer = 0f; // Таймер для минимальной длительности показа экрана
    private static final float MIN_DISPLAY_DURATION = 2.0f; // Минимальная длительность показа экрана в секундах

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
            this.assetManager = new AssetManager();
        } catch (Exception e) {
            Gdx.app.error("LoadingScreen", "Error initializing loading screen", e);
            throw new RuntimeException("Failed to initialize loading screen", e);
        }
    }

    public void setNextLevelNumber(int levelNumber) {
        this.nextLevelNumber = levelNumber;
        Gdx.app.log("LoadingScreen", "Next level number set to: " + nextLevelNumber);
    }

    @Override
    public void show() {
        if (nextLevelNumber <= 0) {
            Gdx.app.error("LoadingScreen", "Invalid nextLevelNumber: " + nextLevelNumber);
            nextLevelNumber = 1; // Устанавливаем значение по умолчанию
        }
        
        Gdx.app.log("LoadingScreen", "Loading resources for level: " + nextLevelNumber);
        
        // Очищаем AssetManager от предыдущих ресурсов
        if (assetManager != null) {
            assetManager.clear();
            Gdx.app.log("LoadingScreen", "AssetManager cleared.");
        }

        // Загружаем фоновую текстуру уровня
        String backgroundPath = "assets/background_level" + nextLevelNumber + ".png";
        // Особый случай для уровня 2, если у него другой фон
        if (nextLevelNumber == 2) {
             backgroundPath = "assets/background_level_bonus.png";
        }
        assetManager.load(backgroundPath, Texture.class);
        Gdx.app.log("LoadingScreen", "Loading background texture: " + backgroundPath);

        // Загружаем музыку уровня
        String musicPath = "assets/musics/level" + nextLevelNumber + ".mp3";
        assetManager.load(musicPath, com.badlogic.gdx.audio.Music.class);
        Gdx.app.log("LoadingScreen", "Loading level music: " + musicPath);
        
        // Добавляем загрузку основных спрайтов и музыки, которые могут быть нужны для GameScreen
        assetManager.load("assets/entities/player.png", Texture.class);
        assetManager.load("assets/platform.png", Texture.class);
        assetManager.load("assets/Heart.png", Texture.class);
        assetManager.load("assets/ui/background.png", Texture.class);
        assetManager.load("assets/ui/healthbar_background.png", Texture.class);
        assetManager.load("assets/ui/healthbar_foreground.png", Texture.class);
        assetManager.load("assets/kimchi/1kimchi.png", Texture.class);
        assetManager.load("assets/kimchi/2kimchi.png", Texture.class);
        assetManager.load("assets/kimchi/3kimchi.png", Texture.class);
        assetManager.load("assets/entities/virus.png", Texture.class);
        assetManager.load("assets/background_level_bonus.png", Texture.class);
        assetManager.load("assets/entities/witch_vpn.png", Texture.class);
        assetManager.load("assets/entities/witchVPN_dialog.png", Texture.class);

        // Сброс таймера загрузки
        stateTime = 0;
        loadingProgress = 0;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновляем stateTime для анимации кимчи
        stateTime += delta;
        // Обновляем таймер минимальной длительности показа
        minDisplayTimer += delta;

        // Обновляем состояние загрузки
        boolean loadingFinished = assetManager.update();
        loadingProgress = assetManager.getProgress();

        // Проверяем, завершена ли загрузка и прошло ли минимальное время отображения
        if (loadingFinished && minDisplayTimer >= MIN_DISPLAY_DURATION) {
            Gdx.app.log("LoadingScreen", "Loading finished and minimum display time elapsed. Transitioning to level: " + nextLevelNumber);
            game.finishLoadingLevelAndTransition(nextLevelNumber, assetManager);
        }

        // Рисуем экран загрузки
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Рисуем фон
        if (background != null) {
            batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }

        // Расчет позиций для центрирования и вертикального расположения
        float totalHeight = 100 + 20 + font.getCapHeight() + 20 + 30; // Суммарная высота кимчи, отступа, текста, отступа, полосы
        float startY = (WORLD_HEIGHT - totalHeight) / 2 + totalHeight; // Начальная Y координата (сверху вниз)

        // Рисуем анимацию кимчи
        if (kimchiFrames != null && !kimchiFrames.isEmpty()) {
            TextureRegion currentKimchiFrame = new TextureRegion(kimchiFrames.get((int) (stateTime / ANIMATION_SPEED) % kimchiFrames.size));
            float kimchiSize = 100;
            float kimchiX = (WORLD_WIDTH - kimchiSize) / 2;
            float kimchiY = startY - kimchiSize; // Располагаем кимчи в верхней части блока
            batch.draw(currentKimchiFrame, kimchiX, kimchiY, kimchiSize, kimchiSize);
            startY = kimchiY - 20; // Обновляем startY для следующего элемента с отступом
        }

        // Рисуем текст загрузки
        if (font != null) {
            // Центрируем текст
            GlyphLayout layout = new GlyphLayout(font, LOADING_TEXT);
            float textX = (WORLD_WIDTH - layout.width) / 2;
            float textY = startY; // Располагаем текст ниже кимчи с отступом
            font.draw(batch, layout, textX, textY);
            startY = textY - font.getCapHeight() - 20; // Обновляем startY для следующего элемента с отступом
        }
        
        // Рисуем полосу загрузки
        // Позиция и размер полосы загрузки
        float progressBarWidth = 600;
        float progressBarHeight = 30;
        float progressBarX = (WORLD_WIDTH - progressBarWidth) / 2;
        float progressBarY = startY - progressBarHeight; // Располагаем полосу ниже текста с отступом

        // Рисуем фон полосы загрузки (серый)
        batch.setColor(0.2f, 0.2f, 0.2f, 1f); // Темно-серый цвет
        batch.draw(whitePixel, progressBarX, progressBarY, progressBarWidth, progressBarHeight);

        // Рисуем заполнение полосы загрузки (фиолетовый)
        batch.setColor(0.5f, 0f, 0.8f, 1f); // Фиолетовый цвет (примерные значения RGB)
        batch.draw(whitePixel, progressBarX, progressBarY, progressBarWidth * loadingProgress, progressBarHeight);

        // Сбрасываем цвет batch обратно на белый
        batch.setColor(1f, 1f, 1f, 1f);

        batch.end();
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
            assetManager.dispose();
        } catch (Exception e) {
            Gdx.app.error("LoadingScreen", "Error disposing resources", e);
        }
    }

    @Override
    public void hide() {
        // Очищаем экран загрузки при переходе на другой экран
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
} 