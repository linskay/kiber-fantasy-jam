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
import java.util.Arrays;
import java.util.Collections;
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
    private static final float MIN_COIN_SPAWN_INTERVAL = 5f;
    private static final float MAX_COIN_SPAWN_INTERVAL = 10f; // Увеличиваем максимальный интервал
    private float nextCoinSpawnTime;
    private static final int MAX_COINS = 15; // Максимальное количество монет на карте
    private static final float MIN_COIN_DISTANCE = 50f; // Минимальное расстояние между монетами
    private List<Item> activeCoins = new ArrayList<>(); // Список активных монет
    private boolean dedinsaidSpawned = false;
    private DedinsaidBoss dedinsaidBoss = null;
    private boolean witchVPNSpawned = false;
    private WitchVPN witchVPN = null;
    private Player player;
    private AssetManager assetManager;

    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem, PhysicsSystem physicsSystem, EntityFactory entityFactory, int level) {
        this.spriteManager = spriteManager;
        this.physicsSystem = physicsSystem;
        this.entityFactory = entityFactory;
        this.levelNumber = level;
        this.nextCoinSpawnTime = MIN_COIN_SPAWN_INTERVAL + random.nextFloat() * (MAX_COIN_SPAWN_INTERVAL - MIN_COIN_SPAWN_INTERVAL);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    // Метод для загрузки уровня, принимает EntitySystem и AssetManager
    public void loadLevel(int levelNumber, EntitySystem entitySystem, AssetManager assetManager) {
        Gdx.app.log("LevelLoader", "Starting loadLevel for level: " + levelNumber);
        this.levelNumber = levelNumber;
        this.assetManager = assetManager;
        resetItemCounter();
        activeCoins.clear();
        catMinerActive = false;
        catMinerSpawnTimer = 0f;
        coinSpawnTimer = 0f;
        dedinsaidSpawned = false;
        dedinsaidBoss = null;
        witchVPNSpawned = false;
        witchVPN = null;
        nextCoinSpawnTime = MIN_COIN_SPAWN_INTERVAL + random.nextFloat() * (MAX_COIN_SPAWN_INTERVAL - MIN_COIN_SPAWN_INTERVAL);

        // Находим игрока в EntitySystem
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Player) {
                this.player = (Player) entity;
                break;
            }
        }

        if (this.player == null) {
            Gdx.app.error("LevelLoader", "Player not found in EntitySystem!");
            return;
        }

        generateLevel(entitySystem, assetManager);
        Gdx.app.log("LevelLoader", "Level " + levelNumber + " loaded successfully");
    }

    // Изменяем сигнатуру, чтобы принимать AssetManager
    private void generateLevel(EntitySystem entitySystem, AssetManager assetManager) {
        Gdx.app.log("LevelLoader", "Starting generateLevel for level: " + levelNumber);
        
        // Очищаем предыдущие платформы
        platforms.clear();
        Gdx.app.log("LevelLoader", "Cleared previous platforms");
        
        // Создаем новые платформы
        createPlatforms(assetManager);
        Gdx.app.log("LevelLoader", "Created " + platforms.size() + " platforms");
        
        // Добавляем платформы в систему
        for (Platform platform : platforms) {
            entitySystem.addEntity(platform);
            physicsSystem.addPlatform(platform.getRectangle());
        }
        Gdx.app.log("LevelLoader", "Added platforms to systems");

        // Спавним предметы на карте (уникальные предметы сразу)
        spawnInitialItems(entitySystem);
        Gdx.app.log("LevelLoader", "Spawned initial items on map");

        // Логика спавна боссов убрана из generateLevel

        Gdx.app.log("LevelLoader", "Level generation completed");
    }

    // Изменяем сигнатуру, чтобы принимать AssetManager
    private void createPlatforms(AssetManager assetManager) {
        Gdx.app.log("LevelLoader", "Starting createPlatforms");
        
        // Создаем начальную платформу (землю)
        Rectangle groundRect = new Rectangle(0, GROUND_Y, LEVEL_WIDTH, PLATFORM_HEIGHT);
        Texture platformTexture = assetManager.get("assets/platform.png", Texture.class);
        Platform groundPlatform = new Platform(groundRect, platformTexture);
        groundPlatform.setGround(true);
        platforms.add(groundPlatform);

        // Фиксированные платформы для первого уровня
        if (levelNumber == 1) {
            // Платформа 1
            Rectangle platform1Rect = new Rectangle(100, 200, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform1 = new Platform(platform1Rect, platformTexture);
            platforms.add(platform1);

            // Платформа 2
            Rectangle platform2Rect = new Rectangle(250, 300, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform2 = new Platform(platform2Rect, platformTexture);
            platforms.add(platform2);

            // Платформа 3
            Rectangle platform3Rect = new Rectangle(400, 400, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform3 = new Platform(platform3Rect, platformTexture);
            platforms.add(platform3);

            // Платформа 4
            Rectangle platform4Rect = new Rectangle(550, 500, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform4 = new Platform(platform4Rect, platformTexture);
            platforms.add(platform4);

            // Платформа 5
            Rectangle platform5Rect = new Rectangle(700, 400, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform5 = new Platform(platform5Rect, platformTexture);
            platforms.add(platform5);

            // Платформа 6
            Rectangle platform6Rect = new Rectangle(850, 300, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform6 = new Platform(platform6Rect, platformTexture);
            platforms.add(platform6);

            // Платформа 7
            Rectangle platform7Rect = new Rectangle(1000, 200, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform7 = new Platform(platform7Rect, platformTexture);
            platforms.add(platform7);

            // Платформа 8
            Rectangle platform8Rect = new Rectangle(150, 350, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform8 = new Platform(platform8Rect, platformTexture);
            platforms.add(platform8);

            // Платформа 9
            Rectangle platform9Rect = new Rectangle(300, 450, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform9 = new Platform(platform9Rect, platformTexture);
            platforms.add(platform9);

            // Платформа 10
            Rectangle platform10Rect = new Rectangle(450, 550, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform10 = new Platform(platform10Rect, platformTexture);
            platforms.add(platform10);
        }
        // Фиксированные платформы для второго уровня
        else if (levelNumber == 2) {
            // Платформа 1
            Rectangle platform1Rect = new Rectangle(150, 250, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform1 = new Platform(platform1Rect, platformTexture);
            platforms.add(platform1);

            // Платформа 2
            Rectangle platform2Rect = new Rectangle(300, 350, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform2 = new Platform(platform2Rect, platformTexture);
            platforms.add(platform2);

            // Платформа 3
            Rectangle platform3Rect = new Rectangle(450, 450, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform3 = new Platform(platform3Rect, platformTexture);
            platforms.add(platform3);

            // Платформа 4
            Rectangle platform4Rect = new Rectangle(600, 550, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform4 = new Platform(platform4Rect, platformTexture);
            platforms.add(platform4);

            // Платформа 5
            Rectangle platform5Rect = new Rectangle(750, 450, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform5 = new Platform(platform5Rect, platformTexture);
            platforms.add(platform5);

            // Платформа 6
            Rectangle platform6Rect = new Rectangle(900, 350, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform6 = new Platform(platform6Rect, platformTexture);
            platforms.add(platform6);

            // Платформа 7
            Rectangle platform7Rect = new Rectangle(1050, 250, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform7 = new Platform(platform7Rect, platformTexture);
            platforms.add(platform7);

            // Платформа 8
            Rectangle platform8Rect = new Rectangle(200, 400, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform8 = new Platform(platform8Rect, platformTexture);
            platforms.add(platform8);

            // Платформа 9
            Rectangle platform9Rect = new Rectangle(350, 500, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform9 = new Platform(platform9Rect, platformTexture);
            platforms.add(platform9);

            // Платформа 10
            Rectangle platform10Rect = new Rectangle(500, 600, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform10 = new Platform(platform10Rect, platformTexture);
            platforms.add(platform10);
        }
        // Фиксированные платформы для третьего уровня
        else if (levelNumber == 3) {
            // Платформа 1
            Rectangle platform1Rect = new Rectangle(200, 300, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform1 = new Platform(platform1Rect, platformTexture);
            platforms.add(platform1);

            // Платформа 2
            Rectangle platform2Rect = new Rectangle(350, 400, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform2 = new Platform(platform2Rect, platformTexture);
            platforms.add(platform2);

            // Платформа 3
            Rectangle platform3Rect = new Rectangle(500, 500, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform3 = new Platform(platform3Rect, platformTexture);
            platforms.add(platform3);

            // Платформа 4
            Rectangle platform4Rect = new Rectangle(650, 600, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform4 = new Platform(platform4Rect, platformTexture);
            platforms.add(platform4);

            // Платформа 5
            Rectangle platform5Rect = new Rectangle(800, 500, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform5 = new Platform(platform5Rect, platformTexture);
            platforms.add(platform5);

            // Платформа 6
            Rectangle platform6Rect = new Rectangle(950, 400, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform6 = new Platform(platform6Rect, platformTexture);
            platforms.add(platform6);

            // Платформа 7
            Rectangle platform7Rect = new Rectangle(1100, 300, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform7 = new Platform(platform7Rect, platformTexture);
            platforms.add(platform7);

            // Платформа 8
            Rectangle platform8Rect = new Rectangle(250, 450, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform8 = new Platform(platform8Rect, platformTexture);
            platforms.add(platform8);

            // Платформа 9
            Rectangle platform9Rect = new Rectangle(400, 550, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform9 = new Platform(platform9Rect, platformTexture);
            platforms.add(platform9);

            // Платформа 10
            Rectangle platform10Rect = new Rectangle(550, 650, PLATFORM_WIDTH, PLATFORM_HEIGHT);
            Platform platform10 = new Platform(platform10Rect, platformTexture);
            platforms.add(platform10);
        }

        Gdx.app.log("LevelLoader", "Created " + platforms.size() + " platforms");
    }

    private void spawnItemsOnMap(EntitySystem entitySystem) {
        // Этот метод теперь используется только для случайного спавна монет во время игры, если spawnRandomCoin вызывается здесь
        // Или он может быть удален, если spawnRandomCoin вызывается напрямую из update
        // Оставляем его пока пустым, или для будущей логики спавна других случайных вещей
        Gdx.app.log("LevelLoader", "spawnItemsOnMap called (currently only for random coins if logic is here)");
    }

    // Новый метод для спавна начальных предметов (6 уникальных предметов)
    private void spawnInitialItems(EntitySystem entitySystem) {
        Gdx.app.log("LevelLoader", "Starting spawnInitialItems for level: " + levelNumber);
        // Спавним 6 уникальных предметов
        if (UNIQUE_ITEMS.length < 6) {
             Gdx.app.error("LevelLoader", "Not enough unique item types defined to spawn 6 items!");
             return;
        }

        List<String> itemsToSpawn = new ArrayList<>();
        // Выбираем 6 случайных уникальных предметов из списка UNIQUE_ITEMS
        List<String> uniqueItemsList = new ArrayList<>(Arrays.asList(UNIQUE_ITEMS));
        Collections.shuffle(uniqueItemsList);
        for (int i = 0; i < 6 && i < uniqueItemsList.size(); i++) {
            itemsToSpawn.add(uniqueItemsList.get(i));
        }
        
        // Спавним выбранные предметы на случайных платформах
        for (String itemType : itemsToSpawn) {
            Platform randomPlatform = getRandomPlatform();
            if (randomPlatform == null) {
                 Gdx.app.error("LevelLoader", "Cannot spawn initial item: " + itemType + ", no non-ground platforms available.");
                 continue; // Пропускаем этот предмет и пытаемся заспавнить следующий
            }
            Rectangle platform = randomPlatform.getRectangle();

            float x = platform.x + SPAWN_MARGIN + random.nextFloat() * (platform.width - 2 * SPAWN_MARGIN);
            float y = platform.y + platform.height + 10;
            Vector2 position = new Vector2(x, y);

            Item item = entityFactory.createItem(itemType, position, 1);
            if (item != null) {
                entitySystem.addEntity(item);
                Gdx.app.log("LevelLoader", "Spawned initial unique item: " + itemType + " at position: " + x + ", " + y);
            } else {
                Gdx.app.error("LevelLoader", "Failed to create initial unique item: " + itemType);
            }
        }
         Gdx.app.log("LevelLoader", "Finished spawning initial items.");
    }

    // Переименованный метод для спавна случайной монеты - вызывается в update
    private void spawnRandomCoin(EntitySystem entitySystem) {
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

    private void spawnUniqueItems(EntitySystem entitySystem, AssetManager assetManager) {
        Gdx.app.log("LevelLoader", "Starting spawnUniqueItems");
        for (String itemType : UNIQUE_ITEMS) {
            if (random.nextFloat() < 0.3f) { // 30% шанс появления каждого предмета
                Platform randomPlatform = getRandomPlatform();
                if (randomPlatform != null) {
                    Rectangle platform = randomPlatform.getRectangle();
                    float x = platform.x + SPAWN_MARGIN + random.nextFloat() * (platform.width - 2 * SPAWN_MARGIN);
                    float y = platform.y + platform.height + 10;
                    Vector2 position = new Vector2(x, y);
                    Item item = entityFactory.createItem(itemType, position, 1);
                    if (item != null) {
                        entitySystem.addEntity(item);
                        Gdx.app.log("LevelLoader", "Spawned unique item: " + itemType + " at position: " + x + ", " + y);
                    }
                }
            }
        }
    }

    private void spawnDedinsaidBoss(EntitySystem entitySystem, AssetManager assetManager) {
        Gdx.app.log("LevelLoader", "Starting spawnDedinsaidBoss");
        if (player != null) {
            // Спавним босса дальше справа и выше от игрока
            float x = player.getPosition().x + 500; // Увеличиваем расстояние по X
            float y = GROUND_Y + 300; // Спавним выше земли, например, на уровне верхних платформ
            Vector2 position = new Vector2(x, y);
            DedinsaidBoss boss = new DedinsaidBoss(position, spriteManager, entitySystem);
            boss.setTarget(player);
            entitySystem.addEntity(boss);
            dedinsaidBoss = boss;
            dedinsaidSpawned = true;
            Gdx.app.log("LevelLoader", "Spawned Dedinsaid boss at position: " + x + ", " + y);
        } else {
            Gdx.app.error("LevelLoader", "Cannot spawn Dedinsaid boss: player is null");
        }
    }

    private void spawnWitchVPN(EntitySystem entitySystem, AssetManager assetManager) {
        Gdx.app.log("LevelLoader", "Starting spawnWitchVPN");
        if (player != null) {
            // Спавним босса дальше справа и выше от игрока
            float x = player.getPosition().x + 400; // Увеличиваем расстояние по X еще больше
            float y = player.getPosition().y + 200; // Добавляем отступ по Y
            Vector2 position = new Vector2(x, y);
            WitchVPN boss = new WitchVPN(position.x, position.y, physicsSystem, spriteManager);
            boss.setTarget(player);
            entitySystem.addEntity(boss);
            witchVPN = boss;
            witchVPNSpawned = true;
            Gdx.app.log("LevelLoader", "Spawned Witch VPN boss at position: " + x + ", " + y);
        } else {
            Gdx.app.error("LevelLoader", "Cannot spawn Witch VPN boss: player is null");
        }
    }

    public void update(float deltaTime, EntitySystem entitySystem, Player player) {
        // Проверка условий для спавна ведьмы VPN на первом уровне
        if (levelNumber == 1 && !witchVPNSpawned) {
            boolean allItemsCollected = true;
            for (GameEntity entity : entitySystem.getEntities()) {
                if (entity instanceof Item && entity.isActive()) {
                    // Если найден хотя бы один активный предмет (кроме монеты), значит, не все уникальные предметы собраны
                    // Уникальные предметы определены в UNIQUE_ITEMS, но здесь просто проверяем все не-монеты
                    if (itemIsUnique(entity)) { // Проверяем, является ли предмет уникальным
                        allItemsCollected = false;
                        break;
                    }
                }
            }
            
            if (allItemsCollected) {
                spawnWitchVPN(entitySystem, this.assetManager);
                witchVPNSpawned = true;
            }
        }

        // Обновление ведьмы VPN, если она существует
        if (witchVPN != null && witchVPN.isActive()) {
            witchVPN.update(deltaTime);
        }

        // Проверка условий для спавна Дединсайда на втором уровне
        if (levelNumber == 2 && !dedinsaidSpawned) {
            int activeNonCoinItemCount = 0; // Счетчик активных предметов, исключая монеты
            for (GameEntity entity : entitySystem.getEntities()) {
                if (entity instanceof Item) {
                    Item item = (Item) entity;
                    if (item.isActive() && item.getItemType() != ItemType.COIN) {
                        activeNonCoinItemCount++; // Учитываем только активные предметы, не монеты
                    }
                }
            }

            // Добавляем логирование
            Gdx.app.log("LevelLoader", "Level 2 boss spawn check: activeNonCoinItemCount = " + activeNonCoinItemCount);

            // Босс появляется, когда активных предметов (не монет) не осталось (т.е. все уникальные предметы собраны)
            if (activeNonCoinItemCount == 0) {
                spawnDedinsaid(entitySystem, player);
                dedinsaidSpawned = true;
            }
        }

        // Обновление Дединсайда, если он существует
        if (dedinsaidBoss != null && !dedinsaidBoss.isDefeated()) {
            dedinsaidBoss.update(deltaTime);
            
            // Проверка столкновения с игроком
            if (player.getCollisionBounds().overlaps(dedinsaidBoss.getCollisionBounds())) {
                // Если игрок падает на босса сверху
                if (player.getVelocity().y < 0 && 
                    player.getPosition().y > dedinsaidBoss.getPosition().y + dedinsaidBoss.getCollisionBounds().height / 2) {
                    dedinsaidBoss.takeHit();
                    player.setVelocity(player.getVelocity().x, 300f); // Исправленная сигнатура
                }
            }
        }

        // Обновляем список активных монет, получая их из EntitySystem
        activeCoins.clear();
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                // Проверяем, что это монета и она активна
                if (item.getItemType() == ItemType.COIN && item.isActive()) {
                    activeCoins.add(item);
                } else if (itemIsUnique(item)) { // Если это уникальный предмет и он активен, не добавляем в список монет
                    // Оставляем уникальные предметы в EntitySystem пока не собраны
                }
            }
        }

        // Спавн CatMiner (Эта логика спавна, возможно, должна остаться здесь, так как зависит от времени и сбора предметов на 3 уровне)
        catMinerSpawnTimer += deltaTime;
        if (levelNumber == 3 && catMinerSpawnTimer >= 10f && !catMinerActive) { // Добавляем проверку уровня
             int activeNonCoinItemCount = 0;
             for (GameEntity entity : entitySystem.getEntities()) {
                 if (entity instanceof Item) {
                     Item item = (Item) entity;
                     if (item.isActive() && item.getItemType() != ItemType.COIN) {
                         activeNonCoinItemCount++;
                     }
                 }
             }
             if (activeNonCoinItemCount == 0) {
                 spawnCatMiner(entitySystem, player);
                 catMinerSpawnTimer = 0f;
                 catMinerActive = true;
             }
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

        // Спавн монет во время игры
        coinSpawnTimer += deltaTime;
        if (coinSpawnTimer >= nextCoinSpawnTime) {
            // Проверяем, не превышено ли максимальное количество монет
            if (activeCoins.size() < MAX_COINS) {
                // Пытаемся заспавнить 1-3 монеты
                int coinsToSpawn = 1 + random.nextInt(3);
                for (int i = 0; i < coinsToSpawn; i++) {
                    spawnRandomCoin(entitySystem);
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
        CatMiner catMiner = (CatMiner) entityFactory.createBoss("CAT_MINER", x, y, entitySystem);
        catMiner.setTarget(player);
        entitySystem.addEntity(catMiner);
    }

    private void spawnDedinsaid(EntitySystem entitySystem, Player player) {
        // Спавним босса в центре уровня
        float x = LEVEL_WIDTH / 2;
        float y = GROUND_Y + 100;
        dedinsaidBoss = (DedinsaidBoss) entityFactory.createBoss("DEDINSAID", x, y, entitySystem);
        if (dedinsaidBoss != null) {
            dedinsaidBoss.setTarget(player);
            entitySystem.addEntity(dedinsaidBoss);
            Gdx.app.log("LevelLoader", "Dedinsaid boss spawned at: (" + x + ", " + y + ")");
        }
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

    // Вспомогательный метод для определения, является ли предмет уникальным
    private boolean itemIsUnique(GameEntity entity) {
        if (entity instanceof Item) {
            Item item = (Item) entity;
            for (String uniqueItemName : UNIQUE_ITEMS) {
                if (item.getItemType().name().equals(uniqueItemName)) {
                    return true;
                }
            }
        }
        return false;
    }
}