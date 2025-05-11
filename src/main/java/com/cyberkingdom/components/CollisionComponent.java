package com.cyberkingdom.components;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CollisionComponent {
    private Vector2 position;
    private int width;
    private int height;
    private Rectangle bounds;

    public CollisionComponent(Vector2 position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(position.x, position.y, width, height);
    }

    public void update(Vector2 newPosition) {
        this.position = newPosition;
        this.bounds.setPosition(newPosition.x, newPosition.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
} 