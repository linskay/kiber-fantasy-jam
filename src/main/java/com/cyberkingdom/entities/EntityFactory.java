package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.screens.GameScreen;
import com.cyberkingdom.rendering.SpriteManager;

import java.util.Random;

public class EntityFactory {
    private static int itemCount = 0;
    private static final Random random = new Random();
    private SpriteManager spriteManager;

    // Используйте enum или константы, чтобы избежать ошибок в строках
    private static final String[] ITEMS = {
            "CRYPTO_COIN", "VPN_TOKEN", "USB_SCATERT", "HARDWARE_WALLET"
    };

    public EntityFactory(SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
    }

    public GameEntity createPlayer(float x, float y) {
        return createPlayer(new Vector2(x, y), null);
    }

    public GameEntity createPlayer(Vector2 position, GameScreen gameScreen) {
        Player player = new Player(position, gameScreen);
        if (player.getAnimation() != null) {
            player.getAnimation().setFrameDuration(0.08f);
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
        Enemy enemy = new Enemy(type, x, y);
        if (enemy.getAnimation() != null) {
            enemy.getAnimation().setFrameDuration(0.1f);
        }
        return enemy;
    }

    public GameEntity createBoss(String name, float x, float y) {
        Boss boss;
        switch (name.toUpperCase()) {
            case "CAT_MINER":
                boss = new CatMiner(x, y);
                break;
            case "WITCH_VPN":
                boss = new Boss(name, x, y);
                boss.setMaxHitsToDefeat(5);
                break;
            default:
                boss = new Boss(name, x, y);
                break;
        }
        if (boss.getAnimation() != null) {
            boss.getAnimation().setFrameDuration(0.1f);
        }
        return boss;
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

            Item item = new Item(position, itemType, quantity, texture);
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
}


