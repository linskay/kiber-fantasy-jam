package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.items.Item;

public class EntityFactory {
    public GameEntity createPlayer(float x, float y) {
        System.out.println("Создание игрока на (" + x + ", " + y + ")");
        return new Player(x, y);
    }

    public GameEntity createEnemy(String name, float x, float y) {
        System.out.println("Создание врага " + name + " на (" + x + ", " + y + ")");
        Enemy.EnemyType type = Enemy.EnemyType.valueOf(name.toUpperCase()); // Предполагаем, что имя соответствует типу
        return new Enemy(type, x, y);
    }

    public GameEntity createBoss(String name, float x, float y) {
        System.out.println("Создание босса " + name + " на (" + x + ", " + y + ")");
        return new Boss(name, x, y);
    }

    public GameEntity createItem(Vector2 position, String name) {
        System.out.println("Создание предмета " + name + " на (" + position.x + ", " + position.y + ")");
        return new Item(position, name);
    }
}