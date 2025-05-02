package com.cyberkingdom.entities;

public class BossFightLogic {
    private Boss boss;

    public BossFightLogic(Boss boss) {
        this.boss = boss;
        System.out.println("Инициализация боя с боссом " + boss.getType());
    }

    public void update(float deltaTime) {
        if ("STOP_GPT".equals(boss.getType())) {
            System.out.println("Обновление боя с боссом STOP_GPT");
            // Здесь можно добавить логику боя с STOP_GPT
        } else {
            System.out.println("Обновление боя с неизвестным боссом: " + boss.getType());
        }
    }
}