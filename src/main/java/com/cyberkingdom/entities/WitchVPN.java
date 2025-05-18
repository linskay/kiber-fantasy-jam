package com.cyberkingdom.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Sound;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class WitchVPN extends Boss {
    private float teleportTimer = 0f;
    private float teleportInterval = 3f; // Интервал между телепортациями
    private PhysicsSystem physicsSystem;
    private Random random = new Random();
    private float attackRange = 200f;
    private float attackCooldown = 2f;
    private float timeSinceLastAttack = 0f;
    private Player target;
    private boolean isVisible = true;
    private float visibilityTimer = 0f;
    private float visibilityDuration = 0.5f;
    private List<Projectile> projectiles;
    private float projectileSpeed = 300f;
    private float projectileDamage = 10f;
    private float minDistanceToPlayer = 100f;
    private float maxDistanceToPlayer = 300f;
    private float moveTimer = 0f;
    private float moveInterval = 1f;
    private Vector2 targetPosition;
    private boolean isMoving = true;
    private EntityFactory entityFactory;
    private float moveSpeed = 150f; // Уменьшаем скорость движения
    private float teleportCooldown = 3f;
    private int hitsToDefeat = 10; // Количество ударов для победы
    private int currentHits = 0; // Текущее количество ударов
    private float initialDelayTimer = 1.0f; // Задержка в 1 секунду перед началом действий
    private Sound welcomeSound; // Звук появления
    private float totalActiveTime = 0f; // Общее время активности
    private float requiredActiveTime = 10f; // Требуемое время для победы (3 секунды)
    private float health = 100f; // Здоровье Ведьмы VPN
    private Sound attackSound; // Звук атаки
    private Sound deathSound; // Звук смерти

    public WitchVPN(float x, float y, PhysicsSystem physicsSystem, SpriteManager spriteManager) {
        super("WITCH_VPN", x, y, spriteManager);
        this.physicsSystem = physicsSystem;
        setMaxHitsToDefeat(5);
        setMoveSpeed(150f); // Уменьшаем скорость движения
        this.attackRange = 200f;
        this.attackCooldown = 2f;
        this.teleportCooldown = 3f;
        this.visibilityTimer = 0f;
        this.isVisible = true;
        this.projectiles = new ArrayList<>();
        this.targetPosition = new Vector2();
        this.isMoving = true;
        this.requiredActiveTime = 10f; // Инициализация, которая станет неактуальной

        Gdx.app.log("WitchVPN", "Initialized with moveSpeed: " + moveSpeed);

        // Инициализируем позицию на первой доступной платформе
        List<Rectangle> platforms = physicsSystem.getPlatforms();
        if (platforms != null && !platforms.isEmpty()) {
            Rectangle firstPlatform = platforms.get(0);
            position.set(
                firstPlatform.x + firstPlatform.width / 2,
                firstPlatform.y + firstPlatform.height + 10
            );
            Gdx.app.log("WitchVPN", "Initialized on platform at " + position.x + "," + position.y);
        } else {
            Gdx.app.error("WitchVPN", "No platforms available for initialization");
        }

        // Загрузка звука появления
        try {
            welcomeSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/vedma_welcome.mp3"));
            Gdx.app.log("WitchVPN", "Welcome sound loaded.");
        } catch (Exception e) {
            Gdx.app.error("WitchVPN", "Failed to load welcome sound: " + e.getMessage());
        }

        // Загрузка звука атаки
        try {
            attackSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/vedma_attac.mp3"));
            Gdx.app.log("WitchVPN", "Attack sound loaded.");
        } catch (Exception e) {
            Gdx.app.error("WitchVPN", "Failed to load attack sound: " + e.getMessage());
        }

        // Загрузка звука смерти
        try {
            deathSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/vedma_death.mp3"));
            Gdx.app.log("WitchVPN", "Death sound loaded.");
        } catch (Exception e) {
            Gdx.app.error("WitchVPN", "Failed to load death sound: " + e.getMessage());
        }
    }

    @Override
    public void setTarget(Player target) {
        super.setTarget(target);
        this.target = target;
        Gdx.app.log("WitchVPN", "Target set to player at position: " + 
            (target != null ? target.getPosition().x + "," + target.getPosition().y : "null"));
        
        // Воспроизводим звук при установке цели (при появлении Ведьмы)
        if (welcomeSound != null) {
            welcomeSound.play();
            Gdx.app.log("WitchVPN", "Playing welcome sound.");
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // Обновляем таймер начальной задержки
        if (initialDelayTimer > 0) {
            initialDelayTimer -= deltaTime;
            Gdx.app.log("WitchVPN", "Initial delay timer: " + initialDelayTimer);
            if (initialDelayTimer <= 0) {
                Gdx.app.log("WitchVPN", "Initial delay finished. Witch VPN is now active.");
                // Возможно, здесь можно добавить какой-то визуальный или звуковой сигнал активации
            }
            return; // Не выполняем остальную логику, пока есть задержка
        }

        // Проверяем столкновение с игроком
        if (target != null && isVisible) {
            handlePlayerCollision(target);
            Gdx.app.log("WitchVPN", "Checking collision with player at position: " + target.getPosition());
        }

        Gdx.app.log("WitchVPN", "Update called with deltaTime: " + deltaTime);
        
        // Обновляем таймеры
        teleportTimer += deltaTime;
        timeSinceLastAttack += deltaTime;
        moveTimer += deltaTime;
        visibilityTimer += deltaTime;

        Gdx.app.log("WitchVPN", String.format(
            "Timers - Teleport: %.2f, Attack: %.2f, Move: %.2f, Visibility: %.2f",
            teleportTimer, timeSinceLastAttack, moveTimer, visibilityTimer
        ));

        // Обновляем снаряды
        updateProjectiles(deltaTime);

        // Проверяем наличие цели
        if (target == null) {
            Gdx.app.error("WitchVPN", "Target is null!");
            return;
        }

        // Если босс видим
        if (isVisible) {
            Gdx.app.log("WitchVPN", "Boss is visible, current position: " + position.x + "," + position.y);
            
            // Проверяем, нужно ли телепортироваться
            if (teleportTimer >= teleportInterval) {
                startTeleport();
                teleportTimer = 0f;
            }

            // Проверяем, нужно ли атаковать
            if (timeSinceLastAttack >= attackCooldown) {
                float distanceToPlayer = position.dst(target.getPosition());
                if (distanceToPlayer <= attackRange) {
                    attack();
                    Gdx.app.log("WitchVPN", "Attacking player at distance: " + distanceToPlayer);
                }
            }

            // Обновляем движение
            updateMovement();

            // Обновляем общее время активности
            totalActiveTime += deltaTime;
            Gdx.app.log("WitchVPN", "Total active time: " + totalActiveTime);

            // Проверяем, прошло ли достаточно времени для победы
            if (totalActiveTime >= requiredActiveTime) {
                Gdx.app.log("WitchVPN", "Witch VPN has been active for " + requiredActiveTime + " seconds. Victory!");
                setActive(false); // Победа
            }
        } else {
            // Если босс невидим, проверяем, нужно ли сделать его видимым
            if (visibilityTimer >= visibilityDuration) {
                isVisible = true;
                visibilityTimer = 0f;
                Gdx.app.log("WitchVPN", "Becoming visible");
            }
        }
    }

    private void updateMovement() {
        if (target == null) {
            Gdx.app.log("WitchVPN", "No target for movement");
            return;
        }

        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        float distanceToPlayer = position.dst(target.getPosition());
        
        Gdx.app.log("WitchVPN", String.format(
            "Movement update - Distance to player: %.2f, Position: (%.2f, %.2f), Target: (%.2f, %.2f), Direction: (%.2f, %.2f)",
            distanceToPlayer, position.x, position.y, target.getPosition().x, target.getPosition().y,
            direction.x, direction.y
        ));

        // Двигаемся к игроку, но не слишком близко
        if (distanceToPlayer > minDistanceToPlayer) {
            float moveX = direction.x * moveSpeed * Gdx.graphics.getDeltaTime();
            float moveY = direction.y * moveSpeed * Gdx.graphics.getDeltaTime();
            
            position.add(moveX, moveY);
            
            Gdx.app.log("WitchVPN", String.format(
                "Moving towards player - Delta: (%.2f, %.2f), New position: (%.2f, %.2f), MoveSpeed: %.2f",
                moveX, moveY, position.x, position.y, moveSpeed
            ));

            // Обновляем коллизию после движения
            if (collision != null) {
                collision.update(position);
            }
        } else if (distanceToPlayer < minDistanceToPlayer) {
             // Если игрок слишком близко, отходим от него
            float moveX = -direction.x * moveSpeed * Gdx.graphics.getDeltaTime(); // Движемся в обратном направлении
            float moveY = -direction.y * moveSpeed * Gdx.graphics.getDeltaTime();

            position.add(moveX, moveY);

            Gdx.app.log("WitchVPN", String.format(
                "Moving away from player - Delta: (%.2f, %.2f), New position: (%.2f, %.2f), MoveSpeed: %.2f",
                moveX, moveY, position.x, position.y, moveSpeed
            ));

            // Обновляем коллизию после движения
            if (collision != null) {
                collision.update(position);
            }
        } else {
            Gdx.app.log("WitchVPN", "Maintaining distance from player");
        }
    }

    private void startTeleport() {
        isVisible = false;
        visibilityTimer = 0f;
        teleportTimer = 0f;
        Gdx.app.log("WitchVPN", "Starting teleport");
        teleportToRandomPlatform();
    }

    private void teleportToRandomPlatform() {
        List<Rectangle> platforms = physicsSystem.getPlatforms();
        if (platforms == null || platforms.isEmpty()) {
            Gdx.app.error("WitchVPN", "No platforms available for teleportation");
            return;
        }

        Gdx.app.log("WitchVPN", "Available platforms: " + platforms.size());

        // Выбираем платформу, которая не слишком близко и не слишком далеко от игрока
        Rectangle bestPlatform = null;
        float bestDistance = Float.MAX_VALUE;
        
        for (Rectangle platform : platforms) {
            if (target != null) {
                float platformCenterX = platform.x + platform.width / 2;
                float platformCenterY = platform.y + platform.height / 2;
                float distance = new Vector2(platformCenterX, platformCenterY)
                    .dst(target.getPosition());
                
                Gdx.app.log("WitchVPN", String.format(
                    "Platform at (%.2f, %.2f) distance to player: %.2f",
                    platform.x, platform.y, distance
                ));
                
                if (distance >= minDistanceToPlayer && 
                    distance <= maxDistanceToPlayer && 
                    distance < bestDistance) {
                    bestPlatform = platform;
                    bestDistance = distance;
                }
            }
        }

        // Если не нашли подходящую платформу, берем случайную
        if (bestPlatform == null) {
            bestPlatform = platforms.get(random.nextInt(platforms.size()));
            Gdx.app.log("WitchVPN", String.format(
                "Using random platform at (%.2f, %.2f)",
                bestPlatform.x, bestPlatform.y
            ));
        }

        // Вычисляем позицию на платформе
        float x = bestPlatform.x + random.nextFloat() * (bestPlatform.width - 64);
        float y = bestPlatform.y + bestPlatform.height + 10;
        
        Gdx.app.log("WitchVPN", String.format(
            "Teleporting to position: (%.2f, %.2f)",
            x, y
        ));
        
        // Телепортируем босса
        position.set(x, y);
        
        // Обновляем коллизию
        if (collision != null) {
            collision.update(position);
        }

        // Устанавливаем новую цель для движения
        if (target != null) {
            targetPosition.set(target.getPosition());
        }
    }

    private void attack() {
        if (target == null) return;

        // Создаем снаряд
        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        Projectile projectile = new Projectile(
            position.x + 32, // Центр босса
            position.y + 32,
            direction.x * projectileSpeed,
            direction.y * projectileSpeed,
            projectileDamage,
            spriteManager
        );
        projectiles.add(projectile);
        
        // Воспроизводим звук атаки
        if (attackSound != null) {
            attackSound.play();
            Gdx.app.log("WitchVPN", "Playing attack sound.");
        }
        
        timeSinceLastAttack = 0f;
    }

    private void updateProjectiles(float deltaTime) {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update(deltaTime);
            
            // Проверяем столкновение с игроком
            if (target != null && projectile.getCollisionBounds()
                .overlaps(target.getCollisionBounds())) {
                target.takeDamage(projectile.getDamage());
                projectiles.remove(i);
                continue;
            }
            
            // Удаляем снаряды, которые вышли за пределы экрана
            if (projectile.isOutOfBounds()) {
                projectiles.remove(i);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // Рендерим снаряды
        for (Projectile projectile : projectiles) {
            projectile.render(batch);
        }

        // Рендерим босса, если он видим
        if (isVisible) {
            super.render(batch);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Projectile projectile : projectiles) {
            projectile.dispose();
        }
        projectiles.clear();
        try {
            if (welcomeSound != null) {
                welcomeSound.dispose();
            }
        } catch (Exception e) {
            Gdx.app.error("WitchVPN", "Error disposing resources", e);
        }
    }

    private void handlePlayerCollision(Player player) {
        if (player != null && collision != null && player.getCollisionComponent() != null) {
            if (collision.collidesWith(player.getCollisionComponent())) {
                // Проверяем, прыгает ли игрок на Ведьму сверху
                // Проверяем, что игрок движется вниз (velocity.y < 0) и находится выше центра Ведьмы
                if (player.getVelocity().y < 0 && player.getPosition().y > position.y + collision.getBounds().height / 2) {
                    // Игрок прыгнул на Ведьму
                    currentHits++; // Увеличиваем счетчик прыжков
                    Gdx.app.log("WitchVPN", "WitchVPN took a jump hit! Current hits: " + currentHits + "/" + hitsToDefeat);

                    // Отбрасываем игрока вверх после успешного прыжка на босса
                    player.getVelocity().y = 400f; // Можно настроить силу отскока

                    // Проверяем условие победы по прыжкам
                    if (currentHits >= hitsToDefeat) {
                         Gdx.app.log("WitchVPN", "WitchVPN defeated after " + currentHits + " jump hits!");
                        // Воспроизводим звук смерти
                        if (deathSound != null) {
                            deathSound.play();
                            Gdx.app.log("WitchVPN", "Playing death sound.");
                        }
                        setActive(false); // Ведьма побеждена
                    }
                } else {
                    // Если игрок столкнулся с Ведьмой, но не прыгал на нее сверху, игрок получает урон
                    player.takeDamage(5f); // Уменьшаем урон
                    Gdx.app.log("WitchVPN", "Player took damage from WitchVPN. Player health: " + player.getHealth());
                    // Отбрасываем игрока в сторону от ведьмы
                    Vector2 knockbackDirection = new Vector2(player.getPosition()).sub(position).nor();
                     player.getVelocity().set(
                        knockbackDirection.x * 300f, // Горизонтальная составляющая отбрасывания
                        player.getVelocity().y // Не меняем вертикальную скорость при горизонтальном отбрасывании
                     );
                    Gdx.app.log("WitchVPN", "Player knocked back with velocity: " + player.getVelocity());
                }
            }
        }
    }

    public void takeDamage(float damage) {
        // Этот метод теперь используется для урона НЕ от прыжков (например, от будущих атак игрока)
        // Он уменьшает здоровье, но не влияет на счетчик currentHits, который теперь только для прыжков
        health -= damage;
        Gdx.app.log("WitchVPN", "WitchVPN took damage: " + damage + ", current health: " + health);
        // Условие победы по здоровью убрано, победа только по прыжкам
    }

    public float getHealth() {
        return health;
    }
} 