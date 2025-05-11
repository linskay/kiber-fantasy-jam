package com.cyberkingdom.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
    private int hitsToDefeat = 3; // Количество прыжков для победы
    private int currentHits = 0; // Текущее количество попаданий

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
    }

    @Override
    public void setTarget(Player target) {
        super.setTarget(target);
        this.target = target;
        Gdx.app.log("WitchVPN", "Target set to player at position: " + 
            (target != null ? target.getPosition().x + "," + target.getPosition().y : "null"));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // Проверяем столкновение с игроком
        if (target != null && isVisible) {
            handlePlayerCollision(target);
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
                "Moving - Delta: (%.2f, %.2f), New position: (%.2f, %.2f), MoveSpeed: %.2f",
                moveX, moveY, position.x, position.y, moveSpeed
            ));

            // Обновляем коллизию после движения
            if (collision != null) {
                collision.update(position);
            }
        } else {
            Gdx.app.log("WitchVPN", "Too close to player, not moving");
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
    }

    private void handlePlayerCollision(Player player) {
        if (player.getCollisionComponent().collidesWith(getCollisionComponent())) {
            // Проверяем, что игрок находится выше ведьмы
            if (player.getPosition().y > getPosition().y + getCollisionComponent().getBounds().height / 2) {
                // Игрок прыгнул на ведьму
                currentHits++;
                Gdx.app.log("WitchVPN", "Player hit witch from above! Hits: " + currentHits + "/" + hitsToDefeat);
                
                // Отбрасываем игрока на случайную платформу
                teleportPlayerToRandomPlatform(player);
                
                if (currentHits >= hitsToDefeat) {
                    // Ведьма побеждена
                    Gdx.app.log("WitchVPN", "Witch defeated!");
                    setActive(false);
                }
            } else {
                // Игрок получил урон от ведьмы
                player.takeDamage(1);
                // Отбрасываем игрока вверх и в сторону от ведьмы
                Vector2 knockbackDirection = new Vector2(player.getPosition()).sub(getPosition()).nor();
                player.getVelocity().set(
                    knockbackDirection.x * 300f, // Горизонтальная составляющая отбрасывания
                    400f // Вертикальная составляющая отбрасывания
                );
                Gdx.app.log("WitchVPN", "Player knocked back with velocity: " + player.getVelocity());
            }
        }
    }

    private void teleportPlayerToRandomPlatform(Player player) {
        List<Rectangle> platforms = physicsSystem.getPlatforms();
        if (!platforms.isEmpty()) {
            // Выбираем случайную платформу
            int randomIndex = random.nextInt(platforms.size());
            Rectangle targetPlatform = platforms.get(randomIndex);
            
            // Вычисляем позицию для телепортации (над платформой)
            float teleportX = targetPlatform.x + targetPlatform.width / 2;
            float teleportY = targetPlatform.y + targetPlatform.height + 50; // 50 пикселей над платформой
            
            // Телепортируем игрока
            player.setPosition(teleportX, teleportY);
            
            // Сбрасываем скорость игрока
            player.getVelocity().set(0, 0);
            
            Gdx.app.log("WitchVPN", "Player teleported to platform at: " + teleportX + ", " + teleportY);
        }
    }

    // Внутренний класс для снарядов
    private static class Projectile {
        private Vector2 position;
        private Vector2 velocity;
        private float damage;
        private CollisionComponent collision;
        private TextureRegion texture;

        public Projectile(float x, float y, float vx, float vy, float damage, SpriteManager spriteManager) {
            this.position = new Vector2(x, y);
            this.velocity = new Vector2(vx, vy);
            this.damage = damage;
            this.collision = new CollisionComponent(16, 16);
            this.collision.update(position);
            
            // Получаем текстуру снаряда
            TextureRegion[] frames = spriteManager.getFrames("WITCH_VPN");
            if (frames != null && frames.length > 0) {
                this.texture = frames[0];
            }
        }

        public void update(float deltaTime) {
            position.add(velocity.x * deltaTime, velocity.y * deltaTime);
            collision.update(position);
        }

        public void render(SpriteBatch batch) {
            if (texture != null) {
                batch.draw(texture, position.x - 8, position.y - 8, 16, 16);
            }
        }

        public Rectangle getCollisionBounds() {
            return collision.getBounds();
        }

        public float getDamage() {
            return damage;
        }

        public boolean isOutOfBounds() {
            return position.x < 0 || position.x > Gdx.graphics.getWidth() ||
                   position.y < 0 || position.y > Gdx.graphics.getHeight();
        }

        public void dispose() {
            collision = null;
            texture = null;
        }
    }
} 