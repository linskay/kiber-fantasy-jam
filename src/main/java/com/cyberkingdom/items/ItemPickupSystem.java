package com.cyberkingdom.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.physics.CollisionComponent;
import com.badlogic.gdx.math.Rectangle;
import com.cyberkingdom.world.LevelLoader;
import com.cyberkingdom.entities.Platform;

import java.util.ArrayList;
import java.util.List;

public class ItemPickupSystem {
    private final EntitySystem entitySystem;
    private final Player player;
    private Sound itemPickupSound;
    private final LevelLoader levelLoader;

    public ItemPickupSystem(EntitySystem entitySystem, Player player, LevelLoader levelLoader) {
        this.entitySystem = entitySystem;
        this.player = player;
        this.levelLoader = levelLoader;
        try {
            this.itemPickupSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/gg_saveItems.mp3"));
        } catch (Exception e) {
            Gdx.app.error("ItemPickupSystem", "Failed to load sound: " + e.getMessage());
        }
    }

    public void update() {
        List<Item> itemsToRemove = new ArrayList<>();
        CollisionComponent playerCollision = player.getCollisionComponent();
        playerCollision.update(player.getPosition());

        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                CollisionComponent itemCollision = item.getCollisionComponent();
                itemCollision.update(item.getPosition());

                if (playerCollision.collidesWith(itemCollision)) {
                    Gdx.app.log("ItemPickupSystem", "Collision detected with item: " + item.getItemType());
                    
                    if (item.getItemType().equals("COIN")) {
                        player.collectCoin();
                        itemsToRemove.add(item);
                        if (itemPickupSound != null) {
                            itemPickupSound.play();
                        }
                    } else {
                        // Добавляем предмет в инвентарь игрока
                        if (player.getInventory() != null) {
                            if (player.getInventory().addItem(item)) {
                                Gdx.app.log("ItemPickupSystem", "Item added to inventory: " + item.getItemType());
                                itemsToRemove.add(item);
                                if (itemPickupSound != null) {
                                    itemPickupSound.play();
                                }
                            } else {
                                Gdx.app.log("ItemPickupSystem", "Failed to add item to inventory: " + item.getItemType());
                            }
                        } else {
                            Gdx.app.error("ItemPickupSystem", "Player inventory is null!");
                        }
                    }
                }
            }
        }

        // Удаляем подобранные предметы
        for (Item item : itemsToRemove) {
            entitySystem.removeEntity(item);
            item.dispose();
        }
    }

    public void dispose() {
        if (itemPickupSound != null) {
            itemPickupSound.dispose();
        }
    }
}

