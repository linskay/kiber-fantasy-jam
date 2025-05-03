package com.cyberkingdom.entities;

//public class BossFightLogic {
//    private Boss boss;
//
//    public BossFightLogic(Boss boss) {
//        this.boss = boss;
//        System.out.println("Инициализация боя с боссом " + boss.getType());
//    }
//
//    public void update(float deltaTime) {
//        if ("STOP_GPT".equals(boss.getType())) {
//            System.out.println("Обновление боя с боссом STOP_GPT");
//            // Здесь можно добавить логику боя с STOP_GPT
//        } else {
//            System.out.println("Обновление боя с неизвестным боссом: " + boss.getType());
//        }
//    }
//}

import com.cyberkingdom.gameengine.GameEngine;

public class BossFightLogic {
    private GameEngine gameEngine;
    private Boss boss;
    private Player player;
    private boolean levelCompleted = false;

    public BossFightLogic(Boss boss,
                          Player player,
     GameEngine gameEngine) {
        this.boss = boss;
        this.player = player;
        this.gameEngine = gameEngine;
    }

    public void update(float deltaTime) {
        boss.update(deltaTime);
        player.update(deltaTime);

        if (!levelCompleted && player.getCollisionComponent().collidesWith(boss.getCollisionComponent())) {
            System.out.println("Игрок атакует босса!");
            if (boss.tryRegisterHit()) {
                System.out.println("Удар по боссу зарегистрирован");
            }

            if (!boss.isActive()) {
                levelCompleted = true;
                System.out.println("Босс побеждён! Переход к следующему уровню...");
                gameEngine.nextLevel();
            }
        }
    }

}
