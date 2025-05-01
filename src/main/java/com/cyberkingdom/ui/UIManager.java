package com.cyberkingdom.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberkingdom.entities.Player;

public class UIManager {
    private SpriteBatch batch;
    private HealthBar healthBar;
    private InventoryUI inventoryUI;

    public UIManager() {
        this.batch = new SpriteBatch();
        this.healthBar = new HealthBar();
        this.inventoryUI = new InventoryUI();
    }

    public void render(Player player) {
        batch.begin();
        healthBar.render(batch, player);
        inventoryUI.render(batch, player.getInventory());
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        healthBar.dispose();
    }
}