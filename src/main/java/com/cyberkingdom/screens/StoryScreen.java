package com.cyberkingdom.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.cyberkingdom.gameengine.GameEngine;

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
    private float textHeight;
    private final float MARGIN = 150f;
    private final float SCROLL_SPEED = 200f;
    private final Color SHADOW_COLOR = new Color(0, 0, 0, 0.7f);

    public StoryScreen(GameEngine engine) {
        this.engine = engine;
        this.font = engine.getMenuFont();
        this.selectSound = engine.getSelectSound();
        this.background = engine.getMainMenuBackground();
        this.cursorTexture = engine.getCursorTexture();

        storyText = "Ну вот и сказочки конец, а кто слушал... Никто не слушал? Галя, отмена, давай по новой!\n\n" +
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
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
        stage = new Stage();

        layout = new GlyphLayout(font, storyText, Color.WHITE,
                Gdx.graphics.getWidth() - (int)(MARGIN * 2), Align.center, true);
        textHeight = layout.height;

        createBackButton();
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                scrollY += amountY * SCROLL_SPEED;
                return true;
            }
        }));
    }

    private void createBackButton() {
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label backLabel = new Label("[НАЗАД]", style);
        backLabel.setPosition(
                Gdx.graphics.getWidth()/2f,
                80,
                Align.center
        );

        backLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectSound.play(0.7f);
                engine.showMainMenu();
            }
        });

        stage.addActor(backLabel);
    }

    @Override
    public void render(float delta) {
        handleInput(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Фон
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Затемнение
        batch.setColor(SHADOW_COLOR);
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);

        // Текст с тенью
        font.setColor(0, 0, 0, 0.5f);
        font.draw(batch, layout, MARGIN + 2, Gdx.graphics.getHeight() - MARGIN + scrollY - 2);
        font.setColor(1, 1, 1, 1);
        font.draw(batch, layout, MARGIN, Gdx.graphics.getHeight() - MARGIN + scrollY);

        // Курсор
        batch.draw(cursorTexture,
                Gdx.input.getX() - 16,
                Gdx.graphics.getHeight() - Gdx.input.getY() - 16,
                32, 32);

        drawScrollArrows();
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void handleInput(float delta) {
        // Прокрутка клавишами
        if (Gdx.input.isKeyPressed(Input.Keys.W)) scrollY += SCROLL_SPEED * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) scrollY -= SCROLL_SPEED * delta;

        // Ограничение прокрутки
        scrollY = MathUtils.clamp(scrollY,
                -textHeight + Gdx.graphics.getHeight() - MARGIN * 2,
                0
        );
    }

    private void drawScrollArrows() {
        batch.setColor(1, 1, 1, 0.8f);
        if (scrollY < 0) {
            font.draw(batch, "▲",
                    Gdx.graphics.getWidth()/2f - 10,
                    Gdx.graphics.getHeight() - 80
            );
        }
        if (scrollY > -textHeight + Gdx.graphics.getHeight() - MARGIN * 2) {
            font.draw(batch, "▼",
                    Gdx.graphics.getWidth()/2f - 10,
                    100
            );
        }
        batch.setColor(Color.WHITE);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}