package com.cyberkingdom.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.Boss;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.input.InputHandler;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem {
    private EntitySystem entitySystem;
    private InputHandler inputHandler;
    private Player player;
    private List<Rectangle> platforms;
    private float gravity = -980f; // Гравитация
    private static final float MIN_Y_POSITION = 150f; // Минимальная высота игрока
    private boolean isPlayerInitialized = false;

    public PhysicsSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
        this.platforms = new ArrayList<>();
    }

    private void initializePlayer() {
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Player) {
                this.player = (Player) entity;
                this.inputHandler = new InputHandler(player);
                System.out.println("Игрок инициализирован в PhysicsSystem: " + player);
                isPlayerInitialized = true;
                break;
            }
        }
        if (!isPlayerInitialized) {
            System.err.println("Игрок не инициализирован в PhysicsSystem");
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.inputHandler = new InputHandler(player);
        isPlayerInitialized = true;
    }

    public void addPlatform(Rectangle platform) {
        platforms.add(platform);
    }

    public List<Rectangle> getPlatforms() {
        return platforms;
    }

    public void clearPlatforms() {
        platforms.clear();
    }

    public void update(float deltaTime) {
        if (!isPlayerInitialized) {
            initializePlayer();
        }
        if (inputHandler != null && player != null) {
            inputHandler.update(deltaTime);
        } else {
            System.err.println("Игрок или InputHandler не инициализированы в PhysicsSystem");
        }

        for (GameEntity entity : entitySystem.getEntities()) {
            if (!entity.isActive()) continue;
            Vector2 position = entity.getPosition();
            Vector2 velocity = entity.getVelocity();

            // Применяем гравитацию
            if (entity instanceof Player || entity instanceof Boss) {
                velocity.y += gravity * deltaTime;
            }

            // Обновляем позицию
            position.add(velocity.cpy().scl(deltaTime));

            // Проверяем столкновения с платформами
            boolean onGround = false;
            for (Rectangle platform : platforms) {
                CollisionComponent platformCollision = new CollisionComponent(platform.width, platform.height);
                platformCollision.update(new Vector2(platform.x, platform.y));
                if (entity.getCollisionComponent().collidesWith(platformCollision)) {
                    if (velocity.y < 0) { // Падение
                        position.y = platform.y + platform.height;
                        velocity.y = 0;
                        onGround = true;
                    }
                }
            }

            // Ограничение по высоте (не ниже 150 пикселей)
            if (entity instanceof Player) {
                if (position.y < MIN_Y_POSITION) {
                    position.y = MIN_Y_POSITION;
                    velocity.y = 0;
                    onGround = true;
                }
                player.setOnGround(onGround);
                player.setJumping(!onGround);
            }
        }
    }
}