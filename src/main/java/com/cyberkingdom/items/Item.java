package com.cyberkingdom.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.Collidable;
import com.cyberkingdom.entities.GameEntity;
import com.cyberkingdom.physics.CollisionComponent;

public class Item extends GameEntity implements Collidable {
    private String itemType;
    private Texture texture;
    private CollisionComponent collision;

    public Item(Vector2 position, String itemType) {
        super("Item_" + itemType);
        this.itemType = itemType;
        this.position.set(position);
        this.collision = new CollisionComponent(16, 16);

        try {
            this.texture = new Texture(Gdx.files.internal("assets/items/" + itemType.toLowerCase() + ".png"));
        } catch (Exception e) {
            // Создаем текстуру-заглушку через Pixmap
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 1, 0, 1); // Желтый цвет
            pixmap.fill();
            this.texture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    @Override
    public CollisionComponent getCollisionComponent() {
        return collision;
    }

    @Override
    public Rectangle getCollisionBounds() {
        return collision.getBounds();
    }

    public void use() {
        System.out.println("Использован предмет: " + itemType);
    }

    public String getItemType() {
        return itemType;
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}