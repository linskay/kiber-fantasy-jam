package com.cyberkingdom.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.rendering.SpriteManager;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.entities.EntitySystem;
import java.util.List;
import java.util.ArrayList;
import com.cyberkingdom.entities.EntityFactory;
import com.cyberkingdom.entities.Projectile;

public class DedinsaidBoss extends GameEntity implements Collidable {
    private static final float MOVE_SPEED = 150f;
    private static final float ATTACK_RANGE = 300f;
    private static final float ATTACK_DAMAGE = 20f;
    private static final float ATTACK_COOLDOWN = 1.0f;
    private static final float HIT_COOLDOWN = 0.5f;
    private static final float BOSS_SIZE = 64f;
    private static final float GRAVITY = -1000f;
    private static final float JUMP_VELOCITY = 500f;
    private static final float LEVEL_WIDTH = 1200f;
    private static final float LEVEL_HEIGHT = 800f;
    private static final float GROUND_Y = 125f;
    
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> attackAnimation;
    private float stateTime;
    private float attackCooldown;
    private float hitCooldown;
    private boolean isAttacking;
    private int hitsRequired = 5;
    private int currentHits = 0;
    private boolean isDefeated;
    private Player target;
    private CollisionComponent collision;
    private Rectangle bounds;
    private float direction = 1f; // 1 для движения вправо, -1 для движения влево
    private boolean onGround = false;
    private EntitySystem entitySystem;
    private boolean isActive = true;
    private List<Projectile> projectiles; // Список снарядов Дединсайда
    private EntityFactory entityFactory; // Для создания снарядов

    public DedinsaidBoss(Vector2 position, SpriteManager spriteManager, EntitySystem entitySystem) {
        super("DedinsaidBoss", spriteManager);
        this.position = position;
        this.velocity = new Vector2();
        this.collision = new CollisionComponent(BOSS_SIZE, BOSS_SIZE);
        this.bounds = new Rectangle(position.x, position.y, BOSS_SIZE, BOSS_SIZE);
        this.stateTime = 0f;
        this.attackCooldown = 0f;
        this.hitCooldown = 0f;
        this.isAttacking = false;
        this.isDefeated = false;
        this.entitySystem = entitySystem;
        this.projectiles = new ArrayList<>(); // Инициализируем список снарядов
        // Получаем EntityFactory из EntitySystem
        if (entitySystem != null && entitySystem.getFactory() != null) {
            this.entityFactory = entitySystem.getFactory();
        } else {
            Gdx.app.error("DedinsaidBoss", "EntityFactory is null! Cannot create projectiles.");
        }
        
        loadAnimations(spriteManager);
        Gdx.app.log("DedinsaidBoss", "Initialized at position: " + position.x + ", " + position.y);
    }

    private void loadAnimations(SpriteManager spriteManager) {
        // Загрузка анимации бездействия
        TextureRegion[] idleFrames = new TextureRegion[4];
        boolean allFramesLoaded = true;
        
        for (int i = 0; i < 4; i++) {
            String textureKey = "dedinsaid" + (i + 1);
            Texture texture = spriteManager.getTexture(textureKey);
            if (texture != null) {
                idleFrames[i] = new TextureRegion(texture);
                Gdx.app.log("DedinsaidBoss", "Loaded texture for frame " + (i + 1) + ": " + textureKey);
            } else {
                Gdx.app.error("DedinsaidBoss", "FAILED to load texture for frame " + (i + 1) + ": " + textureKey);
                allFramesLoaded = false;
            }
        }
        
        if (allFramesLoaded) {
            idleAnimation = new Animation<>(0.2f, idleFrames);
            attackAnimation = idleAnimation;
        } else {
            // Создаем запасную текстуру
            Gdx.app.log("DedinsaidBoss", "Creating fallback texture");
            Texture fallbackTexture = new Texture(64, 64, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            fallbackTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            TextureRegion fallbackFrame = new TextureRegion(fallbackTexture);
            idleAnimation = new Animation<>(0.2f, fallbackFrame);
            attackAnimation = idleAnimation;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (isDefeated) return;

        stateTime += deltaTime;
        attackCooldown -= deltaTime;
        hitCooldown -= deltaTime;

        // Гравитация
        velocity.y += GRAVITY * deltaTime;

        // Проверка платформ под ногами
        boolean landed = false;
        for (GameEntity entity : entitySystem.getEntities()) {
            if (entity instanceof Platform) {
                Platform platform = (Platform) entity;
                Rectangle platRect = platform.getRectangle();
                Rectangle bossRect = getCollisionBounds();
                if (bossRect.overlaps(platRect)) {
                    // Если босс падает сверху на платформу
                    if (velocity.y < 0 && position.y > platRect.y + platRect.height - 10) {
                        position.y = platRect.y + platRect.height;
                        velocity.y = 0;
                        landed = true;
                    }
                }
            }
        }
        onGround = landed;

        // Проверка на выход за пределы экрана
        if (position.y < GROUND_Y) {
            position.y = GROUND_Y;
            velocity.y = 0;
            onGround = true;
        }

        // Прыжок, если игрок выше и босс на платформе
        if (target != null && onGround && target.getPosition().y > position.y + 10) {
            velocity.y = JUMP_VELOCITY;
            onGround = false;
        }

        // Движение к игроку по X
        if (target != null && !isAttacking) {
            float targetX = target.getPosition().x;
            float dx = targetX - position.x;
            if (Math.abs(dx) > 5) {
                velocity.x = Math.signum(dx) * MOVE_SPEED;
                direction = Math.signum(dx);
            } else {
                velocity.x = 0;
            }
        }

        // Если игрок в пределах атаки и кулдаун прошел, атакуем
        if (target != null && !isDefeated) {
            float distanceToTarget = position.dst(target.getPosition());
            if (distanceToTarget <= ATTACK_RANGE && attackCooldown <= 0) {
                attack();
            }
        }

        // Ограничение движения по X
        if (position.x < 0) {
            position.x = 0;
            velocity.x = 0;
        } else if (position.x > LEVEL_WIDTH - BOSS_SIZE) {
            position.x = LEVEL_WIDTH - BOSS_SIZE;
            velocity.x = 0;
        }

        // Обновление позиции
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
        collision.update(position);
        bounds.setPosition(position.x, position.y);

        // Обновляем снаряды
        updateProjectiles(deltaTime);
    }

    private void attack() {
        if (target != null && !isDefeated && entityFactory != null) {
            isAttacking = true;
            attackCooldown = ATTACK_COOLDOWN;
            
            // Создаем снаряд, направленный в игрока
            Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
            // Создаем снаряд через EntityFactory
            Projectile projectile = entityFactory.createProjectile(
                position.x + BOSS_SIZE / 2, // Центр босса
                position.y + BOSS_SIZE / 2,
                direction.x * 300f, // Скорость снаряда (можно настроить)
                direction.y * 300f, // Скорость снаряда
                ATTACK_DAMAGE // Урон снаряда
            );
            
            if (projectile != null) {
                projectiles.add(projectile);
                entitySystem.addEntity(projectile); // Добавляем снаряд в EntitySystem
                 Gdx.app.log("DedinsaidBoss", "Created and added projectile to EntitySystem");
            } else {
                 Gdx.app.error("DedinsaidBoss", "Failed to create projectile.");
            }

            // Сброс состояния атаки (если анимация атаки короткая)
            // isAttacking = false; // Возможно, нужно сбрасывать после анимации
             Gdx.app.log("DedinsaidBoss", "Attacking! Created projectile.");

        }
    }

    private void updateProjectiles(float deltaTime) {
        // Проходим в обратном порядке, чтобы безопасно удалять снаряды
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update(deltaTime);

            // Проверяем столкновение с игроком
            if (target != null && projectile.getCollisionBounds().overlaps(target.getCollisionBounds())) {
                target.takeDamage(projectile.getDamage());
                 Gdx.app.log("DedinsaidBoss", "Projectile hit player! Player health: " + target.getHealth());
                projectiles.remove(i);
                entitySystem.removeEntity(projectile); // Удаляем снаряд из EntitySystem
                 Gdx.app.log("DedinsaidBoss", "Removed projectile after hitting player.");
                continue; // Переходим к следующему снаряду
            }

            // Удаляем снаряды, которые вышли за пределы экрана
            if (projectile.isOutOfBounds()) {
                projectiles.remove(i);
                entitySystem.removeEntity(projectile); // Удаляем снаряд из EntitySystem
                 Gdx.app.log("DedinsaidBoss", "Removed out-of-bounds projectile.");
            }
        }
    }

    public void takeHit() {
        if (hitCooldown <= 0 && !isDefeated) {
            currentHits++;
            hitCooldown = HIT_COOLDOWN;
            Gdx.app.log("DedinsaidBoss", "Took hit! Current hits: " + currentHits + "/" + hitsRequired);
            
            if (currentHits >= hitsRequired) {
                isDefeated = true;
                isActive = false;
                Gdx.app.log("DedinsaidBoss", "Boss defeated! Setting isDefeated=true and isActive=false");
                // Уведомляем систему о победе над боссом
                if (entitySystem != null) {
                    entitySystem.onBossDefeated();
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isDefeated) return;

        // Рендерим снаряды
        for (Projectile projectile : projectiles) {
            projectile.render(batch);
        }

        TextureRegion currentFrame = isAttacking ? 
            attackAnimation.getKeyFrame(stateTime, true) : 
            idleAnimation.getKeyFrame(stateTime, true);

        if (currentFrame != null) {
            if (direction < 0) {
                currentFrame.flip(true, false);
            }
            // Используем размеры кадра для отрисовки
            batch.draw(currentFrame, position.x, position.y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
            if (direction < 0) {
                currentFrame.flip(true, false);
            }
        }
        // Добавляем логирование позиции босса при рендеринге
        Gdx.app.log("DedinsaidBoss", "Rendering at position: " + position.x + ", " + position.y);
    }

    @Override
    public void dispose() {
        super.dispose();

        // Освобождаем ресурсы снарядов
        if (projectiles != null) {
            for (Projectile projectile : projectiles) {
                projectile.dispose();
            }
            projectiles.clear();
        }
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    @Override
    public Rectangle getCollisionBounds() {
        return bounds;
    }

    public void setTarget(Player player) {
        this.target = player;
    }

    public boolean isDefeated() {
        Gdx.app.log("DedinsaidBoss", "isDefeated() called: " + isDefeated + ", currentHits: " + currentHits + "/" + hitsRequired);
        return isDefeated;
    }

    public int getCurrentHits() {
        return currentHits;
    }

    public int getHitsRequired() {
        return hitsRequired;
    }

    private void die() {
        isDefeated = true;
        isActive = false;
        Gdx.app.log("DedinsaidBoss", "Dedinsaid is defeated!");
        // Возможно, здесь нужно уведомить EntitySystem о поражении
    }
} 