package com.cyberkingdom.world;

import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Player;

public class WorldPhysics {
    private static final float GRAVITY = -9.8f * 100;
    private static final float FRICTION = 0.9f;

    public void applyPhysics(GameEntity entity) {
        // Применение гравитации
        entity.getVelocity().y += GRAVITY;

        // Применение трения
        entity.getVelocity().x *= FRICTION;

        // Обновление позиции
        entity.getPosition().add(
                entity.getVelocity().x * 0.016f,
                entity.getVelocity().y * 0.016f
        );

        // Проверка земли
        if (entity.getPosition().y <= 0) {
            entity.getPosition().y = 0;
            if (entity instanceof Player) {
                ((Player) entity).setGrounded(true);
            }
        }
    }
}