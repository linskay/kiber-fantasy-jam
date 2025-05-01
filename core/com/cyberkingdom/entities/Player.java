package com.cyberkingdom.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.items.Item;

import java.util.List;

public class Player {
    private Vector2 position;
    private Texture texture;
    private Vector2 velocity;
    private boolean isJumping;
    private float speedBoostTimer;
    private Rectangle bounds;

    public Player(Vector2 position, String spritePath) {
        this.position = position;
        this.texture = new Texture(spritePath);
        this.velocity = new Vector2(0, 0);
        this.isJumping = false;
        this.speedBoostTimer = 0;
        this.bounds = new Rectangle(position.x, position.y, 32, 32);
    }

    public void update(float delta, TiledMap map) {
        // Apply gravity
        velocity.y -= 500 * delta;
        if (position.y <= 0) {
            position.y = 0;
            velocity.y = 0;
            isJumping = false;
        }

        // Update position
        position.add(velocity.x * delta, velocity.y * delta);
        bounds.setPosition(position);

        // Handle speed boost (RTX 4090)
        if (speedBoostTimer > 0) {
            speedBoostTimer -= delta;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void moveLeft() {
        velocity.x = speedBoostTimer > 0 ? -300 : -200;
    }

    public void moveRight() {
        velocity.x = speedBoostTimer > 0 ? 300 : 200;
    }

    public void jump() {
        if (!isJumping) {
            velocity.y = 400;
            isJumping = true;
        }
    }

    public void useItem(List<Item> inventory) {
        if (!inventory.isEmpty()) {
            Item item = inventory.get(0);
            switch (item.getType()) {
                case "USB_SCATERT":
                    // Restore 50% HP (implement HP system if needed)
                    break;
                case "CRYPTO_SHOVEL":
                    // Break hidden blocks (implement map interaction)
                    break;
                case "RTX_4090":
                    speedBoostTimer = 10; // 10 seconds speed boost
                    break;
                case "TUSHENKA":
                    // Apply temporary armor (implement armor system)
                    break;
                case "GROK_ALGORITHMS":
                    // Reveal map secrets (implement map reveal)
                    break;
                case "WIFI_KEY":
                    // Unlock boss fight (implement boss trigger)
                    break;
            }
            inventory.remove(item);
        }
    }

    public boolean collidesWith(Item item) {
        return bounds.overlaps(item.getBounds());
    }

    public Vector2 getPosition() {
        return position;
    }

    public void dispose() {
        texture.dispose();
    }
}