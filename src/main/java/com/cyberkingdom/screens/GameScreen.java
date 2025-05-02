package com.cyberkingdom.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.cyberkingdom.entities.Boss;
import com.cyberkingdom.entities.BossFightLogic;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.input.InputHandler;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.rendering.SpriteRenderer;
import com.cyberkingdom.ui.UIManager;
import com.cyberkingdom.world.LevelLoader;

public class GameScreen {
    private EntitySystem entitySystem;
    private PhysicsSystem physicsSystem;
    private LevelLoader levelLoader;
    private SpriteRenderer spriteRenderer;
    private UIManager uiManager;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Player player;

    public GameScreen(EntitySystem entitySystem, PhysicsSystem physicsSystem, LevelLoader levelLoader,
                      SpriteRenderer spriteRenderer, UIManager uiManager) {
        System.out.println("Создание GameScreen");
        this.entitySystem = entitySystem;
        this.physicsSystem = physicsSystem;
        this.levelLoader = levelLoader;
        this.spriteRenderer = spriteRenderer;
        this.uiManager = uiManager;
        TiledMap map = levelLoader.getCurrentMap();
        this.mapRenderer = new OrthogonalTiledMapRenderer(map);
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = 1.0f;
        findPlayerAndBoss();
        if (player != null) {
            camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        } else {
            camera.position.set(100, 100, 0);
        }
        camera.update();
    }

    private void findPlayerAndBoss() {
        System.out.println("Поиск игрока и босса в EntitySystem, всего сущностей: " + entitySystem.getEntities().size());
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Player) {
                this.player = (Player) entity;
                System.out.println("Игрок найден: " + entity);
            }
        }
        if (player == null) {
            System.err.println("Предупреждение: Игрок не найден в системе сущностей");
        } else {
            System.out.println("Игрок успешно инициализирован");
        }
    }

    public void render(float deltaTime) {
        System.out.println("Рендеринг кадра, сущностей: " + entitySystem.getEntities().size());
        Gdx.gl.glClearColor(0, 0, 1, 1); // Синий фон
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        System.out.println("Карта отрендерена");

        spriteRenderer.begin();
        int renderedEntities = 0;
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity.isActive() && entity.getAnimation() != null) {
                spriteRenderer.render(entity);
                renderedEntities++;
            }
        }
        if (player != null) {
            uiManager.render(player, spriteRenderer.getBatch());
        }
        spriteRenderer.end();
        System.out.println("Отрендерено сущностей: " + renderedEntities);

        physicsSystem.update(deltaTime);
        if (player != null) {
            camera.position.set(player.getPosition().x, player.getPosition().y, 0);
            camera.update();
        }
    }

    public void dispose() {
        System.out.println("Освобождение ресурсов GameScreen");
        mapRenderer.dispose();
    }
}