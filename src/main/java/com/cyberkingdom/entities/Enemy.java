package com.cyberkingdom.entities;

public class Enemy extends GameEntity {
    public enum EnemyType {
        TROLL_BOT(50, 150),
        VIRUS_FLYING(30, 200),
        STOP_GPT(80, 100);

        private int health;
        private int damage;

        EnemyType(int health, int damage) {
            this.health = health;
            this.damage = damage;
        }
    }

    private EnemyType enemyType;
    private int health;
    private int damage;

    public Enemy(EnemyType type) {
        super(type.name().toLowerCase(), EntityType.ENEMY);
        this.enemyType = type;
        this.health = type.health;
        this.damage = type.damage;
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            destroy();
        }
    }

    public void attack(Player player) {
        // Логика атаки игрока
    }
}