package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.items.ItemType;
import com.cyberkingdom.screens.GameScreen;
import com.cyberkingdom.rendering.SpriteManager;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.entities.Projectile;
import com.cyberkingdom.entities.FlyingBook;

import java.util.Random;

public class EntityFactory {
    private static int itemCount = 0;
    private static final Random random = new Random();
    private SpriteManager spriteManager;
    private PhysicsSystem physicsSystem;

    // Используйте enum или константы, чтобы избежать ошибок в строках
    private static final String[] ITEMS = {
            "USB_SKATERT", "CRYPTO_SHOVEL", "RTX_4090", "TUSHENKA", "KNIGA", "WIFI_KEY"
    };

    public EntityFactory(SpriteManager spriteManager, PhysicsSystem physicsSystem) {
        this.spriteManager = spriteManager;
        this.physicsSystem = physicsSystem;
    }

    public GameEntity createPlayer(float x, float y) {
        return createPlayer(new Vector2(x, y), null);
    }

    public GameEntity createPlayer(Vector2 position, GameScreen gameScreen) {
        Gdx.app.log("EntityFactory", "Creating player at position: " + position.x + ", " + position.y);
        Player player = new Player(position, spriteManager);
        if (gameScreen != null) {
            Gdx.app.log("EntityFactory", "Setting GameScreen for player");
            player.setGameScreen(gameScreen);
            player.setEntitySystem(gameScreen.getEntitySystem());
        } else {
            Gdx.app.error("EntityFactory", "GameScreen is null when creating player");
        }
        return player;
    }

    public GameEntity createEnemy(String name, float x, float y) {
        Enemy.EnemyType type;
        try {
            type = Enemy.EnemyType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown enemy type: " + name + ". Using fallback type TROLL_BOT.");
            type = Enemy.EnemyType.TROLL_BOT;
        }
        Enemy enemy = new Enemy(type, x, y, spriteManager);
        return enemy;
    }

    public GameEntity createBoss(String bossType, float x, float y, EntitySystem entitySystem) {
        Vector2 position = new Vector2(x, y);
        switch (bossType) {
            case "CAT_MINER":
                return new CatMiner(x, y, spriteManager);
            case "DEDINSAID":
                return new DedinsaidBoss(position, spriteManager, entitySystem);
            case "WITCH_VPN":
                return new WitchVPN(x, y, physicsSystem, spriteManager);
            default:
                Gdx.app.error("EntityFactory", "Unknown boss type: " + bossType);
                return null;
        }
    }

    public Item createItem(String itemType, Vector2 position, int quantity) {
        try {
            Gdx.app.log("EntityFactory", "Creating item: " + itemType + " at " + position);
            TextureRegion[] frames = spriteManager.getFrames(itemType);
            if (frames == null || frames.length == 0) {
                Gdx.app.error("EntityFactory", "No frames found for item: " + itemType);
                return null;
            }

            Texture texture = frames[0].getTexture();
            if (texture == null) {
                Gdx.app.error("EntityFactory", "No texture found for item: " + itemType);
                return null;
            }

            // Находим описание предмета
            String description = "";
            for (Item.ItemData data : Item.ALL_ITEMS) {
                if (data.id.equals(itemType)) {
                    description = data.description + "\n\"" + data.effect + "\"";
                    break;
                }
            }

            ItemType type;
            try {
                type = ItemType.valueOf(itemType);
            } catch (IllegalArgumentException e) {
                Gdx.app.error("EntityFactory", "Unknown item type: " + itemType);
                return null;
            }

            Item item = new Item(type, position, quantity, spriteManager);
            item.setName(itemType);
            
            Gdx.app.log("EntityFactory", "Created item: " + itemType + " with texture size: " + 
                texture.getWidth() + "x" + texture.getHeight());
            return item;
        } catch (Exception e) {
            Gdx.app.error("EntityFactory", "Failed to create item: " + itemType, e);
            return null;
        }
    }

    public GameEntity createRandomItem(float x, float y) {
        String randomItemName = ITEMS[random.nextInt(ITEMS.length)];
        return createItem(randomItemName, new Vector2(x, y), 1);
    }

    public static void resetItemCounter() {
        itemCount = 0;
    }

    private String getDescriptionForItem(String itemType) {
        switch (itemType.toUpperCase()) {
            case "USB":
            case "USB_SCATERT":
                return "Восстанавливает 50% HP\n\"Самобранка 2.0\"";
            case "CRYPTO":
            case "CRYPTO_COIN":
                return "Ломает стены (скрытые блоки).\n\"Digging to the Moon\"";
            case "RTX":
                return "Даёт +50% скорости (на 10 сек)\n\"Дрова от Бабы-Яги\"";
            case "BANCA":
                return "Временная броня\n\"Золотой запас сисадмина\"";
            case "BOOK":
                return "Открывает секреты (карта)\n\"Библия backend-разработчика\"";
            case "WIFI":
                return "Нужен для финального босса\n\"Пароль: 12345678\"";
            case "VPN":
            case "VPN_TOKEN":
                return "Токен для доступа к VPN";
            case "HARDWARE":
            case "HARDWARE_WALLET":
                return "Аппаратный кошелёк для криптовалюты";
            default:
                return "Описание отсутствует";
        }
    }

    public Projectile createProjectile(float x, float y, float vx, float vy, float damage) {
        TextureRegion[] frames = spriteManager.getFrames("WITCH_VPN");
        TextureRegion texture = (frames != null && frames.length > 0) ? frames[0] : null;

        if (texture == null) {
             Gdx.app.error("EntityFactory", "Failed to get texture for projectile.");
             return null;
        }

        Projectile projectile = new Projectile(x, y, vx, vy, damage, spriteManager);
        return projectile;
    }

    public FlyingBook createFlyingBook(Vector2 position) {
        try {
             Gdx.app.log("EntityFactory", "Creating FlyingBook at " + position);
            Texture bookTexture = spriteManager.getTexture("KNIGA");
            if (bookTexture == null) {
                Gdx.app.error("EntityFactory", "Failed to get KNIGA texture");
                return null;
            }
            FlyingBook flyingBook = new FlyingBook(position, bookTexture);
            // Устанавливаем цель - игрока из PhysicsSystem
            if (physicsSystem != null && physicsSystem.getPlayer() != null) {
                flyingBook.setTarget(physicsSystem.getPlayer());
            } else {
                Gdx.app.error("EntityFactory", "Cannot set target for FlyingBook - player is null");
            }
            return flyingBook;
        } catch (Exception e) {
             Gdx.app.error("EntityFactory", "Failed to create FlyingBook at " + position, e);
             return null;
        }
    }
}


