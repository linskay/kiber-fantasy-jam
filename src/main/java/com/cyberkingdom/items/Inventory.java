package com.cyberkingdom.items;

import com.cyberkingdom.entities.Player;

public class Inventory {
    private Item[] items;
    private int capacity;

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.items = new Item[capacity];
    }

    public boolean addItem(Item item) {
        for (int i = 0; i < capacity; i++) {
            if (items[i] == null) {
                items[i] = item;
                return true;
            }
        }
        return false;
    }

    public void useItem(int slot, Player player) {
        if (slot >= 0 && slot < capacity && items[slot] != null) {
            items[slot].use(player);
            items[slot] = null;
        }
    }

    public Item getItem(int slot) {
        return items[slot];
    }
}