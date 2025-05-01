package com.cyberkingdom.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Boss {
    private Vector2 position;
    private Texture texture;
    private String type;
    private Rectangle bounds;

    public Boss(Vector2 position, String type, String spritePath) {
        this.position = position;
        this.type = type;
        this.texture = new Texture(spritePath);
        this.bounds = new Rectangle(position.x, position.y, 32, 32);
    }

    public void update(float delta, Player player) {
        switch (type) {
            case "TROLL_BOT":
                // Zmey Gorynych: DDoS attack (three heads)
                position.x += Math.sin(System.currentTimeMillis() / 1000.0) * 100 * delta;
                break;
            case "DEAD_INSIDE.DLL":
                // Koschei: Stationary, requires WiFi-Key
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