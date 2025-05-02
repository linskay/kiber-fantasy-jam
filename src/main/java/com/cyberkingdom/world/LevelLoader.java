package com.cyberkingdom.world;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.EntityFactory;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.rendering.SpriteManager;
import com.cyberkingdom.utils.TiledMapLoader;

public class LevelLoader {
    private TiledMap currentMap;
    private EntitySystem entitySystem;
    private EntityFactory entityFactory;

    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem) {
        System.out.println("Создание LevelLoader");
        this.entitySystem = entitySystem;
        this.entityFactory = new EntityFactory();
        this.currentMap = TiledMapLoader.loadMap("levels/level1.tmx");
        loadLevel1();
    }

    private void loadLevel1() {
        System.out.println("Загрузка сущностей level1");
        GameEntity player = entityFactory.createPlayer(100, 100);
        GameEntity enemy = entityFactory.createEnemy("TROLL_BOT", 300, 100);
        GameEntity boss = entityFactory.createBoss("STOP_GPT", 400, 100);
        GameEntity item = entityFactory.createItem(new Vector2(200, 200), "USB_SCATERT");

        entitySystem.addEntity(player);
        entitySystem.addEntity(enemy);
        entitySystem.addEntity(boss);
        entitySystem.addEntity(item);

        System.out.println("Сущности level1 загружены, всего: " + entitySystem.getEntities().size());
        for (GameEntity entity : entitySystem.getEntities()) {
            System.out.println("Сущность добавлена: " + entity.getName() + " на (" + entity.getPosition().x + ", " + entity.getPosition().y + ")");
        }
    }

    public TiledMap getCurrentMap() {
        return currentMap;
    }
}