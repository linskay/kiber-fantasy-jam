package com.cyberkingdom.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    private Vector2 position;
    private Texture texture;
    private String type;
    private Rectangle bounds;

    public Enemy(Vector2 position, String type, String spritePath) {
        this.position = position;
        this.type = type;
        this.texture = new Texture(spritePath);
        this.bounds = new Rectangle(position.x, position.y, 32, 32);
    }

    public void update(float delta, Player player) {
        switch (type) {
            case "TROLL_BOT":
                // Move towards player and spam messages
                if (player.getPosition().x > position.x) position.x += 100 * delta;
                else position.x -= 100 * delta;
                break;
            case "VIRUS_FLYING":
                // Fly in straight line
                position.x += 150 * delta;
                if (position.x > 1280) position.x = 0;
                break;
        }
        bounds.setPosition(position);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void dispose() {
        texture.dispose();
    }
}