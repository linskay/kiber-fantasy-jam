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

    public List<GameEntity> getEntities() {
        return new ArrayList<>(entities);
    }

    public void clear() {
        entities.clear();
    }
}