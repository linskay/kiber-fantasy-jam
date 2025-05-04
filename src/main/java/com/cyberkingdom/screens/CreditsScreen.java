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

public class CreditsScreen implements Screen {
    private final GameEngine engine;
    private final BitmapFont font;
    private final Texture background;
    private final Texture cursorTexture;
    private final Sound selectSound;

    private Stage stage;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private float scrollY = -500;
    private final String creditsText;
    private GlyphLayout layout;
    private final float MARGIN_SIDES = 100f;
    private final float SCROLL_SPEED = 100f;
    private final Color SHADOW_COLOR = new Color(0, 0, 0, 0.8f);
    private float cursorAlpha = 1f;
    private float cursorTimer = 0f;

    public CreditsScreen(GameEngine engine) {
        this.engine = engine;
        this.font = engine.getMenuFont();
        this.selectSound = engine.getSelectSound();
        this.background = engine.getMainMenuBackground();
        this.cursorTexture = engine.getCursorTexture();

        creditsText = "Мемокомпания \"No PHP-No problems\" представляет\n\n\n" +
                "Совместно с SibGameJamEntertainment\n\n\n" +
                "Игра каких-то багоделов…\n\n\n\n" +
                "В главных ролях:\n\n" +
                "Алина – ловит баги, но иногда путает их с фичами\n\n" +
                "Варя – сводит звук так, что даже Олежка говорит \"Ну это перебор\"\n\n" +
                "Даша – рисует так, что у дизайнеров кровь из глаз\n\n" +
                "Кристина – находит баги там, где их не должно быть\n\n" +
                "Ваня – воюет за кодстайл, но проигрывает\n\n\n\n" +
                "Спасибо за игру!";

        initialize();
    }

    private void initialize() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage();

        font.getData().setScale(0.75f);
        layout = new GlyphLayout(font, creditsText, Color.WHITE,
                Gdx.graphics.getWidth() - (int)(MARGIN_SIDES * 2), Align.center, true);

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
                50,
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
        update(delta);
        draw(delta);
    }

    private void update(float delta) {
        scrollY += SCROLL_SPEED * delta;
        if(scrollY > layout.height + 1000) scrollY = -500;

        cursorTimer += delta * 5;
        cursorAlpha = 0.4f + (MathUtils.sin(cursorTimer) + 1f) / 3f;
    }

    private void draw(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Тень текста
        font.setColor(SHADOW_COLOR);
        font.draw(batch, layout, MARGIN_SIDES + 2, scrollY - 2);

        // Основной текст
        font.setColor(1, 1, 1, 1);
        font.draw(batch, layout, MARGIN_SIDES, scrollY);

        // Курсор
        batch.setColor(1, 1, 1, cursorAlpha);
        batch.draw(cursorTexture,
                Gdx.input.getX(),
                Gdx.graphics.getHeight() - Gdx.input.getY() - cursorTexture.getHeight(),
                cursorTexture.getWidth(),
                cursorTexture.getHeight()
        );
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
        font.getData().setScale(0.75f * (width/1280f));
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        font.getData().setScale(1.0f);
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}