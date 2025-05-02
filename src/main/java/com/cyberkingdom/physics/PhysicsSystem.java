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

    public PhysicsSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
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
            inputHandler.update(player.getVelocity());
        } else {
            System.err.println("Игрок или InputHandler не инициализированы в PhysicsSystem");
        }

        for (GameEntity entity : entitySystem.getEntities()) {
            if (!entity.isActive()) continue;
            Vector2 position = entity.getPosition();
            Vector2 velocity = entity.getVelocity();
            position.add(velocity.cpy().scl(deltaTime));

            if (entity == player) {
                if (position.y > 0) {
                    velocity.y += 500 * deltaTime; // Гравитация
                } else {
                    position.y = 0;
                    velocity.y = 0;
                    player.setJumping(false);
                }
            }

            System.out.println("Обновлена позиция " + entity.getName() + ": (" + position.x + ", " + position.y + ")");
        }
    }
}