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
import com.badlogic.gdx.assets.AssetManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelLoader {
    private static int itemCount = 0;
    private static final Random random = new Random();
    private SpriteManager spriteManager;
    private PhysicsSystem physicsSystem;
    private EntityFactory entityFactory;
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
    private static final float MIN_PLATFORM_Y = 125f;  // Минимальная высота платформы
    private static final float MAX_PLATFORM_Y = 600f;  // Максимальная высота платформы
    private static final float PLATFORM_SPACING = 150f; // Увеличиваем минимальное расстояние между платформами
    private static final String[] UNIQUE_ITEMS = {
        "USB_SKATERT", "CRYPTO_SHOVEL", "RTX_4090", "TUSHENKA", "KNIGA", "WIFI_KEY"
    };
    private float coinSpawnTimer = 0f;
    private static final float MIN_COIN_SPAWN_INTERVAL = 5f; // Увеличиваем интервал между спавнами
    private static final float MAX_COIN_SPAWN_INTERVAL = 10f; // Увеличиваем максимальный интервал
    private float nextCoinSpawnTime;
    private static final int MAX_COINS = 15; // Максимальное количество монет на карте
    private static final float MIN_COIN_DISTANCE = 50f; // Минимальное расстояние между монетами
    private List<Item> activeCoins = new ArrayList<>(); // Список активных монет

    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem, PhysicsSystem physicsSystem, EntityFactory entityFactory, int level) {
        this.spriteManager = spriteManager;
        // this.entitySystem = entitySystem; // Удаляем, EntitySystem передается в loadLevel
        this.physicsSystem = physicsSystem;
        this.entityFactory = entityFactory;
        this.levelNumber = level;
        this.nextCoinSpawnTime = MIN_COIN_SPAWN_INTERVAL + random.nextFloat() * (MAX_COIN_SPAWN_INTERVAL - MIN_COIN_SPAWN_INTERVAL);
    }

    // Метод для загрузки уровня, принимает EntitySystem и AssetManager
    public void loadLevel(int levelNumber, EntitySystem entitySystem, AssetManager assetManager) {
        this.levelNumber = levelNumber;
        resetItemCounter();
        activeCoins.clear();
        catMinerActive = false;
        catMinerSpawnTimer = 0f;
        coinSpawnTimer = 0f;
        nextCoinSpawnTime = MIN_COIN_SPAWN_INTERVAL + random.nextFloat() * (MAX_COIN_SPAWN_INTERVAL - MIN_COIN_SPAWN_INTERVAL);

        generateLevel(entitySystem, assetManager);
        Gdx.app.log("LevelLoader", "loadLevel called for level: " + levelNumber);
    }

    // Изменяем сигнатуру, чтобы принимать AssetManager
    private void generateLevel(EntitySystem entitySystem, AssetManager assetManager) {
        Gdx.app.log("LevelLoader", "Starting level generation for level " + levelNumber);
        
        // Очищаем предыдущие платформы
        platforms.clear();
        physicsSystem.clearPlatforms();
        Gdx.app.log("LevelLoader", "Cleared previous platforms");

        // Создаем платформы в зависимости от уровня, используя AssetManager
        createPlatforms(assetManager);
        Gdx.app.log("LevelLoader", "Created " + platforms.size() + " platforms");
        
        // Добавляем платформы в физическую систему и систему сущностей
        for (Platform platform : platforms) {
            physicsSystem.addPlatform(platform.getRectangle());
            entitySystem.addEntity(platform);
            Gdx.app.log("LevelLoader", "Added platform to systems: " + platform.getRectangle());
        }

        // Спавним уникальные предметы только если их нет в инвентаре
        // Этот метод нужно изменить, чтобы он не зависел от игрока здесь. Игрок уже есть в EntitySystem.
        spawnUniqueItemsOnMap(entitySystem);

        // Спавним предметы
        spawnItemsOnMap(entitySystem);
        Gdx.app.log("LevelLoader", "Spawned " + totalItems + " items");

        Gdx.app.log("LevelLoader", "Level generation completed");
    }

    // Изменяем сигнатуру, чтобы принимать AssetManager
    private void createPlatforms(AssetManager assetManager) {
        Gdx.app.log("LevelLoader", "Creating platforms using AssetManager");
        
        // Получаем текстуру платформы из AssetManager
        String platformTexturePath = "assets/platform.png";
        if (!assetManager.isLoaded(platformTexturePath)) {
            Gdx.app.error("LevelLoader", "Platform texture not loaded by AssetManager!");
            // На этом этапе текстура должна быть загружена LoadingScreen. Если нет, это ошибка загрузки ресурсов.
            return; // Не можем создать платформы без текстуры
        }
        
        Texture platformTexture = assetManager.get(platformTexturePath, Texture.class);
        
        if (platformTexture == null) {
            Gdx.app.error("LevelLoader", "Failed to get platform texture from AssetManager!");
            return; // Не можем создать платформы без текстуры
        }

        float textureWidth = platformTexture.getWidth();
        float platformWidth = textureWidth * 2; // Используем ширину текстуры, умноженную на 2

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
        Platform randomPlatform = getRandomPlatform();
        if (randomPlatform == null) return; // Handle case where no non-ground platforms exist
        Rectangle platform = randomPlatform.getRectangle();

        // Добавляем отступы от краев платформы
        float x = platform.x + SPAWN_MARGIN + random.nextFloat() * (platform.width - 2 * SPAWN_MARGIN);
        float y = platform.y + platform.height + 10;
        Vector2 spawnPos = new Vector2(x, y);

        Item coin = entityFactory.createItem("COIN", spawnPos, 1);
        if (coin != null) {
            Gdx.app.log("LevelLoader", "Spawning coin at: (" + x + ", " + y + ")");
            entitySystem.addEntity(coin);
            activeCoins.add(coin); // Добавляем монету в список активных монет
        } else {
            Gdx.app.error("LevelLoader", "Failed to create coin at: (" + x + ", " + y + ")");
        }
    }

    private void spawnCoinRandomly(EntitySystem entitySystem) {
        // Пытаемся найти подходящую платформу и позицию для монеты
        for (int attempt = 0; attempt < 10; attempt++) { // Ограничиваем количество попыток
            Platform randomPlatform = getRandomPlatform();
            if (randomPlatform == null) continue; // Пропускаем, если нет подходящих платформ
            Rectangle platformRect = randomPlatform.getRectangle();
            // Не спавним монеты на земле
            if (randomPlatform.isGround()) continue;

            // Генерируем случайную позицию на платформе с отступами и случайной высотой
            float x = platformRect.x + SPAWN_MARGIN +
                     (random.nextFloat() * (platformRect.width - 2 * SPAWN_MARGIN));
            float y = platformRect.y + platformRect.height + 10 +
                     (random.nextFloat() * 20); // Добавляем случайную высоту
            Vector2 spawnPos = new Vector2(x, y);

            // Проверяем расстояние до существующих монет
            boolean tooClose = false;
            for (Item existingCoin : activeCoins) {
                if (existingCoin.isActive()) { // Проверяем только активные монеты
                    float distance = spawnPos.dst(existingCoin.getPosition());
                    if (distance < MIN_COIN_DISTANCE) {
                        tooClose = true;
                        break;
                    }
                }
            }

            // Если место подходит, спавним монету
            if (!tooClose) {
                try {
                    Item coin = entityFactory.createItem("COIN", spawnPos, 1);
                    if (coin != null) {
                        entitySystem.addEntity(coin);
                        activeCoins.add(coin);
                        Gdx.app.log("LevelLoader", String.format(
                            "Random coin spawned at: (%.1f, %.1f), total active coins: %d",
                            x, y, activeCoins.size()
                        ));
                        return; // Монета успешно заспавнена, выходим из попыток
                    }
                } catch (Exception e) {
                    Gdx.app.error("LevelLoader", "Failed to spawn random coin: " + e.getMessage(), e);
                }
            }
        }
        Gdx.app.log("LevelLoader", "Failed to find suitable spot for coin after 10 attempts.");
    }

    private void spawnUniqueItemsOnMap(EntitySystem entitySystem) {
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
            // Проверяем, есть ли предмет уже в EntitySystem (т.е. на карте) - временно, пока не будет инвентаря
            boolean alreadyOnMap = false;
            for (GameEntity entity : entitySystem.getEntities()) {
                 if (entity instanceof Item && ((Item)entity).getItemType() == itemType && entity.isActive()) {
                     alreadyOnMap = true;
                     break;
                 }
            }
            if (alreadyOnMap) continue;

            Platform platformObject;
            do {
                platformObject = getRandomPlatform();
                if (platformObject == null) continue; // Пропускаем, если нет подходящих платформ
            } while (usedPlatforms.contains(platformObject.getRectangle()) && usedPlatforms.size() < platforms.size());
            
            if (platformObject == null) continue; // Если не нашли подходящую платформу после попыток

            Rectangle platform = platformObject.getRectangle();
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

        // Обновляем список активных монет, получая их из EntitySystem
        activeCoins.clear();
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                // Проверяем, что это монета и она активна
                if (item.getItemType() == ItemType.COIN && item.isActive()) {
                    activeCoins.add(item);
                }
            }
        }

        // Спавн монет во время игры
        coinSpawnTimer += deltaTime;
        if (coinSpawnTimer >= nextCoinSpawnTime) {
            // Проверяем, не превышено ли максимальное количество монет
            if (activeCoins.size() < MAX_COINS) {
                // Пытаемся заспавнить 1-3 монеты
                int coinsToSpawn = 1 + random.nextInt(3);
                for (int i = 0; i < coinsToSpawn; i++) {
                    spawnCoinRandomly(entitySystem);
                }
            }
            // Сбрасываем таймер и устанавливаем время следующего спавна
            coinSpawnTimer = 0f;
            nextCoinSpawnTime = MIN_COIN_SPAWN_INTERVAL +
                                 random.nextFloat() * (MAX_COIN_SPAWN_INTERVAL - MIN_COIN_SPAWN_INTERVAL);
            Gdx.app.log("LevelLoader", "Next coin spawn in: " + nextCoinSpawnTime + " seconds");
        }
    }

    private void spawnCatMiner(EntitySystem entitySystem, Player player) {
        Platform randomPlatform = getRandomPlatform();
        if (randomPlatform == null) return; // Handle case where no non-ground platforms exist
        Rectangle platform = randomPlatform.getRectangle();
        float x = platform.x + platform.width / 2;
        float y = platform.y + platform.height + 50;
        CatMiner catMiner = (CatMiner) entityFactory.createBoss("CAT_MINER", x, y);
        catMiner.setTarget(player);
        entitySystem.addEntity(catMiner);
    }

    private Platform getRandomPlatform() {
        // Выбираем случайную платформу, кроме земли
        List<Platform> nonGroundPlatforms = new ArrayList<>();
        for (Platform platform : platforms) {
            if (!platform.isGround()) {
                nonGroundPlatforms.add(platform);
            }
        }
        if (nonGroundPlatforms.isEmpty()) {
            return null;
        }
        return nonGroundPlatforms.get(random.nextInt(nonGroundPlatforms.size()));
    }

    private void resetItemCounter() {
        itemCount = 0;
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

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    // Метод для удаления платформы из списка при сборе монеты - возможно, больше не нужен
    public void removePlatformFromCoinsList(Rectangle platform) {
        // Этот метод, возможно, нуждается в доработке, если монеты не привязаны к платформам физически.
        // Сейчас он просто удаляет платформу, перекрывающую переданный прямоугольник.
        platforms.removeIf(p -> p.getRectangle().overlaps(platform));
    }

}