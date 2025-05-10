package com.cyberkingdom.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.physics.CollisionComponent;

import java.util.ArrayList;
import java.util.List;

public class ItemPickupSystem {
    private EntitySystem entitySystem;
    private Player player;
    private Sound itemPickupSound;

    public ItemPickupSystem(EntitySystem entitySystem, Player player) {
        this.entitySystem = entitySystem;
        this.player = player;
        this.itemPickupSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/gg_saveItems.mp3"));
    }

    public void update() {
        CollisionComponent playerCollision = player.getCollisionComponent();
        playerCollision.update(player.getPosition());

        List<GameEntity> entities = entitySystem.getEntities();
        List<Item> itemsToRemove = new ArrayList<>();

        for (GameEntity entity : entities) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                CollisionComponent itemCollision = item.getCollisionComponent();
                itemCollision.update(item.getPosition());

                if (playerCollision.collidesWith(itemCollision)) {
                    // Добавляем предмет в инвентарь игрока
                    if (player.getInventory().addItem(item)) {
                        // Воспроизводим звук только если предмет не монетка
                        if (!item.getItemType().equals("COIN")) {
                            itemPickupSound.play();
                        }
                        // Помечаем предмет для удаления из мира
                        itemsToRemove.add(item);
                    }
                }
            }
        }

        // Удаляем предметы, которые собраны
        for (Item item : itemsToRemove) {
            entitySystem.removeEntity(item);
            item.dispose();
        }
    }

    public void dispose() {
        if (itemPickupSound != null) {
            itemPickupSound.dispose();
            itemPickupSound = null;
        }
    }
}

