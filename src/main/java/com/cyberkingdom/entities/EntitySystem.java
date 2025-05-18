package com.cyberkingdom.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class EntitySystem {
    private List<GameEntity> entities;
    private int currentLevel = 1;

    public EntitySystem() {
        this.entities = new ArrayList<>();
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
        Iterator<GameEntity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            GameEntity entity = iterator.next();
            // Проверяем, если сущность не является игроком
            if (!(entity instanceof Player)) {
                // Возможно, нужно вызвать dispose() для удаляемой сущности
                // entity.dispose(); // Раскомментируйте, если сущности требуют очистки ресурсов
                iterator.remove();
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
}
