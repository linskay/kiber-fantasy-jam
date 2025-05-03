package com.cyberkingdom.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.rendering.SpriteRenderer;

public class UIManager {
    private InventoryUI inventoryUI;
    private HealthBar healthBar;
    private SpriteRenderer spriteRenderer;

    public UIManager(SpriteRenderer spriteRenderer) {
        this.spriteRenderer = spriteRenderer;
        this.inventoryUI = new InventoryUI();
        this.healthBar = new HealthBar();
    }

    public void render(Player player) {
        SpriteBatch batch = spriteRenderer.getBatch();

        // Рендерим здоровье
        healthBar.render(player, batch);

        // Рендерим инвентарь
        inventoryUI.render(player, batch);
    }

    public void dispose() {
        inventoryUI.dispose();
        healthBar.dispose();
    }
}