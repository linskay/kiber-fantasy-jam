package com.cyberkingdom.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.Collidable;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.physics.CollisionComponent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Item extends GameEntity implements Collidable {
    private String itemType;
    private String name;
    private String description;
    private String effect;
    private int quantity;
    private Texture texture;
    private CollisionComponent collision;

    // Внутренний класс для описания предмета
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

    // Статический список всех предметов
    public static final List<ItemData> ALL_ITEMS = Arrays.asList(
            new ItemData("USB_SCATERT", "USB-Скатерть", "Восстанавливает 50% HP", "Самобранка 2.0"),
            new ItemData("crypto_shovel", "Крипто-Лопата", "Ломает стены (скрытые блоки)", "Digging to the Moon"),
            new ItemData("rtx4090", "RTX 4090", "Даёт +50% скорости (на 10 сек)", "Дрова от Бабы-Яги"),
            new ItemData("canned_food", "Банка тушёнки", "Временная броня", "Золотой запас сисадмина"),
            new ItemData("grokaem_algorithms", "Книга \"Грокаем алгоритмы\"", "Открывает секреты (карта)", "Библия backend-разработчика"),
            new ItemData("wifi_key", "WiFi-Ключ", "Нужен для финального босса", "Пароль: 12345678"),
            new ItemData("vpn_token", "vpn_token", "Создает доп монетки", "Нужно что то написать"),
            new ItemData("hardware_wallet", "hardware_wallet", "Нужно что то написать", "Нужно что то написать"),
            new ItemData("crypto_coin", "crypto_coin", "Нужно что то написать", "Нужно что то написать")

    );

    // Конструктор по ItemData
    public Item(Vector2 position, ItemData data, int quantity) {
        super("Item_" + data.id);
        this.itemType = data.id;
        this.name = data.name;
        this.description = data.description;
        this.effect = data.effect;
        this.quantity = quantity > 0 ? quantity : 1;
        this.position.set(position);
        this.collision = new CollisionComponent(16, 16);
        collision.update(position);

        try {
            this.texture = new Texture(Gdx.files.internal("assets/items/" + itemType.toLowerCase() + ".png"));
        } catch (Exception e) {
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 1, 0, 1);
            pixmap.fill();
            this.texture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    // Старый конструктор (если нужен)
    public Item(Vector2 position, String itemType, String description, int quantity) {
        super("Item_" + itemType);
        this.itemType = itemType;
        this.name = itemType; // Можно заменить на itemType, если нет данных
        this.description = description;
        this.effect = "";
        this.quantity = quantity > 0 ? quantity : 1;
        this.position.set(position);
        this.collision = new CollisionComponent(16, 16);
        collision.update(position);

        try {
            this.texture = new Texture(Gdx.files.internal("assets/items/" + itemType.toLowerCase() + ".png"));
        } catch (Exception e) {
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 1, 0, 1);
            pixmap.fill();
            this.texture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    // Метод для создания случайного предмета из списка
    public static Item createRandomItem(Vector2 position) {
        Random random = new Random();
        ItemData data = ALL_ITEMS.get(random.nextInt(ALL_ITEMS.size()));
        return new Item(position, data, 1);
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    @Override
    public Rectangle getCollisionBounds() {
        return collision.getBounds();
    }

    public String getItemType() {
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

    public void increaseQuantity(int amount) {
        if (amount > 0) {
            quantity += amount;
        }
    }

    public void decreaseQuantity(int amount) {
        if (amount > 0) {
            quantity = Math.max(0, quantity - amount);
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public void use() {
        System.out.println("Использован предмет: " + itemType);
        // Логика использования предмета (например, восстановление HP, повышение скорости и т.д.)
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    /**
     * Отрисовка предмета в игровом мире
     */
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y);
        }
    }
}


