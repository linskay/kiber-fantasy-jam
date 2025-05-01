package com.cyberkingdom.world;

import com.cyberkingdom.entities.Enemy;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.EntityFactory;
import com.cyberkingdom.entities.Player;

public class LevelLoader {
    private final EntitySystem entitySystem;
    private final EntityFactory entityFactory;
    private int currentLevel;

    public LevelLoader(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
        this.entityFactory = new EntityFactory();
        this.currentLevel = 1;
    }

    public void loadLevel(int level) {
        currentLevel = level;
        entitySystem.getEntities().clear();

        // Загрузка уровня в зависимости от номера
        if (level == 1) {
            loadLevel1();
            // Другие уровни
        }
    }

    private void loadLevel1() {
        // Создание игрока
        Player player = entityFactory.createPlayer(100, 100);
        entitySystem.addEntity(player);

        // Создание врагов
        entitySystem.addEntity(entityFactory.createEnemy(Enemy.EnemyType.TROLL_BOT, 300, 100));
        // Другие сущности уровня
    }
}