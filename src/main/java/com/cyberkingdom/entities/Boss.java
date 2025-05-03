package com.cyberkingdom.entities;

import com.cyberkingdom.physics.CollisionComponent;

//public class Boss extends GameEntity {
//    private String type;
//
//    public Boss(String name, float x, float y) {
//        super(name);
//        this.type = name;
//        position.set(x, y);
//    }
//
//    @Override
//    public void setupAnimations() {
//        super.setupAnimations();
//    }
//
//    public String getType() {
//        return type;
//    }
//}

//public class Boss extends GameEntity {
//    private CollisionComponent collision;
//    private String type;
//    private int hitCount = 0;          // Счётчик ударов
//    private int maxHitsToDefeat=3;       // Количество ударов для победы
//
//    public Boss(String name, float x, float y) {
//        super(name);
//        this.type = name;
//        this.position.set(x, y);
//        this.collision = new CollisionComponent(64, 64); // размер коллизии
//    }
//
//    // Обновление позиции коллизии
//    public void update(float deltaTime) {
//        collision.update(position);
//        // Можно добавить другую логику обновления босса
//    }
//
//    public void registerHit() {
//        if (!isActive()) return; // Если босс уже мёртв, игнорируем удары
//
//        hitCount++;
//        System.out.println(type + " получил удар #" + hitCount);
//        if (hitCount >= maxHitsToDefeat) {
//            die();
//        }
//    }
//
//    private void die() {
//        System.out.println(type + " побеждён после " + hitCount + " ударов!");
//        setActive(false);
//        // Дополнительная логика смерти босса (анимация, звук и т.д.)
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public CollisionComponent getCollisionComponent() {
//        return collision;
//    }
//
//    // (Опционально) сброс состояния босса
//    public void reset() {
//        hitCount = 0;
//        setActive(true);
//    }
//}

public class Boss extends GameEntity {
    private CollisionComponent collision;
    private String type;
    private int hitCount = 0;          // Счётчик ударов
    private int maxHitsToDefeat = 3;   // Количество ударов для победы

    private float hitCooldown = 1.0f;  // Время между ударами в секундах
    private float timeSinceLastHit = 0f;

    public Boss(String name, float x, float y) {
        super(name);
        this.type = name;
        this.position.set(x, y);
        this.collision = new CollisionComponent(64, 64); // размер коллизии
    }

    // Обновление позиции коллизии и таймера
    public void update(float deltaTime) {
        collision.update(position);

        if (timeSinceLastHit < hitCooldown) {
            timeSinceLastHit += deltaTime;
        }

        // Можно добавить другую логику обновления босса
    }

    // Метод попытки зарегистрировать удар с учётом задержки
    public boolean tryRegisterHit() {
        if (!isActive()) return false; // Если босс мёртв, не регистрируем

        if (timeSinceLastHit >= hitCooldown) {
            hitCount++;
            System.out.println(type + " получил удар #" + hitCount);
            timeSinceLastHit = 0f;

            if (hitCount >= maxHitsToDefeat) {
                die();
            }
            return true;
        }
        return false;
    }

    private void die() {
        System.out.println(type + " побеждён после " + hitCount + " ударов!");
        setActive(false);
        // Дополнительная логика смерти босса (анимация, звук и т.д.)
    }

    public String getType() {
        return type;
    }

    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    // (Опционально) сброс состояния босса
    public void reset() {
        hitCount = 0;
        timeSinceLastHit = hitCooldown; // Чтобы можно было сразу получать удары после сброса
        setActive(true);
    }
}
