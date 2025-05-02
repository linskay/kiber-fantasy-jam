package com.cyberkingdom.physics;


import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.input.InputHandler;

public class PhysicsSystem {
    private EntitySystem entitySystem;
    private InputHandler inputHandler;
    private Player player;

    private float worldWidth;
    private float worldHeight;

    // Конструктор с передачей размеров игрового мира/экрана
    public PhysicsSystem(EntitySystem entitySystem, float worldWidth, float worldHeight) {
        this.entitySystem = entitySystem;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        initializePlayer();
    }

    private void initializePlayer() {
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Player) {
                this.player = (Player) entity;
                this.inputHandler = new InputHandler(player);
                System.out.println("Игрок инициализирован в PhysicsSystem: " + player);
                break;
            }
        }
        if (player == null) {
            System.err.println("Игрок не инициализирован в PhysicsSystem");
        }
    }

    public void update(float deltaTime) {
        if (player == null) {
            initializePlayer(); // Проверяем еще раз, если игрок не найден
        }

        if (inputHandler != null && player != null) {
            // Обновляем скорость игрока на основе ввода
            inputHandler.update(player.getVelocity());
        } else {
            System.err.println("Игрок или InputHandler не инициализированы в PhysicsSystem");
        }

        for (GameEntity entity : entitySystem.getEntities()) {
            if (!entity.isActive()) continue;

            Vector2 position = entity.getPosition();
            Vector2 velocity = entity.getVelocity();

            // Обновляем позицию с учётом скорости и времени
            position.add(velocity.cpy().scl(deltaTime));

            // Гравитация для игрока
            if (entity == player) {
                if (position.y > 0) {
                    velocity.y += -500 * deltaTime; // гравитация вниз
                } else {
                    position.y = 0;
                    velocity.y = 0;
                    player.setJumping(false);
                }
            }

            // Ограничение позиции по границам мира (учитываем размер сущности)
            float halfWidth = 16f*2f;  // примерный размер (половина ширины) спрайта/коллайдера
            float halfHeight = 16f*2f; // примерный размер (половина высоты)

            // Ограничиваем по X
            if (position.x < halfWidth) position.x = halfWidth;
            if (position.x > worldWidth - halfWidth) position.x = worldWidth - halfWidth;

            // Ограничиваем по Y
            if (position.y < 0) {
                position.y = 0;
                velocity.y = 0;
                if (entity == player) player.setJumping(false);
            }
            if (position.y > worldHeight - halfHeight) {
                position.y = worldHeight - halfHeight;
                velocity.y = 0;
            }

            // Обновляем позицию сущности после ограничений
            entity.getPosition().set(position);

            System.out.println("Обновлена позиция " + entity.getName() + ": (" + position.x + ", " + position.y + ")");
        }
    }
}
