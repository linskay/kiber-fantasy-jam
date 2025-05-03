package com.cyberkingdom.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CollisionComponent {
    private Rectangle bounds;
    private boolean isSolid;

    public CollisionComponent(float width, float height) {
        this.bounds = new Rectangle(64, 64, width, height);
        this.isSolid = true;
    }

//    public void update(Vector2 position) {
//        bounds.setPosition(position);
//    }

    public void update(Vector2 position) {
        bounds.setPosition(position.x - bounds.getWidth() / 2, position.y - bounds.getHeight() / 2);
    }

    public boolean collidesWith(CollisionComponent other) {
        return bounds.overlaps(other.bounds);
    }

    // Getters
    public Rectangle getBounds() { return bounds; }
    public boolean isSolid() { return isSolid; }



    // Метод для отладки - выводит координаты и размеры прямоугольника
    public void debugPrint() {
        System.out.println("Bounds: x=" + bounds.x + ", y=" + bounds.y + ", w=" + bounds.width + ", h=" + bounds.height);
    }
}