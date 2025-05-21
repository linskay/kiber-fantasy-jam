package com.cyberkingdom.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.cyberkingdom.gameengine.GameEngine;
import com.cyberkingdom.audio.MusicManager;

public class AchievementsScreen implements Screen {
    private final GameEngine engine;
    private final Stage stage;
    private final ShapeRenderer shapeRenderer;
    private final Texture cursorTexture;
    private final Texture background;
    private final Sound selectSound;

    private final Color NEON_CORE = new Color(0.1f, 0.9f, 1f, 1f);
    private final Color NEON_GLOW = new Color(0.3f, 0.6f, 1f, 0.3f);
    private final float CELL_PADDING = 40f;
    private final int ROWS = 2;
    private final int COLS = 3;
    private final Rectangle[][] grid = new Rectangle[ROWS][COLS];
    private float glowTimer;

    public AchievementsScreen(GameEngine engine) {
        this.engine = engine;
        this.stage = new Stage();
        this.shapeRenderer = new ShapeRenderer();
        this.cursorTexture = engine.getCursorTexture();
        this.background = engine.getMainMenuBackground();
        this.selectSound = engine.getSelectSound();

        initialize();
    }

    private void initialize() {
        setupUI();
        calculateGrid();
    }

    private void setupUI() {
        if (background != null) {
            stage.addActor(new Image(background));
        } else {
            // Создаем заглушку для фона
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.DARK_GRAY);
            pixmap.fill();
            Texture fallbackTexture = new Texture(pixmap);
            pixmap.dispose();
            stage.addActor(new Image(fallbackTexture));
        }
        createBackButton();
    }

    private void createBackButton() {
        Label backLabel = new Label("[НАЗАД]", new Label.LabelStyle(engine.getMenuFont(), Color.WHITE));

        // Центрирование кнопки
        backLabel.setSize(200, 60);
        backLabel.setPosition(
                Gdx.graphics.getWidth() / 2f,
                50,
                Align.center | Align.bottom
        );

        // Обработка кликов
        backLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goBack();
            }
        });

        stage.addActor(backLabel);
    }

    private void calculateGrid() {
        float gridWidth = Gdx.graphics.getWidth() * 0.7f;
        float gridHeight = Gdx.graphics.getHeight() * 0.5f;
        float startX = (Gdx.graphics.getWidth() - gridWidth) / 2;
        float startY = (Gdx.graphics.getHeight() - gridHeight) / 2 + 120;

        float cellWidth = (gridWidth - (COLS-1)*CELL_PADDING) / COLS;
        float cellHeight = (gridHeight - (ROWS-1)*CELL_PADDING) / ROWS;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = new Rectangle(
                        startX + col*(cellWidth + CELL_PADDING),
                        startY + (ROWS-1-row)*(cellHeight + CELL_PADDING),
                        cellWidth,
                        cellHeight
                );
            }
        }
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(
                new InputAdapter() { // Клавиши напрямую
                    @Override
                    public boolean keyDown(int keycode) {
                        if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ESCAPE) {
                            goBack();
                            return true;
                        }
                        return false;
                    }
                },
                stage // Клики через Stage, поставим последним
        ));
    }

    private void drawNeonGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Rectangle cell = grid[row][col];

                // Основной контур
                shapeRenderer.setColor(NEON_CORE);
                shapeRenderer.rect(cell.x, cell.y, cell.width, cell.height);

                // Свечение
                float glow = 0.5f + MathUtils.sin(glowTimer * 3) * 0.5f;
                for (int i = 0; i < 3; i++) {
                    float spread = i * 4f;
                    shapeRenderer.setColor(NEON_GLOW.r, NEON_GLOW.g, NEON_GLOW.b, glow * (1 - i*0.2f));
                    shapeRenderer.rect(
                            cell.x - spread,
                            cell.y - spread,
                            cell.width + spread*2,
                            cell.height + spread*2
                    );
                }
            }
        }
        shapeRenderer.end();
    }

    private void drawCursor() {
        if (cursorTexture != null) {
            stage.getBatch().begin();
            stage.getBatch().setColor(1, 1, 1, 0.8f);
            stage.getBatch().draw(
                    cursorTexture,
                    Gdx.input.getX() - 16,
                    Gdx.graphics.getHeight() - Gdx.input.getY() - 16,
                    32,
                    32
            );
            stage.getBatch().end();
        }
    }

    private void goBack() {
        if (selectSound != null) {
            selectSound.play(0.7f);
        }
        engine.showMainMenu();
    }

    @Override
    public void render(float delta) {
        glowTimer += delta * 2f;

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        drawNeonGrid();
        drawCursor();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        calculateGrid();
    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public void show() {
        MusicManager.play("assets/musics/menu.mp3", true);
        setupInput(); // Вызываем setupInput() здесь
        // ... остальной код show ...
    }

    @Override
    public void hide() {}

    @Override public void pause() {}
    @Override public void resume() {}
}