package com.cyberkingdom.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.Collidable;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.utils.Array;
import com.cyberkingdom.entities.AnimationComponent;
import com.cyberkingdom.entities.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Item extends GameEntity implements Collidable {
    private ItemType itemType;
    public String name;
    public String description;
    public String effect;
    private int quantity;
    private Texture texture;
    private CollisionComponent collision;
    private boolean isActive;
    private int value;
    private static final Random random = new Random();
    private SpriteManager spriteManager;
    private boolean isCollected;
    private Rectangle bounds;
    private static int itemCounter = 0;
    private int itemId;
    private AnimationComponent animation;

    public static final String ITEM_COIN = "COIN";
    public static final String ITEM_CRYPTO_COIN = "CRYPTO_COIN";
    public static final String ITEM_VPN_TOKEN = "VPN_TOKEN";
    public static final String ITEM_USB_SCATTER = "USB_SCATTER";
    public static final String ITEM_HARDWARE_WALLET = "HARDWARE_WALLET";
    public static final String ITEM_USB_SKATERT = "USB_SKATERT";
    public static final String ITEM_CRYPTO_SHOVEL = "CRYPTO_SHOVEL";
    public static final String ITEM_RTX_4090 = "RTX_4090";
    public static final String ITEM_TUSHENKA = "TUSHENKA";
    public static final String ITEM_KNIGA = "KNIGA";
    public static final String ITEM_WIFI_KEY = "WIFI_KEY";

    public static class ItemData {
        public final String id;
        public final String name;
        public final String description;
        public final String effect;

        public ItemData(String id, String name, String description, String effect) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.effect = effect;
        }
    }

    public static final List<ItemData> ALL_ITEMS = Arrays.asList(
        new ItemData(ITEM_COIN, "Дай.Токен", "Обычная монета", "Увеличивает счетчик монет"),
        new ItemData(ITEM_CRYPTO_COIN, "Криптомонета", "Цифровая валюта", "Увеличивает счетчик криптомонет"),
        new ItemData(ITEM_VPN_TOKEN, "VPN Токен", "Токен для VPN", "Дает временную защиту"),
        new ItemData(ITEM_USB_SCATTER, "USB Scatter", "USB устройство", "Увеличивает скорость"),
        new ItemData(ITEM_HARDWARE_WALLET, "Аппаратный кошелек", "Защищенный кошелек", "Увеличивает защиту"),
        new ItemData(ITEM_USB_SKATERT, "USB-Скатерть", "Восстанавливает 50% HP", "Самобранка 2.0"),
        new ItemData(ITEM_CRYPTO_SHOVEL, "Крипто-Лопата", "Ломает стены (скрытые блоки)", "Digging to the Moon"),
        new ItemData(ITEM_RTX_4090, "RTX 4090", "+50% скорости на 10 сек", "Дрова от Бабы-Яги"),
        new ItemData(ITEM_TUSHENKA, "Банка тушёнки", "Временная броня", "Золотой запас сисадмина"),
        new ItemData(ITEM_KNIGA, "Грокаем алгоритмы", "Открывает секреты (карта)", "Библия backend-разработчика"),
        new ItemData(ITEM_WIFI_KEY, "WiFi-Ключ", "Нужен для финального босса", "Пароль: 12345678")
    );

    public Item(ItemType type, Vector2 position, int quantity, SpriteManager spriteManager) {
        super(type.name(), spriteManager);
        this.itemType = type;
        this.position = position;
        this.quantity = quantity;
        this.isActive = true;
        this.collision = new CollisionComponent(32, 32);
        this.collision.update(position);
        this.isCollected = false;
        this.bounds = new Rectangle(position.x, position.y, 32, 32);
        this.itemId = ++itemCounter;
        this.animation = new AnimationComponent();
        setTextureFromSpriteManager(spriteManager);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (isActive && collision != null) {
            collision.update(position);
        }
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    @Override
    public Rectangle getCollisionBounds() {
        return collision.getBounds();
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEffect() {
        return effect;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public Rectangle getBounds() {
        bounds.setPosition(position.x, position.y);
        return bounds;
    }

    public int getItemId() {
        return itemId;
    }

    public static void resetItemCounter() {
        itemCounter = 0;
    }

    @Override
    public void dispose() {
        super.dispose();
        texture = null;
        if (collision != null) {
            collision = null;
        }
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
        if (animation != null) {
            animation.dispose();
            animation = null;
        }
    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isCollected) {
            float x = position.x;
            float y = position.y;
            float width = 32;
            float height = 32;

            if (getAnimation() != null) {
                TextureRegion currentFrame = getAnimation().getCurrentFrame(Gdx.graphics.getDeltaTime());
                if (currentFrame != null) {
                    batch.draw(currentFrame, x, y, width, height);
                    return;
                }
            }

            if (getTexture() != null) {
                batch.draw(getTexture(), x, y, width, height);
            }
        }
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        this.quantity = Math.max(0, this.quantity - amount);
    }

    public void setSpriteManager(SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
        setupAnimations();
    }

    public void initializeWithSpriteManager(SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
        if (spriteManager != null) {
            setupAnimations();
        }
    }

    public void setTextureFromSpriteManager(SpriteManager spriteManager) {
        try {
            if (spriteManager != null) {
                texture = spriteManager.getTexture(itemType.name());
                if (texture != null) {
                    Array<TextureRegion> frames = new Array<>();
                    frames.add(new TextureRegion(texture));
                    animation.addAnimation(frames);
                    return;
                }
            }
        } catch (Exception e) {
            Gdx.app.error("Item", "Error loading texture for " + itemType.name(), e);
        }
    }

    public void setupAnimations() {
        if (spriteManager != null) {
            TextureRegion[] frames = spriteManager.getFrames(itemType.name());
            if (frames != null && frames.length > 0) {
                texture = frames[0].getTexture();
                Array<TextureRegion> animationFrames = new Array<>();
                for (TextureRegion frame : frames) {
                    animationFrames.add(frame);
                }
                animation.addAnimation(animationFrames);
                Gdx.app.log("Item", "Set texture for item: " + itemType.name() + " with size: " + 
                    texture.getWidth() + "x" + texture.getHeight());
            } else {
                Gdx.app.error("Item", "No frames found for item: " + itemType.name());
            }
        }
    }

    public Item(ItemType type, String name, Texture texture) {
        super(type.name(), null);
        this.itemType = type;
        this.name = name;
        this.texture = texture;
    }

    public Item(ItemType type, String name, TextureRegion texture) {
        super(type.name(), null);
        this.itemType = type;
        this.name = name;
        this.texture = texture.getTexture();
    }

    public ItemType getType() {
        return itemType;
    }

    public void use(Player player) {
        if (itemType == ItemType.WIFI_KEY) {
            Gdx.app.log("Item", "Using WiFi Key, itemType: " + itemType + ", name: " + name);
            if (player != null) {
                Gdx.app.log("Item", "Player is not null, starting minigame");
                player.startWifiKeyMinigame();
            } else {
                Gdx.app.error("Item", "Player is null!");
            }
        } else {
            Gdx.app.log("Item", "Item is not WiFi Key, type: " + itemType);
        }
    }
}