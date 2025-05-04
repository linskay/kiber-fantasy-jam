package com.cyberkingdom.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.rendering.SpriteRenderer;
import com.cyberkingdom.world.LevelLoader;

public class GameScreen implements Screen {
    private final EntitySystem entitySystem;
    private final PhysicsSystem physicsSystem;
    private final SpriteRenderer spriteRenderer;
    private final OrthographicCamera camera;
    private final Player player;
    private final Texture background;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public GameScreen(EntitySystem entitySystem, PhysicsSystem physicsSystem,
                      LevelLoader levelLoader, SpriteRenderer spriteRenderer,
                      Object uiManager, Object bossFightLogic) {
        this.entitySystem = entitySystem;
        this.physicsSystem = physicsSystem;
        this.spriteRenderer = spriteRenderer;

        // Загрузка фона
        background = new Texture(Gdx.files.internal("assets/ui/background.png"));

        // Настройка камеры
        camera = new OrthographicCamera(1200, 800);
        camera.setToOrtho(false);

        // Поиск игрока
        Player foundPlayer = null;
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Player) {
                foundPlayer = (Player) entity;
                break;
            }
        }
        player = foundPlayer;
    }

    @Override
    public void render(float delta) {
        updateCamera(delta);

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Рендер фона
        spriteRenderer.getBatch().begin();
        spriteRenderer.getBatch().draw(background, 0, 0, 1200, 800);
        spriteRenderer.getBatch().end();

        // Рендер платформ
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.6f, 0.2f, 1);
        for (Rectangle platform : physicsSystem.getPlatforms()) {
            shapeRenderer.rect(platform.x, platform.y, platform.width, platform.height);
        }
        shapeRenderer.end();

        // Рендер сущностей
        spriteRenderer.begin();
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity.isActive()) {
                spriteRenderer.render(entity);
            }
        }
        spriteRenderer.end();
    }

    private void updateCamera(float delta) {
        if (player != null) {
            camera.position.set(
                    MathUtils.lerp(camera.position.x, player.getPosition().x, delta * 5),
                    MathUtils.lerp(camera.position.y, player.getPosition().y + 200, delta * 5),
                    0
            );
            camera.update();
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        background.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}