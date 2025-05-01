package com.cyberkingdom.physics;

import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.world.WorldPhysics;
import java.util.List;

public class PhysicsSystem {
    private WorldPhysics worldPhysics;

    public PhysicsSystem() {
        worldPhysics = new WorldPhysics();
    }

    public void update(float deltaTime, List<GameEntity> entities) {
        // Применяем гравитацию и проверяем коллизии
        entities.stream()
                .filter(e -> e.getPhysics() != null)
                .forEach(entity -> {
                    worldPhysics.applyPhysics(entity);
                    checkCollisions(entity, entities);
                });
    }

    private void checkCollisions(GameEntity entity, List<GameEntity> entities) {
        entities.stream()
                .filter(other -> entity != other)
                .filter(other -> entity.getCollision() != null && other.getCollision() != null)
                .filter(other -> entity.getCollision().collidesWith(other.getCollision()))
                .forEach(other -> handleCollision(entity, other));
    }

    private void handleCollision(GameEntity a, GameEntity b) {
        // Логика обработки столкновений
    }
}