package com.cyberkingdom.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.cyberkingdom.physics.CollisionComponent;

public class Platform extends GameEntity {
    protected static final float DEFAULT_WIDTH = 128f;
    protected static final float DEFAULT_HEIGHT = 32f;

    protected Rectangle rectangle;
    protected CollisionComponent collision;
    protected boolean isGround;

    public Platform(Rectangle rectangle, Texture texture) {
        super("Platform", null);
        initializePlatform(rectangle, texture);
    }

    protected void initializePlatform(Rectangle rectangle, Texture texture) {
        this.texture = texture;
        this.rectangle = rectangle;
        this.collision = new CollisionComponent(rectangle.width, rectangle.height);
        this.position.set(rectangle.x, rectangle.y);
        this.isGround = false;
        collision.update(position);
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public float getX() {
        return rectangle.x;
    }

    public float getY() {
        return rectangle.y;
    }

    public float getWidth() {
        return rectangle.width;
    }

    public float getHeight() {
        return rectangle.height;
    }

    public boolean isGround() {
        return isGround;
    }

    public void setGround(boolean ground) {
        isGround = ground;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        collision.update(position);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y, rectangle.width, rectangle.height);
        }
    }
}