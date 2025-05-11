package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.rendering.SpriteManager;

public class CatMiner extends Boss {
    private int attackAttempts = 0;
    private float attackCooldown = 0f;
    private Player target;
    private float miningSpeed = 50f;
    private float miningRange = 100f;

    public CatMiner(float x, float y, SpriteManager spriteManager) {
        super("CAT_MINER", x, y, spriteManager);
        setMaxHitsToDefeat(1);
        this.miningSpeed = 50f;
        this.miningRange = 100f;
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