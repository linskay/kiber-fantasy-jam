package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;

public class CatMiner extends Boss {
    private int attackAttempts = 0;
    private float attackCooldown = 0f;
    private Player target;

    public CatMiner(float x, float y) {
        super("CAT_MINER", x, y);
        setMaxHitsToDefeat(1);
    }

    public void setTarget(Player player) {
        this.target = player;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        attackCooldown -= deltaTime;
        if (attackCooldown <= 0 && target != null) {
            attemptAttack();
            attackCooldown = 2f;
        }
    }

    private void attemptAttack() {
        attackAttempts++;
        if (getCollisionComponent().collidesWith(target.getCollisionComponent())) {
            target.takeDamage(5f);
        }

        if (attackAttempts >= 3) {
            setActive(false);
        }
    }
}