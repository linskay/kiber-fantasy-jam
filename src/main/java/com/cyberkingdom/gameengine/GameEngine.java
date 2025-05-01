package com.cyberkingdom.gameengine;

import com.badlogic.gdx.ApplicationListener;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.world.LevelLoader;

public class GameEngine implements ApplicationListener {
    private EntitySystem entitySystem;
    private PhysicsSystem physicsSystem;
    private LevelLoader levelLoader;
    private boolean paused;
    private float accumulator;

    public GameEngine() {
        entitySystem = new EntitySystem();
        physicsSystem = new PhysicsSystem();
        levelLoader = new LevelLoader(entitySystem);
        paused = false;
        accumulator = 0f;
    }

    public void update(float deltaTime) {
        if (paused) return;

        // Фиксированный шаг физики
        accumulator += deltaTime;
        while (accumulator >= 0.016f) {
            physicsSystem.update(0.016f, entitySystem.getEntities());
            accumulator -= 0.016f;
        }

        entitySystem.update(deltaTime);
        levelLoader.update(deltaTime);
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void render() {

    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    @Override
    public void dispose() {

    }
}