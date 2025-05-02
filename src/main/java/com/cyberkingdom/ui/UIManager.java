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

    public void render(Player player, SpriteBatch batch) {
        System.out.println("Рендеринг UIManager");
        inventoryUI.render(player, batch);
        healthBar.render(player, batch);
        System.out.println("UIManager отрендерен");
    }

    public void dispose() {
        inventoryUI.dispose();
        healthBar.dispose();
    }
}