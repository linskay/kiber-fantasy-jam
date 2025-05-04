package com.cyberkingdom.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.cyberkingdom.gameengine.GameEngine;

public class MainMenuScreen implements Screen {
    private final GameEngine engine;
    private Stage stage;
    private BitmapFont titleFont;
    private BitmapFont menuFont;
    private BitmapFont descFont;
    private MenuItem[] menuItems;
    private int selectedIndex = 0;
    private Texture cursorTexture;
    private Texture backgroundTexture;
    private float glowTimer;
    private ShapeRenderer shapeRenderer;
    private Music menuMusic;
    private Sound selectSound;

    private float descAlpha = 0;
    private float targetScrollX = 0;
    private float currentScrollX = 0;
    private final float ITEM_PADDING = 60;
    private final float GLOW_SPEED = 3f;
    private final float SCROLL_SPEED = 300f;

    private static final Color DESC_COLOR = Color.WHITE;
    private static final Color DESC_BORDER = Color.BLACK;
    private static final float DESC_Y_OFFSET = -20f;

    private static final Color NEON_CORE = new Color(0.1f, 0.9f, 1f, 1f);
    private static final Color NEON_GLOW = new Color(0.3f, 0.6f, 1f, 0.3f);
    private static final int GLOW_LAYERS = 5;
    private static final float GLOW_SPREAD = 8f;

    private class MenuItem {
        String title;
        String description;
        Rectangle bounds;

        public MenuItem(String title, String description) {
            this.title = title;
            this.description = description;
            this.bounds = new Rectangle();
        }
    }

    public MainMenuScreen(GameEngine engine) {
        this.engine = engine;
        this.stage = new Stage(new ScreenViewport());
        this.shapeRenderer = new ShapeRenderer();
        this.shapeRenderer.setAutoShapeType(true);

        loadResources();
        initializeMenuItems();
        initializeFonts();
        setupUI();
        setupAudio();
    }

    private void loadResources() {
        try {
            cursorTexture = new Texture(Gdx.files.internal("assets/kursor.png"));
            backgroundTexture = new Texture(Gdx.files.internal("assets/menu.png"));
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/musics/menu_levels.mp3"));
            selectSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/select.mp3"));
        } catch (Exception e) {
            Gdx.app.error("MainMenu", "Ошибка загрузки ресурсов", e);
            createFallbackResources();
        }
    }

    private void createFallbackResources() {
        Pixmap pm = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        cursorTexture = new Texture(pm);
        pm.dispose();
    }

    private void setupAudio() {
        Gdx.input.setInputProcessor(stage);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
        if (menuMusic != null) {
            menuMusic.setLooping(true);
            menuMusic.setVolume(0.5f);
            menuMusic.play();
        }
    }

    private void initializeMenuItems() {
        menuItems = new MenuItem[]{
                new MenuItem("Сисадминить", "Начать цифровое приключение"),
                new MenuItem("Шо-то типа ачивок", "Посмотреть ваши достижения"),
                new MenuItem("То, что обычно не читают", "Информация об игре"),
                new MenuItem("Для вас старались", "Авторы и благодарности"),
                new MenuItem("Да ну это все...", "Выйти из матрицы")
        };
    }

    private void initializeFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/arial.ttf")
        );
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<>";

        FreeTypeFontGenerator.FreeTypeFontParameter menuParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        menuParams.size = 32;
        menuParams.color = Color.WHITE;
        menuParams.borderWidth = 3;
        menuParams.borderColor = NEON_CORE;
        menuParams.characters = characters;
        menuFont = generator.generateFont(menuParams);

        FreeTypeFontGenerator.FreeTypeFontParameter descParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        descParams.size = 20;
        descParams.color = DESC_COLOR;
        descParams.borderWidth = 1;
        descParams.borderColor = DESC_BORDER;
        descParams.characters = characters;
        descFont = generator.generateFont(descParams);

        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 64;
        titleParams.borderWidth = 5;
        titleParams.color = Color.WHITE;
        titleParams.borderColor = NEON_CORE;
        titleParams.characters = characters;
        titleFont = generator.generateFont(titleParams);

        generator.dispose();
    }

    private void setupUI() {
        stage.addActor(new Image(backgroundTexture));

        Label titleLabel = new Label("", new Label.LabelStyle(titleFont, Color.WHITE));
        titleLabel.setPosition(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight() - 150, Align.center);
        stage.addActor(titleLabel);

        float startY = Gdx.graphics.getHeight()/2f;
        for(int i = 0; i < menuItems.length; i++) {
            Label itemLabel = new Label(menuItems[i].title, new Label.LabelStyle(menuFont, Color.WHITE));
            itemLabel.setPosition(Gdx.graphics.getWidth()/2f, startY - i*ITEM_PADDING, Align.center);
            menuItems[i].bounds.set(
                    Gdx.graphics.getWidth()/2f - 280,
                    startY - i*ITEM_PADDING - 30,
                    560,
                    60
            );
            stage.addActor(itemLabel);
        }
    }

    private void update(float delta) {
        glowTimer += delta * GLOW_SPEED;
        handleInput();
        updateHover();
        updateDescription(delta);
    }

    private void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.W)) selectedIndex--;
        if(Gdx.input.isKeyJustPressed(Input.Keys.S)) selectedIndex++;
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if(selectSound != null) selectSound.play(0.7f);
            handleSelection();
        }
    }

    private void updateHover() {
        Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        boolean hoverFound = false;

        for(int i = 0; i < menuItems.length; i++) {
            if(menuItems[i].bounds.contains(mousePos)) {
                selectedIndex = i;
                hoverFound = true;
                if(Gdx.input.justTouched()) handleSelection();
            }
        }

        if(!hoverFound) selectedIndex = MathUtils.clamp(selectedIndex, 0, menuItems.length - 1);
    }

    private void updateDescription(float delta) {
        float targetAlpha = menuItems[selectedIndex].description.isEmpty() ? 0 : 1;
        descAlpha = MathUtils.lerp(descAlpha, targetAlpha, delta * 10);

        if(descAlpha > 0.1f) {
            float cursorX = Gdx.input.getX();
            GlyphLayout layout = new GlyphLayout(descFont, menuItems[selectedIndex].description);

            targetScrollX = MathUtils.clamp(
                    cursorX - layout.width / 2,
                    20,
                    Gdx.graphics.getWidth() - layout.width - 20
            );

            currentScrollX = MathUtils.lerp(currentScrollX, targetScrollX, delta * 8);
        } else {
            currentScrollX = Gdx.graphics.getWidth();
        }
    }

    private void drawNeonFrame(Rectangle bounds) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(NEON_CORE);
        shapeRenderer.rect(
                bounds.x + 2,
                bounds.y + 2,
                bounds.width - 4,
                bounds.height - 4
        );

        for(int i = 1; i <= GLOW_LAYERS; i++) {
            float alpha = NEON_GLOW.a * (1 - (float)i/GLOW_LAYERS);
            float spread = GLOW_SPREAD * i;
            float pulse = 0.5f + MathUtils.sin(glowTimer * 2) * 0.5f;

            shapeRenderer.setColor(NEON_GLOW.r, NEON_GLOW.g, NEON_GLOW.b, alpha * pulse);
            shapeRenderer.rect(bounds.x - spread, bounds.y - spread, bounds.width + spread*2, bounds.height + spread*2);
        }
        shapeRenderer.end();
    }

    private void drawScrollingText() {
        if(descAlpha > 0.01f) {
            String text = menuItems[selectedIndex].description;
            GlyphLayout layout = new GlyphLayout(descFont, text);
            float yPos = menuItems[selectedIndex].bounds.y + DESC_Y_OFFSET;

            Batch batch = stage.getBatch();
            batch.begin();

            // Неоновое свечение
            descFont.setColor(0.1f, 0.9f, 1f, descAlpha * 0.4f);
            for(int i = -2; i <= 2; i++) {
                descFont.draw(batch, text, currentScrollX + i, yPos + i);
            }

            // Основной текст
            descFont.setColor(1, 1, 1, descAlpha);
            descFont.draw(batch, text, currentScrollX, yPos);

            batch.end();
        }
    }

    private void drawCursor() {
        Batch batch = stage.getBatch();
        batch.begin();
        batch.setColor(NEON_CORE);
        batch.draw(cursorTexture,
                Gdx.input.getX() - 16,
                Gdx.graphics.getHeight() - Gdx.input.getY() - 16,
                32, 32
        );
        batch.end();
    }

    private void handleSelection() {
        selectSound.play(0.7f);
        switch(selectedIndex) {
            case 0:
                engine.startGame();
                break;
            case 1:
                engine.showAchievementsScreen();
                break;
            case 2:
                engine.showStoryScreen();
                break;
            case 3:
                engine.showCreditsScreen();
                break;
            case 4:
                Gdx.app.exit();
                break;
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        drawNeonFrame(menuItems[selectedIndex].bounds);
        drawScrollingText();
        drawCursor();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        titleFont.dispose();
        menuFont.dispose();
        descFont.dispose();
        if(cursorTexture != null) cursorTexture.dispose();
        if(backgroundTexture != null) backgroundTexture.dispose();
        shapeRenderer.dispose();
        if(menuMusic != null) menuMusic.dispose();
        if(selectSound != null) selectSound.dispose();
    }

    public boolean isMusicPlaying() {
        return menuMusic != null && menuMusic.isPlaying();
    }

    public void pauseMusic() {
        if (menuMusic != null) menuMusic.pause();
    }

    public BitmapFont getMenuFont() {
        return menuFont;
    }

    public Sound getSelectSound() {
        return selectSound;
    }

    @Override
    public void hide() {
        // Не останавливаем музыку при переходе
    }

    public void resumeMusic() {
        if (menuMusic != null && !menuMusic.isPlaying()) {
            menuMusic.play();
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void show() {}
    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }

    public Texture getCursorTexture() {
        return cursorTexture;
    }
}