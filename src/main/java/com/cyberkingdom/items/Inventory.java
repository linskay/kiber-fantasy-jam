package com.cyberkingdom.items;

import java.util.ArrayList;
import java.util.List;

//public class Inventory {
//    private List<Item> items;
//    private int capacity = 5;
//
//    public Inventory() {
//        this.items = new ArrayList<>();
//    }
//
//    public int getCapacity() { return capacity; }
//    public List<Item> getItems() { return items; }
//    public void addItem(Item item) {
//        if (items.size() < capacity) items.add(item);
//    }
//}

//public class Inventory {
//    private List<Item> items;
//    private int capacity = 9; // 3x3 ячейки
//
//    public Inventory() {
//        this.items = new ArrayList<>();
//    }
//
//    public int getCapacity() {
//        return capacity;
//    }
//
//    public List<Item> getItems() {
//        return items;
//    }
//
//    /** Добавляет предмет в инвентарь.
//     * Если предмет того же типа уже есть и он стекается, увеличивает количество.
//     * Иначе добавляет новый предмет, если есть место.
//     * Возвращает true, если предмет добавлен, false - если инвентарь полон.
//     */
//    public boolean addItem(Item newItem) {
//        for (Item item : items) {
//            if (item.getItemType().equals(newItem.getItemType())) {
//                // Предполагаем, что предметы одного типа можно стекать
//                item.increaseQuantity(newItem.getQuantity());
//                return true;
//            }
//        }
//        if (items.size() < capacity) {
//            items.add(newItem);
//            return true;
//        }
//        return false; // нет места
//    }
//
//    /** Получить предмет по индексу */
//    public Item getItem(int index) {
//        if (index < 0 || index >= items.size()) return null;
//        return items.get(index);
//    }
//
//    /** Удалить предмет из инвентаря */
//    public boolean removeItem(Item item) {
//        return items.remove(item);
//    }
//
//    /** Удалить предмет по индексу */
//    public boolean removeItem(int index) {
//        if (index < 0 || index >= items.size()) return false;
//        items.remove(index);
//        return true;
//    }
//}

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {
    private List<Item> items;
    private int capacity = 9; // 3x3 ячейки

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * Возвращает неизменяемый список предметов,
     * чтобы внешний код не мог менять содержимое инвентаря напрямую.
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Добавляет предмет в инвентарь.
     * Если предмет того же типа уже есть и он стекается, увеличивает количество.
     * Иначе добавляет копию нового предмета, если есть место.
     * Возвращает true, если предмет добавлен, false - если инвентарь полон.
     */
    public boolean addItem(Item newItem) {
        for (Item item : items) {
            if (item.getItemType().equals(newItem.getItemType())) {
                // Предполагаем, что предметы одного типа можно стекать
                item.increaseQuantity(newItem.getQuantity());
                return true;
            }
        }
        if (items.size() < capacity) {
            // Создаем копию предмета, чтобы отделить инвентарь от игрового объекта
            Item copy = new Item(newItem.getPosition(), newItem.getItemType(), newItem.getDescription(), newItem.getQuantity());
            items.add(copy);
            return true;
        }
        return false; // нет места
    }

    /** Получить предмет по индексу */
    public Item getItem(int index) {
        if (index < 0 || index >= items.size()) return null;
        return items.get(index);
    }

    /** Удалить предмет из инвентаря */
    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    /** Удалить предмет по индексу */
    public boolean removeItem(int index) {
        if (index < 0 || index >= items.size()) return false;
        items.remove(index);
        return true;
    }
}

