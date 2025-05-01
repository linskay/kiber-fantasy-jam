package com.cyberkingdom.entities;

import static com.cyberkingdom.entities.Boss.BossType.DEAD_INSIDE_DLL;
import static com.cyberkingdom.entities.Boss.BossType.MINER_CAT;

public class BossFightLogic {
    private Boss boss;
    private int phase;

    public BossFightLogic(Boss boss) {
        this.boss = boss;
        this.phase = 1;
    }

    public void update(float deltaTime) {
        switch (boss.getType()) {
            case DEAD_INSIDE_DLL:
                updateDeadInsideDLL(deltaTime);
                break;
            case MINER_CAT:
                updateMinerCat(deltaTime);
                break;
        }
    }

    private void updateDeadInsideDLL(float deltaTime) {
        // Логика босса DEAD_INSIDE.DLL
    }

    private void updateMinerCat(float deltaTime) {
        // Логика Кота-майнера
    }
}