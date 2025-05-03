package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;
import com.cyberkingdom.physics.CollisionComponent;

public class Enemy extends GameEntity implements Collidable {
    public enum EnemyType {
        TROLL_BOT,
        GOBLIN,
        CYBER_DEMON
    }

    private EnemyType type;
    private CollisionComponent collision;

    public Enemy(EnemyType type, float x, float y) {
        super(type.name());
        this.type = type;
        this.position.set(x, y);
        this.collision = new CollisionComponent(32, 32); // Размер коллизии
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    @Override
    public Rectangle getCollisionBounds() {
        return collision.getBounds();
    }

    @Override
    public AnimationComponent getAnimation() {
        return super.getAnimation();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    public EnemyType getEnemyType() {
        return type;
    }

    public void update(float deltaTime) {
        collision.update(position);
        // Базовая логика движения врагов
    }
}