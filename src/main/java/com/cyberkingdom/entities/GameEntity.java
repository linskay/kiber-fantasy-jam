package com.cyberkingdom.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.physics.CollisionComponent;
import com.cyberkingdom.rendering.SpriteManager;
import com.cyberkingdom.items.Item;

public abstract class GameEntity {
    protected Vector2 position;
    protected Vector2 velocity;
    protected String name;
    protected boolean isActive;
    protected AnimationComponent animation;
    protected static SpriteManager spriteManager;
    protected Texture texture;
    protected CollisionComponent collision;

    public GameEntity(String name) {
        this.name = name;
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.isActive = true;
        this.animation = new AnimationComponent();
        setupAnimations();
    }

    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public void setVelocity(float x, float y) { velocity.set(x, y); }
    public AnimationComponent getAnimation() { return animation; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public String getName() { return name; }
    public Texture getTexture() { return texture; }

    public void setupAnimations() {
        initializeTexture(spriteManager);
    }

    protected void initializeTexture(SpriteManager spriteManager) {
        if (spriteManager == null) {
            Gdx.app.error("GameEntity", "SpriteManager is null for entity: " + name);
            return;
        }

        TextureRegion[] frames = spriteManager.getFrames(name);
        if (frames != null && frames.length > 0) {
            Gdx.app.log("GameEntity", "Found " + frames.length + " frames for entity: " + name);
            for (TextureRegion frame : frames) {
                if (frame != null) {
                    animation.addFrame(frame);
                    Gdx.app.log("GameEntity", "Added frame with size: " + frame.getRegionWidth() + "x" + frame.getRegionHeight());
                } else {
                    Gdx.app.error("GameEntity", "Null frame found for entity: " + name);
                }
            }
            animation.setFrameDuration(0.1f);
            texture = frames[0].getTexture();
            Gdx.app.log("GameEntity", "Set texture from first frame for: " + name + 
                " with size: " + texture.getWidth() + "x" + texture.getHeight());
        } else {
            Gdx.app.error("GameEntity", "No frames found for entity: " + name);
        }
    }

    private void createFallbackTexture() {
        int width, height;
        if (name.equals("Platform")) {
            width = 128;
            height = 32;
        } else if (name.equals("COIN")) {
            width = 32;
            height = 32;
        } else if (name.equals("Player")) {
            width = 64;
            height = 64;
        } else {
            width = 48;
            height = 48;
        }

        Gdx.app.log("GameEntity", "Creating fallback texture for: " + name + " with size: " + width + "x" + height);
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        if (name.equals("Platform")) {
            for (int y = 0; y < height; y++) {
                float intensity = 0.5f + (y / (float)height) * 0.2f;
                pixmap.setColor(intensity, intensity, intensity, 1f);
                pixmap.drawLine(0, y, width - 1, y);
            }
        } else if (name.equals("COIN")) {
            pixmap.setColor(1f, 0.8f, 0f, 1f);
            pixmap.fillCircle(width/2, height/2, width/2 - 2);
            pixmap.setColor(1f, 1f, 0.8f, 0.8f);
            pixmap.fillCircle(width/3, height/3, width/8);
            pixmap.setColor(0.8f, 0.6f, 0f, 1f);
            pixmap.drawCircle(width/2, height/2, width/2 - 2);
        } else if (name.equals("Player")) {
            pixmap.setColor(0f, 0.5f, 1f, 1f);
            pixmap.fill();
            pixmap.setColor(0f, 0.3f, 0.8f, 1f);
            pixmap.fillRectangle(width/4, height/4, width/2, height/2);
        } else {
            pixmap.setColor(1f, 0.2f, 0.2f, 1f);
            pixmap.fill();
            pixmap.setColor(0.8f, 0f, 0f, 1f);
            pixmap.drawRectangle(width/8, height/8, width*3/4, height*3/4);
        }
        texture = new Texture(pixmap);
        animation.addFrame(new TextureRegion(texture));
        pixmap.dispose();
        Gdx.app.log("GameEntity", "Created fallback texture for: " + name + 
            " with size: " + texture.getWidth() + "x" + texture.getHeight());
    }

    public static void setSpriteManager(SpriteManager manager) {
        spriteManager = manager;
        Gdx.app.log("GameEntity", "SpriteManager set");
    }

    public void update(float deltaTime) {
        if (animation != null) {
            animation.update(deltaTime);
        }
    }

    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
        if (collision != null) {
            collision.update(position);
        }
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void dispose() {
        texture = null;
        if (animation != null) {
            animation.dispose();
            animation = null;
        }
        if (collision != null) {
            collision = null;
        }
    }
}