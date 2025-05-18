package com.cyberkingdom.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class EntitySystem {
    private List<GameEntity> entities = new CopyOnWriteArrayList<>();
    private int currentLevel = 1;
    private EntityFactory entityFactory;

    public EntitySystem() {
    }

    public void addEntity(GameEntity entity) {
        entities.add(entity);
    }

    /**
     * Удаляет сущность из системы.
     * @param entity сущность для удаления
     */
    public void removeEntity(GameEntity entity) {
        if (entity != null) {
            entity.dispose();
            entities.remove(entity);
        }
    }

    /**
     * Возвращает копию списка всех сущностей.
     * @return список сущностей
     */
    public List<GameEntity> getEntities() {
        return new ArrayList<>(entities);
    }

    /**
     * Очищает список сущностей.
     */
    public void clear() {
        for (GameEntity entity : entities) {
            if (entity != null) {
                entity.dispose();
            }
        }
        entities.clear();
    }

    public void dispose() {
        clear();
        entities = null;
    }

    public void update(float deltaTime) {
        for (GameEntity entity : entities) {
            if (entity != null && entity.isActive()) {
                entity.update(deltaTime);
            }
        }
    }

    // Метод для удаления всех сущностей, кроме игрока
    public void removeAllEntitiesExceptPlayer() {
        List<GameEntity> entitiesToRemove = new ArrayList<>();
        
        // Сначала собираем все сущности для удаления
        for (GameEntity entity : entities) {
            if (!(entity instanceof Player)) {
                entitiesToRemove.add(entity);
            }
        }
        
        // Затем удаляем их
        for (GameEntity entity : entitiesToRemove) {
            if (entity != null) {
                entity.dispose();
                entities.remove(entity);
            }
        }
        
        Gdx.app.log("EntitySystem", "Removed all entities except player.");
    }

    /**
     * Вызывается при победе над боссом
     */
    public void onBossDefeated() {
        Gdx.app.log("EntitySystem", "Boss defeated! Preparing for next level...");
        // Здесь можно добавить логику перехода на следующий уровень
        // Например, через GameScreen или другой менеджер уровней
    }

    public int getLevelNumber() {
        return currentLevel;
    }

    public void setLevelNumber(int level) {
        this.currentLevel = level;
    }

    public EntityFactory getFactory() {
        return entityFactory;
    }

    public void setFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }
}
