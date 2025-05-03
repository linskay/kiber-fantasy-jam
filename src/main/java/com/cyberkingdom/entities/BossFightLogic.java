package com.cyberkingdom.entities;

import com.cyberkingdom.gameengine.GameEngine;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.world.LevelLoader;
import java.util.ArrayList;
import java.util.List;

public class BossFightLogic {
    private GameEngine gameEngine;
    private Boss boss;
    private Player player;
    private LevelLoader levelLoader;
    private int collectedItems = 0;
    private boolean finalBossSpawned = false;
    private boolean levelCompleted = false;

    public BossFightLogic(Boss boss, Player player, GameEngine gameEngine, LevelLoader levelLoader) {
        this.player = player;
        this.gameEngine = gameEngine;
        this.levelLoader = levelLoader;
        this.boss = boss;
    }

    public void update(float deltaTime) {
        if (levelCompleted) return;

        // Создаем копию списка для безопасной итерации
        List<GameEntity> entitiesCopy = new ArrayList<>(gameEngine.getEntitySystem().getEntities());
        checkItemCollisions(entitiesCopy);

        if (boss != null && boss.isActive()) {
            boss.update(deltaTime);
            checkBossCollision();
        }

        checkLevelCompletion();
    }

    private void checkItemCollisions(List<GameEntity> entities) {
        for (GameEntity entity : entities) {
            if (entity instanceof Item && entity.isActive()) {
                if (checkCollision(player, entity)) {
                    collectItem((Item)entity);
                }
            }
        }
    }

    private boolean checkCollision(GameEntity a, GameEntity b) {
        return a.getCollisionComponent().collidesWith(b.getCollisionComponent());
    }

    private void collectItem(Item item) {
        item.setActive(false);
        collectedItems++;
        player.getInventory().addItem(item);

        if (levelLoader != null && levelLoader.getLevelNumber() == 1 &&
                collectedItems >= levelLoader.getTotalItems() &&
                !finalBossSpawned) {
            spawnFinalBoss();
        }
    }

    private void checkBossCollision() {
        if (checkCollision(player, boss)) {
            if (boss.tryRegisterHit()) {
                System.out.println("Босс получил удар!");
            }
        }
    }

    private void spawnFinalBoss() {
        if (levelLoader.getLevelNumber() == 1) {
            boss = (Boss)gameEngine.getEntityFactory().createBoss(
                    "WITCH_VPN",
                    player.getPosition().x + 300,
                    player.getPosition().y + 100
            );
            gameEngine.getEntitySystem().addEntity(boss);
            finalBossSpawned = true;
        }
    }

    private void checkLevelCompletion() {
        if (finalBossSpawned && (boss == null || !boss.isActive())) {
            levelCompleted = true;
            gameEngine.nextLevel();
        }
    }

    public boolean isLevelCompleted() {
        return levelCompleted;
    }
}