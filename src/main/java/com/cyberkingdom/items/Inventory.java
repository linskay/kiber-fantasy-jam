package com.cyberkingdom.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Inventory {
    private List<Item> items;
    private int maxSize;
    private SpriteManager spriteManager;
    private boolean isVisible = false;

    public Inventory(SpriteManager spriteManager) {
        this.items = new ArrayList<>();
        this.maxSize = 20;
        this.spriteManager = spriteManager;
    }

    public boolean addItem(Item newItem) {
        if (newItem == null) {
            Gdx.app.error("Inventory", "Attempted to add null item");
            return false;
        }

        // Проверяем, есть ли уже такой предмет
        for (Item item : items) {
            if (item.getItemType() == newItem.getItemType()) {
                // Если есть, увеличиваем количество
                item.increaseQuantity(newItem.getQuantity());
                Gdx.app.log("Inventory", "Stacked item: " + item.getItemType() + ", qty: " + item.getQuantity());
                return true;
            }
        }

        // Если предмета нет и есть место, добавляем новый
        if (items.size() < maxSize) {
            Item copy = new Item(newItem.getItemType(), new Vector2(), newItem.getQuantity(), spriteManager);
            items.add(copy);
            Gdx.app.log("Inventory", "Added new item: " + copy.getItemType());
            return true;
        }

        Gdx.app.log("Inventory", "Inventory full, cannot add: " + newItem.getItemType());
        return false;
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void dispose() {
        if (items != null) {
            for (Item item : items) {
                item.dispose();
            }
            items.clear();
        }
    }

    public void toggle() {
        isVisible = !isVisible;
        Gdx.app.log("Inventory", "Inventory visibility toggled to: " + isVisible);
    }

    public boolean isVisible() {
        return isVisible;
    }
}

