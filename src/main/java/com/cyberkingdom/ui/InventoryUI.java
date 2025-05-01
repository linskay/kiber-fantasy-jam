package com.cyberkingdom.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cyberkingdom.items.Inventory;
import com.cyberkingdom.rendering.SpriteManager;

public class InventoryUI {
    private SpriteManager spriteManager;
    private float slotSize = 40;
    private float padding = 5;

    public InventoryUI() {
        // Инициализация менеджера спрайтов
        spriteManager = new SpriteManager();
    }

    public void render(SpriteBatch batch, Inventory inventory) {
        float startX = 20;
        float startY = 50;

        for (int i = 0; i < inventory.getCapacity(); i++) {
            // Отрисовка слота
            batch.draw(spriteManager.getSlotTexture(),
                    startX + i * (slotSize + padding),
                    startY,
                    slotSize,
                    slotSize);

            // Отрисовка предмета
            if (inventory.getItem(i) != null) {
                TextureRegion itemTexture = spriteManager.getItemTexture(inventory.getItem(i).getType());
                batch.draw(itemTexture,
                        startX + i * (slotSize + padding) + 4,
                        startY + 4,
                        slotSize - 8,
                        slotSize - 8);
            }
        }
    }
}