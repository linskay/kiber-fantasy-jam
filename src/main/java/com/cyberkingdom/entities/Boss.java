package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;
import com.cyberkingdom.physics.CollisionComponent;

public class Boss extends GameEntity implements Collidable {
    private CollisionComponent collision;
    private String type;
    private int hitCount = 0;
    private int maxHitsToDefeat = 3;
    private float hitCooldown = 1.0f;
    private float timeSinceLastHit = 0f;

    public Boss(String name, float x, float y) {
        super(name);
        this.type = name;
        this.position.set(x, y);
        this.collision = new CollisionComponent(64, 64);
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

    public void setMaxHitsToDefeat(int hits) {
        this.maxHitsToDefeat = hits;
    }

    protected void die() {
        setActive(false);
        System.out.println(getType() + " побежден!");
    }

    public void update(float deltaTime) {
        collision.update(position);
        if (timeSinceLastHit < hitCooldown) {
            timeSinceLastHit += deltaTime;
        }
    }

    public boolean tryRegisterHit() {
        if (!isActive()) return false;
        if (timeSinceLastHit >= hitCooldown) {
            hitCount++;
            timeSinceLastHit = 0f;
            if (hitCount >= maxHitsToDefeat) {
                die();
            }
            return true;
        }
        return false;
    }

    public String getType() {
        return type;
    }

    public CollisionComponent getCollisionComponent() {
        return collision;
    }
}