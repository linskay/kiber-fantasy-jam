package com.cyberkingdom.inventory;

import com.cyberkingdom.items.Item;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;
    private int maxSize;

    public Inventory() {
        this.items = new ArrayList<>();
        this.maxSize = 20;
    }

    public void copyFrom(Inventory other) {
        if (other == null) return;
        this.items.clear();
        this.items.addAll(other.items);
        this.maxSize = other.maxSize;
    }

    public boolean addItem(Item item) {
        if (items.size() < maxSize) {
            items.add(item);
            return true;
        }
        return false;
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void dispose() {
        if (items != null) {
            items.clear();
        }
    }
} 