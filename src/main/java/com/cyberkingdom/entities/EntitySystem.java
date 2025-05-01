package com.cyberkingdom.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySystem {
    private List<GameEntity> entities;

    public EntitySystem() {
        entities = new ArrayList<>();
    }

    public void addEntity(GameEntity entity) {
        entities.add(entity);
    }

    public void update(float deltaTime) {
        // Обновляем только активные сущности
        entities.stream()
                .filter(GameEntity::isActive)
                .forEach(e -> e.update(deltaTime));

        // Удаляем уничтоженные сущности
        entities = entities.stream()
                .filter(GameEntity::isActive)
                .collect(Collectors.toList());
    }

    public List<GameEntity> getEntities() {
        return new ArrayList<>(entities);
    }
}