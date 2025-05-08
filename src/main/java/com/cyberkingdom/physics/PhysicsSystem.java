package com.cyberkingdom.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.input.InputHandler;
import com.cyberkingdom.items.Item;

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

    public PhysicsSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
        this.platforms = new ArrayList<>();
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

    public void update(float deltaTime) {
        if (!isPlayerInitialized) {
            initializePlayer();
        }
        if (inputHandler != null && player != null) {
            inputHandler.update(deltaTime);
        } else {
            Gdx.app.error("Physics", "Player or InputHandler not initialized in PhysicsSystem");
            return;
        }

        // Обновляем физику игрока
        Vector2 position = player.getPosition();
        Vector2 velocity = player.getVelocity();

        // Применяем гравитацию
        velocity.y += gravity * deltaTime;

        // Сохраняем предыдущую позицию
        float previousY = position.y;
        float previousX = position.x;

        // Обновляем позицию
        position.add(velocity.cpy().scl(deltaTime));

        // Ограничения по X
        float minX = 0;
        float maxX = 1200 - 64; // 1200 — ширина уровня, 64 — ширина игрока
        if (position.x < minX) {
            position.x = minX;
            if (velocity.x < 0) velocity.x = 0;
        } else if (position.x > maxX) {
            position.x = maxX;
            if (velocity.x > 0) velocity.x = 0;
        }

        // Проверяем столкновение с верхней границей
        if (position.y > MAX_Y_POSITION) {
            position.y = MAX_Y_POSITION;
            velocity.y = BOUNCE_VELOCITY; // Отскок вниз
            Gdx.app.debug("Physics", "Player hit ceiling, bouncing down");
        }

        // Проверяем столкновения с платформами
        boolean onGround = false;
        Rectangle playerBounds = player.getCollisionBounds();
        
        for (Rectangle platform : platforms) {
            if (playerBounds.overlaps(platform)) {
                // Проверяем, падает ли игрок на платформу сверху
                if (velocity.y < 0 && previousY > platform.y + platform.height - 5) {
                    position.y = platform.y + platform.height;
                    velocity.y = 0;
                    onGround = true;
                    player.setOnGround(true);
                    player.setJumping(false);
                }
                // Проверяем, прыгает ли игрок вверх
                else if (velocity.y > 0 && previousY < platform.y) {
                    position.y = platform.y - playerBounds.height;
                    velocity.y = 0;
                }
                // Проверяем столкновение с левой стороной платформы
                else if (velocity.x > 0 && previousX < platform.x) {
                    position.x = platform.x - playerBounds.width;
                    velocity.x = 0;
                }
                // Проверяем столкновение с правой стороной платформы
                else if (velocity.x < 0 && previousX > platform.x + platform.width) {
                    position.x = platform.x + platform.width;
                    velocity.x = 0;
                }
            }
        }

        // Проверяем выход за пределы экрана
        if (position.y < MIN_Y_POSITION) {
            position.y = MIN_Y_POSITION;
            velocity.y = 0;
            onGround = true;
            player.setOnGround(true);
            player.setJumping(false);
            Gdx.app.debug("Physics", "Player reached ground level");
        }

        // Ограничиваем максимальную скорость падения
        if (velocity.y < -600f) {
            velocity.y = -600f;
        }

        // Если игрок не на земле, сбрасываем флаг
        if (!onGround) {
            player.setOnGround(false);
        }

        // Обновляем коллизию игрока
        player.getCollisionComponent().update(position);

        // Проверка столкновений с монетами
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Item && ((Item) entity).getItemType().equals("COIN")) {
                Item coin = (Item) entity;
                if (coin.isActive() && player.getCollisionComponent().getBounds().overlaps(coin.getCollisionComponent().getBounds())) {
                    player.collectCoin();
                    coin.setActive(false);
                    entitySystem.removeEntity(coin);
                }
            }
        }
    }

    public void dispose() {
        if (platforms != null) {
            platforms.clear();
            platforms = null;
        }
        if (inputHandler != null) {
            inputHandler = null;
        }
        player = null;
        entitySystem = null;
    }
}