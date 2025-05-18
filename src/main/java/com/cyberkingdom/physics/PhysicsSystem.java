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

        // Сохраняем предыдущую позицию игрока
        Vector2 previousPosition = new Vector2(player.getPosition());
        Rectangle playerBounds = player.getCollisionBounds();
        Vector2 velocity = player.getVelocity();

        // Применяем гравитацию
        velocity.y += gravity * deltaTime;

        // Пробное обновление позиции
        Vector2 nextPosition = new Vector2(playerBounds.x + velocity.x * deltaTime, playerBounds.y + velocity.y * deltaTime);
        Rectangle nextPlayerBounds = new Rectangle(nextPosition.x, nextPosition.y, playerBounds.width, playerBounds.height);

        // Флаг для отслеживания приземления
        boolean landedOnPlatform = false;

        // Проверяем столкновения с платформами
        for (Rectangle platform : platforms) {
             // Check for collision with the platform using the next position
            if (nextPlayerBounds.overlaps(platform)) {
                // Determine the overlap amount on both axes
                float overlapX = Math.min(
                    nextPlayerBounds.x + nextPlayerBounds.width - platform.x,
                    platform.x + platform.width - nextPlayerBounds.x
                );
                float overlapY = Math.min(
                    nextPlayerBounds.y + nextPlayerBounds.height - platform.y,
                    platform.y + platform.height - nextPlayerBounds.y
                );

                if (overlapX < overlapY) {
                    // Horizontal collision
                    if (previousPosition.x < platform.x) { // Was to the left of the platform
                         nextPosition.x = platform.x - nextPlayerBounds.width; // Adjust to the left
                    } else { // Was to the right of the platform
                         nextPosition.x = platform.x + platform.width; // Adjust to the right
                    }
                    velocity.x = 0;
                } else {
                    // Вертикальное столкновение
                    if (velocity.y <= 0) { // Игрок двигается вниз или стоит на месте (проверка на приземление)
                         // Проверяем, что нижняя граница игрока находится на или выше верхней границы платформы ДО коллизии
                         if (previousPosition.y >= platform.y + platform.height) {
                            nextPosition.y = platform.y + platform.height; // Ставим игрока на верх платформы
                            velocity.y = 0; // Останавливаем падение
                            landedOnPlatform = true; // Отмечаем приземление
                         }
                    } else { // Игрок двигается вверх (проверка на столкновение с потолком)
                         // Проверяем, что верхняя граница игрока находится на или ниже нижней границы платформы ДО коллизии
                         if (previousPosition.y + playerBounds.height <= platform.y) {
                             // Check if it's the ground platform
                            boolean isGround = platforms.indexOf(platform) == 0; // Adjust if ground is not always the first

                            if (isGround) {
                                // Если это земля, блокируем движение вверх
                                nextPosition.y = platform.y - nextPlayerBounds.height; // Отталкиваем вниз
                                velocity.y = 0; // Останавливаем движение вверх
                            } else {
                                // Если это не земля (обычная платформа), игнорируем коллизию (проходим насквозь)
                                // Ничего не делаем с позицией и скоростью по Y, позволяя игроку пройти через низ платформы.
                                // Возможно, нужно сбросить velocity.y = 0; чтобы не залипал, но пока оставим.
                                // velocity.y = 0; // <-- Если игрок залипает под платформой при прыжке
                            }
                         }
                    }
                    // В других случаях вертикального столкновения (например, внутри платформы) ничего не делаем с позицией по Y
                }
            }
        }

        // Обновляем позицию игрока только после обработки всех коллизий
        player.setPosition(nextPosition.x, nextPosition.y);

        // Обновляем состояние на земле
        if (landedOnPlatform) {
            player.setOnGround(true);
        } else {
            player.setOnGround(false);
        }

        // Обновляем скорость игрока
        player.setVelocity(velocity.x, velocity.y);

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