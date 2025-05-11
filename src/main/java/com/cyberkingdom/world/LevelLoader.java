package com.cyberkingdom.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.items.ItemType;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.rendering.SpriteManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelLoader {
    private final PhysicsSystem physicsSystem;
    private final EntityFactory entityFactory;
    private final Random random = new Random();
    private int levelNumber;
    private int totalItems = 0;
    private List<Platform> platforms = new ArrayList<>();
    private float catMinerSpawnTimer = 0f;
    private boolean catMinerActive = false;
    private static float PLATFORM_HEIGHT = 32f;
    private static float PLATFORM_WIDTH = 128f;
    private static final float GROUND_Y = 125f;
    private static final float LEVEL_WIDTH = 1200f;
    private static final float LEVEL_HEIGHT = 800f;
    private static final float SPAWN_MARGIN = 50f; // Отступ от границ экрана
    private SpriteManager spriteManager;
    private static final float MIN_PLATFORM_Y = 125f;  // Минимальная высота платформы
    private static final float MAX_PLATFORM_Y = 600f;  // Максимальная высота платформы
    private static final float PLATFORM_SPACING = 150f; // Увеличиваем минимальное расстояние между платформами
    private static final String[] UNIQUE_ITEMS = {
        "USB_SKATERT", "CRYPTO_SHOVEL", "RTX_4090", "TUSHENKA", "KNIGA", "WIFI_KEY"
    };
    private float coinSpawnTimer = 0f;
    private static final float MIN_COIN_SPAWN_INTERVAL = 1f;
    private static final float MAX_COIN_SPAWN_INTERVAL = 3f;
    private float nextCoinSpawnTime;
    private List<Rectangle> platformsWithCoins = new ArrayList<>(); // Список платформ с монетами

    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem, PhysicsSystem physicsSystem, EntityFactory entityFactory, int level) {
        EntityFactory.resetItemCounter();
        this.spriteManager = spriteManager;
        this.physicsSystem = physicsSystem;
        this.entityFactory = entityFactory;
        this.levelNumber = level;
        generateLevel(entitySystem);
    }

    private void generateLevel(EntitySystem entitySystem) {
        Gdx.app.log("LevelLoader", "Starting level generation for level " + levelNumber);
        
        // Очищаем предыдущие платформы
        platforms.clear();
        physicsSystem.clearPlatforms();
        Gdx.app.log("LevelLoader", "Cleared previous platforms");

        // Создаем платформы в зависимости от уровня
        createPlatforms();
        Gdx.app.log("LevelLoader", "Created " + platforms.size() + " platforms");
        
        // Добавляем платформы в физическую систему и систему сущностей
        for (Platform platform : platforms) {
            physicsSystem.addPlatform(platform.getRectangle());
            entitySystem.addEntity(platform);
            Gdx.app.log("LevelLoader", "Added platform to systems: " + platform.getRectangle());
        }
        // Получаем игрока для проверки инвентаря
        Player player = null;
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Player) {
                player = (Player) entity;
                break;
            }
        }
        // Спавним уникальные предметы только если их нет в инвентаре
        spawnUniqueItemsOnMap(entitySystem, player);
        // Спавним предметы
        spawnItemsOnMap(entitySystem);
        Gdx.app.log("LevelLoader", "Spawned " + totalItems + " items");
        // Начальный спавн монет
        spawnCoins(entitySystem);
        Gdx.app.log("LevelLoader", "Level generation completed");
    }

    private void createPlatforms() {
        Gdx.app.log("LevelLoader", "Creating platforms");
        TextureRegion[] platformFrames = spriteManager.getFrames("Platform");
        if (platformFrames == null || platformFrames.length == 0) {
            Gdx.app.error("LevelLoader", "No platform frames found");
            return;
        }
        
        Texture platformTexture = platformFrames[0].getTexture();
        float textureWidth = platformTexture.getWidth();
        float platformWidth = textureWidth * 2;

        // Создаем землю
        Rectangle groundPlatform = new Rectangle(0, GROUND_Y, LEVEL_WIDTH, PLATFORM_HEIGHT);
        Platform ground = new Platform(groundPlatform, platformTexture);
        ground.setGround(true);
        platforms.add(ground);
        Gdx.app.log("LevelLoader", "Created ground platform at y=" + GROUND_Y);

        if (levelNumber == 1) {
            // Красивое расположение платформ для 1 уровня
            addPlatform(100, 200, platformWidth, platformTexture);
            addPlatform(400, 300, platformWidth, platformTexture);
            addPlatform(800, 250, platformWidth, platformTexture);
            addPlatform(200, 400, platformWidth, platformTexture);
            addPlatform(600, 420, platformWidth, platformTexture);
            addPlatform(900, 380, platformWidth, platformTexture);
            addPlatform(150, 550, platformWidth, platformTexture);
            addPlatform(500, 600, platformWidth, platformTexture);
            addPlatform(850, 570, platformWidth, platformTexture);
        } else if (levelNumber == 2) {
            // Красивое расположение платформ для 2 уровня
            addPlatform(120, 220, platformWidth, platformTexture);
            addPlatform(350, 320, platformWidth, platformTexture);
            addPlatform(700, 260, platformWidth, platformTexture);
            addPlatform(950, 340, platformWidth, platformTexture);
            addPlatform(250, 470, platformWidth, platformTexture);
            addPlatform(600, 500, platformWidth, platformTexture);
            addPlatform(900, 430, platformWidth, platformTexture);
            addPlatform(400, 600, platformWidth, platformTexture);
            addPlatform(800, 590, platformWidth, platformTexture);
        } else if (levelNumber == 3) {
            // Красивое расположение платформ для 3 уровня
            addPlatform(180, 210, platformWidth, platformTexture);
            addPlatform(500, 250, platformWidth, platformTexture);
            addPlatform(850, 220, platformWidth, platformTexture);
            addPlatform(300, 370, platformWidth, platformTexture);
            addPlatform(700, 400, platformWidth, platformTexture);
            addPlatform(950, 370, platformWidth, platformTexture);
            addPlatform(200, 520, platformWidth, platformTexture);
            addPlatform(600, 570, platformWidth, platformTexture);
            addPlatform(900, 540, platformWidth, platformTexture);
        }
    }

    private void addPlatform(float x, float y, float width, Texture texture) {
        Rectangle newPlatform = new Rectangle(x, y, width, PLATFORM_HEIGHT);
        // Проверяем на перекрытие
        boolean overlaps = false;
        for (Platform existing : platforms) {
            if (newPlatform.overlaps(existing.getRectangle())) {
                overlaps = true;
                break;
            }
        }
        if (!overlaps) {
            platforms.add(new Platform(newPlatform, texture));
            Gdx.app.log("LevelLoader", String.format("Created platform at (%.1f, %.1f) size %.1fx%.1f", x, y, width, PLATFORM_HEIGHT));
        } else {
            Gdx.app.log("LevelLoader", "Skipped overlapping platform at (" + x + ", " + y + ")");
        }
    }

    private void spawnItemsOnMap(EntitySystem entitySystem) {
        totalItems = 3 + random.nextInt(5);
        for (int i = 0; i < totalItems; i++) {
            spawnRandomItem(entitySystem);
        }
    }

    private void spawnRandomItem(EntitySystem entitySystem) {
        Rectangle platform = getRandomPlatform();
        // Проверяем, нет ли уже монеты на этой платформе
        if (platformsWithCoins.contains(platform)) {
            return;
        }

        // Добавляем отступы от краев платформы
        float x = platform.x + SPAWN_MARGIN + random.nextFloat() * (platform.width - 2 * SPAWN_MARGIN);
        float y = platform.y + platform.height + 10;
        Vector2 spawnPos = new Vector2(x, y);

        Item coin = entityFactory.createItem("COIN", spawnPos, 1);
        if (coin != null) {
            Gdx.app.log("LevelLoader", "Spawning coin at: (" + x + ", " + y + ")");
            entitySystem.addEntity(coin);
            platformsWithCoins.add(platform); // Добавляем платформу в список платформ с монетами
        } else {
            Gdx.app.error("LevelLoader", "Failed to create coin at: (" + x + ", " + y + ")");
        }
    }

    private void spawnCoins(EntitySystem entitySystem) {
        platformsWithCoins.clear(); // Очищаем список платформ с монетами
        for (Platform platform : platforms) {
            if (!platform.isGround()) { // Не спавним монеты на земле
                // Добавляем отступы от краев платформы
                float x = platform.getRectangle().x + SPAWN_MARGIN + 
                         random.nextFloat() * (platform.getRectangle().width - 2 * SPAWN_MARGIN);
                float y = platform.getRectangle().y + platform.getRectangle().height + 10;
                Vector2 spawnPos = new Vector2(x, y);
                
                try {
                    Item coin = entityFactory.createItem("COIN", spawnPos, 1);
                    if (coin != null) {
                        entitySystem.addEntity(coin);
                        platformsWithCoins.add(platform.getRectangle());
                        Gdx.app.log("LevelLoader", String.format(
                            "Initial coin spawned at: (%.1f, %.1f)",
                            x, y
                        ));
                    }
                } catch (Exception e) {
                    Gdx.app.error("LevelLoader", "Failed to spawn initial coin: " + e.getMessage(), e);
                }
            }
        }
        // Устанавливаем время следующего спавна
        nextCoinSpawnTime = MIN_COIN_SPAWN_INTERVAL;
        coinSpawnTimer = 0f;
        Gdx.app.log("LevelLoader", "Initial coins spawned, next spawn in: " + nextCoinSpawnTime + " seconds");
    }

    public void update(float deltaTime, EntitySystem entitySystem, Player player) {
        // Спавн CatMiner
        catMinerSpawnTimer += deltaTime;
        if (catMinerSpawnTimer >= 10f && !catMinerActive) {
            spawnCatMiner(entitySystem, player);
            catMinerSpawnTimer = 0f;
            catMinerActive = true;
        }

        // Проверяем, если CatMiner исчез
        boolean catMinerExists = false;
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof CatMiner && entity.isActive()) {
                catMinerExists = true;
                break;
            }
        }
        if (!catMinerExists) {
            catMinerActive = false;
        }

        // Обновляем таймер спавна монеток
        coinSpawnTimer += deltaTime;
        if (coinSpawnTimer >= nextCoinSpawnTime) {
            // Проверяем, есть ли свободные платформы
            if (platformsWithCoins.size() < platforms.size()) {
                // Спавним одну случайную монетку
                Rectangle platform = getRandomPlatform();
                // Проверяем, нет ли уже монеты на этой платформе
                if (!platformsWithCoins.contains(platform)) {
                    float x = platform.x + SPAWN_MARGIN + 
                             random.nextFloat() * (platform.width - 2 * SPAWN_MARGIN);
                    float y = platform.y + platform.height + 10;
                    Vector2 spawnPos = new Vector2(x, y);
                    
                    try {
                        Item coin = entityFactory.createItem("COIN", spawnPos, 1);
                        if (coin != null) {
                            entitySystem.addEntity(coin);
                            platformsWithCoins.add(platform);
                            Gdx.app.log("LevelLoader", String.format(
                                "Random coin spawned at: (%.1f, %.1f)",
                                x, y
                            ));
                        }
                    } catch (Exception e) {
                        Gdx.app.error("LevelLoader", "Failed to spawn random coin: " + e.getMessage(), e);
                    }
                }
            }
            
            // Сбрасываем таймер и устанавливаем новое время спавна
            coinSpawnTimer = 0f;
            nextCoinSpawnTime = MIN_COIN_SPAWN_INTERVAL + random.nextFloat() * (MAX_COIN_SPAWN_INTERVAL - MIN_COIN_SPAWN_INTERVAL);
            Gdx.app.log("LevelLoader", "Next coin spawn in: " + nextCoinSpawnTime + " seconds");
        }
    }

    private void spawnCatMiner(EntitySystem entitySystem, Player player) {
        Rectangle platform = getRandomPlatform();
        float x = platform.x + platform.width / 2;
        float y = platform.y + platform.height + 50;
        CatMiner catMiner = (CatMiner) entityFactory.createBoss("CAT_MINER", x, y);
        catMiner.setTarget(player);
        entitySystem.addEntity(catMiner);
    }

    private Rectangle getRandomPlatform() {
        int randomIndex = random.nextInt(physicsSystem.getPlatforms().size());
        return physicsSystem.getPlatforms().get(randomIndex);
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    private void spawnUniqueItemsOnMap(EntitySystem entitySystem, Player player) {
        List<Rectangle> usedPlatforms = new ArrayList<>();
        Random rand = new Random();
        for (ItemType itemType : new ItemType[] {
            ItemType.USB_SKATERT,
            ItemType.CRYPTO_SHOVEL,
            ItemType.RTX_4090,
            ItemType.TUSHENKA,
            ItemType.KNIGA,
            ItemType.WIFI_KEY
        }) {
            // Проверяем, есть ли предмет уже в инвентаре
            boolean alreadyInInventory = false;
            if (player != null) {
                alreadyInInventory = player.getInventory().getItems().stream()
                    .anyMatch(item -> item.getItemType() == itemType);
            }
            if (alreadyInInventory) continue;
            Rectangle platform;
            do {
                platform = getRandomPlatform();
            } while (usedPlatforms.contains(platform) && usedPlatforms.size() < platforms.size());
            usedPlatforms.add(platform);
            float x = platform.x + rand.nextFloat() * platform.width;
            float y = platform.y + platform.height + 10;
            Vector2 spawnPos = new Vector2(x, y);
            Item item = entityFactory.createItem(itemType.name(), spawnPos, 1);
            if (item != null) {
                Gdx.app.log("LevelLoader", "Spawning unique item " + itemType + " at: (" + x + ", " + y + ")");
                entitySystem.addEntity(item);
            } else {
                Gdx.app.error("LevelLoader", "Failed to create unique item " + itemType + " at: (" + x + ", " + y + ")");
            }
        }
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    // Метод для удаления платформы из списка при сборе монеты
    public void removePlatformFromCoinsList(Rectangle platform) {
        platformsWithCoins.remove(platform);
        Gdx.app.log("LevelLoader", "Platform removed from coins list");
    }
}