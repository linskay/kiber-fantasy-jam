package com.cyberkingdom.minigames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.items.Item;
import com.cyberkingdom.items.ItemType;
import com.cyberkingdom.rendering.SpriteManager;

import java.util.Random;

public class WifiKeyMinigame {
    private static final float PLAYER_SPEED = 300f;
    private static final float SYMBOL_SPEED = 200f;
    private static final float BOSS_SPEED = 150f;
    private static final float SYMBOL_SPAWN_INTERVAL = 1.0f;
    private static final float BOSS_SPAWN_INTERVAL = 3.0f;
    private static final int TARGET_SCORE = 100;
    private static final int PENALTY_SCORE = -100;
    private static final float PLAYER_SIZE = 96f;
    private static final float SYMBOL_SIZE = 64f;
    private static final float BOSS_SIZE = 96f;
    private static final float ANIMATION_FRAME_DURATION = 0.2f;
    private static final float PLATFORM_Y = 100f; // Позиция платформы по Y
    private static final float PLATFORM_HEIGHT = 32f; // Высота платформы

    private final Player player;
    private final SpriteManager spriteManager;
    private final Rectangle playerBounds;
    private final Array<Symbol> symbols;
    private final Array<Boss> bosses;
    private final Random random;
    private final BitmapFont font;
    private float symbolSpawnTimer;
    private float bossSpawnTimer;
    private int score;
    private boolean isGameOver;
    private boolean isWin;
    private TextureRegion playerTexture;
    private TextureRegion wifiKeyTexture;
    private float gameOverTimer;
    private Item wifiKeyItem;
    private float animationTimer;
    private int currentKimchiFrame;
    private Rectangle platformBounds;
    private TextureRegion platformTexture;

    public WifiKeyMinigame(Player player, SpriteManager spriteManager, Item wifiKeyItem) {
        this.player = player;
        this.spriteManager = spriteManager;
        this.wifiKeyItem = wifiKeyItem;
        this.playerBounds = new Rectangle(0, PLATFORM_Y + PLATFORM_HEIGHT, PLAYER_SIZE, PLAYER_SIZE);
        this.symbols = new Array<>();
        this.bosses = new Array<>();
        this.random = new Random();
        this.font = new BitmapFont();
        this.gameOverTimer = 0;
        this.animationTimer = 0;
        this.currentKimchiFrame = 0;
        
        // Инициализация платформы
        this.platformBounds = new Rectangle(0, PLATFORM_Y, Gdx.graphics.getWidth(), PLATFORM_HEIGHT);
        Texture platformTexture = spriteManager.getTexture("Platform");
        if (platformTexture != null) {
            this.platformTexture = new TextureRegion(platformTexture);
        }
        
        Gdx.app.log("WifiKeyMinigame", "Constructor called");
        if (player == null) {
            Gdx.app.error("WifiKeyMinigame", "Player is null!");
            return;
        }
        if (spriteManager == null) {
            Gdx.app.error("WifiKeyMinigame", "SpriteManager is null!");
            return;
        }
        
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(2);
        
        // Загружаем текстуры
        Texture playerTexture = spriteManager.getTexture("Player_Idle");
        if (playerTexture != null) {
            this.playerTexture = new TextureRegion(playerTexture);
            Gdx.app.log("WifiKeyMinigame", "Player texture loaded successfully");
        } else {
            Gdx.app.error("WifiKeyMinigame", "Failed to load player texture");
        }
        
        Texture wifiKeyTexture = spriteManager.getTexture("WIFI_KEY");
        if (wifiKeyTexture != null) {
            this.wifiKeyTexture = new TextureRegion(wifiKeyTexture);
            Gdx.app.log("WifiKeyMinigame", "WiFi Key texture loaded successfully");
        } else {
            Gdx.app.error("WifiKeyMinigame", "Failed to load WiFi Key texture");
        }

        // Удаляем попытки получить текстуры, которые не используются
        // Texture codeSymbolTexture = spriteManager.getTexture("CodeSymbol");
        // if (codeSymbolTexture == null) {
        //     Gdx.app.error("WifiKeyMinigame", "Failed to load CodeSymbol texture");
        // }

        // Texture bossTexture = spriteManager.getTexture("Boss");
        // if (bossTexture == null) {
        //     Gdx.app.error("WifiKeyMinigame", "Failed to load Boss texture");
        // }

        // Texture backgroundTexture = spriteManager.getTexture("CyberBackground"); // Это не тот фон, что используется в render
        // if (backgroundTexture == null) {
        //     Gdx.app.error("WifiKeyMinigame", "Failed to load CyberBackground texture");
        // }
        
        Gdx.app.log("WifiKeyMinigame", "Initialized with player at position: " + playerBounds.x + "," + playerBounds.y);
    }

    public void update(float deltaTime) {
        if (isGameOver) {
            // Если игра окончена, ждем 2 секунды и закрываем мини-игру
            if (gameOverTimer < 2.0f) {
                gameOverTimer += deltaTime;
            } else {
                if (isWin) {
                    // Если выиграли, добавляем ключ в инвентарь и удаляем его с карты
                    if (wifiKeyItem != null) {
                        player.getInventory().addItem(wifiKeyItem);
                        player.getEntitySystem().removeEntity(wifiKeyItem);
                        Gdx.app.log("WifiKeyMinigame", "Game won! Added WiFi Key to inventory and removed from map");
                    }
                } else {
                    player.takeDamage(10);
                    Gdx.app.log("WifiKeyMinigame", "Game lost! Player took damage");
                }
                // Сначала очищаем все объекты
                symbols.clear();
                bosses.clear();
                // Затем завершаем мини-игру
                player.endMinigame();
                return;
            }
            return;
        }

        // Обновляем анимацию
        animationTimer += deltaTime;
        if (animationTimer >= ANIMATION_FRAME_DURATION) {
            animationTimer = 0;
            currentKimchiFrame = (currentKimchiFrame + 1) % 3;
        }

        // Обновляем позицию игрока
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            playerBounds.x -= PLAYER_SPEED * deltaTime;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            playerBounds.x += PLAYER_SPEED * deltaTime;
        }

        // Ограничиваем движение игрока
        playerBounds.x = Math.max(0, Math.min(Gdx.graphics.getWidth() - PLAYER_SIZE, playerBounds.x));

        // Проверяем столкновение с платформой
        if (playerBounds.y < PLATFORM_Y + PLATFORM_HEIGHT) {
            playerBounds.y = PLATFORM_Y + PLATFORM_HEIGHT;
        }

        // Спавн символов
        symbolSpawnTimer += deltaTime;
        if (symbolSpawnTimer >= SYMBOL_SPAWN_INTERVAL) {
            spawnSymbol();
            symbolSpawnTimer = 0;
        }

        // Спавн боссов
        bossSpawnTimer += deltaTime;
        if (bossSpawnTimer >= BOSS_SPAWN_INTERVAL) {
            spawnBoss();
            bossSpawnTimer = 0;
        }

        // Обновляем символы
        for (int i = symbols.size - 1; i >= 0; i--) {
            Symbol symbol = symbols.get(i);
            symbol.position.y -= SYMBOL_SPEED * deltaTime;
            symbol.bounds.setPosition(symbol.position.x, symbol.position.y);

            // Проверяем столкновение с платформой
            if (symbol.bounds.overlaps(platformBounds)) {
                symbols.removeIndex(i);
                Gdx.app.log("WifiKeyMinigame", "Kimchi hit platform and disappeared");
                continue;
            }

            // Проверяем столкновение с игроком
            if (symbol.bounds.overlaps(playerBounds)) {
                score += 10;
                symbols.removeIndex(i);
                Gdx.app.log("WifiKeyMinigame", "Collected symbol, new score: " + score);
            } else if (symbol.position.y < -SYMBOL_SIZE) {
                symbols.removeIndex(i);
            }
        }

        // Обновляем боссов
        for (int i = bosses.size - 1; i >= 0; i--) {
            Boss boss = bosses.get(i);
            boss.position.y -= BOSS_SPEED * deltaTime;
            boss.bounds.setPosition(boss.position.x, boss.position.y);

            // Проверяем столкновение с платформой
            if (boss.bounds.overlaps(platformBounds)) {
                bosses.removeIndex(i);
                Gdx.app.log("WifiKeyMinigame", "Virus hit platform and disappeared");
                continue;
            }

            // Проверяем столкновение с игроком
            if (boss.bounds.overlaps(playerBounds)) {
                score += PENALTY_SCORE;
                bosses.removeIndex(i);
                Gdx.app.log("WifiKeyMinigame", "Hit by boss, new score: " + score);
            } else if (boss.position.y < -BOSS_SIZE) {
                bosses.removeIndex(i);
            }
        }

        // Проверяем условия победы/поражения
        if (score >= TARGET_SCORE) {
            isGameOver = true;
            isWin = true;
            gameOverTimer = 0;
        } else if (score <= PENALTY_SCORE) {
            isGameOver = true;
            isWin = false;
            gameOverTimer = 0;
        }
    }

    public void render(SpriteBatch batch) {
        // Если игра окончена и прошло время ожидания, не рисуем ничего
        if (isGameOver && gameOverTimer >= 2.0f) {
            return;
        }

        // Рисуем фон
        Texture backgroundTexture = spriteManager.getTexture("MINIGAME_BACKGROUND");
        if (backgroundTexture != null) {
            batch.draw(new TextureRegion(backgroundTexture), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            Gdx.app.error("WifiKeyMinigame", "Background texture is null, cannot draw.");
        }

        // Рисуем платформу
        if (platformTexture != null) {
            batch.setColor(1, 1, 1, 0.5f); // Устанавливаем прозрачность
            batch.draw(platformTexture, platformBounds.x, platformBounds.y, platformBounds.width, platformBounds.height);
            batch.setColor(1, 1, 1, 1); // Возвращаем нормальную прозрачность
        }

        // Рисуем игрока
        if (playerTexture != null) {
            batch.draw(playerTexture, playerBounds.x, playerBounds.y, PLAYER_SIZE, PLAYER_SIZE);
        } else {
            Gdx.app.error("WifiKeyMinigame", "Player texture is null, cannot draw.");
        }

        // Рисуем символы (кимчи)
        for (Symbol symbol : symbols) {
            if (symbol.frames != null && !symbol.frames.isEmpty()) {
                TextureRegion currentFrame = symbol.frames.get(currentKimchiFrame);
                batch.draw(currentFrame, symbol.position.x, symbol.position.y, SYMBOL_SIZE, SYMBOL_SIZE);
            }
        }

        // Рисуем боссов (вирусы)
        for (Boss boss : bosses) {
            if (boss.texture != null) {
                batch.draw(boss.texture, boss.position.x, boss.position.y, BOSS_SIZE, BOSS_SIZE);
            }
        }

        // Рисуем счет
        if (font != null) {
             font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
        } else {
             Gdx.app.error("WifiKeyMinigame", "Font is null, cannot draw score.");
        }

        if (isGameOver) {
            String message = isWin ? "You Win! +1 WiFi Key" : "Game Over! -10 Health";
            if (font != null) {
                font.draw(batch, message, Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2);
            } else {
                Gdx.app.error("WifiKeyMinigame", "Font is null, cannot draw game over message.");
            }
        }
    }

    private void spawnSymbol() {
        // Создаем анимацию кимчи
        Array<TextureRegion> kimchiFrames = new Array<>();
        for (int i = 1; i <= 3; i++) {
            Texture kimchiTexture = spriteManager.getTexture("KIMCHI_" + i);
            if (kimchiTexture != null) {
                kimchiFrames.add(new TextureRegion(kimchiTexture));
            }
        }
        
        if (kimchiFrames.isEmpty()) {
            Gdx.app.error("WifiKeyMinigame", "Failed to load kimchi textures");
            return;
        }

        float x = random.nextFloat() * (Gdx.graphics.getWidth() - SYMBOL_SIZE);
        Vector2 position = new Vector2(x, Gdx.graphics.getHeight());
        Rectangle bounds = new Rectangle(position.x, position.y, SYMBOL_SIZE, SYMBOL_SIZE);
        symbols.add(new Symbol(position, bounds, kimchiFrames));
        Gdx.app.log("WifiKeyMinigame", "Spawned animated kimchi at: " + position.x + "," + position.y);
    }

    private void spawnBoss() {
        Texture virusTexture = spriteManager.getTexture("VIRUS");
        if (virusTexture == null) {
            Gdx.app.error("WifiKeyMinigame", "Skipping boss spawn due to null texture: VIRUS");
            return;
        }
        float x = random.nextFloat() * (Gdx.graphics.getWidth() - BOSS_SIZE);
        Vector2 position = new Vector2(x, Gdx.graphics.getHeight());
        Rectangle bounds = new Rectangle(position.x, position.y, BOSS_SIZE, BOSS_SIZE);
        TextureRegion texture = new TextureRegion(virusTexture);
        bosses.add(new Boss(position, bounds, texture));
        Gdx.app.log("WifiKeyMinigame", "Spawned virus at: " + position.x + "," + position.y);
    }

    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        symbols.clear();
        bosses.clear();
        if (platformTexture != null) {
            platformTexture.getTexture().dispose();
        }
        // Очищаем все ссылки
        playerTexture = null;
        wifiKeyTexture = null;
        platformTexture = null;
    }

    private static class Symbol {
        Vector2 position;
        Rectangle bounds;
        Array<TextureRegion> frames;

        Symbol(Vector2 position, Rectangle bounds, Array<TextureRegion> frames) {
            this.position = position;
            this.bounds = bounds;
            this.frames = frames;
        }
    }

    private static class Boss {
        Vector2 position;
        Rectangle bounds;
        TextureRegion texture;

        Boss(Vector2 position, Rectangle bounds, TextureRegion texture) {
            this.position = position;
            this.bounds = bounds;
            this.texture = texture;
        }
    }
} 