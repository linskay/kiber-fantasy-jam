package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.items.Inventory;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.rendering.SpriteManager;

public class Player extends GameEntity implements Collidable {
    private CollisionComponent collision;
    private Inventory inventory;
    private boolean isJumping;
    private boolean onGround;
    private int jumpsLeft = 2; // Максимальное количество прыжков
    private float jumpVelocity = 400f;
    private float moveSpeed = 200f;
    private float health = 100f;
    private float maxHealth = 100f;
    private int coins = 0;
    private int cryptoCoins = 0;
    private boolean isFalling = false;
    private boolean isMoving = false;
    private boolean isFacingRight = true;
    private boolean isAttacking = false;
    private float attackCooldown = 0;
    private float attackDuration = 0;
    private float attackRange = 50;
    private float attackDamage = 20;
    private float attackCooldownTime = 0.5f;
    private float attackDurationTime = 0.2f;
    private float gravity = -500f;
    private GameScreen gameScreen;
    private Texture texture;
    private AnimationComponent animation;
    private float jumpForce = 400f;
    private float direction = 1;
    private boolean isInvulnerable = false;
    private float invulnerabilityTime = 0;
    private float invulnerabilityDuration = 1.0f;
    private boolean isDead = false;
    private float deathTime = 0;
    private float deathDuration = 1.0f;
    private Vector2 respawnPosition;
    private float respawnHealth;
    private int respawnCoins;
    private Inventory respawnInventory;
    private boolean isRespawned = false;
    private float respawnTime = 0;
    private float respawnDuration = 1.0f;
    private boolean isRespawnInvulnerable = false;
    private float respawnInvulnerabilityTime = 0;
    private float respawnInvulnerabilityDuration = 2.0f;
    private Rectangle bounds;
    private static final int ANIMATION_IDLE = 0;
    private static final int ANIMATION_RUN_LEFT = 1;
    private static final int ANIMATION_RUN_RIGHT = 2;
    private static final float MOVE_SPEED = 300f;
    private static final float JUMP_VELOCITY = 500f;
    private static final float GRAVITY = -1000f;
    private static final int ANIMATION_DEATH = 3;

    public Player(Vector2 position, SpriteManager spriteManager) {
        super("Player", spriteManager);
        this.position = position;
        this.velocity = new Vector2();
        this.moveSpeed = 200f;
        this.jumpForce = 400f;
        this.gravity = 980f;
        this.isJumping = false;
        this.isFacingRight = true;
        this.health = 100;
        this.maxHealth = 100;
        this.coins = 0;
        this.cryptoCoins = 0;
        this.inventory = new Inventory(spriteManager);
        this.respawnPosition = new Vector2(position);
        this.respawnInventory = new Inventory(spriteManager);
        this.collision = new CollisionComponent(32, 32);
        this.collision.update(position);
        this.isFalling = false;
        this.isMoving = false;
        this.isAttacking = false;
        this.attackCooldown = 0;
        this.attackDuration = 0;
        this.attackRange = 50;
        this.attackDamage = 20;
        this.attackCooldownTime = 0.5f;
        this.attackDurationTime = 0.2f;
        this.isInvulnerable = false;
        this.invulnerabilityTime = 0;
        this.invulnerabilityDuration = 1.0f;
        this.isDead = false;
        this.deathTime = 0;
        this.deathDuration = 1.0f;
        this.respawnHealth = maxHealth;
        this.respawnCoins = coins;
        if (inventory != null && inventory.getItems() != null) {
            for (Item item : inventory.getItems()) {
                respawnInventory.addItem(item);
            }
        }
        this.isRespawned = false;
        this.respawnTime = 0;
        this.respawnDuration = 1.0f;
        this.isRespawnInvulnerable = false;
        this.respawnInvulnerabilityTime = 0;
        this.respawnInvulnerabilityDuration = 2.0f;
        this.bounds = new Rectangle(position.x, position.y, 32, 32);
        
        // Инициализируем анимацию
        this.animation = new AnimationComponent();
        
        // Загружаем текстуры для анимации
        loadAnimations();
    }

    private void loadAnimations() {
        try {
            // Загружаем текстуру покоя
            Texture idleTexture = new Texture(Gdx.files.internal("assets/entities/player.png"));
            Array<TextureRegion> idleFrames = new Array<>();
            idleFrames.add(new TextureRegion(idleTexture));
            animation.addAnimation(idleFrames);
            Gdx.app.log("Player", "Idle animation loaded");

            // Загружаем анимации из SpriteManager
            TextureRegion[] leftFrames = spriteManager.getFrames("Player_Left");
            TextureRegion[] rightFrames = spriteManager.getFrames("Player_Right");
            TextureRegion[] deathFrames = spriteManager.getFrames("Player_Death");

            if (leftFrames != null && rightFrames != null && deathFrames != null) {
                // Добавляем анимации в правильном порядке
                Array<TextureRegion> leftArray = new Array<>(leftFrames);
                Array<TextureRegion> rightArray = new Array<>(rightFrames);
                Array<TextureRegion> deathArray = new Array<>(deathFrames);

                animation.addAnimation(leftArray);  // RUN_LEFT
                Gdx.app.log("Player", "Left animation added with " + leftArray.size + " frames");
                
                animation.addAnimation(rightArray); // RUN_RIGHT
                Gdx.app.log("Player", "Right animation added with " + rightArray.size + " frames");
                
                animation.addAnimation(deathArray); // DEATH
                Gdx.app.log("Player", "Death animation added with " + deathArray.size + " frames");

                // Устанавливаем начальную анимацию (покой)
                animation.setCurrentAnimation(ANIMATION_IDLE);
                
                Gdx.app.log("Player", "All animations loaded successfully");
            } else {
                Gdx.app.error("Player", "Failed to load animation frames");
                if (leftFrames == null) Gdx.app.error("Player", "Left frames are null");
                if (rightFrames == null) Gdx.app.error("Player", "Right frames are null");
                if (deathFrames == null) Gdx.app.error("Player", "Death frames are null");
            }
        } catch (Exception e) {
            Gdx.app.error("Player", "Error loading animations", e);
        }
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    @Override
    public Rectangle getCollisionBounds() {
        bounds.setPosition(position.x, position.y);
        return bounds;
    }

    public float getMaxHealth() { return maxHealth; }
    public Inventory getInventory() { return inventory; }
    public boolean isJumping() { return isJumping; }
    public void setJumping(boolean jumping) { this.isJumping = jumping; }
    public boolean isOnGround() { return onGround; }
    public void setOnGround(boolean onGround) { 
        this.onGround = onGround;
        if (onGround) {
            jumpsLeft = 2; // Восстанавливаем прыжки при приземлении
            isJumping = false;
        }
    }
    public boolean canJump() {
        return jumpsLeft > 0;
    }
    public void useJump() {
        if (jumpsLeft > 0) {
            jumpsLeft--;
            isJumping = true;
            onGround = false;
        }
    }
    public int getJumpsLeft() {
        return jumpsLeft;
    }
    public float getJumpVelocity() {
        return jumpVelocity;
    }
    public float getMoveSpeed() {
        return moveSpeed;
    }
    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = Math.max(0, Math.min(maxHealth, health)); }
    public void takeDamage(float damage) { setHealth(health - damage); }
    public int getCoins() { return coins; }
    public void addCoin() { coins++; }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        collision.update(position);

        // Обновляем анимацию в зависимости от движения
        if (velocity.x < 0) {
            animation.setCurrentAnimation(ANIMATION_RUN_LEFT);
            isFacingRight = false;
            Gdx.app.log("Player", "Setting LEFT animation, velocity: " + velocity.x + ", current animation: " + animation.getCurrentAnimation());
        } else if (velocity.x > 0) {
            animation.setCurrentAnimation(ANIMATION_RUN_RIGHT);
            isFacingRight = true;
            Gdx.app.log("Player", "Setting RIGHT animation, velocity: " + velocity.x + ", current animation: " + animation.getCurrentAnimation());
        } else {
            animation.setCurrentAnimation(ANIMATION_IDLE);
            Gdx.app.log("Player", "Setting IDLE animation, velocity: " + velocity.x + ", current animation: " + animation.getCurrentAnimation());
        }

        // Ограничения по X
        float minX = 0;
        float maxX = 1200 - 64;

        if (position.x < minX) {
            position.x = minX;
            velocity.x = 0;
        } else if (position.x > maxX) {
            position.x = maxX;
            velocity.x = 0;
        }
    }

    public void move(float deltaX, float deltaY) {
        float minX = 0;
        float maxX = 1200 - 64; // 1200 — ширина уровня, 64 — ширина игрока

        float newX = position.x + deltaX;

        if (newX < minX) {
            position.x = minX;
            velocity.x = Math.abs(velocity.x) * 0.5f; // небольшой отскок вправо
        } else if (newX > maxX) {
            position.x = maxX;
            velocity.x = -Math.abs(velocity.x) * 0.5f; // небольшой отскок влево
        } else {
            position.x = newX;
        }

        position.y += deltaY;
        collision.update(position);
    }

    public void collectCoin() {
        coins++;
        if (gameScreen != null) {
            gameScreen.updateCoinCount(coins);
        }
        Gdx.app.log("Player", "Collected coin, total: " + coins);
    }

    public void collectItem(Item item) {
        if (inventory != null) {
            inventory.addItem(item);
            Gdx.app.log("Player", "Collected item: " + item.getItemType());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (animation != null) {
            TextureRegion currentFrame = animation.getCurrentFrame(Gdx.graphics.getDeltaTime());
            if (currentFrame != null) {
                float x = position.x;
                float y = position.y;
                float width = 64;
                float height = 64;
                
                // Просто отрисовываем текущий кадр без отражения
                batch.draw(currentFrame, x, y, width, height);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (inventory != null) {
            inventory.dispose();
            inventory = null;
        }
        if (collision != null) {
            collision = null;
        }
        gameScreen = null;
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
        if (animation != null) {
            animation.dispose();
            animation = null;
        }
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void moveLeft() {
        velocity.x = -MOVE_SPEED;
    }

    public void moveRight() {
        velocity.x = MOVE_SPEED;
    }

    public void stop() {
        velocity.x = 0;
    }

    public void jump() {
        if (!isJumping) {
            velocity.y = JUMP_VELOCITY;
            isJumping = true;
        }
    }

    public void heal(float amount) {
        health = Math.min(maxHealth, health + amount);
    }
}