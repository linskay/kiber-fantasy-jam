package com.cyberkingdom.entities;

import com.badlogic.gdx.math.Vector2;

public class Boss extends GameEntity {
    private String type;

    public Boss(String name, float x, float y) {
        super(name);
        this.type = name;
        position.set(x, y);
    }

    @Override
    public void setupAnimations() {
        super.setupAnimations();
    }

    public String getType() {
        return type;
    }
}