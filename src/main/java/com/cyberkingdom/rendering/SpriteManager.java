package com.cyberkingdom.rendering;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cyberkingdom.items.Item;

public class SpriteManager {
    private Texture itemsAtlas;
    private Texture uiAtlas;

    public SpriteManager() {
        // Загрузка текстур (временная реализация)
        itemsAtlas = new Texture("items.png");
        uiAtlas = new Texture("ui.png");
    }

    public TextureRegion getSlotTexture() {
        return new TextureRegion(uiAtlas, 0, 0, 32, 32);
    }

    public TextureRegion getItemTexture(String itemType) {
        // Логика выбора текстуры по типу предмета
        return new TextureRegion(itemsAtlas, 0, 0, 32, 32);
    }

    public void dispose() {
        itemsAtlas.dispose();
        uiAtlas.dispose();
    }
}