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
import com.cyberkingdom.minigames.WifiKeyMinigame;
import com.cyberkingdom.entities.EntitySystem;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.audio.Sound;

public class Player extends GameEntity implements Collidable {
    private static final float MOVE_SPEED = 300f;
    private static final float JUMP_VELOCITY = 500f;
    private static final float GRAVITY = -1000f;
    private static final float ATTACK_RANGE = 50f;
    private static final float ATTACK_DAMAGE = 20f;
    private static final float ATTACK_COOLDOWN = 0.5f;
    private static final float ATTACK_DURATION = 0.2f;
    private static final float INVULNERABILITY_DURATION = 1.0f;
    private static final float DEATH_DURATION = 1.0f;
    private static final float RESPAWN_DURATION = 1.0f;
    private static final float RESPAWN_INVULNERABILITY_DURATION = 2.0f;
    private static final int MAX_JUMPS = 2;
    private static final int ANIMATION_IDLE = 0;
    private static final int ANIMATION_RUN_LEFT = 1;
    private static final int ANIMATION_RUN_RIGHT = 2;
    private static final int ANIMATION_DEATH = 3;
    private static final float PLAYER_SIZE = 64f;
    private static final float LEVEL_WIDTH = 1200f;

    private Inventory inventory;
    private boolean isJumping;
    private boolean onGround;
    private int jumpsLeft;
    private float health;
    private float maxHealth;
    private int coins;
    private int cryptoCoins;
    private boolean isFalling;
    private boolean isMoving;
    private boolean isFacingRight;
    private boolean isAttacking;
    private float attackCooldown;
    private float attackDuration;
    private float invulnerabilityTime;
    private boolean isInvulnerable;
    private boolean isDead;
    private float deathTime;
    private Vector2 respawnPosition;
    private float respawnHealth;
    private int respawnCoins;
    private Inventory respawnInventory;
    private boolean isRespawned;
    private float respawnTime;
    private boolean isRespawnInvulnerable;
    private float respawnInvulnerabilityTime;
    private GameScreen gameScreen;
    private Rectangle bounds;
    private CollisionComponent collision;
    private AnimationComponent animation;
    private float jumpForce = 400f;
    private float direction = 1;
    private float attackCooldownTime = 0.5f;
    private float attackDurationTime = 0.2f;
    private float gravity = -500f;
    private float deathDuration = 1.0f;
    private float respawnDuration = 1.0f;
    private float respawnInvulnerabilityDuration = 2.0f;
    private float respawnInvulnerabilityDurationTime = 0;
    private WifiKeyMinigame wifiKeyMinigame;
    private boolean isPlayingMinigame;
    private SpriteManager spriteManager;
    private boolean isInMinigame;
    private EntitySystem entitySystem;
    private Texture texture;
    private Sound coinSound;
    private Sound jumpSound;
    private Sound inventorySound;

    public Player(Vector2 position, SpriteManager spriteManager) {
        super("Player", spriteManager);
        this.spriteManager = spriteManager;
        this.inventory = new Inventory(spriteManager);
        this.health = 100;
        this.maxHealth = 100;
        this.coins = 0;
        this.isInMinigame = false;
        this.wifiKeyMinigame = null;
        initializePlayer(position);
        loadAnimations();
        loadSounds();
    }

    private void initializePlayer(Vector2 position) {
        this.position = position;
        this.velocity = new Vector2();
        this.jumpsLeft = MAX_JUMPS;
        this.health = 100;
        this.maxHealth = 100;
        this.coins = 0;
        this.cryptoCoins = 0;
        this.inventory = new Inventory(spriteManager);
        this.respawnPosition = new Vector2(position);
        this.respawnInventory = new Inventory(spriteManager);
        this.collision = new CollisionComponent(PLAYER_SIZE, PLAYER_SIZE);
        this.collision.update(position);
        this.isFacingRight = true;
        this.bounds = new Rectangle(position.x, position.y, PLAYER_SIZE, PLAYER_SIZE);
        
        // Инициализируем анимацию
        this.animation = new AnimationComponent();
    }

    private void loadAnimations() {
        if (spriteManager == null) {
            Gdx.app.error("Player", "SpriteManager is null!");
            return;
        }

        try {
            // Загрузка анимации idle
            Texture idleTexture = spriteManager.getTexture("Player_Idle");
            if (idleTexture != null) {
                Array<TextureRegion> idleFrames = new Array<>();
                idleFrames.add(new TextureRegion(idleTexture));
                animation.addAnimation(idleFrames, 0.1f);
                Gdx.app.log("Player", "Idle animation loaded successfully");
            } else {
                Gdx.app.error("Player", "Failed to load idle texture");
                createFallbackTexture();
            }

            // Загрузка анимации бега влево
            Array<TextureRegion> runLeftFrames = new Array<>();
            boolean leftFramesLoaded = true;
            for (int i = 1; i <= 4; i++) {
                String textureKey = "Player_Run_Left_" + i;
                Texture frame = spriteManager.getTexture(textureKey);
                if (frame != null) {
                    runLeftFrames.add(new TextureRegion(frame));
                    Gdx.app.log("Player", "Loaded left run frame " + i);
                } else {
                    Gdx.app.error("Player", "Failed to load left run frame " + i + " with key: " + textureKey);
                    leftFramesLoaded = false;
                }
            }
            if (leftFramesLoaded && !runLeftFrames.isEmpty()) {
                animation.addAnimation(runLeftFrames, 0.2f);
                Gdx.app.log("Player", "Run left animation loaded successfully with " + runLeftFrames.size + " frames");
            } else {
                Gdx.app.error("Player", "Failed to load run left textures");
            }

            // Загрузка анимации бега вправо
            Array<TextureRegion> runRightFrames = new Array<>();
            boolean rightFramesLoaded = true;
            for (int i = 1; i <= 4; i++) {
                String textureKey = "Player_Run_Right_" + i;
                Texture frame = spriteManager.getTexture(textureKey);
                if (frame != null) {
                    runRightFrames.add(new TextureRegion(frame));
                    Gdx.app.log("Player", "Loaded right run frame " + i);
                } else {
                    Gdx.app.error("Player", "Failed to load right run frame " + i + " with key: " + textureKey);
                    rightFramesLoaded = false;
                }
            }
            if (rightFramesLoaded && !runRightFrames.isEmpty()) {
                animation.addAnimation(runRightFrames, 0.2f);
                Gdx.app.log("Player", "Run right animation loaded successfully with " + runRightFrames.size + " frames");
            } else {
                Gdx.app.error("Player", "Failed to load run right textures");
            }

            // Загрузка анимации смерти
            Array<TextureRegion> deathFrames = new Array<>();
            boolean deathFramesLoaded = true;
            for (int i = 1; i <= 4; i++) {
                String textureKey = "Player_Death_" + i;
                Texture frame = spriteManager.getTexture(textureKey);
                if (frame != null) {
                    deathFrames.add(new TextureRegion(frame));
                    Gdx.app.log("Player", "Loaded death frame " + i);
                } else {
                    Gdx.app.error("Player", "Failed to load death frame " + i + " with key: " + textureKey);
                    deathFramesLoaded = false;
                }
            }
            if (deathFramesLoaded && !deathFrames.isEmpty()) {
                animation.addAnimation(deathFrames, 0.2f);
                Gdx.app.log("Player", "Death animation loaded successfully with " + deathFrames.size + " frames");
            } else {
                Gdx.app.error("Player", "Failed to load death textures");
            }
        } catch (Exception e) {
            Gdx.app.error("Player", "Error loading animations", e);
            createFallbackTexture();
        }
    }

    private void createFallbackTexture() {
        Gdx.app.log("Player", "Creating fallback texture");
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.MAGENTA);
        pixmap.fill();
        texture = new Texture(pixmap);
        pixmap.dispose();
        
        Array<TextureRegion> fallbackFrames = new Array<>();
        fallbackFrames.add(new TextureRegion(texture));
        animation.addAnimation(fallbackFrames, 0.1f);
        Gdx.app.log("Player", "Fallback texture created and added to animation");
    }

    private void loadSounds() {
        try {
            coinSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/coin.mp3"));
            Gdx.app.log("Player", "Coin sound loaded.");
        } catch (Exception e) {
            Gdx.app.error("Player", "Failed to load coin sound: " + e.getMessage());
        }

        try {
            jumpSound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/gg_jump.mp3"));
            Gdx.app.log("Player", "Jump sound loaded.");
        } catch (Exception e) {
            Gdx.app.error("Player", "Failed to load jump sound: " + e.getMessage());
        }

        try {
            inventorySound = Gdx.audio.newSound(Gdx.files.internal("assets/musics/inventory.mp3"));
            Gdx.app.log("Player", "Inventory sound loaded.");
        } catch (Exception e) {
            Gdx.app.error("Player", "Failed to load inventory sound: " + e.getMessage());
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
            jumpsLeft = MAX_JUMPS;
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
        return JUMP_VELOCITY;
    }
    public float getMoveSpeed() {
        return MOVE_SPEED;
    }
    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = Math.max(0, Math.min(maxHealth, health)); }
    public void takeDamage(float damage) {
        if (!isInvulnerable && !isRespawnInvulnerable) {
            health = Math.max(0, health - damage);
            isInvulnerable = true;
            invulnerabilityTime = 0;
            
            if (health <= 0) {
                die();
            }
        }
    }
    public int getCoins() { return coins; }
    public void addCoin() { coins++; }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        collision.update(position);

        updateAnimation();
        updatePosition(deltaTime);
        updateTimers(deltaTime);
    }

    private void updateAnimation() {
        if (velocity.x < 0) {
            if (animation.getCurrentAnimation() != ANIMATION_RUN_LEFT) {
                animation.setCurrentAnimation(ANIMATION_RUN_LEFT);
                isFacingRight = false;
            }
        } else if (velocity.x > 0) {
            if (animation.getCurrentAnimation() != ANIMATION_RUN_RIGHT) {
                animation.setCurrentAnimation(ANIMATION_RUN_RIGHT);
                isFacingRight = true;
            }
        } else {
            if (animation.getCurrentAnimation() != ANIMATION_IDLE) {
                animation.setCurrentAnimation(ANIMATION_IDLE);
            }
        }
    }

    private void updatePosition(float deltaTime) {
        float minX = 0;
        float maxX = LEVEL_WIDTH - PLAYER_SIZE;

        if (position.x < minX) {
            position.x = minX;
            velocity.x = 0;
        } else if (position.x > maxX) {
            position.x = maxX;
            velocity.x = 0;
        }
    }

    private void updateTimers(float deltaTime) {
        if (isInvulnerable) {
            invulnerabilityTime += deltaTime;
            if (invulnerabilityTime >= INVULNERABILITY_DURATION) {
                isInvulnerable = false;
            }
        }

        if (isDead) {
            deathTime += deltaTime;
            if (deathTime >= DEATH_DURATION) {
                respawn();
            }
        }

        if (isRespawned) {
            respawnTime += deltaTime;
            if (respawnTime >= RESPAWN_DURATION) {
                isRespawned = false;
            }
        }

        if (isRespawnInvulnerable) {
            respawnInvulnerabilityTime += deltaTime;
            if (respawnInvulnerabilityTime >= RESPAWN_INVULNERABILITY_DURATION) {
                isRespawnInvulnerable = false;
            }
        }
    }

    public void move(float deltaX, float deltaY) {
        float minX = 0;
        float maxX = LEVEL_WIDTH - PLAYER_SIZE;

        float newX = position.x + deltaX;

        if (newX < minX) {
            position.x = minX;
            velocity.x = Math.abs(velocity.x) * 0.5f;
        } else if (newX > maxX) {
            position.x = maxX;
            velocity.x = -Math.abs(velocity.x) * 0.5f;
        } else {
            position.x = newX;
        }

        position.y += deltaY;
        collision.update(position);
    }

    public void collectCoin(Item coinEntity) {
        coins++;
        if (gameScreen != null) {
            gameScreen.updateCoinCount(coins);

            // Воспроизводим звук сбора монеты
            if (coinSound != null) {
                coinSound.play();
                Gdx.app.log("Player", "Playing coin sound.");
            }

            // Удаляем сущность монеты из EntitySystem после сбора
            EntitySystem es = getEntitySystem();
            if (es != null) {
                es.removeEntity(coinEntity);
                Gdx.app.log("Player", "Removed coin entity from EntitySystem.");
            } else {
                 Gdx.app.error("Player", "EntitySystem is null in collectCoin, cannot remove coin entity.");
            }
        }
        Gdx.app.log("Player", "Collected coin, total: " + coins);
    }

    public void collectItem(Item item) {
        if (inventory != null) {
            inventory.addItem(item);
            item.setActive(false);
            
            EntitySystem es = getEntitySystem();
            if (es != null) {
                es.removeEntity(item);
                Gdx.app.log("Player", "Removed item entity from EntitySystem: " + item.getItemType());
            } else {
                 Gdx.app.error("Player", "EntitySystem is null, cannot remove item entity.");
            }
            
            Gdx.app.log("Player", "Collected item: " + item.getItemType() + ", set to inactive.");
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (animation != null) {
            TextureRegion currentFrame = animation.getCurrentFrame(Gdx.graphics.getDeltaTime());
            if (currentFrame != null) {
                batch.draw(currentFrame, position.x, position.y, PLAYER_SIZE, PLAYER_SIZE);
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
        // Освобождаем ресурсы звуков
        if (coinSound != null) {
            coinSound.dispose();
            coinSound = null;
        }
        if (jumpSound != null) {
            jumpSound.dispose();
            jumpSound = null;
        }
        if (inventorySound != null) {
            inventorySound.dispose();
            inventorySound = null;
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
        if (canJump()) {
            velocity.y = JUMP_VELOCITY;
            jumpsLeft--;
            isJumping = true;
            onGround = false;

            // Воспроизводим звук прыжка
            if (jumpSound != null) {
                jumpSound.play();
                Gdx.app.log("Player", "Playing jump sound.");
            }
        }
    }

    public void heal(float amount) {
        health = Math.min(maxHealth, health + amount);
    }

    private void die() {
        isDead = true;
        deathTime = 0;
        animation.setCurrentAnimation(ANIMATION_DEATH);
    }

    public void respawn() {
        position.set(respawnPosition);
        health = respawnHealth;
        coins = respawnCoins;
        inventory = new Inventory(spriteManager);
        if (respawnInventory != null) {
            for (Item item : respawnInventory.getItems()) {
                inventory.addItem(item);
            }
        }
        isDead = false;
        isRespawned = true;
        isRespawnInvulnerable = true;
        respawnTime = 0;
        respawnInvulnerabilityTime = 0;
        animation.setCurrentAnimation(ANIMATION_IDLE);
    }

    public void startMinigame(Item wifiKeyItem) {
        if (!isInMinigame) {
            isInMinigame = true;
            wifiKeyMinigame = new WifiKeyMinigame(this, spriteManager, wifiKeyItem);
            Gdx.app.log("Player", "Started WiFi Key minigame");
        }
    }

    public void endMinigame() {
        isInMinigame = false;
        wifiKeyMinigame = null;
        Gdx.app.log("Player", "Ended WiFi Key minigame");
    }

    public boolean isInMinigame() {
        return isInMinigame;
    }

    public WifiKeyMinigame getWifiKeyMinigame() {
        return wifiKeyMinigame;
    }

    public EntitySystem getEntitySystem() {
        return gameScreen.getEntitySystem();
    }

    public void toggleInventory() {
        if (inventory != null) {
            inventory.toggle();
            // Воспроизводим звук открытия инвентаря
            if (inventorySound != null) {
                inventorySound.play();
                Gdx.app.log("Player", "Playing inventory sound.");
            }
        }
    }
}