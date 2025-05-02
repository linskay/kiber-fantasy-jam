package com.cyberkingdom.items;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;
    private int capacity = 5;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public int getCapacity() { return capacity; }
    public List<Item> getItems() { return items; }
    public void addItem(Item item) {
        if (items.size() < capacity) items.add(item);
    }
}