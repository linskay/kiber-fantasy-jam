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

    public static final String ITEM_COIN = "COIN";
    public static final String ITEM_CRYPTO_COIN = "CRYPTO_COIN";
    public static final String ITEM_VPN_TOKEN = "VPN_TOKEN";
    public static final String ITEM_USB_SCATTER = "USB_SCATTER";
    public static final String ITEM_HARDWARE_WALLET = "HARDWARE_WALLET";

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
        new ItemData(ITEM_COIN, "Монета", "Обычная монета", "Увеличивает счетчик монет"),
        new ItemData(ITEM_CRYPTO_COIN, "Криптомонета", "Цифровая валюта", "Увеличивает счетчик криптомонет"),
        new ItemData(ITEM_VPN_TOKEN, "VPN Токен", "Токен для VPN", "Дает временную защиту"),
        new ItemData(ITEM_USB_SCATTER, "USB Scatter", "USB устройство", "Увеличивает скорость"),
        new ItemData(ITEM_HARDWARE_WALLET, "Аппаратный кошелек", "Защищенный кошелек", "Увеличивает защиту")
    );

    public Item(Vector2 position, String itemType, int quantity, Texture texture) {
        super(itemType);
        this.position = position;
        this.itemType = itemType;
        this.quantity = quantity;
        this.texture = texture;
        this.collision = new CollisionComponent(32, 32);
        this.isActive = true;
        this.value = 1;
        this.name = itemType;
        collision.update(position);
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
}