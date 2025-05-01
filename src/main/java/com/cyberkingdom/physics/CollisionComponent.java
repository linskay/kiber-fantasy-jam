package com.cyberkingdom.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CollisionComponent {
    private Rectangle bounds;
    private boolean isSolid;

    public CollisionComponent(float width, float height) {
        this.bounds = new Rectangle(0, 0, width, height);
        this.isSolid = true;
    }

    public void update(Vector2 position) {
        bounds.setPosition(position);
    }

    public boolean collidesWith(CollisionComponent other) {
        return bounds.overlaps(other.bounds);
    }

    // Getters
    public Rectangle getBounds() { return bounds; }
    public boolean isSolid() { return isSolid; }
}