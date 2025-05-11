package com.cyberkingdom.boss;

import com.badlogic.gdx.Gdx;
import com.cyberkingdom.entities.Boss;
import com.cyberkingdom.entities.EntityFactory;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.entities.WitchVPN;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.physics.PhysicsSystem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BossSpawnManager {
    private final EntitySystem entitySystem;
    private final EntityFactory entityFactory;
    private final PhysicsSystem physicsSystem;
    private final Player player;
    private boolean bossSpawned = false;
    private static final Set<String> REQUIRED_ITEMS = new HashSet<>(Arrays.asList(
        "USB_SKATERT", "CRYPTO_SHOVEL", "RTX_4090", "TUSHENKA", "KNIGA", "WIFI_KEY"
    ));

    public BossSpawnManager(EntitySystem entitySystem, EntityFactory entityFactory, 
                          PhysicsSystem physicsSystem, Player player) {
        this.entitySystem = entitySystem;
        this.entityFactory = entityFactory;
        this.physicsSystem = physicsSystem;
        this.player = player;
    }

    public void update() {
        if (bossSpawned) return;

        // Проверяем, собраны ли все необходимые предметы
        Set<String> collectedItems = new HashSet<>();
        for (Item item : player.getInventory().getItems()) {
            collectedItems.add(item.getItemType());
        }

        // Если все предметы собраны, спавним босса
        if (collectedItems.containsAll(REQUIRED_ITEMS)) {
            spawnBoss();
            bossSpawned = true;
            Gdx.app.log("BossSpawnManager", "Все предметы собраны! Появляется Ведьма.VPN!");
        }
    }

    private void spawnBoss() {
        // Выбираем случайную платформу для появления босса
        if (physicsSystem.getPlatforms().isEmpty()) return;

        int randomIndex = (int) (Math.random() * physicsSystem.getPlatforms().size());
        var platform = physicsSystem.getPlatforms().get(randomIndex);
        
        float x = platform.x + platform.width / 2;
        float y = platform.y + platform.height + 50;

        // Создаем и добавляем босса
        var boss = entityFactory.createBoss("WITCH_VPN", x, y);
        if (boss != null) {
            if (boss instanceof WitchVPN) {
                ((WitchVPN) boss).setTarget(player);
                Gdx.app.log("BossSpawnManager", "Установлена цель для Ведьмы.VPN");
            } else if (boss instanceof Boss) {
                ((Boss) boss).setTarget(player);
            }
            entitySystem.addEntity(boss);
            Gdx.app.log("BossSpawnManager", "Ведьма.VPN появилась на позиции: " + x + ", " + y);
        }
    }
} 