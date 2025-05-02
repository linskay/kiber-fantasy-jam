package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Vector2;

public class Enemy extends GameEntity {
    public enum EnemyType {
        TROLL_BOT
    }

    public Enemy(EnemyType type, float x, float y) {
        super(type.name());
        position.set(x, y);
    }
}