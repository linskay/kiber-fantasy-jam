package com.cyberkingdom.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.GameEntity;

public class Item extends GameEntity {
    private String itemType;
    private Texture texture;

    public Item(Vector2 position, String itemType) {
        super("Item_" + itemType);
        this.itemType = itemType;
        this.position.set(position);
        try {
            this.texture = new Texture(Gdx.files.internal("assets/items/" + itemType.toLowerCase() + ".png"));
        } catch (Exception e) {
            System.err.println("Не удалось загрузить текстуру: " + e.getMessage());
            this.texture = new Texture(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        }
    }

    public void use() {
        System.out.println("Использован предмет: " + itemType);
    }

    public String getItemType() { return itemType; }
    public void dispose() { if (texture != null) texture.dispose(); }
}