package com.cyberkingdom.items;

import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.physics.CollisionComponent;

import java.util.ArrayList;
import java.util.List;

public class ItemPickupSystem {
    private EntitySystem entitySystem;
    private Player player;

    public ItemPickupSystem(EntitySystem entitySystem, Player player) {
        this.entitySystem = entitySystem;
        this.player = player;
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
                    player.getInventory().addItem(item);

                    // Помечаем предмет для удаления из мира
                    itemsToRemove.add(item);
                }
            }
        }

        // Удаляем предметы, которые собраны
        for (Item item : itemsToRemove) {
            entitySystem.removeEntity(item);
            item.dispose();
        }
    }
}

