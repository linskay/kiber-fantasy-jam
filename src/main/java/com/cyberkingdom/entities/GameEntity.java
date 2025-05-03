package com.cyberkingdom.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.rendering.SpriteManager;

public abstract class GameEntity {

    protected Vector2 position;
    protected Vector2 velocity;
    protected String name;
    protected boolean isActive;
    protected AnimationComponent animation;
    protected static SpriteManager spriteManager;

    public GameEntity(String name) {
        this.name = name;
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.isActive = true;
        this.animation = new AnimationComponent();
        setupAnimations();
    }

    public AnimationComponent getAnimation() { return animation; }
    public String getName() { return name; }
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public static void setSpriteManager(SpriteManager manager) {
        spriteManager = manager;
    }

    public abstract CollisionComponent getCollisionComponent();

    public void setupAnimations() {
        if (spriteManager == null) {
            Gdx.app.error("GameEntity", "SpriteManager is null!");
            return;
        }

        TextureRegion[] frames = spriteManager.getFrames(name);
        if (frames == null || frames.length == 0) {
            Gdx.app.error("GameEntity", "No frames for: " + name);
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.RED);
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            animation.addFrame(new TextureRegion(texture));
            pixmap.dispose();
        } else {
            for (TextureRegion frame : frames) {
                animation.addFrame(frame);
            }
            animation.setFrameDuration(0.1f);
        }
    }
}