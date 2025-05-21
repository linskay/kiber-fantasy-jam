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
import com.cyberkingdom.entities.FlyingBook;
import com.cyberkingdom.items.ItemType;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    private boolean isBookAnimationActive = false;
    private float bookAnimationTimer = 0f;
    private static final float BOOK_ANIMATION_DURATION = 3.0f;
    private TextureRegion originalTextureRegion;
    private Texture bookTexture;
    private FlyingBook activeFlyingBook;
    private boolean isControlsLocked = false;
    private Item collectedBookItem;

    private BitmapFont font;
    private String currentPhrase = null;
    private float phraseTimer = 0f;
    private static final float PHRASE_DURATION = 3.0f; // Длительность отображения фразы
    private float phraseCooldown = 0f;
    private static final float PHRASE_COOLDOWN_MIN = 5.0f; // Минимальный интервал между фразами
    private static final float PHRASE_COOLDOWN_MAX = 15.0f; // Максимальный интервал между фразами

    private static final List<String> PHRASES = Arrays.asList(
        "Вот это я понимаю\nсловил баг в браузере",
        "Надо было апгрейдить броню,\nа не драйвера",
        "Ты че, на PHP пишешь?!\nРэдфлаг",
        "Интересно, это баг\nили я тупой?",
        "Где загуглить\n\"Как пройти эту игру\"?",
        "Дайте сюда этих гениев,\nкоторые написали этот код",
        "А это точно\nне вирус?",
        "Я не программист,\nя просто гуглил как сделать игру...",
        "Где мой\nкофе?",
        "Это не баг,\nэто фича",
        "Может\nперезапустимся?"
    );
    private Random random = new Random();

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
        // Сохраняем оригинальную текстуру
        this.originalTextureRegion = animation.getCurrentFrame(0);
        Gdx.app.log("Player", "Original texture region saved.");
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

            // Загрузка текстуры для анимации книги (knigaboshka.png)
            /*
            try {
                Gdx.app.log("Player", "Attempting to load book texture (knigaboshka.png)...");
                // Используем полный путь, так как SpriteManager может не знать подкаталоги
                bookTexture = new Texture(Gdx.files.internal("assets/entities/oleg_run/knigaboshka.png"));
                if (bookTexture != null) {
                    Gdx.app.log("Player", "Book texture loaded successfully. Size: " + bookTexture.getWidth() + "x" + bookTexture.getHeight());
                } else {
                    Gdx.app.error("Player", "Failed to load book texture for animation. Texture object is null.");
                }
            } catch (Exception e) {
                Gdx.app.error("Player", "Error loading book texture for animation", e);
            }
            */
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

        if (isBookAnimationActive) {
            bookAnimationTimer += deltaTime;
            // Gdx.app.log("Player", "Book animation active. Timer: " + bookAnimationTimer + "/" + BOOK_ANIMATION_DURATION);
            
            // Проверяем, достигла ли книга игрока ИЛИ истекло ли время анимации
            if ((activeFlyingBook != null && activeFlyingBook.isCollected()) || bookAnimationTimer >= BOOK_ANIMATION_DURATION) {
                Gdx.app.log("Player", "Book animation condition met. Book collected: " + (activeFlyingBook != null && activeFlyingBook.isCollected()) + ", Timer elapsed: " + (bookAnimationTimer >= BOOK_ANIMATION_DURATION));
                endBookAnimation();
            } else {
                 // Обновляем только FlyingBook, если он активен и анимация активна
                 if (activeFlyingBook != null && activeFlyingBook.isActive()) {
                     activeFlyingBook.update(deltaTime);
                 }
            }
        } else { // Обычное обновление игрока, когда анимация книги не активна
            updateAnimation();
            updatePosition(deltaTime);
            updateTimers(deltaTime);
            updatePhrase(deltaTime); // Обновляем таймер фраз
        }
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

    private void updatePhrase(float deltaTime) {
        if (currentPhrase != null) {
            phraseTimer += deltaTime;
            if (phraseTimer >= PHRASE_DURATION) {
                currentPhrase = null;
                // Устанавливаем новый случайный кулдаун перед следующей фразой
                phraseCooldown = PHRASE_COOLDOWN_MIN + random.nextFloat() * (PHRASE_COOLDOWN_MAX - PHRASE_COOLDOWN_MIN);
                Gdx.app.log("Player", "Phrase ended. Next phrase cooldown: " + phraseCooldown);
            }
        } else {
            phraseCooldown -= deltaTime;
            if (phraseCooldown <= 0) {
                // Выбираем случайную фразу
                currentPhrase = PHRASES.get(random.nextInt(PHRASES.size()));
                phraseTimer = 0f;
                Gdx.app.log("Player", "New random phrase: " + currentPhrase + ", font: " + (font != null ? "loaded" : "null"));
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
        Gdx.app.log("Player", "collectItem called for item type: " + (item != null ? item.getItemType() : "null"));
        if (inventory != null) {
            inventory.addItem(item);
            item.setActive(false);

            EntitySystem es = getEntitySystem();
            if (es != null) {
                Gdx.app.log("Player", "Checking item type for KNIGA animation");
                if (item != null && item.getItemType() == ItemType.KNIGA) {
                    Gdx.app.log("Player", "Item is KNIGA. Attempting to start animation.");
                    Gdx.app.log("Player", "Collected KNIGA! Starting animation. Book texture: " + (bookTexture != null ? "loaded" : "null"));
                    Gdx.app.log("Player", "Player position: " + position.x + ", " + position.y);
                    Gdx.app.log("Player", "Item position: " + item.getPosition().x + ", " + item.getPosition().y);
                    collectedBookItem = item;
                    startBookAnimation(item);
                } else {
                    if (item != null) {
                        Gdx.app.log("Player", "Item is not KNIGA, removing from EntitySystem: " + item.getItemType());
                        es.removeEntity(item);
                    } else {
                         Gdx.app.error("Player", "Collected item is null, cannot remove from EntitySystem.");
                    }
                }

            } else {
                 Gdx.app.error("Player", "EntitySystem is null, cannot process collected item.");
            }

            Gdx.app.log("Player", "Collected item: " + (item != null ? item.getItemType() : "null") + ", set to inactive.");
        } else {
            Gdx.app.error("Player", "Inventory is null, cannot collect item.");
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isBookAnimationActive) {
            // Отрисовываем книгу вместо игрока
            if (bookTexture != null) {
                batch.draw(bookTexture, position.x, position.y, PLAYER_SIZE, PLAYER_SIZE);
            }
        } else if (animation != null) {
            TextureRegion currentFrame = animation.getCurrentFrame(Gdx.graphics.getDeltaTime());
            if (currentFrame != null) {
                batch.draw(currentFrame, position.x, position.y, PLAYER_SIZE, PLAYER_SIZE);
            }
        }

        // Отрисовка случайной фразы над игроком
        if (currentPhrase != null && font != null) {
            Gdx.app.log("Player", "Rendering phrase: " + currentPhrase);
            GlyphLayout layout = new GlyphLayout(font, currentPhrase);
            float textX = position.x + (PLAYER_SIZE - layout.width) / 2;
            float textY = position.y + PLAYER_SIZE + layout.height + 10; // 10 пикселей отступ над игроком
            font.draw(batch, layout, textX, textY);
        } else {
            if (currentPhrase != null && font == null) {
                Gdx.app.error("Player", "Cannot render phrase: font is null");
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
        // Удаляем dispose для texture, так как он может быть fallbackTexture или null
        // if (texture != null) {
        //     texture.dispose();
        //     texture = null;
        // }
        if (animation != null) {
            animation.dispose(); // Освобождаем ресурсы анимаций
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
        // Освобождаем ресурс текстуры книги, если она была загружена напрямую
        if (bookTexture != null) {
             Gdx.app.log("Player", "Disposing knigaboshka texture");
            bookTexture.dispose();
            bookTexture = null;
        }
         Gdx.app.log("Player", "Player disposed.");
    }

    public void setGameScreen(GameScreen gameScreen) {
        Gdx.app.log("Player", "Setting GameScreen: " + (gameScreen != null ? "not null" : "null"));
        this.gameScreen = gameScreen;
        if (gameScreen != null) {
            Gdx.app.log("Player", "GameScreen EntitySystem: " + (gameScreen.getEntitySystem() != null ? "not null" : "null"));
        }
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void moveLeft() {
        if (!isControlsLocked) {
            velocity.x = -MOVE_SPEED;
        }
    }

    public void moveRight() {
        if (!isControlsLocked) {
            velocity.x = MOVE_SPEED;
        }
    }

    public void stop() {
        if (!isControlsLocked) {
            velocity.x = 0;
        }
    }

    public void jump() {
        if (!isControlsLocked && canJump()) {
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
        if (!isControlsLocked && inventory != null) {
            inventory.toggle();
            // Воспроизводим звук открытия инвентаря
            if (inventorySound != null) {
                inventorySound.play();
                Gdx.app.log("Player", "Playing inventory sound.");
            }
        }
    }

    public void startBookAnimation(Item bookItem) {
        Gdx.app.log("Player", "Starting book animation");
        if (gameScreen == null) {
            Gdx.app.error("Player", "GameScreen is null in startBookAnimation");
            return;
        }
        
        EntitySystem entitySystem = gameScreen.getEntitySystem();
        if (entitySystem == null) {
            Gdx.app.error("Player", "EntitySystem is null in startBookAnimation");
            return;
        }
        
        EntityFactory entityFactory = entitySystem.getFactory();
        if (entityFactory == null) {
            Gdx.app.error("Player", "EntityFactory is null in startBookAnimation");
            return;
        }
        
        // Останавливаем игрока
        velocity.set(0, 0);
        Gdx.app.log("Player", "Player stopped for book animation");
        
        // Сохраняем текущую текстуру игрока
        if (animation != null) {
            originalTextureRegion = animation.getCurrentFrame(0);
             Gdx.app.log("Player", "Saved original texture region");
        } else {
             Gdx.app.error("Player", "Animation component is null, cannot save original texture.");
        }
        
        // Получаем текстуру knigaboshka для игрока
        try {
            bookTexture = new Texture(Gdx.files.internal("assets/entities/oleg_run/knigaboshka.png"));
            if (bookTexture != null) {
                 Gdx.app.log("Player", "Loaded knigaboshka texture successfully");
            } else {
                 Gdx.app.error("Player", "Failed to load knigaboshka texture: Texture object is null");
            }
        } catch (Exception e) {
            Gdx.app.error("Player", "Failed to load knigaboshka texture", e);
            return;
        }
        
        // Создаем FlyingBook с обычной текстурой книги
        Vector2 spawnPosition = new Vector2(position.x, position.y + 200); // Немного выше игрока
        Gdx.app.log("Player", "Creating FlyingBook at position: " + spawnPosition.x + ", " + spawnPosition.y);
        
        FlyingBook flyingBook = entityFactory.createFlyingBook(spawnPosition);
        if (flyingBook != null) {
            flyingBook.setTarget(this);
            activeFlyingBook = flyingBook;
            entitySystem.addEntity(flyingBook);
            
            // Активируем анимацию
            isBookAnimationActive = true;
            bookAnimationTimer = 0f;
            isControlsLocked = true;
            collectedBookItem = bookItem;
            
            Gdx.app.log("Player", "Book animation started successfully. isBookAnimationActive: " + isBookAnimationActive + ", isControlsLocked: " + isControlsLocked);
        } else {
            Gdx.app.error("Player", "Failed to create FlyingBook");
        }
    }

    private void endBookAnimation() {
        Gdx.app.log("Player", "Ending book animation");
        isBookAnimationActive = false;
        bookAnimationTimer = 0f;
        isControlsLocked = false;

        EntitySystem es = getEntitySystem();
        if (es != null) {
            if (activeFlyingBook != null) {
                 Gdx.app.log("Player", "Removing active FlyingBook");
                es.removeEntity(activeFlyingBook);
                activeFlyingBook = null;
            }
            
            // Удаляем собранный предмет только после завершения анимации
            if (collectedBookItem != null && es.getEntities().contains(collectedBookItem)) {
                 Gdx.app.log("Player", "Removing collected Book Item");
                es.removeEntity(collectedBookItem);
                collectedBookItem = null;
            }
        } else {
             Gdx.app.error("Player", "EntitySystem is null in endBookAnimation");
        }
        
        // Восстанавливаем анимацию игрока
        if (animation != null) {
            animation.setCurrentAnimation(ANIMATION_IDLE);
             Gdx.app.log("Player", "Restored player animation to IDLE");
        } else {
             Gdx.app.error("Player", "Animation component is null, cannot restore animation.");
        }
        
        // Освобождаем текстуру книгобошка
        if (bookTexture != null) {
             Gdx.app.log("Player", "Disposing knigaboshka texture");
            bookTexture.dispose();
            bookTexture = null;
        }
        
        Gdx.app.log("Player", "Book animation ended. isBookAnimationActive: " + isBookAnimationActive + ", isControlsLocked: " + isControlsLocked);
    }

    public void setEntitySystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
        Gdx.app.log("Player", "EntitySystem set: " + (entitySystem != null ? "not null" : "null"));
    }

    public void setFont(BitmapFont font) {
        this.font = font;
        Gdx.app.log("Player", "Font set for player.");
    }
}