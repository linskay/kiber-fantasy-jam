package com.cyberkingdom.boss;

import com.badlogic.gdx.Gdx;
import com.cyberkingdom.entities.Boss;
import com.cyberkingdom.entities.EntityFactory;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.entities.WitchVPN;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.items.ItemType;
import com.cyberkingdom.physics.PhysicsSystem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BossSpawnManager {
    private final EntitySystem entitySystem;
    private final EntityFactory entityFactory;
    private final PhysicsSystem physicsSystem;
    private Player player;
    private boolean bossSpawned = false;
    private static final Set<ItemType> REQUIRED_ITEMS = new HashSet<>(Arrays.asList(
        ItemType.USB_SKATERT,
        ItemType.CRYPTO_SHOVEL,
        ItemType.RTX_4090,
        ItemType.TUSHENKA,
        ItemType.KNIGA,
        ItemType.WIFI_KEY
    ));

    public BossSpawnManager(EntitySystem entitySystem, EntityFactory entityFactory, 
                          PhysicsSystem physicsSystem) {
        this.entitySystem = entitySystem;
        this.entityFactory = entityFactory;
        this.physicsSystem = physicsSystem;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void update() {
        if (bossSpawned) return;

        // Проверяем, собраны ли все необходимые предметы
        Set<ItemType> collectedItems = new HashSet<>();
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
        float x = 600; // Центр экрана по X
        float y = 400; // Центр экрана по Y
        
        // Определяем, какой босс нужно спавнить в зависимости от уровня
        String bossType = "WITCH_VPN"; // По умолчанию Ведьма VPN
        if (entitySystem.getLevelNumber() == 2) {
            bossType = "DEDINSAID";
        }
        
        Boss boss = (Boss) entityFactory.createBoss(bossType, x, y, entitySystem);
        if (boss != null) {
            boss.setTarget(player);
            entitySystem.addEntity(boss);
            Gdx.app.log("BossSpawnManager", bossType + " появился и получил цель!");
        } else {
            Gdx.app.error("BossSpawnManager", "Не удалось создать босса " + bossType + "!");
        }
    }
} 