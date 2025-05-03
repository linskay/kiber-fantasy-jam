package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.items.Item;

import java.util.Random;

public class EntityFactory {
    private static int itemCount = 0;
    private static final Random random = new Random();

    // Используйте enum или константы, чтобы избежать ошибок в строках
    private static final String[] ITEMS = {
            "CRYPTO_COIN", "VPN_TOKEN", "USB_SCATERT", "HARDWARE_WALLET"
    };

    public GameEntity createPlayer(float x, float y) {
        Player player = new Player(x, y);
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
            type = Enemy.EnemyType.TROLL_BOT; // Используем первый или подходящий тип по умолчанию
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

    // Новый метод создания предмета с указанием количества
    public Item createItem(String itemType, Vector2 position, int quantity) {
        for (Item.ItemData data : Item.ALL_ITEMS) {
            if (data.id.equalsIgnoreCase(itemType)) {
                return new Item(position, data, quantity);
            }
        }
        System.err.println("Неизвестный тип предмета: " + itemType);
        return null;
    }

    // Обновлённый существующий метод для совместимости
    public GameEntity createItem(Vector2 position, String name) {
        return createItem(name.toLowerCase(), position, 1);
    }

    public GameEntity createRandomItem(float x, float y) {
        String randomItemName = ITEMS[random.nextInt(ITEMS.length)];
        return createItem(new Vector2(x, y), randomItemName);
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


