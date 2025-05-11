package com.cyberkingdom.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.input.InputHandler;
import com.cyberkingdom.items.Item;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem {
    private EntitySystem entitySystem;
    private InputHandler inputHandler;
    private Player player;
    private List<Rectangle> platforms;
    private float gravity = -600f;
    private static final float MIN_Y_POSITION = 150f;
    private static final float MAX_Y_POSITION = 700f; // Максимальная высота экрана
    private static final float BOUNCE_VELOCITY = -500f; // Скорость отскока
    private boolean isPlayerInitialized = false;
    private Sound coinSound;

    public PhysicsSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
        this.platforms = new ArrayList<>();
        // Загружаем звук монетки
        coinSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/coin.mp3"));
    }

    private void initializePlayer() {
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Player) {
                this.player = (Player) entity;
                this.inputHandler = new InputHandler(player);
                Gdx.app.debug("Physics", "Player initialized in PhysicsSystem");
                isPlayerInitialized = true;
                break;
            }
        }
        if (!isPlayerInitialized) {
            Gdx.app.error("Physics", "Player not initialized in PhysicsSystem");
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

    public Player getPlayer() {
        return player;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public void update(float deltaTime) {
        if (player == null) {
            Gdx.app.log("PhysicsSystem", "Player is null in update");
            return;
        }

        // Применяем гравитацию
        Vector2 velocity = player.getVelocity();
        velocity.y += gravity * deltaTime;

        // Обновляем позицию
        Vector2 position = player.getPosition();
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;

        // Проверяем столкновения с платформами
        boolean onGround = false;
        for (Rectangle platform : platforms) {
            if (checkCollision(player, platform)) {
                Gdx.app.log("PhysicsSystem", "Collision detected with platform");
                // Определяем направление столкновения
                float overlapX = Math.min(
                    player.getCollisionBounds().x + player.getCollisionBounds().width - platform.x,
                    platform.x + platform.width - player.getCollisionBounds().x
                );
                float overlapY = Math.min(
                    player.getCollisionBounds().y + player.getCollisionBounds().height - platform.y,
                    platform.y + platform.height - player.getCollisionBounds().y
                );

                if (overlapX < overlapY) {
                    // Горизонтальное столкновение
                    if (position.x < platform.x) {
                        position.x = platform.x - player.getCollisionBounds().width;
                    } else {
                        position.x = platform.x + platform.width;
                    }
                    velocity.x = 0;
                } else {
                    // Вертикальное столкновение
                    if (position.y < platform.y) {
                        position.y = platform.y - player.getCollisionBounds().height;
                        velocity.y = 0;
                    } else {
                        position.y = platform.y + platform.height;
                        velocity.y = 0;
                        onGround = true;
                    }
                }
            }
        }

        // Обновляем позицию и скорость игрока
        player.setPosition(position.x, position.y);
        player.setVelocity(velocity.x, velocity.y);
        player.setOnGround(onGround);

        // Если игрок на земле и не прыгает, сбрасываем вертикальную скорость
        if (onGround && !player.isJumping()) {
            velocity.y = 0;
            player.setVelocity(velocity.x, velocity.y);
        }

        // Обновляем состояние прыжка
        if (player.isJumping() && velocity.y <= 0) {
            player.setJumping(false);
        }

        // Обновляем обработчик ввода
        if (inputHandler != null) {
            inputHandler.update(deltaTime);
        }
    }

    private boolean checkCollision(Player player, Rectangle platform) {
        return player.getCollisionBounds().overlaps(platform);
    }

    public void dispose() {
        if (platforms != null) {
            platforms.clear();
            platforms = null;
        }
        if (inputHandler != null) {
            inputHandler = null;
        }
        if (coinSound != null) {
            coinSound.dispose();
        }
        player = null;
        entitySystem = null;
    }
}