package com.cyberkingdom.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.items.Item;
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
    private SpriteManager spriteManager;
    private static final float MIN_PLATFORM_Y = 125f;  // Минимальная высота платформы
    private static final float MAX_PLATFORM_Y = 600f;  // Максимальная высота платформы
    private static final float PLATFORM_SPACING = 150f; // Увеличиваем минимальное расстояние между платформами

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

        // Спавним предметы
        spawnItemsOnMap(entitySystem);
        Gdx.app.log("LevelLoader", "Spawned " + totalItems + " items");

        // Начальный спавн монет
        spawnCoins(entitySystem);
        Gdx.app.log("LevelLoader", "Level generation completed");
    }

    private void createPlatforms() {
        Gdx.app.log("LevelLoader", "Creating platforms");
        Texture platformTexture = spriteManager.getFrames("Platform")[0].getTexture();
        float textureWidth = platformTexture.getWidth(); // 64 пикселя
        float platformWidth = textureWidth * 2; // Делаем платформу в 2 раза шире текстуры (128 пикселей)

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
        float x = platform.x + random.nextFloat() * platform.width;
        float y = platform.y + platform.height + 10;
        
        GameEntity item = entityFactory.createRandomItem(x, y);
        if (item != null) {
            entitySystem.addEntity(item);
            Gdx.app.debug("LevelLoader", "Spawned random item: " + ((Item)item).getItemType());
        }
    }

    private void spawnCoins(EntitySystem entitySystem) {
        for (Platform platform : platforms) {
            int coinsOnPlatform = 1 + random.nextInt(2);
            for (int i = 0; i < coinsOnPlatform; i++) {
                float x = platform.getRectangle().x + random.nextFloat() * (platform.getRectangle().width - 32);
                float y = platform.getRectangle().y + platform.getRectangle().height + 10;
                try {
                    TextureRegion[] coinFrames = spriteManager.getFrames("COIN");
                    if (coinFrames == null || coinFrames.length == 0) {
                        Gdx.app.error("LevelLoader", "Failed to get coin frames from SpriteManager");
                        continue;
                    }
                    Texture coinTexture = coinFrames[0].getTexture();
                    if (coinTexture == null) {
                        Gdx.app.error("LevelLoader", "Failed to get coin texture from frames");
                        continue;
                    }
                    Item coin = new Item(new Vector2(x, y), Item.ITEM_COIN, 1, coinTexture);
                    entitySystem.addEntity(coin);
                    Gdx.app.debug("LevelLoader", String.format(
                        "Spawned coin at: (%.1f, %.1f) with texture: %s",
                        x, y, coin.getTexture() != null ? "loaded" : "null"
                    ));
                } catch (Exception e) {
                    Gdx.app.error("LevelLoader", "Failed to spawn coin: " + e.getMessage(), e);
                }
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

    private Texture createCoinTexture() {
        Pixmap pm = null;
        try {
            pm = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            
            pm.setColor(0, 0, 0, 0);
            pm.fill();
            
            pm.setColor(1f, 0.8f, 0, 1f);
            pm.fillCircle(16, 16, 14);
            
            pm.setColor(1f, 1f, 0.8f, 0.8f);
            pm.fillCircle(12, 12, 4);
            
            pm.setColor(0.8f, 0.6f, 0, 1f);
            pm.drawCircle(16, 16, 14);
            
            return new Texture(pm);
        } catch (Exception e) {
            Gdx.app.error("LevelLoader", "Failed to create coin texture", e);
            throw new RuntimeException("Failed to create coin texture", e);
        } finally {
            if (pm != null) {
                pm.dispose();
            }
        }
    }
}