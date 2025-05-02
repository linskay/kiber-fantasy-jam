package com.cyberkingdom.gameengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.EntityFactory;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.rendering.SpriteManager;
import com.cyberkingdom.rendering.SpriteRenderer;
import com.cyberkingdom.screens.GameScreen;
import com.cyberkingdom.ui.UIManager;
import com.cyberkingdom.world.LevelLoader;

public class GameEngine extends ApplicationAdapter {
    private SpriteBatch batch;
    private SpriteManager spriteManager;
    private SpriteRenderer spriteRenderer;
    private UIManager uiManager;
    private EntitySystem entitySystem;
    private PhysicsSystem physicsSystem;
    private LevelLoader levelLoader;
    private GameScreen gameScreen;
    private EntityFactory entityFactory;

    @Override
    public void create() {
        System.out.println("Запуск GameEngine");
        batch = new SpriteBatch();
        spriteManager = new SpriteManager();
        GameEntity.setSpriteManager(spriteManager); // Устанавливаем SpriteManager до создания сущностей
        spriteRenderer = new SpriteRenderer(batch);
        entitySystem = new EntitySystem();
        levelLoader = new LevelLoader(spriteManager, entitySystem); // Загружаем уровень и сущности
        physicsSystem = new PhysicsSystem(entitySystem); // Инициализируем после загрузки уровня
        uiManager = new UIManager(spriteRenderer);
        gameScreen = new GameScreen(entitySystem, physicsSystem, levelLoader, spriteRenderer, uiManager);
        entityFactory = new EntityFactory();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gameScreen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        System.out.println("Освобождение ресурсов GameEngine");
        spriteRenderer.dispose();
        uiManager.dispose();
        gameScreen.dispose();
        levelLoader.getCurrentMap().dispose();
        spriteManager.dispose();
        batch.dispose();
    }
}