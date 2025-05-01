package com.cyberkingdom.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Item {
    private Vector2 position;
    private Texture texture;
    private String type;
    private Rectangle bounds;

    public Item(Vector2 position, String type, String spritePath) {
        this.position = position;
        this.type = type;
        this.texture = new Texture(spritePath);
        this.bounds = new Rectangle(position.x, position.y, 32, 32);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public String getType() {
        return type;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}