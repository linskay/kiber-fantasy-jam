package com.cyberkingdom.world;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.rendering.SpriteManager;
import com.cyberkingdom.utils.TiledMapLoader;

//public class LevelLoader {
//    private TiledMap currentMap;
//    private PhysicsSystem physicsSystem;
//    private int levelNumber;
//    private int totalItems = 0;
//
//    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem,
//                       PhysicsSystem physicsSystem, String mapPath, int level) {
//        EntityFactory.resetItemCounter();
//        this.physicsSystem = physicsSystem;
//        this.levelNumber = level;
//        this.currentMap = TiledMapLoader.loadMap(mapPath);
//
//        loadMapObjects();
//
//        if (levelNumber == 1) {
//            generateLevel1Content(entitySystem);
//        }
//    }
//
//    private void loadMapObjects() {
//        // Проверяем наличие слоя с платформами
//        if (currentMap.getLayers().get("platforms") != null) {
//            MapObjects platforms = currentMap.getLayers().get("platforms").getObjects();
//            for (MapObject object : platforms) {
//                if (object instanceof RectangleMapObject) {
//                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
//                    physicsSystem.addPlatform(rect);
//                }
//            }
//        }
//    }
//
//    private void generateLevel1Content(EntitySystem entitySystem) {
//        // Создаем обязательные платформы, если их нет в TiledMap
//        if (physicsSystem.getPlatforms().isEmpty()) {
//            createDefaultPlatforms();
//        }
//
//        // Случайные предметы (3-7 штук)
//        totalItems = 3 + (int)(Math.random() * 5);
//        for (int i = 0; i < totalItems; i++) {
//            spawnRandomItem(entitySystem);
//        }
//
//        // Кот-майнер (30% шанс)
//        if (Math.random() < 0.3) {
//            spawnCatMiner(entitySystem);
//        }
//    }
//
//    private void createDefaultPlatforms() {
//        // Создаем базовые платформы, если их нет в TiledMap
//        physicsSystem.addPlatform(new Rectangle(0, 0, 800, 50)); // Пол
//        physicsSystem.addPlatform(new Rectangle(100, 150, 200, 20));
//        physicsSystem.addPlatform(new Rectangle(400, 250, 200, 20));
//    }
//
//    private void spawnRandomItem(EntitySystem entitySystem) {
//        if (!physicsSystem.getPlatforms().isEmpty()) {
//            Rectangle platform = getRandomPlatform();
//            float x = platform.x + (float)(Math.random() * platform.width);
//            float y = platform.y + platform.height + 10;
//
//            entitySystem.addEntity(new EntityFactory().createRandomItem(x, y));
//        }
//    }
//
//    private void spawnCatMiner(EntitySystem entitySystem) {
//        if (!physicsSystem.getPlatforms().isEmpty()) {
//            Rectangle platform = getRandomPlatform();
//            float x = platform.x + platform.width/2;
//            float y = platform.y + platform.height + 50;
//
//            entitySystem.addEntity(new EntityFactory().createBoss("CAT_MINER", x, y));
//        }
//    }
//
//    private Rectangle getRandomPlatform() {
//        // Безопасное получение случайной платформы
//        if (physicsSystem.getPlatforms().isEmpty()) {
//            return new Rectangle(0, 0, 100, 20); // Возвращаем платформу по умолчанию
//        }
//        int randomIndex = (int)(Math.random() * physicsSystem.getPlatforms().size());
//        return physicsSystem.getPlatforms().get(randomIndex);
//    }
//
//    public TiledMap getCurrentMap() {
//        return currentMap;
//    }
//
//    public int getLevelNumber() {
//        return levelNumber;
//    }
//
//    public int getTotalItems() {
//        return totalItems;
//    }
//}
import java.util.*;

import com.badlogic.gdx.math.Vector2;

//public class LevelLoader {
//    private TiledMap currentMap;
//    private PhysicsSystem physicsSystem;
//    private int levelNumber;
//    private int totalItems = 0;
//
//    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem,
//                       PhysicsSystem physicsSystem, String mapPath, int level) {
//        EntityFactory.resetItemCounter();
//        this.physicsSystem = physicsSystem;
//        this.levelNumber = level;
//        this.currentMap = TiledMapLoader.loadMap(mapPath);
//
//        loadMapObjects();
//
//        if (levelNumber == 1) {
//            generateLevel1Content(entitySystem);
//        }
//    }
//
//    private void loadMapObjects() {
//        if (currentMap.getLayers().get("platforms") != null) {
//            MapObjects platforms = currentMap.getLayers().get("platforms").getObjects();
//            for (MapObject object : platforms) {
//                if (object instanceof RectangleMapObject) {
//                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
//                    physicsSystem.addPlatform(rect);
//                }
//            }
//        }
//    }
//
//    private void generateLevel1Content(EntitySystem entitySystem) {
//        if (physicsSystem.getPlatforms().isEmpty()) {
//            createDefaultPlatforms();
//        }
//
//        // Спавним предметы через новый метод
//        spawnItemsOnMap(entitySystem);
//
//        // Кот-майнер (30% шанс)
//        if (Math.random() < 0.3) {
//            spawnCatMiner(entitySystem);
//        }
//    }
//
//    private void createDefaultPlatforms() {
//        physicsSystem.addPlatform(new Rectangle(0, 0, 800, 50)); // Пол
//        physicsSystem.addPlatform(new Rectangle(100, 150, 200, 20));
//        physicsSystem.addPlatform(new Rectangle(400, 250, 200, 20));
//    }
//
//    /**
//     * Спавн предметов на карте.
//     * Если слой "spawn_points" есть - используем его.
//     * Иначе - рандомно размещаем на платформах.
//     */
//    private void spawnItemsOnMap(EntitySystem entitySystem) {
//        MapLayer spawnLayer = currentMap.getLayers().get("spawn_points");
//        List<Vector2> spawnPositions = new ArrayList<>();
//
//        if (spawnLayer != null) {
//            for (MapObject object : spawnLayer.getObjects()) {
//                if (object instanceof RectangleMapObject) {
//                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
//                    spawnPositions.add(new Vector2(rect.x, rect.y));
//                }
//                // Можно добавить поддержку PointMapObject, если нужно
//            }
//        }
//
//        totalItems = 3 + (int)(Math.random() * 5); // 3-7 предметов
//
//        if (!spawnPositions.isEmpty()) {
//            // Перемешиваем позиции и спавним на первых totalItems
//            Collections.shuffle(spawnPositions);
//            int count = Math.min(totalItems, spawnPositions.size());
//
//            for (int i = 0; i < count; i++) {
//                Vector2 pos = spawnPositions.get(i);
//                entitySystem.addEntity(new EntityFactory().createRandomItem(pos.x, pos.y));
//            }
//        } else {
//            // Если нет слоя spawn_points - спавним на платформах
//            for (int i = 0; i < totalItems; i++) {
//                spawnRandomItem(entitySystem);
//            }
//        }
//    }
//
//    private void spawnRandomItem(EntitySystem entitySystem) {
//        if (!physicsSystem.getPlatforms().isEmpty()) {
//            Rectangle platform = getRandomPlatform();
//            float x = platform.x + (float)(Math.random() * platform.width);
//            float y = platform.y + platform.height + 10;
//
//            entitySystem.addEntity(new EntityFactory().createRandomItem(x, y));
//        }
//    }
//
//    private void spawnCatMiner(EntitySystem entitySystem) {
//        if (!physicsSystem.getPlatforms().isEmpty()) {
//            Rectangle platform = getRandomPlatform();
//            float x = platform.x + platform.width/2;
//            float y = platform.y + platform.height + 50;
//
//            entitySystem.addEntity(new EntityFactory().createBoss("CAT_MINER", x, y));
//        }
//    }
//
//    private Rectangle getRandomPlatform() {
//        if (physicsSystem.getPlatforms().isEmpty()) {
//            return new Rectangle(0, 0, 100, 20);
//        }
//        int randomIndex = (int)(Math.random() * physicsSystem.getPlatforms().size());
//        return physicsSystem.getPlatforms().get(randomIndex);
//    }
//
//    public TiledMap getCurrentMap() {
//        return currentMap;
//    }
//
//    public int getLevelNumber() {
//        return levelNumber;
//    }
//
//    public int getTotalItems() {
//        return totalItems;
//    }
//}


//public class LevelLoader {
//    private static final String[] ITEM_TYPES = {
//            "CRYPTO_COIN", "VPN_TOKEN", "USB_SCATERT", "HARDWARE_WALLET"
//    };
//
//    private TiledMap currentMap;
//    private PhysicsSystem physicsSystem;
//    private int levelNumber;
//    private int totalItems = 0;
//    private Random random = new Random();
//
//    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem,
//                       PhysicsSystem physicsSystem, String mapPath, int level) {
//        EntityFactory.resetItemCounter();
//        this.physicsSystem = physicsSystem;
//        this.levelNumber = level;
//        this.currentMap = TiledMapLoader.loadMap(mapPath);
//
//        loadMapObjects();
//
//        if (levelNumber == 1) {
//            generateLevel1Content(entitySystem);
//        }
//    }
//
//    private void loadMapObjects() {
//        if (currentMap.getLayers().get("platforms") != null) {
//            MapObjects platforms = currentMap.getLayers().get("platforms").getObjects();
//            for (MapObject object : platforms) {
//                if (object instanceof RectangleMapObject) {
//                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
//                    physicsSystem.addPlatform(rect);
//                }
//            }
//        }
//    }
//
//    private void generateLevel1Content(EntitySystem entitySystem) {
//        if (physicsSystem.getPlatforms().isEmpty()) {
//            createDefaultPlatforms();
//        }
//
//        // Спавним предметы через новый метод
//        spawnItemsOnMap(entitySystem);
//
//        // Кот-майнер (30% шанс)
//        if (Math.random() < 0.3) {
//            spawnCatMiner(entitySystem);
//        }
//    }
//
//    private void createDefaultPlatforms() {
//        physicsSystem.addPlatform(new Rectangle(0, 0, 800, 50)); // Пол
//        physicsSystem.addPlatform(new Rectangle(100, 150, 200, 20));
//        physicsSystem.addPlatform(new Rectangle(400, 250, 200, 20));
//    }
//
//    /**
//     * Спавн предметов на карте.
//     * Если слой "spawn_points" есть - используем его.
//     * Иначе - рандомно размещаем на платформах.
//     */
//    private void spawnItemsOnMap(EntitySystem entitySystem) {
//        MapLayer spawnLayer = currentMap.getLayers().get("spawn_points");
//        List<Vector2> spawnPositions = new ArrayList<>();
//
//        if (spawnLayer != null) {
//            for (MapObject object : spawnLayer.getObjects()) {
//                if (object instanceof RectangleMapObject) {
//                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
//                    spawnPositions.add(new Vector2(rect.x, rect.y));
//                }
//                // Можно добавить поддержку PointMapObject, если нужно
//            }
//        }
//
//        totalItems = 3 + random.nextInt(5); // 3-7 предметов
//
//        if (!spawnPositions.isEmpty()) {
//            Collections.shuffle(spawnPositions);
//            int count = Math.min(totalItems, spawnPositions.size());
//
//            for (int i = 0; i < count; i++) {
//                Vector2 pos = spawnPositions.get(i);
//                String randomItemType = ITEM_TYPES[random.nextInt(ITEM_TYPES.length)];
//                entitySystem.addEntity(new EntityFactory().createItem(pos, randomItemType));
//            }
//        } else {
//            for (int i = 0; i < totalItems; i++) {
//                spawnRandomItem(entitySystem);
//            }
//        }
//    }
//
//    private void spawnRandomItem(EntitySystem entitySystem) {
//        if (!physicsSystem.getPlatforms().isEmpty()) {
//            Rectangle platform = getRandomPlatform();
//            float x = platform.x + random.nextFloat() * platform.width;
//            float y = platform.y + platform.height + 10;
//
//            String randomItemType = ITEM_TYPES[random.nextInt(ITEM_TYPES.length)];
//            entitySystem.addEntity(new EntityFactory().createItem(new Vector2(x, y), randomItemType));
//        }
//    }
//
//    private void spawnCatMiner(EntitySystem entitySystem) {
//        if (!physicsSystem.getPlatforms().isEmpty()) {
//            Rectangle platform = getRandomPlatform();
//            float x = platform.x + platform.width / 2;
//            float y = platform.y + platform.height + 50;
//
//            entitySystem.addEntity(new EntityFactory().createBoss("CAT_MINER", x, y));
//        }
//    }
//
//    private Rectangle getRandomPlatform() {
//        if (physicsSystem.getPlatforms().isEmpty()) {
//            return new Rectangle(0, 0, 100, 20);
//        }
//        int randomIndex = random.nextInt(physicsSystem.getPlatforms().size());
//        return physicsSystem.getPlatforms().get(randomIndex);
//    }
//
//    public TiledMap getCurrentMap() {
//        return currentMap;
//    }
//
//    public int getLevelNumber() {
//        return levelNumber;
//    }
//
//    public int getTotalItems() {
//        return totalItems;
//    }
//}

public class LevelLoader {
    // Весовые коэффициенты для предметов, USB_SCATERT - редкий предмет
    private static final Map<String, Integer> ITEM_WEIGHTS = Map.of(
            "CRYPTO_COIN", 40,
            "VPN_TOKEN", 30,
            "HARDWARE_WALLET", 25,
            "USB_SCATERT", 5  // Очень редкий предмет
    );

    private final TiledMap currentMap;
    private final PhysicsSystem physicsSystem;
    private final EntityFactory entityFactory; // Добавляем поле для EntityFactory
    private final Random random = new Random();

    private int levelNumber;
    private int totalItems = 0;

    // Конструктор теперь принимает EntityFactory
    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem,
                       PhysicsSystem physicsSystem, EntityFactory entityFactory,
                       String mapPath, int level) {
        EntityFactory.resetItemCounter();
        this.physicsSystem = physicsSystem;
        this.entityFactory = entityFactory;
        this.levelNumber = level;
        this.currentMap = TiledMapLoader.loadMap(mapPath);

        loadMapObjects();

        if (levelNumber == 1) {
            generateLevel1Content(entitySystem);
        }
    }

    private void loadMapObjects() {
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
        if (physicsSystem.getPlatforms().isEmpty()) {
            createDefaultPlatforms();
        }

        spawnItemsOnMap(entitySystem);

        if (Math.random() < 0.3) {
            spawnCatMiner(entitySystem);
        }
    }

    private void createDefaultPlatforms() {
        physicsSystem.addPlatform(new Rectangle(0, 0, 800, 50));
        physicsSystem.addPlatform(new Rectangle(100, 150, 200, 20));
        physicsSystem.addPlatform(new Rectangle(400, 250, 200, 20));
    }

    private void spawnItemsOnMap(EntitySystem entitySystem) {
        MapLayer spawnLayer = currentMap.getLayers().get("spawn_points");
        List<Vector2> spawnPositions = new ArrayList<>();

        if (spawnLayer != null) {
            for (MapObject object : spawnLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    spawnPositions.add(new Vector2(rect.x, rect.y));
                }
            }
        }

        totalItems = 3 + random.nextInt(5);

        if (!spawnPositions.isEmpty()) {
            Collections.shuffle(spawnPositions);
            int count = Math.min(totalItems, spawnPositions.size());

            for (int i = 0; i < count; i++) {
                Vector2 pos = spawnPositions.get(i);
                String randomItemType = getRandomItemType();
                // Используем entityFactory и передаём количество 1
                Item item = entityFactory.createItem(randomItemType, pos, 1);
                if (item != null) {
                    entitySystem.addEntity(item);
                } else {
                    System.err.println("Не удалось создать предмет: " + randomItemType);
                }
            }
        } else {
            for (int i = 0; i < totalItems; i++) {
                spawnRandomItem(entitySystem);
            }
        }
    }

    private void spawnRandomItem(EntitySystem entitySystem) {
        if (!physicsSystem.getPlatforms().isEmpty()) {
            Rectangle platform = getRandomPlatform();
            float x = platform.x + random.nextFloat() * platform.width;
            float y = platform.y + platform.height + 10;

            String randomItemType = getRandomItemType();
            Item item = entityFactory.createItem(randomItemType, new Vector2(x, y), 1);
            if (item != null) {
                entitySystem.addEntity(item);
            } else {
                System.err.println("Не удалось создать предмет: " + randomItemType);
            }
        }
    }

    private String getRandomItemType() {
        int totalWeight = ITEM_WEIGHTS.values().stream().mapToInt(Integer::intValue).sum();
        int randomWeight = random.nextInt(totalWeight);
        int currentSum = 0;
        for (Map.Entry<String, Integer> entry : ITEM_WEIGHTS.entrySet()) {
            currentSum += entry.getValue();
            if (randomWeight < currentSum) {
                return entry.getKey();
            }
        }
        // На всякий случай
        return "CRYPTO_COIN";
    }

    private void spawnCatMiner(EntitySystem entitySystem) {
        if (!physicsSystem.getPlatforms().isEmpty()) {
            Rectangle platform = getRandomPlatform();
            float x = platform.x + platform.width / 2;
            float y = platform.y + platform.height + 50;

            entitySystem.addEntity(entityFactory.createBoss("CAT_MINER", x, y));
        }
    }

    private Rectangle getRandomPlatform() {
        if (physicsSystem.getPlatforms().isEmpty()) {
            return new Rectangle(0, 0, 100, 20);
        }
        int randomIndex = random.nextInt(physicsSystem.getPlatforms().size());
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

