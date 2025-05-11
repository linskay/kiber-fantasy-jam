package com.cyberkingdom.entities;

import java.util.ArrayList;
import java.util.List;

public class EntitySystem {
    private List<GameEntity> entities;

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
}
