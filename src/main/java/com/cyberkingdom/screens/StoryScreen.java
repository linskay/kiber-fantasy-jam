package com.cyberkingdom.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.cyberkingdom.gameengine.GameEngine;
import com.cyberkingdom.audio.MusicManager;

public class StoryScreen implements Screen {
    private final GameEngine engine;
    private final BitmapFont font;
    private final Texture background;
    private final Texture cursorTexture;
    private final Sound selectSound;

    private Stage stage;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private float scrollY = 0;
    private final String storyText;
    private GlyphLayout layout;
    private final float MARGIN_TOP = 120f;
    private final float MARGIN_BOTTOM = 120f;
    private final float BUTTON_HEIGHT = 80f;
    private final float MARGIN_SIDES = 100f;
    private final float SCROLL_SPEED = 250f;
    private final Color SHADOW_COLOR = new Color(0, 0, 0, 0.7f);
    private float maxScroll;
    private final float ARROW_OFFSET = 70f;
    private float cursorAlpha = 1f;
    private float cursorTimer = 0f;
    private final float EXTRA_SCROLL_SPACE = 100f;

    public StoryScreen(GameEngine engine, BitmapFont font) {
        this.engine = engine;
        this.font = font;
        this.selectSound = engine.getSelectSound();
        this.background = engine.getMainMenuBackground();
        this.cursorTexture = engine.getCursorTexture();

        Gdx.app.log("StoryScreen", "Font in constructor before initialize(): " + (this.font != null ? "not null" : "null"));
        storyText = "Ну вот и сказочки конец, а кто спУшИл... Никто не слушал? Галя, отмена, давай по новой!\n\n" +
                "Главный герой Олег(сисадмин так-то), жил себе, не тужил в своей скромной, но уютной халупке, " +
                "обнимаясь со своим верным другом компьютером. Каждый день он запускал его, радуясь, " +
                "что все работает исправно и не дает сбоев.\n\n" +
                "Но вот однажды, когда Олежка в очередной раз залез на просторы программ для обновления драйверов, " +
                "экран замерцал синими цветами, а потом посреди появилась надпись \"Ошибка 404: Сказка не найдена\". " +
                "В одночасье парня затянуло внутрь, где и начались его невероятнейшие приключения.\n\n" +
                "Нашему Олегу предстоит пройти по ныне неизведанному миру, полному багов и глюков. " +
                "Пять уровней, пять боссов и много пасхалок, которые помогут ему победить всех и спасти сказочный мир. " +
                "А получится ли это у него мы с вами сейчас и узнаем.\n\n" +
                "Управление:\n\n" +
                "Прямо пойдешь (W) - глюк обнаружишь;\n" +
                "Налево пойдешь (A) - код сломаешь;\n" +
                "Назад пойдешь (S) - опасности минуешь;\n" +
                "Направо пойдешь (D) - баг поймаешь;\n\n" +
                "Прыгнешь (SPACE или W) - преграды позади оставишь;";
        initialize();
    }

    private void initialize() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage();

        font.getData().setScale(0.75f);
        layout = new GlyphLayout(font, storyText, Color.WHITE,
                Gdx.graphics.getWidth() - (int)(MARGIN_SIDES * 2), Align.center, true);

        float availableHeight = Gdx.graphics.getHeight() - MARGIN_TOP - BUTTON_HEIGHT + EXTRA_SCROLL_SPACE;
        maxScroll = Math.min(-(layout.height - availableHeight), EXTRA_SCROLL_SPACE);

        createBackButton();
        setupInput();
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(
                new InputAdapter() {
                    @Override
                    public boolean keyDown(int keycode) {
                        if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ESCAPE) {
                            goBack();
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean scrolled(float amountX, float amountY) {
                        scrollY -= amountY * SCROLL_SPEED * 2f;
                        scrollY = MathUtils.clamp(scrollY, maxScroll - EXTRA_SCROLL_SPACE, EXTRA_SCROLL_SPACE);
                        return true;
                    }
                },
                stage
        ));
    }

    private void createBackButton() {
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label backLabel = new Label("[НАЗАД]", style);

        backLabel.setSize(200, 60);
        backLabel.setPosition(
                Gdx.graphics.getWidth()/2f,
                BUTTON_HEIGHT,
                Align.center | Align.bottom
        );

        backLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goBack();
            }
        });

        stage.addActor(backLabel);
    }

    private void goBack() {
        selectSound.play(0.7f);
        engine.showMainMenu();
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        updateCursor(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Затемняем фон
        batch.setColor(0.5f, 0.5f, 0.5f, 1.0f); // Устанавливаем серый цвет (можно настроить)
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Сбрасываем цвет батча на белый для остальной отрисовки
        batch.setColor(Color.WHITE);

        drawTextWithShadow();
        drawCursor();
        drawScrollArrows();
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void drawTextWithShadow() {
        float baseY = Gdx.graphics.getHeight() - MARGIN_TOP + scrollY + EXTRA_SCROLL_SPACE;

        // Убедимся, что цвет батча установлен в белый перед отрисовкой текста
        batch.setColor(Color.WHITE);
        
        // Отрисовка тени (черный цвет)
        font.setColor(0, 0, 0, 0.7f);
        font.draw(batch, layout, MARGIN_SIDES + 2, baseY - 2);
        
        // Отрисовка основного текста ярко-желтым цветом
        font.setColor(1.0f, 1.0f, 0.0f, 1.0f);
        font.draw(batch, layout, MARGIN_SIDES, baseY);
        
        // Важно: сбрасываем цвет шрифта на белый после отрисовки текста,
        // чтобы он не влиял на отрисовку других элементов (например, Label в Stage)
        font.setColor(Color.WHITE);
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) scrollY -= SCROLL_SPEED * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) scrollY += SCROLL_SPEED * delta;
        scrollY = MathUtils.clamp(scrollY, maxScroll - EXTRA_SCROLL_SPACE, EXTRA_SCROLL_SPACE);
    }

    private void updateCursor(float delta) {
        cursorTimer += delta * 5;
        cursorAlpha = 0.4f + (MathUtils.sin(cursorTimer) + 1f) / 3f;
    }

    private void drawCursor() {
        batch.setColor(1, 1, 1, cursorAlpha);
        batch.draw(cursorTexture,
                Gdx.input.getX(),
                Gdx.graphics.getHeight() - Gdx.input.getY() - cursorTexture.getHeight(),
                cursorTexture.getWidth(),
                cursorTexture.getHeight());
        batch.setColor(Color.WHITE);
    }

    private void drawScrollArrows() {
        float rightEdge = Gdx.graphics.getWidth() - ARROW_OFFSET;
        batch.setColor(1, 1, 1, 0.9f);

        if (scrollY > (maxScroll - EXTRA_SCROLL_SPACE)) {
            font.draw(batch, "▲", rightEdge - 25, Gdx.graphics.getHeight() - ARROW_OFFSET);
        }
        if (scrollY < EXTRA_SCROLL_SPACE) {
            font.draw(batch, "▼", rightEdge - 25, ARROW_OFFSET + 40);
        }
        batch.setColor(Color.WHITE);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
        font.getData().setScale(0.75f * (width/1280f));
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.getData().setScale(1.0f);
    }

    @Override
    public void show() {
        if (batch == null) {
            batch = new SpriteBatch();
        }
        MusicManager.play("assets/musics/menu.mp3", true);
        setupInput();
        Gdx.app.log("StoryScreen", "StoryScreen shown, SpriteBatch created.");
    }

    @Override
    public void hide() {
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        Gdx.input.setInputProcessor(null);
        Gdx.app.log("StoryScreen", "StoryScreen hidden, SpriteBatch disposed, input processor set to null");
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
}