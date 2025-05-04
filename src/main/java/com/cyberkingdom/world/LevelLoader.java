package com.cyberkingdom.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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

    public LevelLoader(SpriteManager spriteManager, EntitySystem entitySystem, PhysicsSystem physicsSystem, EntityFactory entityFactory, int level) {
        EntityFactory.resetItemCounter();
        this.physicsSystem = physicsSystem;
        this.entityFactory = entityFactory;
        this.levelNumber = level;
        generateLevel1Content(entitySystem);
    }

    private void generateLevel1Content(EntitySystem entitySystem) {
        // Создаем платформы
        createPlatforms();
        for (Platform platform : platforms) {
            physicsSystem.addPlatform(platform.getRectangle());
            entitySystem.addEntity(platform);
        }

        // Спавним предметы
        spawnItemsOnMap(entitySystem);

        // Начальный спавн монет
        spawnCoins(entitySystem);
    }

    private void createPlatforms() {
        Texture platformTexture = createPlatformTexture();

        platforms.add(new Platform(new Rectangle(0, 150, 1200, 50), platformTexture)); // Основная платформа
        platforms.add(new Platform(new Rectangle(200, 300, 150, 20), platformTexture));
        platforms.add(new Platform(new Rectangle(400, 450, 200, 20), platformTexture));
        platforms.add(new Platform(new Rectangle(600, 350, 150, 20), platformTexture));
        platforms.add(new Platform(new Rectangle(800, 500, 200, 20), platformTexture));
        platforms.add(new Platform(new Rectangle(1000, 400, 150, 20), platformTexture));
        platforms.add(new Platform(new Rectangle(150, 550, 200, 20), platformTexture));
        platforms.add(new Platform(new Rectangle(350, 600, 150, 20), platformTexture));
        platforms.add(new Platform(new Rectangle(700, 250, 200, 20), platformTexture));
        platforms.add(new Platform(new Rectangle(900, 350, 150, 20), platformTexture));
    }

    private Texture createPlatformTexture() {
        try {
            return new Texture(Gdx.files.internal("assets/platform.png"));
        } catch (Exception e) {
            Pixmap pm = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pm.setColor(0.5f, 0.5f, 0.5f, 1f);
            pm.fill();
            Texture texture = new Texture(pm);
            pm.dispose();
            return texture;
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
        Item item = Item.createRandomItem(new Vector2(x, y));
        entitySystem.addEntity(item);
    }

    private void spawnCoins(EntitySystem entitySystem) {
        for (int i = 0; i < 5; i++) { // Начальные 5 монет
            spawnRandomCoin(entitySystem);
        }
    }

    private void spawnRandomCoin(EntitySystem entitySystem) {
        Rectangle platform = getRandomPlatform();
        float x = platform.x + random.nextFloat() * platform.width;
        float y = platform.y + platform.height + 10;
        Item coin = entityFactory.createItem("COIN", new Vector2(x, y), 1);
        if (coin != null) {
            entitySystem.addEntity(coin);
        }
    }

    public void update(float deltaTime, EntitySystem entitySystem, Player player) {
        // Периодический спавн монет
        if (random.nextFloat() < 0.01f) { // 1% шанс каждый кадр
            spawnRandomCoin(entitySystem);
        }

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
}