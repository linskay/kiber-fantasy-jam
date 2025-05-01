package com.cyberkingdom.entities;

public class Boss extends Enemy {
    public enum BossType {
        DEAD_INSIDE_DLL(500, 50, 3),
        MINER_CAT(300, 70, 2);

        private int health;
        private int damage;
        private int phases;

        BossType(int health, int damage, int phases) {
            this.health = health;
            this.damage = damage;
            this.phases = phases;
        }
    }

    private BossType bossType;
    private int currentPhase;

    public Boss(BossType type) {
        super(Enemy.EnemyType.STOP_GPT); // Базовые параметры
        this.bossType = type;
        this.currentPhase = 1;
    }

    public void startNextPhase() {
        if (currentPhase < bossType.phases) {
            currentPhase++;
            // Усиление босса в новой фазе
        }
    }
}