package com.cyberkingdom.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CollisionComponent {
    private Rectangle bounds;
    private Vector2 offset;

    public CollisionComponent(float width, float height) {
        this.bounds = new Rectangle(0, 0, width, height);
        this.offset = new Vector2(-width/2, -height/2);
    }

    public void update(Vector2 position) {
        bounds.setPosition(position.x + offset.x, position.y + offset.y);
    }

    public boolean collidesWith(CollisionComponent other) {
        return bounds.overlaps(other.getBounds());
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void debugPrint() {
        System.out.printf("Collision bounds: (%.1f, %.1f) %.1fx%.1f%n",
                bounds.x, bounds.y, bounds.width, bounds.height);
    }
}