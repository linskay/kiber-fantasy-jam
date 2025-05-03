package com.cyberkingdom.physics;

import com.cyberkingdom.entities.EntitySystem;

public class PhysicsSystemBuilder {
    private EntitySystem entitySystem;
    private float worldWidth;
    private float worldHeight;

    public PhysicsSystemBuilder setEntitySystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
        return this;
    }

    public PhysicsSystemBuilder setWorldWidth(float worldWidth) {
        this.worldWidth = worldWidth;
        return this;
    }

    public PhysicsSystemBuilder setWorldHeight(float worldHeight) {
        this.worldHeight = worldHeight;
        return this;
    }

    public PhysicsSystem createPhysicsSystem() {
        return new PhysicsSystem(entitySystem, worldWidth, worldHeight);
    }
}