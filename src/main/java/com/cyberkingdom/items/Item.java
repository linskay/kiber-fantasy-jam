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

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Item extends GameEntity implements Collidable {
    private String itemType;
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

    public Item(String itemType, Vector2 position, int quantity, SpriteManager spriteManager) {
        super(itemType, spriteManager);
        this.itemType = itemType;
        this.position = position;
        this.quantity = quantity;
        this.isActive = true;
        this.collision = new CollisionComponent(32, 32);
        this.collision.update(position);
    }

    @Override
    public void update(float deltaTime) {
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

    public String getItemType() { return itemType; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getEffect() { return effect; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    @Override
    public void dispose() {
        super.dispose();
        texture = null;
        if (collision != null) {
            collision = null;
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public void render(SpriteBatch batch) {
        float x = getPosition().x;
        float y = getPosition().y;
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
        if (spriteManager == null) {
            Gdx.app.error("Item", "SpriteManager is null for item: " + itemType);
            return;
        }

        TextureRegion[] frames = spriteManager.getFrames(itemType);
        if (frames != null && frames.length > 0) {
            texture = frames[0].getTexture();
            if (animation != null) {
                animation.clearFrames();
                for (TextureRegion frame : frames) {
                    animation.addFrame(frame);
                }
                animation.setFrameDuration(0.1f);
            }
            Gdx.app.log("Item", "Set texture for item: " + itemType + " with size: " + 
                texture.getWidth() + "x" + texture.getHeight());
        } else {
            Gdx.app.error("Item", "No frames found for item: " + itemType);
        }
    }
}