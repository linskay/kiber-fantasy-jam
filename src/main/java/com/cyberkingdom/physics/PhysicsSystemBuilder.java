package com.cyberkingdom.physics;

import com.cyberkingdom.entities.EntitySystem;

public class PhysicsSystemBuilder {
    private EntitySystem entitySystem;
    private float worldWidth = 1200f;
    private float worldHeight = 800f;

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
        PhysicsSystem physicsSystem = new PhysicsSystem(entitySystem);
        return physicsSystem;
    }
}