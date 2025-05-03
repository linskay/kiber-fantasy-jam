package com.cyberkingdom.world;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.EntityFactory;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.rendering.SpriteManager;
import com.cyberkingdom.utils.TiledMapLoader;

//public class LevelLoader {
//    private TiledMap currentMap;
//    private EntitySystem entitySystem;
//    private EntityFactory entityFactory;
//
//    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem) {
//        System.out.println("Создание LevelLoader");
//        this.entitySystem = entitySystem;
//        this.entityFactory = new EntityFactory();
//        this.currentMap = TiledMapLoader.loadMap("levels/level1.tmx");
//        loadLevel1();
//    }
//
//    private void loadLevel1() {
//        System.out.println("Загрузка сущностей level1");
//        GameEntity player = entityFactory.createPlayer(100, 100);
//        GameEntity enemy = entityFactory.createEnemy("TROLL_BOT", 300, 100);
//        GameEntity boss = entityFactory.createBoss("STOP_GPT", 400, 100);
//        GameEntity item = entityFactory.createItem(new Vector2(200, 200), "USB_SCATERT");
//
//        entitySystem.addEntity(player);
//        entitySystem.addEntity(enemy);
//        entitySystem.addEntity(boss);
//        entitySystem.addEntity(item);
//
//        System.out.println("Сущности level1 загружены, всего: " + entitySystem.getEntities().size());
//        for (GameEntity entity : entitySystem.getEntities()) {
//            System.out.println("Сущность добавлена: " + entity.getName() + " на (" + entity.getPosition().x + ", " + entity.getPosition().y + ")");
//        }
//    }
//
//    public TiledMap getCurrentMap() {
//        return currentMap;
//    }
//}

public class LevelLoader {
    private TiledMap currentMap;
    private EntitySystem entitySystem;
    private EntityFactory entityFactory;

    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem, String mapPath, int level) {
        System.out.println("Создание LevelLoader для карты: " + mapPath + ", уровень: " + level);
        this.entitySystem = entitySystem;
        this.entityFactory = new EntityFactory();

        // Загружаем карту по переданному пути
        this.currentMap = TiledMapLoader.loadMap(mapPath);

        // Загружаем сущности в зависимости от уровня
        switch (level) {
            case 1:
                loadLevel1();
                break;
            case 2:
                loadLevel2();
                break;
            case 3:
                loadLevel3();
                break;
            case 4:
                loadLevel4();
                break;
            case 5:
                loadLevel5();
                break;
            // Добавьте другие уровни по необходимости
            default:
                System.err.println("Неизвестный уровень: " + level + ", загружаем level1 по умолчанию");
                loadLevel1();
                break;
        }
    }

    private void loadLevel1() {
        System.out.println("Загрузка сущностей level1");
        GameEntity player = entityFactory.createPlayer(100, 100);
        GameEntity enemy = entityFactory.createEnemy("TROLL_BOT", 300, 100);
        GameEntity boss = entityFactory.createBoss("STOP_GPT", 200, 35);
        GameEntity item = entityFactory.createItem(new Vector2(200, 200), "USB_SCATERT");

        entitySystem.addEntity(player);
        entitySystem.addEntity(enemy);
        entitySystem.addEntity(boss);
        entitySystem.addEntity(item);

        logEntities();
    }

    private void loadLevel2() {
        System.out.println("Загрузка сущностей level2");
        GameEntity player = entityFactory.createPlayer(150, 150);
        GameEntity enemy = entityFactory.createEnemy("GOBLIN", 350, 150);
        // Добавьте другие сущности для level2

        entitySystem.addEntity(player);
        entitySystem.addEntity(enemy);

        logEntities();
    }
    private void loadLevel3() {


        logEntities();
    }
    private void loadLevel4() {

        logEntities();
    }
    private void loadLevel5() {

        logEntities();
    }
    private void logEntities() {
        System.out.println("Сущности загружены, всего: " + entitySystem.getEntities().size());
        for (GameEntity entity : entitySystem.getEntities()) {
            System.out.println("Сущность добавлена: " + entity.getName() + " на (" + entity.getPosition().x + ", " + entity.getPosition().y + ")");
        }
    }

    public TiledMap getCurrentMap() {
        return currentMap;
    }
}
