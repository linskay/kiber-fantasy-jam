package com.cyberkingdom.world;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.rendering.SpriteManager;
import com.cyberkingdom.utils.TiledMapLoader;

public class LevelLoader {
    private TiledMap currentMap;
    private PhysicsSystem physicsSystem;
    private int levelNumber;
    private int totalItems = 0;

    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem,
                       PhysicsSystem physicsSystem, String mapPath, int level) {
        EntityFactory.resetItemCounter();
        this.physicsSystem = physicsSystem;
        this.levelNumber = level;
        this.currentMap = TiledMapLoader.loadMap(mapPath);

        loadMapObjects();

        if (levelNumber == 1) {
            generateLevel1Content(entitySystem);
        }
    }

    private void loadMapObjects() {
        // Проверяем наличие слоя с платформами
        if (currentMap.getLayers().get("platforms") != null) {
            MapObjects platforms = currentMap.getLayers().get("platforms").getObjects();
            for (MapObject object : platforms) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    physicsSystem.addPlatform(rect);
                }
            }
        }
    }

    private void generateLevel1Content(EntitySystem entitySystem) {
        // Создаем обязательные платформы, если их нет в TiledMap
        if (physicsSystem.getPlatforms().isEmpty()) {
            createDefaultPlatforms();
        }

        // Случайные предметы (3-7 штук)
        totalItems = 3 + (int)(Math.random() * 5);
        for (int i = 0; i < totalItems; i++) {
            spawnRandomItem(entitySystem);
        }

        // Кот-майнер (30% шанс)
        if (Math.random() < 0.3) {
            spawnCatMiner(entitySystem);
        }
    }

    private void createDefaultPlatforms() {
        // Создаем базовые платформы, если их нет в TiledMap
        physicsSystem.addPlatform(new Rectangle(0, 0, 800, 50)); // Пол
        physicsSystem.addPlatform(new Rectangle(100, 150, 200, 20));
        physicsSystem.addPlatform(new Rectangle(400, 250, 200, 20));
    }

    private void spawnRandomItem(EntitySystem entitySystem) {
        if (!physicsSystem.getPlatforms().isEmpty()) {
            Rectangle platform = getRandomPlatform();
            float x = platform.x + (float)(Math.random() * platform.width);
            float y = platform.y + platform.height + 10;

            entitySystem.addEntity(new EntityFactory().createRandomItem(x, y));
        }
    }

    private void spawnCatMiner(EntitySystem entitySystem) {
        if (!physicsSystem.getPlatforms().isEmpty()) {
            Rectangle platform = getRandomPlatform();
            float x = platform.x + platform.width/2;
            float y = platform.y + platform.height + 50;

            entitySystem.addEntity(new EntityFactory().createBoss("CAT_MINER", x, y));
        }
    }

    private Rectangle getRandomPlatform() {
        // Безопасное получение случайной платформы
        if (physicsSystem.getPlatforms().isEmpty()) {
            return new Rectangle(0, 0, 100, 20); // Возвращаем платформу по умолчанию
        }
        int randomIndex = (int)(Math.random() * physicsSystem.getPlatforms().size());
        return physicsSystem.getPlatforms().get(randomIndex);
    }

    public TiledMap getCurrentMap() {
        return currentMap;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public int getTotalItems() {
        return totalItems;
    }
}