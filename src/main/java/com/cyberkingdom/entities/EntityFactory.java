package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.items.Item;

import java.util.Random;

public class EntityFactory {
    private static int itemCount = 0;
    private static final Random random = new Random();
    private static final String[] ITEMS = {
            "CRYPTO_COIN", "VPN_TOKEN", "USB_SCATTER", "HARDWARE_WALLET"
    };

    public GameEntity createPlayer(float x, float y) {
        Player player = new Player(x, y);
        player.getAnimation().setFrameDuration(0.08f);
        return player;
    }

    public GameEntity createEnemy(String name, float x, float y) {
        Enemy.EnemyType type = Enemy.EnemyType.valueOf(name.toUpperCase());
        Enemy enemy = new Enemy(type, x, y);
        enemy.getAnimation().setFrameDuration(0.1f);
        return enemy;
    }

    public GameEntity createBoss(String name, float x, float y) {
        Boss boss = switch (name.toUpperCase()) {
            case "CAT_MINER" -> new CatMiner(x, y);
            case "WITCH_VPN" -> {
                Boss b = new Boss(name, x, y);
                b.setMaxHitsToDefeat(5);
                yield b;
            }
            default -> new Boss(name, x, y);
        };
        boss.getAnimation().setFrameDuration(0.1f);
        return boss;
    }

    public GameEntity createItem(Vector2 position, String name) {
        String baseName = name.split("_")[0];
        return new Item(position, baseName);
    }

    public GameEntity createRandomItem(float x, float y) {
        return createItem(new Vector2(x, y), ITEMS[random.nextInt(ITEMS.length)]);
    }

    public static void resetItemCounter() {
        itemCount = 0;
    }
}