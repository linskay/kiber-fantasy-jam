package com.cyberkingdom.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.cyberkingdom.physics.CollisionComponent;

public class Platform extends GameEntity {
    private Rectangle rectangle;
    private CollisionComponent collision;

    public Platform(Rectangle rectangle, Texture texture) {
        super("Platform");
        this.rectangle = rectangle;
        this.collision = new CollisionComponent(rectangle.width, rectangle.height);
        this.position.set(rectangle.x, rectangle.y);
        this.texture = texture;
        collision.update(position);
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}