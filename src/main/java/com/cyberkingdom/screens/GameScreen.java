package com.cyberkingdom.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.items.InventoryWindow;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.rendering.SpriteRenderer;
import com.cyberkingdom.ui.UIManager;
import com.cyberkingdom.world.LevelLoader;
import java.util.HashMap;
import java.util.Map;

//public class GameScreen {
//    private final ShapeRenderer platformRenderer = new ShapeRenderer();
//    private Texture currentBackground;
//    private Map<Integer, Texture> backgroundTextures = new HashMap<>();
//    private EntitySystem entitySystem;
//    private PhysicsSystem physicsSystem;
//    private SpriteRenderer spriteRenderer;
//    private OrthogonalTiledMapRenderer mapRenderer;
//    private OrthographicCamera camera;
//    private ShapeRenderer shapeRenderer;
//    private Player player;
//
//    private Texture backgroundTexture;
//
//    public GameScreen() {
//        // Загрузка фона
//        backgroundTexture = new Texture(Gdx.files.internal("assets/ui/background.png"));
//    }
//
//    public GameScreen(EntitySystem entitySystem, PhysicsSystem physicsSystem,
//                      LevelLoader levelLoader, SpriteRenderer spriteRenderer,
//                      UIManager uiManager, BossFightLogic bossFightLogic) {
//        this.entitySystem = entitySystem;
//        this.physicsSystem = physicsSystem;
//        this.spriteRenderer = spriteRenderer;
//
//        // Инициализация фона
//        backgroundTextures.put(1, new Texture(Gdx.files.internal("assets/ui/background.png")));
//        currentBackground = backgroundTextures.get(1);
//
//        // Инициализация камеры
//        camera = new OrthographicCamera(1200, 800);
//        camera.setToOrtho(false);
//
//        // Поиск игрока
//        for (GameEntity entity : entitySystem.getEntities()) {
//            if (entity instanceof Player) {
//                player = (Player) entity;
//                break;
//            }
//        }
//    }
//
//    public void render(float deltaTime) {
//        updateCamera(deltaTime);
//        renderBackground();
//        renderPlatforms();
//
//        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        spriteRenderer.begin();
//        for (GameEntity entity : entitySystem.getEntities()) {
//            if (entity.isActive()) {
//                spriteRenderer.render(entity);
//            }
//        }
//        spriteRenderer.end();
//    }
//
//    private void renderBackground() {
//        if (currentBackground != null) {
//            spriteRenderer.getBatch().begin();
//            spriteRenderer.getBatch().draw(currentBackground, 0, 0, 1200, 800);
//            spriteRenderer.getBatch().end();
//        }
//    }
//
//    private void updateCamera(float deltaTime) {
//        if (player != null) {
//            camera.position.set(player.getPosition().x, player.getPosition().y + 200, 0);
//            camera.update();
//        }
//    }
//
//    private void renderPlatforms() {
//        platformRenderer.setProjectionMatrix(camera.combined);
//        platformRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        platformRenderer.setColor(0.2f, 0.6f, 0.2f, 1); // Зеленый цвет платформ
//
//        for (Rectangle platform : physicsSystem.getPlatforms()) {
//            platformRenderer.rect(
//                    platform.x,
//                    platform.y,
//                    platform.width,
//                    platform.height
//            );
//        }
//
//        platformRenderer.end();
//    }
//
//    public void dispose() {
//        currentBackground.dispose();
//    }
//}

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.Map;

public class GameScreen {
    private final ShapeRenderer platformRenderer = new ShapeRenderer();
    private Map<Integer, Texture> backgroundTextures = new HashMap<>();
    private Texture currentBackground;
    private EntitySystem entitySystem;
    private PhysicsSystem physicsSystem;
    private SpriteRenderer spriteRenderer;
    private OrthographicCamera camera;
    private Player player;
    private EntityFactory entityFactory;  // Убрано дублирование entitySystem
    private Stage stage;
    private InventoryWindow inventoryWindow;

    public GameScreen(EntitySystem entitySystem, PhysicsSystem physicsSystem,
                      LevelLoader levelLoader, SpriteRenderer spriteRenderer,
                      UIManager uiManager, BossFightLogic bossFightLogic, Skin skin) {
        this.entitySystem = entitySystem;
        this.physicsSystem = physicsSystem;
        this.spriteRenderer = spriteRenderer;
        this.entityFactory = new EntityFactory();  // Инициализация фабрики сущностей

        // Инициализация фона
        backgroundTextures.put(1, new Texture(Gdx.files.internal("assets/ui/background.png")));
        currentBackground = backgroundTextures.get(1);

        // Инициализация камеры
        camera = new OrthographicCamera(1200, 800);
        camera.setToOrtho(false);

        // Поиск игрока
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Player) {
                player = (Player) entity;
                break;
            }
        }

        // Инициализация Stage и окна инвентаря
        stage = new Stage(new ScreenViewport());

        if (player != null && skin != null) {
            inventoryWindow = new InventoryWindow(skin, 800, 400,
                    player.getInventory(),
                    player,
                    entitySystem,
                    entityFactory,
                    physicsSystem);
            inventoryWindow.setVisible(false); // Можно сделать false, если хотите скрывать изначально
            stage.addActor(inventoryWindow);
        } else {
            Gdx.app.error("GameScreen", "Player or Skin is null. InventoryWindow not created.");
        }

        // Устанавливаем input processor на stage для обработки UI
        Gdx.input.setInputProcessor(stage);
    }

    public Stage getStage() {
        return stage;
    }

    public void toggleInventory() {
        if (inventoryWindow == null) return;
        boolean visible = inventoryWindow.isVisible();
        inventoryWindow.setVisible(!visible);
        if (!visible) {
            stage.setKeyboardFocus(inventoryWindow);
        } else {
            stage.unfocus(inventoryWindow);
        }
        if (inventoryWindow.isVisible()) {
            inventoryWindow.refresh();
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {  // <-- Здесь изменена клавиша с I на E
            toggleInventory();
        }
    }

    public void render(float deltaTime) {
        handleInput();

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateCamera(deltaTime);

        renderBackground();
        renderPlatforms();

        spriteRenderer.getBatch().setProjectionMatrix(camera.combined);
        spriteRenderer.begin();
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity.isActive()) {
                spriteRenderer.render(entity);
            }
        }
        spriteRenderer.end();

        stage.act(deltaTime);
        stage.draw();
    }

    private void updateCamera(float deltaTime) {
        if (player != null) {
            camera.position.set(player.getPosition().x, player.getPosition().y + 200, 0);
            camera.update();
        }
    }

    private void renderBackground() {
        if (currentBackground != null) {
            SpriteBatch batch = spriteRenderer.getBatch();
            batch.begin();
            batch.draw(currentBackground, 0, 0, 1200, 800);
            batch.end();
        }
    }

    private void renderPlatforms() {
        platformRenderer.setProjectionMatrix(camera.combined);
        platformRenderer.begin(ShapeRenderer.ShapeType.Filled);
        platformRenderer.setColor(0.2f, 0.6f, 0.2f, 1);

        for (Rectangle platform : physicsSystem.getPlatforms()) {
            platformRenderer.rect(platform.x, platform.y, platform.width, platform.height);
        }

        platformRenderer.end();
    }

    public void dispose() {
        for (Texture tex : backgroundTextures.values()) {
            if (tex != null) tex.dispose();
        }
        platformRenderer.dispose();
        if (stage != null) stage.dispose();
    }
}



