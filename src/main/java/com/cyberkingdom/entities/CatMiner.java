package com.cyberkingdom.entities;

public class CatMiner extends Boss {
    private int attackAttempts = 0;
    private float attackCooldown = 0f;

    public CatMiner(float x, float y) {
        super("CAT_MINER", x, y);
        setMaxHitsToDefeat(1); // У кота 1 хп
    }


    @Override
    public AnimationComponent getAnimation() {
        return super.getAnimation();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        attackCooldown -= deltaTime;
        if (attackCooldown <= 0) {
            attemptAttack();
            attackCooldown = 2f;
        }
    }

    private void attemptAttack() {
        attackAttempts++;
        System.out.println("Кот-майнер кричит: Мои монеты упали на 200%!");

        if (attackAttempts >= 3) {
            System.out.println("Кот-майнер исчезает, так и не попав!");
            setActive(false);
        }
    }
}